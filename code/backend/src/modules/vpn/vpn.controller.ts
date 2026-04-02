import { Body, Controller, Get, Headers, Post } from '@nestjs/common';
import { IssueVpnConfigRequestDto } from './dto/issue-vpn-config.request';
import { VpnService } from './vpn.service';

@Controller('client/v1/vpn')
export class VpnController {
  constructor(private readonly vpnService: VpnService) {}

  @Get('regions')
  getRegions(@Headers('authorization') authorization?: string) {
    return this.vpnService.listRegions(this.extractBearer(authorization));
  }

  @Post('config/issue')
  issueConfig(
    @Headers('authorization') authorization: string | undefined,
    @Body() body: IssueVpnConfigRequestDto,
  ) {
    return this.vpnService.issueConfig(this.extractBearer(authorization), body);
  }

  @Get('status')
  getStatus(@Headers('authorization') authorization?: string) {
    return this.vpnService.getVpnStatus(this.extractBearer(authorization));
  }

  private extractBearer(authorization?: string) {
    if (!authorization?.startsWith('Bearer ')) {
      return '';
    }
    return authorization.slice('Bearer '.length);
  }
}
