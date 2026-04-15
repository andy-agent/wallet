import {
  BadRequestException,
  ConflictException,
  ForbiddenException,
  Injectable,
  NotFoundException,
  OnModuleInit,
  UnauthorizedException,
} from '@nestjs/common';
import {
  createHmac,
  randomBytes,
  randomUUID,
  scryptSync,
  timingSafeEqual,
} from 'crypto';
import { RuntimeStateRepository } from '../database/runtime-state.repository';
import { AuthAccount, AuthSession, VerificationCodeRecord } from './auth.types';

type SignedTokenType = 'access' | 'refresh';

interface SignedSessionPayload {
  accountId: string;
  email: string;
  installationId: string | null;
  referralCode: string;
  sessionId: string;
  accountStatus: AuthAccount['status'];
  accessTokenExpiresAt: string;
  refreshTokenExpiresAt: string;
}

@Injectable()
export class AuthService implements OnModuleInit {
  private readonly accounts = new Map<string, AuthAccount>();
  private readonly accountsByEmail = new Map<string, string>();
  private readonly sessionsById = new Map<string, AuthSession>();
  private readonly sessionsByAccessToken = new Map<string, AuthSession>();
  private readonly sessionsByRefreshToken = new Map<string, AuthSession>();
  private readonly sessionsByAccountId = new Map<string, AuthSession>();
  private readonly codes = new Map<string, VerificationCodeRecord>();
  private readonly tokenSecret =
    process.env.AUTH_TOKEN_SECRET ?? 'cryptovpn-backend-dev-secret';
  private readonly accessTokenTtlMs = this.resolveDurationMs(
    'AUTH_ACCESS_TOKEN_TTL_MS',
    365 * 24 * 60 * 60 * 1000,
  );
  private readonly refreshTokenTtlMs = this.resolveDurationMs(
    'AUTH_REFRESH_TOKEN_TTL_MS',
    3650 * 24 * 60 * 60 * 1000,
  );

  constructor(
    private readonly runtimeStateRepository: RuntimeStateRepository,
  ) {}

  async onModuleInit() {
    const [accounts, sessions, codes] = await Promise.all([
      this.runtimeStateRepository.listAccounts(),
      this.runtimeStateRepository.listSessions(),
      this.runtimeStateRepository.listVerificationCodes(),
    ]);

    this.accounts.clear();
    this.accountsByEmail.clear();
    this.sessionsById.clear();
    this.sessionsByAccessToken.clear();
    this.sessionsByRefreshToken.clear();
    this.sessionsByAccountId.clear();
    this.codes.clear();

    for (const account of accounts) {
      this.storeAccount(account);
    }

    for (const session of sessions) {
      this.storeSession(session);
    }

    for (const code of codes) {
      this.codes.set(this.codeKey(code.email, code.purpose), code);
    }

    await this.ensureBootstrapSystemAccount();
  }

  async requestRegisterCode(email: string) {
    const normalizedEmail = email.toLowerCase();
    if (this.accountsByEmail.has(normalizedEmail)) {
      throw new ConflictException({
        code: 'EMAIL_ALREADY_EXISTS',
        message: 'Email already exists',
      });
    }

    const now = new Date().toISOString();
    const record: VerificationCodeRecord = {
      email: normalizedEmail,
      codeHash: this.hashVerificationCode('123456'),
      purpose: 'REGISTER',
      expiresAt: Date.now() + 5 * 60 * 1000,
      createdAt: now,
      updatedAt: now,
    };

    const persisted = await this.runtimeStateRepository.saveVerificationCode(record);
    this.codes.set(this.codeKey(normalizedEmail, persisted.purpose), persisted);
  }

  async requestResetCode(email: string) {
    const account = this.getAccountByEmail(email);
    const now = new Date().toISOString();
    const record: VerificationCodeRecord = {
      email: account.email,
      codeHash: this.hashVerificationCode('123456'),
      purpose: 'RESET_PASSWORD',
      expiresAt: Date.now() + 5 * 60 * 1000,
      createdAt: now,
      updatedAt: now,
    };

    const persisted = await this.runtimeStateRepository.saveVerificationCode(record);
    this.codes.set(this.codeKey(account.email, persisted.purpose), persisted);
  }

