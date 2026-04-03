export type AdminRole = 'SUPER_ADMIN' | 'OPS_ADMIN' | 'FINANCE_ADMIN' | 'SUPPORT_ADMIN';

export interface AdminUser {
  adminId: string;
  username: string;
  password: string;
  role: AdminRole;
  status: 'ACTIVE' | 'DISABLED';
}

export interface AdminSession {
  sessionId: string;
  adminId: string;
  accessToken: string;
  accessTokenExpiresAt: string;
  role: AdminRole;
  status: 'ACTIVE' | 'EXPIRED' | 'REVOKED';
}
