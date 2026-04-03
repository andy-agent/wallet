import { Body, Controller, HttpCode, Post } from '@nestjs/common';
import { AdminAuthService } from './admin-auth.service';
import { AdminLoginRequestDto } from './dto/admin-login.request';

@Controller('admin/v1/auth')
export class AdminAuthController {
  constructor(private readonly adminAuthService: AdminAuthService) {}

  @Post('login')
  @HttpCode(200)
  login(@Body() body: AdminLoginRequestDto) {
    return this.adminAuthService.login(body);
  }
}
