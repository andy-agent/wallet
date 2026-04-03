import {
  CanActivate,
  ExecutionContext,
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

/**
 * Guard for internal service-to-service authentication.
 * Validates X-API-Key header against configured INTERNAL_API_KEY.
 * 
 * This is used for:
 * - Main backend API calling chain-side service
 * - Admin operations on chain service
 * - Health checks from internal monitoring
 */
@Injectable()
export class InternalAuthGuard implements CanActivate {
  constructor(private readonly configService: ConfigService) {}

  canActivate(context: ExecutionContext): boolean {
    const request = context.switchToHttp().getRequest<{
      headers: { 'x-api-key'?: string };
    }>();

    const apiKey = request.headers['x-api-key'];
    const expectedKey = this.configService.get<string>('INTERNAL_API_KEY');

    if (!expectedKey) {
      throw new UnauthorizedException({
        code: 'INTERNAL_AUTH_NOT_CONFIGURED',
        message: 'Internal API key not configured',
      });
    }

    if (!apiKey) {
      throw new UnauthorizedException({
        code: 'INTERNAL_AUTH_MISSING_KEY',
        message: 'Missing X-API-Key header',
      });
    }

    if (apiKey !== expectedKey) {
      throw new UnauthorizedException({
        code: 'INTERNAL_AUTH_INVALID_KEY',
        message: 'Invalid API key',
      });
    }

    return true;
  }
}
