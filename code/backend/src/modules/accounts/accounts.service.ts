import { Injectable } from '@nestjs/common';
import { AuthService } from '../auth/auth.service';

@Injectable()
export class AccountsService {
  constructor(private readonly authService: AuthService) {}

  getMe(accessToken: string) {
    return this.authService.getMe(accessToken);
  }

  getSessionSummary(accessToken: string) {
    return this.authService.getSessionSummary(accessToken);
  }
}
