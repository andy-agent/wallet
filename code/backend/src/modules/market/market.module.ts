import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { MarketConfig } from './market.config';
import { MarketController } from './market.controller';
import { CoinGeckoMarketDataProvider } from './market.provider';
import { MarketService } from './market.service';
import { MARKET_DATA_PROVIDER } from './market.types';

@Module({
  imports: [ConfigModule],
  controllers: [MarketController],
  providers: [
    MarketConfig,
    MarketService,
    {
      provide: MARKET_DATA_PROVIDER,
      useClass: CoinGeckoMarketDataProvider,
    },
  ],
  exports: [MarketService],
})
export class MarketModule {}
