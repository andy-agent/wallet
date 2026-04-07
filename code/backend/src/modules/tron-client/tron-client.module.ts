import { HttpModule } from '@nestjs/axios';
import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { TronClientConfig } from './tron-client.config';
import { TronClientService } from './tron-client.service';

@Module({
  imports: [HttpModule, ConfigModule],
  providers: [TronClientConfig, TronClientService],
  exports: [TronClientService],
})
export class TronClientModule {}
