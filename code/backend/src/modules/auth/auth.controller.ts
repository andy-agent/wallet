import {
  Body,
  Controller,
  Headers,
  HttpCode,
  Post,
} from '@nestjs/common';
import { RegisterEmailCodeRequestDto } from './dto/register-email-code.request';
import { RegisterEmailRequestDto } from './dto/register-email.request';
import { LoginPasswordRequestDto } from './dto/login-password.request';
import { RefreshTokenRequestDto } from './dto/refresh-token.request';
import { LogoutRequestDto } from './dto/logout.request';
import { PasswordResetCodeRequestDto } from './dto/password-reset-code.request';
import { PasswordResetRequestDto } from './dto/password-reset.request';
import { AuthService } from './auth.service';

@Controller('client/v1/auth')
export class AuthController {
  constructor(private readonly authService: AuthService) {}

  @Post('register/email/request-code')
  @HttpCode(200)
  requestRegisterCode(@Body() body: RegisterEmailCodeRequestDto) {
    this.authService.requestRegisterCode(body.email);
    return {};
  }

  @Post('register/email')
  @HttpCode(200)
  register(
    @Body() body: RegisterEmailRequestDto,
    @Headers('x-idempotency-key') idempotencyKey?: string,
  ) {
    return this.authService.register(body, idempotencyKey ?? 'missing');
  }

  @Post('login/password')
  @HttpCode(200)
  login(@Body() body: LoginPasswordRequestDto) {
    return this.authService.login(body);
  }

  @Post('refresh')
  @HttpCode(200)
  refresh(@Body() body: RefreshTokenRequestDto) {
    return this.authService.refresh(body);
  }

  @Post('logout')
  @HttpCode(200)
  logout(
    @Body() _body: LogoutRequestDto,
    @Headers('authorization') authorization?: string,
  ) {
    const accessToken = this.extractBearer(authorization);
    this.authService.logout(accessToken);
    return {};
  }

  @Post('password/forgot/request-code')
  @HttpCode(200)
  requestResetCode(@Body() body: PasswordResetCodeRequestDto) {
    this.authService.requestResetCode(body.email);
    return {};
  }

  @Post('password/reset')
  @HttpCode(200)
  resetPassword(
    @Body() body: PasswordResetRequestDto,
    @Headers('x-idempotency-key') _idempotencyKey?: string,
  ) {
    this.authService.resetPassword(body);
    return {};
  }

  private extractBearer(authorization?: string) {
    if (!authorization?.startsWith('Bearer ')) {
      return '';
    }
    return authorization.slice('Bearer '.length);
  }
}
