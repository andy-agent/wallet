import { Controller, Get, Header, Query } from '@nestjs/common';
import { PUBLIC_EDGE_VERSION_CACHE_CONTROL } from '../../common/http/public-cache-control';
import { AppVersionsService } from './app-versions.service';

@Controller('client/v1/app-versions')
export class AppVersionsController {
  constructor(private readonly appVersionsService: AppVersionsService) {}

  @Get('latest')
  @Header('Cache-Control', PUBLIC_EDGE_VERSION_CACHE_CONTROL)
  getLatestVersion(
    @Query('platform') platform?: string,
    @Query('channel') channel?: string,
  ) {
    return this.appVersionsService.getLatestVersion(platform, channel);
  }
}