  async register(
    params: {
      email: string;
      code: string;
      password: string;
      installationId?: string;
    },
    _idempotencyKey: string,
  ) {
    const email = params.email.toLowerCase();
    if (this.accountsByEmail.has(email)) {
      throw new ConflictException({
        code: 'EMAIL_ALREADY_EXISTS',
        message: 'Email already exists',
      });
    }

    this.assertCode(email, params.code, 'REGISTER');

    const now = new Date().toISOString();
    const account: AuthAccount = {
      accountId: randomUUID(),
      email,
      passwordHash: this.hashPassword(params.password),
      status: 'ACTIVE',
      referralCode: this.generateReferralCode(),
      createdAt: now,
      updatedAt: now,
    };

    const persistedAccount = await this.runtimeStateRepository.saveAccount(account);
    this.storeAccount(persistedAccount);
    return this.issueSession(persistedAccount, params.installationId);
  }

  async login(params: {
    email: string;
    password: string;
    installationId?: string;
  }) {
    const account = this.getAccountByEmail(params.email);
    const passwordCheck = this.verifyPassword(params.password, account.passwordHash);
    if (!passwordCheck.valid) {
      throw new UnauthorizedException({
        code: 'AUTH_INVALID_CREDENTIALS',
        message: 'Invalid credentials',
      });
    }
    if (account.status === 'FROZEN') {
      throw new ForbiddenException({
        code: 'AUTH_ACCOUNT_FROZEN',
        message: 'Account is frozen',
      });
    }

    let persistedAccount = account;
    if (passwordCheck.needsRehash) {
      persistedAccount = {
        ...account,
        passwordHash: this.hashPassword(params.password),
        updatedAt: new Date().toISOString(),
      };
      persistedAccount = await this.runtimeStateRepository.saveAccount(
        persistedAccount,
      );
      this.storeAccount(persistedAccount);
    }

    return this.issueSession(persistedAccount, params.installationId);
  }

  async refresh(params: { refreshToken: string; installationId?: string }) {
    const session = this.requireRefreshSession(params.refreshToken);

    if (new Date(session.refreshTokenExpiresAt).getTime() <= Date.now()) {
      const expiredSession = {
        ...session,
        status: 'EXPIRED' as const,
        updatedAt: new Date().toISOString(),
      };
      const persistedExpiredSession = await this.runtimeStateRepository.saveSession(
        expiredSession,
      );
      this.storeSession(persistedExpiredSession);
      throw new UnauthorizedException({
        code: 'AUTH_REFRESH_INVALID',
        message: 'Refresh token expired',
      });
    }

    const account = this.accounts.get(session.accountId);
    if (!account) {
      throw new NotFoundException({
        code: 'ACCOUNT_NOT_FOUND',
        message: 'Account not found',
      });
    }

    return this.issueSession(
      account,
      params.installationId ?? session.installationId ?? undefined,
      session,
    );
  }

  async logout(accessToken: string) {
    const session = this.requireAccessSession(accessToken);
    const persistedSession = await this.runtimeStateRepository.saveSession({
      ...session,
      status: 'REVOKED',
      updatedAt: new Date().toISOString(),
    });
    this.storeSession(persistedSession);
  }

  async resetPassword(params: {
    email: string;
    code: string;
    newPassword: string;
  }) {
    const account = this.getAccountByEmail(params.email);
    this.assertCode(account.email, params.code, 'RESET_PASSWORD');

    const persistedAccount = await this.runtimeStateRepository.saveAccount({
      ...account,
      passwordHash: this.hashPassword(params.newPassword),
      updatedAt: new Date().toISOString(),
    });
    this.storeAccount(persistedAccount);

    for (const activeSession of this.listActiveSessionsForAccount(account.accountId)) {
      const persistedSession = await this.runtimeStateRepository.saveSession({
        ...activeSession,
        status: 'REVOKED',
        updatedAt: new Date().toISOString(),
      });
      this.storeSession(persistedSession);
    }
  }

  getMe(accessToken: string) {
    const session = this.requireAccessSession(accessToken);
    const account = this.accounts.get(session.accountId);

    if (!account) {
      throw new NotFoundException({
        code: 'ACCOUNT_NOT_FOUND',
        message: 'Account not found',
      });
    }

    return {
      accountId: session.accountId,
      email: account.email,
      status: account.status,
      referralCode: account.referralCode,
      subscription: null,
    };
  }

  getSessionSummary(accessToken: string) {
    const session = this.requireAccessSession(accessToken);
    return {
      sessionId: session.sessionId,
      status: session.status,
      installationId: session.installationId ?? null,
      expiresAt: session.refreshTokenExpiresAt,
    };
  }

