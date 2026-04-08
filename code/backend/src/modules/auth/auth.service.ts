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
  private readonly sessionsByAccessToken = new Map<string, AuthSession>();
  private readonly sessionsByRefreshToken = new Map<string, AuthSession>();
  private readonly sessionsByAccountId = new Map<string, AuthSession>();
  private readonly codes = new Map<string, VerificationCodeRecord>();
  private readonly tokenSecret =
    process.env.AUTH_TOKEN_SECRET ?? 'cryptovpn-backend-dev-secret';

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
    const session = this.sessionsByRefreshToken.get(params.refreshToken);
    if (!session || session.status !== 'ACTIVE') {
      throw new UnauthorizedException({
        code: 'AUTH_REFRESH_INVALID',
        message: 'Refresh token is invalid',
      });
    }

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

    const activeSession = this.sessionsByAccountId.get(account.accountId);
    if (activeSession) {
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

  private async issueSession(account: AuthAccount, installationId?: string) {
    const persistedSessions: AuthSession[] = [];
    const previous = this.sessionsByAccountId.get(account.accountId);

    if (previous && previous.status === 'ACTIVE') {
      persistedSessions.push(
        await this.runtimeStateRepository.saveSession({
          ...previous,
          status: 'EVICTED',
          updatedAt: new Date().toISOString(),
        }),
      );
    }

    const now = Date.now();
    const createdAt = new Date(now).toISOString();
    const session: AuthSession = {
      sessionId: randomUUID(),
      accountId: account.accountId,
      installationId: installationId ?? null,
      accessToken: '',
      refreshToken: '',
      accessTokenExpiresAt: new Date(now + 2 * 60 * 60 * 1000).toISOString(),
      refreshTokenExpiresAt: new Date(
        now + 30 * 24 * 60 * 60 * 1000,
      ).toISOString(),
      status: 'ACTIVE',
      createdAt,
      updatedAt: createdAt,
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

    persistedSessions.push(await this.runtimeStateRepository.saveSession(session));

    for (const persistedSession of persistedSessions) {
      this.storeSession(persistedSession);
    }

    return {
      accessToken: session.accessToken,
      refreshToken: session.refreshToken,
      accessTokenExpiresAt: session.accessTokenExpiresAt,
      refreshTokenExpiresAt: session.refreshTokenExpiresAt,
      accountId: account.accountId,
      accountStatus: account.status,
    };
  }

  private requireAccessSession(accessToken: string) {
    const session = this.sessionsByAccessToken.get(accessToken);
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
    this.sessionsByAccessToken.set(session.accessToken, session);
    this.sessionsByRefreshToken.set(session.refreshToken, session);

    const currentAccountSession = this.sessionsByAccountId.get(session.accountId);
    if (
      session.status === 'ACTIVE' ||
      !currentAccountSession ||
      currentAccountSession.sessionId === session.sessionId
    ) {
      this.sessionsByAccountId.set(session.accountId, session);
    }
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

  private signToken(encodedPayload: string) {
    return createHmac('sha256', this.tokenSecret)
      .update(encodedPayload)
      .digest('base64url');
  }
}
