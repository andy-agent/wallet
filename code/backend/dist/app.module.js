"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.AppModule = void 0;
const common_1 = require("@nestjs/common");
const config_1 = require("@nestjs/config");
const nestjs_pino_1 = require("nestjs-pino");
const app_controller_1 = require("./app.controller");
const request_context_middleware_1 = require("./common/middleware/request-context.middleware");
const admin_module_1 = require("./modules/admin/admin.module");
const accounts_module_1 = require("./modules/accounts/accounts.module");
const auth_module_1 = require("./modules/auth/auth.module");
const database_module_1 = require("./modules/database/database.module");
const health_module_1 = require("./modules/health/health.module");
const orders_module_1 = require("./modules/orders/orders.module");
const plans_module_1 = require("./modules/plans/plans.module");
const provisioning_module_1 = require("./modules/provisioning/provisioning.module");
const referral_module_1 = require("./modules/referral/referral.module");
const vpn_module_1 = require("./modules/vpn/vpn.module");
const wallet_module_1 = require("./modules/wallet/wallet.module");
const withdrawals_module_1 = require("./modules/withdrawals/withdrawals.module");
let AppModule = class AppModule {
    configure(consumer) {
        consumer.apply(request_context_middleware_1.RequestContextMiddleware).forRoutes('*');
    }
};
exports.AppModule = AppModule;
exports.AppModule = AppModule = __decorate([
    (0, common_1.Module)({
        imports: [
            config_1.ConfigModule.forRoot({
                isGlobal: true,
                envFilePath: ['.env.local', '.env'],
            }),
            nestjs_pino_1.LoggerModule.forRoot({
                pinoHttp: {
                    level: process.env.LOG_LEVEL ?? 'info',
                    redact: ['req.headers.authorization'],
                    customProps: (req) => ({
                        requestId: req.requestId,
                        service: 'cryptovpn-backend',
                    }),
                },
            }),
            health_module_1.HealthModule,
            database_module_1.DatabaseModule,
            auth_module_1.AuthModule,
            accounts_module_1.AccountsModule,
            plans_module_1.PlansModule,
            orders_module_1.OrdersModule,
            provisioning_module_1.ProvisioningModule,
            vpn_module_1.VpnModule,
            wallet_module_1.WalletModule,
            referral_module_1.ReferralModule,
            withdrawals_module_1.WithdrawalsModule,
            admin_module_1.AdminModule,
        ],
        controllers: [app_controller_1.AppController],
        providers: [],
    })
], AppModule);
//# sourceMappingURL=app.module.js.map