  findAccountByReferralCode(referralCode: string) {
    for (const account of this.accounts.values()) {
      if (account.referralCode === referralCode) {
        return account;
      }
    }
    return null;
  }

  getAccountById(accountId: string) {
    return this.accounts.get(accountId) ?? null;
  }

  getTotalAccounts() {
    return this.accounts.size;
  }

  listAccounts(params: {
    page?: number;
    pageSize?: number;
    email?: string;
    status?: string;
  }) {
    const page = Math.max(1, params.page ?? 1);
    const pageSize = Math.min(100, Math.max(1, params.pageSize ?? 20));

    let items = Array.from(this.accounts.values());

    if (params.email) {
      const emailLower = params.email.toLowerCase();
      items = items.filter((item) => item.email.includes(emailLower));
    }

    if (params.status) {
      items = items.filter((item) => item.status === params.status);
    }

    items = items.sort((left, right) =>
      right.createdAt.localeCompare(left.createdAt),
    );

    const total = items.length;
    const start = (page - 1) * pageSize;
    const end = start + pageSize;

    return {
      items: items.slice(start, end).map((account) => ({
        accountId: account.accountId,
        email: account.email,
        status: account.status,
        referralCode: account.referralCode,
      })),
      page: {
        page,
        pageSize,
        total,
      },
    };
  }

  getAccountDetail(accountId: string) {
    const account = this.accounts.get(accountId);
    if (!account) {
      throw new NotFoundException({
        code: 'ACCOUNT_NOT_FOUND',
        message: 'Account not found',
      });
    }
    return {
      accountId: account.accountId,
      email: account.email,
      status: account.status,
      referralCode: account.referralCode,
    };
  }

  maskEmail(accountId: string) {
    const account = this.accounts.get(accountId);
    if (!account) {
      return 'unknown';
    }
    const [name, domain] = account.email.split('@');
    const safeName =
      name.length <= 2 ? `${name[0] ?? '*'}*` : `${name.slice(0, 2)}***`;
    return `${safeName}@${domain}`;
  }

  private async ensureBootstrapSystemAccount() {
    const passwordHash = this.resolveBootstrapSystemPasswordHash();
    if (!passwordHash) {
      return;
    }

    const email = (
      process.env.AUTH_BOOTSTRAP_SYSTEM_EMAIL ?? 'system@cnyirui.cn'
    ).trim().toLowerCase();
    const now = new Date().toISOString();
    const existing = this.findAccountByEmail(email);
    const account: AuthAccount = existing
      ? {
          ...existing,
          email,
          passwordHash,
          status: 'ACTIVE',
          updatedAt: now,
        }
      : {
          accountId: randomUUID(),
          email,
          passwordHash,
          status: 'ACTIVE',
          referralCode: this.generateReferralCode(),
          createdAt: now,
          updatedAt: now,
        };

    const persistedAccount = await this.runtimeStateRepository.saveAccount(account);
    this.storeAccount(persistedAccount);
  }

  private async issueSession(
    account: AuthAccount,
    installationId?: string,
    reusableSession?: AuthSession | null,
  ) {
    const previous =
      reusableSession ?? this.findReusableSession(account.accountId, installationId);
    const now = Date.now();
    const nowIso = new Date(now).toISOString();
    const session: AuthSession = {
      sessionId: previous?.sessionId ?? randomUUID(),
      accountId: account.accountId,
      installationId: installationId ?? previous?.installationId ?? null,
      accessToken: '',
      refreshToken: '',
      accessTokenExpiresAt: new Date(now + this.accessTokenTtlMs).toISOString(),
      refreshTokenExpiresAt: new Date(now + this.refreshTokenTtlMs).toISOString(),
      status: 'ACTIVE',
      createdAt: previous?.createdAt ?? nowIso,
      updatedAt: nowIso,
    };

    const payload: SignedSessionPayload = {
      sessionId: session.sessionId,
      accountId: account.accountId,
      email: account.email,
      installationId: session.installationId ?? null,
      referralCode: account.referralCode,
      accountStatus: account.status,
      accessTokenExpiresAt: session.accessTokenExpiresAt,
      refreshTokenExpiresAt: session.refreshTokenExpiresAt,
    };

    session.accessToken = this.createSignedToken('access', payload);
    session.refreshToken = this.createSignedToken('refresh', payload);

    const persistedSession = await this.runtimeStateRepository.saveSession(session);
    this.storeSession(persistedSession);

    return {
      accessToken: persistedSession.accessToken,
      refreshToken: persistedSession.refreshToken,
      accessTokenExpiresAt: persistedSession.accessTokenExpiresAt,
      refreshTokenExpiresAt: persistedSession.refreshTokenExpiresAt,
      accountId: account.accountId,
      accountStatus: account.status,
    };
  }

