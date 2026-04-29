export type AccountStatus = 'ACTIVE' | 'FROZEN' | 'CLOSED';
export type SessionStatus = 'ACTIVE' | 'EVICTED' | 'REVOKED' | 'EXPIRED';

export interface AuthAccount {
  accountId: string;
  email: string;
  passwordHash: string;
  status: AccountStatus;
  referralCode: string;
  inviterAccountId: string | null;
  createdAt: string;
  updatedAt: string;
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
  createdAt: string;
  updatedAt: string;
}

export interface VerificationCodeRecord {
  email: string;
  codeHash: string;
  purpose: 'REGISTER' | 'RESET_PASSWORD';
  expiresAt: number;
  createdAt: string;
  updatedAt: string;
}
