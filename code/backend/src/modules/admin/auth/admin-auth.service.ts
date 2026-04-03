import { Injectable, UnauthorizedException } from '@nestjs/common';
import { randomUUID } from 'crypto';
import { AdminRole, AdminSession, AdminUser } from './admin-auth.types';

@Injectable()
export class AdminAuthService {
  private readonly users = new Map<string, AdminUser>();
  private readonly usersByUsername = new Map<string, string>();
  private readonly sessionsByAccessToken = new Map<string, AdminSession>();

  constructor() {
    const defaultUsername = process.env.ADMIN_USERNAME ?? 'admin';
    const defaultPassword = process.env.ADMIN_PASSWORD ?? 'admin123';
    const defaultRole = (process.env.ADMIN_ROLE as AdminRole) ?? 'SUPER_ADMIN';
    const admin: AdminUser = {
      adminId: randomUUID(),
      username: defaultUsername,
      password: defaultPassword,
      role: defaultRole,
      status: 'ACTIVE',
    };
    this.users.set(admin.adminId, admin);
    this.usersByUsername.set(admin.username.toLowerCase(), admin.adminId);
  }

  login(params: { username: string; password: string }) {
    const user = this.getUserByUsername(params.username);
    if (user.password !== params.password) {
      throw new UnauthorizedException({
        code: 'ADMIN_INVALID_CREDENTIALS',
        message: 'Invalid admin credentials',
      });
    }
    if (user.status === 'DISABLED') {
      throw new UnauthorizedException({
        code: 'ADMIN_DISABLED',
        message: 'Admin account is disabled',
      });
    }
    return this.issueSession(user);
  }

  logout(accessToken: string) {
    const session = this.sessionsByAccessToken.get(accessToken);
    if (session) {
      session.status = 'REVOKED';
    }
  }

  validateAccessToken(accessToken: string) {
    const session = this.sessionsByAccessToken.get(accessToken);
    if (!session) {
      throw new UnauthorizedException({
        code: 'ADMIN_AUTH_INVALID',
        message: 'Invalid admin access token',
      });
    }
    if (session.status !== 'ACTIVE') {
      throw new UnauthorizedException({
        code: 'ADMIN_AUTH_INVALID',
        message: 'Admin session is not active',
      });
    }
    if (new Date(session.accessTokenExpiresAt).getTime() <= Date.now()) {
      session.status = 'EXPIRED';
      throw new UnauthorizedException({
        code: 'ADMIN_AUTH_INVALID',
        message: 'Admin access token expired',
      });
    }
    const user = this.users.get(session.adminId);
    if (!user || user.status === 'DISABLED') {
      throw new UnauthorizedException({
        code: 'ADMIN_DISABLED',
        message: 'Admin account is disabled',
      });
    }
    return {
      adminId: session.adminId,
      role: session.role,
    };
  }

  private getUserByUsername(username: string) {
    const adminId = this.usersByUsername.get(username.toLowerCase());
    if (!adminId) {
      throw new UnauthorizedException({
        code: 'ADMIN_INVALID_CREDENTIALS',
        message: 'Invalid admin credentials',
      });
    }
    const user = this.users.get(adminId);
    if (!user) {
      throw new UnauthorizedException({
        code: 'ADMIN_INVALID_CREDENTIALS',
        message: 'Invalid admin credentials',
      });
    }
    return user;
  }

  private issueSession(user: AdminUser) {
    const now = Date.now();
    const session: AdminSession = {
      sessionId: randomUUID(),
      adminId: user.adminId,
      accessToken: `admin_access_${randomUUID()}`,
      accessTokenExpiresAt: new Date(now + 8 * 60 * 60 * 1000).toISOString(),
      role: user.role,
      status: 'ACTIVE',
    };
    this.sessionsByAccessToken.set(session.accessToken, session);
    return {
      accessToken: session.accessToken,
      accessTokenExpiresAt: session.accessTokenExpiresAt,
      adminId: user.adminId,
      role: user.role,
    };
  }
}