  private findReusableSession(accountId: string, installationId?: string) {
    const activeSessions = this.listActiveSessionsForAccount(accountId);
    if (installationId) {
      return (
        activeSessions.find(
          (session) => session.installationId === installationId,
        ) ??
        activeSessions[0] ??
        null
      );
    }
    return this.sessionsByAccountId.get(accountId) ?? activeSessions[0] ?? null;
  }

  private listActiveSessionsForAccount(accountId: string) {
    return Array.from(this.sessionsById.values()).filter(
      (session) => session.accountId === accountId && session.status === 'ACTIVE',
    );
  }

  private requireAccessSession(accessToken: string) {
    const session = this.resolveSessionByToken(accessToken, 'access');
    if (!session) {
      throw new UnauthorizedException({
        code: 'UNAUTHORIZED',
        message: 'Access token is invalid',
      });
    }
    if (session.status === 'EVICTED') {
      throw new UnauthorizedException({
        code: 'AUTH_SESSION_EVICTED',
        message: 'Session evicted',
      });
    }
    if (session.status !== 'ACTIVE') {
      throw new UnauthorizedException({
        code: 'UNAUTHORIZED',
        message: 'Session is not active',
      });
    }
    if (new Date(session.accessTokenExpiresAt).getTime() <= Date.now()) {
      throw new UnauthorizedException({
        code: 'UNAUTHORIZED',
        message: 'Access token expired',
      });
    }
    return session;
  }

  private requireRefreshSession(refreshToken: string) {
    const session = this.resolveSessionByToken(refreshToken, 'refresh');
    if (!session || session.status !== 'ACTIVE') {
      throw new UnauthorizedException({
        code: 'AUTH_REFRESH_INVALID',
        message: 'Refresh token is invalid',
      });
    }
    return session;
  }

  private getAccountByEmail(email: string) {
    const account = this.findAccountByEmail(email);
    if (!account) {
      throw new NotFoundException({
        code: 'ACCOUNT_NOT_FOUND',
        message: 'Account not found',
      });
    }
    return account;
  }

  private findAccountByEmail(email: string) {
    const accountId = this.accountsByEmail.get(email.toLowerCase());
    return accountId ? this.accounts.get(accountId) ?? null : null;
  }

  private assertCode(
    email: string,
    code: string,
    purpose: VerificationCodeRecord['purpose'],
  ) {
    const record = this.codes.get(this.codeKey(email, purpose));
    if (!record || record.codeHash !== this.hashVerificationCode(code)) {
      throw new BadRequestException({
        code: 'CODE_INVALID',
        message: 'Code invalid',
      });
    }
    if (record.expiresAt <= Date.now()) {
      throw new BadRequestException({
        code: 'CODE_EXPIRED',
        message: 'Code expired',
      });
    }
  }

  private codeKey(
    email: string,
    purpose: VerificationCodeRecord['purpose'],
  ) {
    return `${purpose}:${email.toLowerCase()}`;
  }

  private storeAccount(account: AuthAccount) {
    this.accounts.set(account.accountId, account);
    this.accountsByEmail.set(account.email.toLowerCase(), account.accountId);
  }

  private storeSession(session: AuthSession) {
    const previous = this.sessionsById.get(session.sessionId);
    const persistedSession: AuthSession = {
      ...session,
      accessToken: session.accessToken || previous?.accessToken || '',
      refreshToken: session.refreshToken || previous?.refreshToken || '',
    };

    this.sessionsById.set(persistedSession.sessionId, persistedSession);

    if (persistedSession.accessToken) {
      this.sessionsByAccessToken.set(
        persistedSession.accessToken,
        persistedSession,
      );
    }

    if (persistedSession.refreshToken) {
      this.sessionsByRefreshToken.set(
        persistedSession.refreshToken,
        persistedSession,
      );
    }

    const currentAccountSession = this.sessionsByAccountId.get(
      persistedSession.accountId,
    );
    if (
      persistedSession.status === 'ACTIVE' ||
      !currentAccountSession ||
      currentAccountSession.sessionId === persistedSession.sessionId
    ) {
      this.sessionsByAccountId.set(
        persistedSession.accountId,
        persistedSession,
      );
    }
  }

