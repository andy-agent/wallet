import { Controller, Get, Header, Param, Query } from '@nestjs/common';
import { PUBLIC_EDGE_MARKET_CACHE_CONTROL } from '../../common/http/public-cache-control';
import { MarketService } from './market.service';

@Controller('client/v1/market')
export class MarketController {
  constructor(private readonly marketService: MarketService) {}

  @Get('overview')
  @Header('Cache-Control', PUBLIC_EDGE_MARKET_CACHE_CONTROL)
  async getOverview() {
    return this.marketService.getOverview();
  }

  @Get('search')
  @Header('Cache-Control', PUBLIC_EDGE_MARKET_CACHE_CONTROL)
  async getSearch(@Query('q') query = '') {
    return this.marketService.getSearch(query);
  }

  @Get('spotlights')
  @Header('Cache-Control', PUBLIC_EDGE_MARKET_CACHE_CONTROL)
  async getSpotlights() {
    return this.marketService.getSpotlights();
  }

  @Get('favorites')
  @Header('Cache-Control', PUBLIC_EDGE_MARKET_CACHE_CONTROL)
  getFavorites() {
    return this.marketService.getFavorites();
  }

  @Get('rankings')
  @Header('Cache-Control', PUBLIC_EDGE_MARKET_CACHE_CONTROL)
  async getRankings() {
    return this.marketService.getRankings();
  }

  @Get('instruments/:instrumentId')
  @Header('Cache-Control', PUBLIC_EDGE_MARKET_CACHE_CONTROL)
  async getInstrumentDetail(@Param('instrumentId') instrumentId: string) {
    return this.marketService.getInstrumentDetail(instrumentId);
  }

  @Get('instruments/:instrumentId/candles')
  @Header('Cache-Control', PUBLIC_EDGE_MARKET_CACHE_CONTROL)
  async getCandles(
    @Param('instrumentId') instrumentId: string,
    @Query('timeframe') timeframe?: string,
    @Query('limit') limit?: string,
  ) {
    return this.marketService.getCandles(instrumentId, timeframe, limit);
  }
}
