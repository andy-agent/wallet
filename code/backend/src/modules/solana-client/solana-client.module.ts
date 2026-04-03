/**
 * Solana Client Module
 *
 * Module for interacting with sol/usdt chain-side service.
 * Provides SolanaClientService for broadcasting transactions,
 * checking balances, and monitoring transaction status.
 */

import { HttpModule } from '@nestjs/axios';
import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { SolanaClientConfig } from './solana-client.config';
import { SolanaClientService } from './solana-client.service';

@Module({
  imports: [
    // HttpModule for making HTTP requests to sol/usdt service
    HttpModule,
    // ConfigModule for environment-based configuration
    ConfigModule,
  ],
  providers: [SolanaClientConfig, SolanaClientService],
  exports: [SolanaClientService],
})
export class SolanaClientModule {}
