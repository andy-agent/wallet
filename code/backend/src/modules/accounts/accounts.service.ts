import { Injectable } from '@nestjs/common';
import { AuthService } from '../auth/auth.service';
import { VpnService } from '../vpn/vpn.service';

@Injectable()
export class AccountsService {
  constructor(
    private readonly authService: AuthService,
    private readonly vpnService: VpnService,
  ) {}

  async getMe(accessToken: string) {
    const me = this.authService.getMe(accessToken);
    const subscription = await this.vpnService.getCurrentSubscription(accessToken);
    return {
      ...me,
      subscription,
    };
  }

  getSessionSummary(accessToken: string) {
    return this.authService.getSessionSummary(accessToken);
  }
}
