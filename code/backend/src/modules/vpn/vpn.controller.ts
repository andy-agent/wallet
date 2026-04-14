import { Body, Controller, Get, Headers, Post, Query } from '@nestjs/common';
import { IssueVpnConfigRequestDto } from './dto/issue-vpn-config.request';
import { SelectVpnNodeRequestDto } from './dto/select-vpn-node.request';
import { VpnService } from './vpn.service';

@Controller('client/v1/vpn')
export class VpnController {
  constructor(private readonly vpnService: VpnService) {}

  @Get('regions')
  getRegions(@Headers('authorization') authorization?: string) {
    return this.vpnService.listRegions(this.extractBearer(authorization));
  }

  @Get('nodes')
  getNodes(
    @Headers('authorization') authorization?: string,
    @Query('lineCode') lineCode?: string,
  ) {
    return this.vpnService.listNodes(this.extractBearer(authorization), { lineCode });
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

  @Post('selection')
  selectNode(
    @Headers('authorization') authorization: string | undefined,
    @Body() body: SelectVpnNodeRequestDto,
  ) {
    return this.vpnService.selectNode(this.extractBearer(authorization), body);
  }

  private extractBearer(authorization?: string) {
    if (!authorization?.startsWith('Bearer ')) {
      return '';
    }
    return authorization.slice('Bearer '.length);
  }
}
