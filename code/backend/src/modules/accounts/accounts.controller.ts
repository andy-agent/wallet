import { Controller, Get, Headers } from '@nestjs/common';
import { AccountsService } from './accounts.service';

@Controller('client/v1')
export class AccountsController {
  constructor(private readonly accountsService: AccountsService) {}

  @Get('me')
  getMe(@Headers('authorization') authorization?: string) {
    return this.accountsService.getMe(this.extractBearer(authorization));
  }

  @Get('me/session')
  getSessionSummary(@Headers('authorization') authorization?: string) {
    return this.accountsService.getSessionSummary(this.extractBearer(authorization));
  }

  private extractBearer(authorization?: string) {
    if (!authorization?.startsWith('Bearer ')) {
      return '';
    }
    return authorization.slice('Bearer '.length);
  }
}
