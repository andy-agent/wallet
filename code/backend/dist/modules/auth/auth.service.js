"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.AuthService = void 0;
const common_1 = require("@nestjs/common");
const crypto_1 = require("crypto");
let AuthService = class AuthService {
    constructor() {
        this.accounts = new Map();
        this.accountsByEmail = new Map();
        this.sessionsByAccessToken = new Map();
        this.sessionsByRefreshToken = new Map();
        this.sessionsByAccountId = new Map();
        this.codes = new Map();
    }
    requestRegisterCode(email) {
        if (this.accountsByEmail.has(email.toLowerCase())) {
            throw new common_1.ConflictException({
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
    requestResetCode(email) {
        if (!this.accountsByEmail.has(email.toLowerCase())) {
            throw new common_1.NotFoundException({
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
    register(params, _idempotencyKey) {
        const email = params.email.toLowerCase();
        if (this.accountsByEmail.has(email)) {
            throw new common_1.ConflictException({
                code: 'EMAIL_ALREADY_EXISTS',
                message: 'Email already exists',
            });
        }
        this.assertCode(email, params.code, 'REGISTER');
        const account = {
            accountId: (0, crypto_1.randomUUID)(),
            email,
            password: params.password,
            status: 'ACTIVE',
            referralCode: this.generateReferralCode(),
        };
        this.accounts.set(account.accountId, account);
        this.accountsByEmail.set(email, account.accountId);
        return this.issueSession(account, params.installationId);
    }
    login(params) {
        const account = this.getAccountByEmail(params.email);
        if (account.password !== params.password) {
            throw new common_1.UnauthorizedException({
                code: 'AUTH_INVALID_CREDENTIALS',
                message: 'Invalid credentials',
            });
        }
        if (account.status === 'FROZEN') {
            throw new common_1.ForbiddenException({
                code: 'AUTH_ACCOUNT_FROZEN',
                message: 'Account is frozen',
            });
        }
        return this.issueSession(account, params.installationId);
    }
    refresh(params) {
        const session = this.sessionsByRefreshToken.get(params.refreshToken);
        if (!session || session.status !== 'ACTIVE') {
            throw new common_1.UnauthorizedException({
                code: 'AUTH_REFRESH_INVALID',
                message: 'Refresh token is invalid',
            });
        }
        if (new Date(session.refreshTokenExpiresAt).getTime() <= Date.now()) {
            session.status = 'EXPIRED';
            throw new common_1.UnauthorizedException({
                code: 'AUTH_REFRESH_INVALID',
                message: 'Refresh token expired',
            });
        }
        const account = this.accounts.get(session.accountId);
        if (!account) {
            throw new common_1.NotFoundException({
                code: 'ACCOUNT_NOT_FOUND',
                message: 'Account not found',
            });
        }
        return this.issueSession(account, params.installationId ?? session.installationId ?? undefined);
    }
    logout(accessToken) {
        const session = this.requireAccessSession(accessToken);
        session.status = 'REVOKED';
    }
    resetPassword(params) {
        const account = this.getAccountByEmail(params.email);
        this.assertCode(account.email, params.code, 'RESET_PASSWORD');
        account.password = params.newPassword;
        const activeSession = this.sessionsByAccountId.get(account.accountId);
        if (activeSession) {
            activeSession.status = 'REVOKED';
        }
    }
    getMe(accessToken) {
        const session = this.requireAccessSession(accessToken);
        const account = this.accounts.get(session.accountId);
        if (!account) {
            throw new common_1.NotFoundException({
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
    getSessionSummary(accessToken) {
        const session = this.requireAccessSession(accessToken);
        return {
            sessionId: session.sessionId,
            status: session.status,
            installationId: session.installationId ?? null,
            expiresAt: session.refreshTokenExpiresAt,
        };
    }
    issueSession(account, installationId) {
        const previous = this.sessionsByAccountId.get(account.accountId);
        if (previous && previous.status === 'ACTIVE') {
            previous.status = 'EVICTED';
        }
        const now = Date.now();
        const session = {
            sessionId: (0, crypto_1.randomUUID)(),
            accountId: account.accountId,
            installationId: installationId ?? null,
            accessToken: `access_${(0, crypto_1.randomUUID)()}`,
            refreshToken: `refresh_${(0, crypto_1.randomUUID)()}`,
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
    requireAccessSession(accessToken) {
        const session = this.sessionsByAccessToken.get(accessToken);
        if (!session) {
            throw new common_1.UnauthorizedException({
                code: 'UNAUTHORIZED',
                message: 'Access token is invalid',
            });
        }
        if (session.status === 'EVICTED') {
            throw new common_1.UnauthorizedException({
                code: 'AUTH_SESSION_EVICTED',
                message: 'Session evicted',
            });
        }
        if (session.status !== 'ACTIVE') {
            throw new common_1.UnauthorizedException({
                code: 'UNAUTHORIZED',
                message: 'Session is not active',
            });
        }
        if (new Date(session.accessTokenExpiresAt).getTime() <= Date.now()) {
            session.status = 'EXPIRED';
            throw new common_1.UnauthorizedException({
                code: 'UNAUTHORIZED',
                message: 'Access token expired',
            });
        }
        return session;
    }
    getAccountByEmail(email) {
        const accountId = this.accountsByEmail.get(email.toLowerCase());
        if (!accountId) {
            throw new common_1.NotFoundException({
                code: 'ACCOUNT_NOT_FOUND',
                message: 'Account not found',
            });
        }
        const account = this.accounts.get(accountId);
        if (!account) {
            throw new common_1.NotFoundException({
                code: 'ACCOUNT_NOT_FOUND',
                message: 'Account not found',
            });
        }
        return account;
    }
    assertCode(email, code, purpose) {
        const record = this.codes.get(`${purpose}:${email.toLowerCase()}`);
        if (!record || record.code !== code) {
            throw new common_1.BadRequestException({
                code: 'CODE_INVALID',
                message: 'Code invalid',
            });
        }
        if (record.expiresAt <= Date.now()) {
            throw new common_1.BadRequestException({
                code: 'CODE_EXPIRED',
                message: 'Code expired',
            });
        }
    }
    generateReferralCode() {
        return (0, crypto_1.randomUUID)().replace(/-/g, '').slice(0, 8).toUpperCase();
    }
};
exports.AuthService = AuthService;
exports.AuthService = AuthService = __decorate([
    (0, common_1.Injectable)()
], AuthService);
//# sourceMappingURL=auth.service.js.map