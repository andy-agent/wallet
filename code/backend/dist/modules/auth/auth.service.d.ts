export declare class AuthService {
    private readonly accounts;
    private readonly accountsByEmail;
    private readonly sessionsByAccessToken;
    private readonly sessionsByRefreshToken;
    private readonly sessionsByAccountId;
    private readonly codes;
    requestRegisterCode(email: string): void;
    requestResetCode(email: string): void;
    register(params: {
        email: string;
        code: string;
        password: string;
        installationId?: string;
    }, _idempotencyKey: string): {
        accessToken: string;
        refreshToken: string;
        accessTokenExpiresAt: string;
        refreshTokenExpiresAt: string;
        accountId: string;
        accountStatus: import("./auth.types").AccountStatus;
    };
    login(params: {
        email: string;
        password: string;
        installationId?: string;
    }): {
        accessToken: string;
        refreshToken: string;
        accessTokenExpiresAt: string;
        refreshTokenExpiresAt: string;
        accountId: string;
        accountStatus: import("./auth.types").AccountStatus;
    };
    refresh(params: {
        refreshToken: string;
        installationId?: string;
    }): {
        accessToken: string;
        refreshToken: string;
        accessTokenExpiresAt: string;
        refreshTokenExpiresAt: string;
        accountId: string;
        accountStatus: import("./auth.types").AccountStatus;
    };
    logout(accessToken: string): void;
    resetPassword(params: {
        email: string;
        code: string;
        newPassword: string;
    }): void;
    getMe(accessToken: string): {
        accountId: string;
        email: string;
        status: import("./auth.types").AccountStatus;
        referralCode: string;
        subscription: null;
    };
    getSessionSummary(accessToken: string): {
        sessionId: string;
        status: import("./auth.types").SessionStatus;
        installationId: string | null;
        expiresAt: string;
    };
    private issueSession;
    private requireAccessSession;
    private getAccountByEmail;
    private assertCode;
    private generateReferralCode;
}
