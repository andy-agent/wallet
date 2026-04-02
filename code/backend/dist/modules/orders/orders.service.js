"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.OrdersService = void 0;
const common_1 = require("@nestjs/common");
const crypto_1 = require("crypto");
const auth_service_1 = require("../auth/auth.service");
const provisioning_service_1 = require("../provisioning/provisioning.service");
let OrdersService = class OrdersService {
    constructor(authService, provisioningService) {
        this.authService = authService;
        this.provisioningService = provisioningService;
        this.ordersByNo = new Map();
        this.idempotencyIndex = new Map();
    }
    createOrder(accessToken, dto, idempotencyKey) {
        const account = this.authService.getMe(accessToken);
        const compositeKey = `${account.accountId}:${idempotencyKey}`;
        const existingOrderNo = this.idempotencyIndex.get(compositeKey);
        if (existingOrderNo) {
            return this.mustGet(existingOrderNo);
        }
        if (!idempotencyKey) {
            throw new common_1.ConflictException({
                code: 'ORDER_CREATE_CONFLICT',
                message: 'Missing idempotency key',
            });
        }
        const orderNo = `ORD-${Date.now()}`;
        const order = {
            orderId: (0, crypto_1.randomUUID)(),
            orderNo,
            accountId: account.accountId,
            planCode: dto.planCode,
            planName: dto.planCode === 'BASIC_1M' ? '基础版-1个月' : dto.planCode,
            orderType: dto.orderType,
            quoteAssetCode: dto.quoteAssetCode,
            quoteNetworkCode: dto.quoteNetworkCode,
            quoteUsdAmount: '9.99',
            payableAmount: dto.quoteAssetCode === 'SOL' ? '0.04500000' : '9.990000',
            status: 'AWAITING_PAYMENT',
            expiresAt: new Date(Date.now() + 15 * 60 * 1000).toISOString(),
            confirmedAt: null,
            completedAt: null,
            failureReason: null,
            submittedClientTxHash: null,
        };
        this.ordersByNo.set(orderNo, order);
        this.idempotencyIndex.set(compositeKey, orderNo);
        return order;
    }
    getOrder(accessToken, orderNo) {
        const account = this.authService.getMe(accessToken);
        const order = this.mustGet(orderNo);
        if (order.accountId !== account.accountId) {
            throw new common_1.NotFoundException({
                code: 'ORDER_NOT_FOUND',
                message: 'Order not found',
            });
        }
        return order;
    }
    getPaymentTarget(accessToken, orderNo) {
        const order = this.getOrder(accessToken, orderNo);
        return {
            orderNo: order.orderNo,
            networkCode: order.quoteNetworkCode,
            assetCode: order.quoteAssetCode,
            collectionAddress: order.quoteNetworkCode === 'SOLANA'
                ? 'So11111111111111111111111111111111111111112'
                : 'TR7NhqjeKQxGTCi8q8ZY4pL8otSzgjLj6t',
            payableAmount: order.payableAmount,
            uniqueAmountDelta: '0.000001',
            qrText: `${order.quoteNetworkCode}:${order.orderNo}:${order.payableAmount}`,
            expiresAt: order.expiresAt,
        };
    }
    submitClientTx(accessToken, orderNo, dto) {
        const order = this.getOrder(accessToken, orderNo);
        order.submittedClientTxHash = dto.txHash;
        order.status = 'PAYMENT_DETECTED';
        return {};
    }
    refreshStatus(accessToken, orderNo, _dto) {
        const order = this.getOrder(accessToken, orderNo);
        if (new Date(order.expiresAt).getTime() <= Date.now() && order.status === 'AWAITING_PAYMENT') {
            order.status = 'EXPIRED';
            return order;
        }
        if (order.submittedClientTxHash && order.status === 'PAYMENT_DETECTED') {
            order.status = 'CONFIRMING';
        }
        else if (order.submittedClientTxHash && order.status === 'CONFIRMING') {
            order.status = 'PAID';
            order.confirmedAt = new Date().toISOString();
        }
        else if (order.status === 'PAID') {
            order.status = 'PROVISIONING';
            this.provisioningService.provisionPaidOrder({
                accountId: order.accountId,
                planCode: order.planCode,
            });
            order.status = 'COMPLETED';
            order.completedAt = new Date().toISOString();
        }
        return order;
    }
    mustGet(orderNo) {
        const order = this.ordersByNo.get(orderNo);
        if (!order) {
            throw new common_1.NotFoundException({
                code: 'ORDER_NOT_FOUND',
                message: 'Order not found',
            });
        }
        return order;
    }
};
exports.OrdersService = OrdersService;
exports.OrdersService = OrdersService = __decorate([
    (0, common_1.Injectable)(),
    __metadata("design:paramtypes", [auth_service_1.AuthService,
        provisioning_service_1.ProvisioningService])
], OrdersService);
//# sourceMappingURL=orders.service.js.map