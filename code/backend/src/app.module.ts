import { MiddlewareConsumer, Module, NestModule } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { LoggerModule } from 'nestjs-pino';
import { AppController } from './app.controller';
import { RequestContextMiddleware } from './common/middleware/request-context.middleware';
import { AdminModule } from './modules/admin/admin.module';
import { AccountsModule } from './modules/accounts/accounts.module';
import { AuthModule } from './modules/auth/auth.module';
import { DatabaseModule } from './modules/database/database.module';
import { HealthModule } from './modules/health/health.module';
import { OrdersModule } from './modules/orders/orders.module';
import { PlansModule } from './modules/plans/plans.module';
import { ProvisioningModule } from './modules/provisioning/provisioning.module';
import { ReferralModule } from './modules/referral/referral.module';
import { SolanaClientModule } from './modules/solana-client/solana-client.module';
import { VpnModule } from './modules/vpn/vpn.module';
import { WalletModule } from './modules/wallet/wallet.module';
import { WithdrawalsModule } from './modules/withdrawals/withdrawals.module';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
      envFilePath: ['.env.local', '.env'],
    }),
    LoggerModule.forRoot({
      pinoHttp: {
        level: process.env.LOG_LEVEL ?? 'info',
        redact: ['req.headers.authorization'],
        customProps: (req) => ({
          requestId: (req as { requestId?: string }).requestId,
          service: 'cryptovpn-backend',
        }),
      },
    }),
    HealthModule,
    DatabaseModule,
    AuthModule,
    AccountsModule,
    PlansModule,
    OrdersModule,
    ProvisioningModule,
    VpnModule,
    WalletModule,
    SolanaClientModule,
    ReferralModule,
    WithdrawalsModule,
    AdminModule,
  ],
  controllers: [AppController],
  providers: [],
})
export class AppModule implements NestModule {
  configure(consumer: MiddlewareConsumer) {
    consumer.apply(RequestContextMiddleware).forRoutes('*');
  }
}
