import {
  Injectable,
  UnauthorizedException,
  ConflictException,
  ForbiddenException,
  BadRequestException,
  NotFoundException,
} from '@nestjs/common';
import { randomUUID } from 'crypto';
import { AuthAccount, AuthSession, VerificationCodeRecord } from './auth.types';

@Injectable()
export class AuthService {
  private readonly accounts = new Map<string, AuthAccount>();
  private readonly accountsByEmail = new Map<string, string>();
  private readonly sessionsByAccessToken = new Map<string, AuthSession>();
  private readonly sessionsByRefreshToken = new Map<string, AuthSession>();
  private readonly sessionsByAccountId = new Map<string, AuthSession>();
  private readonly codes = new Map<string, VerificationCodeRecord>();

  requestRegisterCode(email: string) {
    if (this.accountsByEmail.has(email.toLowerCase())) {
      throw new ConflictException({
        code: 'EMAIL_ALREADY_EXISTS',
        message: 'Email already exists',
      });
    }

    this.codes.set(`REGISTER:${email.toLowerCase()}`, {
      email,
      code: '123456',
      purpose: 'REGISTER',
      expiresAt: Date.now() + 5 * 60 * 1000,
    });
  }

  requestResetCode(email: string) {
    if (!this.accountsByEmail.has(email.toLowerCase())) {
      throw new NotFoundException({
        code: 'ACCOUNT_NOT_FOUND',
        message: 'Account not found',
      });
    }

    this.codes.set(`RESET_PASSWORD:${email.toLowerCase()}`, {
      email,
      code: '123456',
      purpose: 'RESET_PASSWORD',
      expiresAt: Date.now() + 5 * 60 * 1000,
    });
  }

  register(
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
    const account: AuthAccount = {
      accountId: randomUUID(),
      email,
      password: params.password,
      status: 'ACTIVE',
      referralCode: this.generateReferralCode(),
    };
    this.accounts.set(account.accountId, account);
    this.accountsByEmail.set(email, account.accountId);
    return this.issueSession(account, params.installationId);
  }

  login(params: { email: string; password: string; installationId?: string }) {
    const account = this.getAccountByEmail(params.email);
    if (account.password !== params.password) {
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
    return this.issueSession(account, params.installationId);
  }

  refresh(params: { refreshToken: string; installationId?: string }) {
    const session = this.sessionsByRefreshToken.get(params.refreshToken);
    if (!session || session.status !== 'ACTIVE') {
      throw new UnauthorizedException({
        code: 'AUTH_REFRESH_INVALID',
        message: 'Refresh token is invalid',
      });
    }
    if (new Date(session.refreshTokenExpiresAt).getTime() <= Date.now()) {
      session.status = 'EXPIRED';
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
    return this.issueSession(account, params.installationId ?? session.installationId ?? undefined);
  }

  logout(accessToken: string) {
    const session = this.requireAccessSession(accessToken);
    session.status = 'REVOKED';
  }

  resetPassword(params: { email: string; code: string; newPassword: string }) {
    const account = this.getAccountByEmail(params.email);
    this.assertCode(account.email, params.code, 'RESET_PASSWORD');
    account.password = params.newPassword;
    const activeSession = this.sessionsByAccountId.get(account.accountId);
    if (activeSession) {
      activeSession.status = 'REVOKED';
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
      accountId: account.accountId,
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

  private issueSession(account: AuthAccount, installationId?: string) {
    const previous = this.sessionsByAccountId.get(account.accountId);
    if (previous && previous.status === 'ACTIVE') {
      previous.status = 'EVICTED';
    }

    const now = Date.now();
    const session: AuthSession = {
      sessionId: randomUUID(),
      accountId: account.accountId,
      installationId: installationId ?? null,
      accessToken: `access_${randomUUID()}`,
      refreshToken: `refresh_${randomUUID()}`,
      accessTokenExpiresAt: new Date(now + 2 * 60 * 60 * 1000).toISOString(),
      refreshTokenExpiresAt: new Date(now + 30 * 24 * 60 * 60 * 1000).toISOString(),
      status: 'ACTIVE',
    };

    this.sessionsByAccountId.set(account.accountId, session);
    this.sessionsByAccessToken.set(session.accessToken, session);
    this.sessionsByRefreshToken.set(session.refreshToken, session);

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
      session.status = 'EXPIRED';
      throw new UnauthorizedException({
        code: 'UNAUTHORIZED',
        message: 'Access token expired',
      });
    }
    return session;
  }

  private getAccountByEmail(email: string) {
    const accountId = this.accountsByEmail.get(email.toLowerCase());
    if (!accountId) {
      throw new NotFoundException({
        code: 'ACCOUNT_NOT_FOUND',
        message: 'Account not found',
      });
    }
    const account = this.accounts.get(accountId);
    if (!account) {
      throw new NotFoundException({
        code: 'ACCOUNT_NOT_FOUND',
        message: 'Account not found',
      });
    }
    return account;
  }

  private assertCode(
    email: string,
    code: string,
    purpose: VerificationCodeRecord['purpose'],
  ) {
    const record = this.codes.get(`${purpose}:${email.toLowerCase()}`);
    if (!record || record.code !== code) {
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

  private generateReferralCode() {
    return randomUUID().replace(/-/g, '').slice(0, 8).toUpperCase();
  }
}
