export type AccountStatus = 'ACTIVE' | 'FROZEN' | 'CLOSED';
export type SessionStatus = 'ACTIVE' | 'EVICTED' | 'REVOKED' | 'EXPIRED';
export interface AuthAccount {
    accountId: string;
    email: string;
    password: string;
    status: AccountStatus;
    referralCode: string;
}
export interface AuthSession {
    sessionId: string;
    accountId: string;
    installationId?: string | null;
    accessToken: string;
    refreshToken: string;
    accessTokenExpiresAt: string;
    refreshTokenExpiresAt: string;
    status: SessionStatus;
}
export interface VerificationCodeRecord {
    email: string;
    code: string;
    purpose: 'REGISTER' | 'RESET_PASSWORD';
    expiresAt: number;
}
