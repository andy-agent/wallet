import {
  CanActivate,
  ExecutionContext,
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';
import { Request } from 'express';
import { AdminAuthService } from './admin-auth.service';

@Injectable()
export class AdminAuthGuard implements CanActivate {
  constructor(private readonly adminAuthService: AdminAuthService) {}

  canActivate(context: ExecutionContext): boolean {
    const request = context.switchToHttp().getRequest<Request & { admin?: { adminId: string; role: string } }>();
    const token = this.extractBearer(request.headers.authorization);
    if (!token) {
      throw new UnauthorizedException({
        code: 'ADMIN_AUTH_INVALID',
        message: 'Missing admin authorization token',
      });
    }
    const admin = this.adminAuthService.validateAccessToken(token);
    request.admin = admin;
    return true;
  }

  private extractBearer(authorization?: string) {
    if (!authorization?.startsWith('Bearer ')) {
      return '';
    }
    return authorization.slice('Bearer '.length);
  }
}
