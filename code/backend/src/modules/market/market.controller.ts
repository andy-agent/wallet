import { Controller, Get, Param, Query } from '@nestjs/common';
import { MarketService } from './market.service';

@Controller('client/v1/market')
export class MarketController {
  constructor(private readonly marketService: MarketService) {}

  @Get('overview')
  async getOverview() {
    return this.marketService.getOverview();
  }

  @Get('search')
  async getSearch(@Query('q') query = '') {
    return this.marketService.getSearch(query);
  }

  @Get('spotlights')
  async getSpotlights() {
    return this.marketService.getSpotlights();
  }

  @Get('favorites')
  getFavorites() {
    return this.marketService.getFavorites();
  }

  @Get('rankings')
  async getRankings() {
    return this.marketService.getRankings();
  }

  @Get('instruments/:instrumentId')
  async getInstrumentDetail(@Param('instrumentId') instrumentId: string) {
    return this.marketService.getInstrumentDetail(instrumentId);
  }

  @Get('instruments/:instrumentId/candles')
  async getCandles(
    @Param('instrumentId') instrumentId: string,
    @Query('timeframe') timeframe?: string,
    @Query('limit') limit?: string,
  ) {
    return this.marketService.getCandles(instrumentId, timeframe, limit);
  }
}
