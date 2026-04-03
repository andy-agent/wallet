import {
  CanActivate,
  ExecutionContext,
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

/**
 * 内部服务间调用鉴权 Guard
 * 调用方需在 Header 中携带 X-Internal-Auth: Bearer <token>
 */
@Injectable()
export class InternalAuthGuard implements CanActivate {
  constructor(private readonly configService: ConfigService) {}

  canActivate(context: ExecutionContext): boolean {
    const request = context.switchToHttp().getRequest<{
      headers: Record<string, string | string[]>;
    }>();
    const authHeader = request.headers['x-internal-auth'];
    const expectedToken = this.configService.get<string>('INTERNAL_AUTH_TOKEN');

    if (!expectedToken) {
      throw new UnauthorizedException('Internal auth token not configured');
    }

    const token =
      typeof authHeader === 'string' && authHeader.startsWith('Bearer ')
        ? authHeader.slice('Bearer '.length)
        : '';

    if (token !== expectedToken) {
      throw new UnauthorizedException('Invalid internal auth token');
    }

    return true;
  }
}