  private resolveSessionByToken(
    token: string,
    expectedType: SignedTokenType,
  ) {
    const cachedSession =
      expectedType === 'access'
        ? this.sessionsByAccessToken.get(token)
        : this.sessionsByRefreshToken.get(token);

    if (cachedSession) {
      return cachedSession;
    }

    const payload = this.parseSignedToken(token, expectedType);
    if (!payload) {
      return null;
    }

    const persistedSession = this.sessionsById.get(payload.sessionId);
    if (!persistedSession || persistedSession.accountId !== payload.accountId) {
      return null;
    }

    const hydratedSession: AuthSession = {
      ...persistedSession,
      installationId:
        persistedSession.installationId ?? payload.installationId ?? null,
      accessToken:
        expectedType === 'access' ? token : persistedSession.accessToken,
      refreshToken:
        expectedType === 'refresh' ? token : persistedSession.refreshToken,
      accessTokenExpiresAt: payload.accessTokenExpiresAt,
      refreshTokenExpiresAt: payload.refreshTokenExpiresAt,
    };

    this.storeSession(hydratedSession);
    return this.sessionsById.get(hydratedSession.sessionId) ?? hydratedSession;
  }

  private hashPassword(password: string) {
    const salt = randomBytes(16).toString('base64url');
    const hash = scryptSync(password, salt, 64).toString('base64url');
    return `scrypt$${salt}$${hash}`;
  }

  private verifyPassword(password: string, passwordHash: string) {
    if (!passwordHash.startsWith('scrypt$')) {
      return {
        valid: password === passwordHash,
        needsRehash: password === passwordHash,
      };
    }

    const [, salt, expectedHash] = passwordHash.split('$');
    if (!salt || !expectedHash) {
      return {
        valid: false,
        needsRehash: false,
      };
    }

    const derivedHash = scryptSync(password, salt, 64);
    const expectedBuffer = Buffer.from(expectedHash, 'base64url');
    if (expectedBuffer.length !== derivedHash.length) {
      return {
        valid: false,
        needsRehash: false,
      };
    }

    return {
      valid: timingSafeEqual(derivedHash, expectedBuffer),
      needsRehash: false,
    };
  }

  private hashVerificationCode(code: string) {
    return createHmac('sha256', this.tokenSecret)
      .update(`verification:${code}`)
      .digest('base64url');
  }

  private resolveBootstrapSystemPasswordHash() {
    const passwordHash = process.env.AUTH_BOOTSTRAP_SYSTEM_PASSWORD_HASH?.trim();
    if (passwordHash) {
      return passwordHash;
    }

    const password = process.env.AUTH_BOOTSTRAP_SYSTEM_PASSWORD;
    return password ? this.hashPassword(password) : null;
  }

  private generateReferralCode() {
    return randomUUID().replace(/-/g, '').slice(0, 8).toUpperCase();
  }

  private createSignedToken(
    type: SignedTokenType,
    payload: SignedSessionPayload,
  ) {
    const encodedPayload = Buffer.from(
      JSON.stringify({ ...payload, type }),
      'utf8',
    ).toString('base64url');
    const signature = this.signToken(encodedPayload);
    return `${type}.${encodedPayload}.${signature}`;
  }

  private parseSignedToken(
    token: string,
    expectedType: SignedTokenType,
  ): (SignedSessionPayload & { type: SignedTokenType }) | null {
    const [type, encodedPayload, signature] = token.split('.');
    if (type !== expectedType || !encodedPayload || !signature) {
      return null;
    }

    if (this.signToken(encodedPayload) !== signature) {
      return null;
    }

    try {
      const payload = JSON.parse(
        Buffer.from(encodedPayload, 'base64url').toString('utf8'),
      ) as SignedSessionPayload & { type: SignedTokenType };

      if (
        payload.type !== expectedType ||
        !payload.accountId ||
        !payload.sessionId
      ) {
        return null;
      }

      return payload;
    } catch {
      return null;
    }
  }

  private signToken(encodedPayload: string) {
    return createHmac('sha256', this.tokenSecret)
      .update(encodedPayload)
      .digest('base64url');
  }

  private resolveDurationMs(envKey: string, fallbackMs: number) {
    const raw = process.env[envKey];
    const parsed = raw ? Number(raw) : NaN;
    return Number.isFinite(parsed) && parsed > 0 ? parsed : fallbackMs;
  }
}
