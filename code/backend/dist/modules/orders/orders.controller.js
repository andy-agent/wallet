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
var __param = (this && this.__param) || function (paramIndex, decorator) {
    return function (target, key) { decorator(target, key, paramIndex); }
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.OrdersController = void 0;
const common_1 = require("@nestjs/common");
const create_order_request_1 = require("./dto/create-order.request");
const refresh_order_status_request_1 = require("./dto/refresh-order-status.request");
const submit_client_tx_request_1 = require("./dto/submit-client-tx.request");
const orders_service_1 = require("./orders.service");
let OrdersController = class OrdersController {
    constructor(ordersService) {
        this.ordersService = ordersService;
    }
    createOrder(authorization, idempotencyKey, body) {
        return this.ordersService.createOrder(this.extractBearer(authorization), body, idempotencyKey ?? '');
    }
    getOrder(authorization, orderNo) {
        return this.ordersService.getOrder(this.extractBearer(authorization), orderNo);
    }
    getPaymentTarget(authorization, orderNo) {
        return this.ordersService.getPaymentTarget(this.extractBearer(authorization), orderNo);
    }
    submitClientTx(authorization, orderNo, body) {
        return this.ordersService.submitClientTx(this.extractBearer(authorization), orderNo, body);
    }
    refreshStatus(authorization, orderNo, body) {
        return this.ordersService.refreshStatus(this.extractBearer(authorization), orderNo, body);
    }
    extractBearer(authorization) {
        if (!authorization?.startsWith('Bearer ')) {
            return '';
        }
        return authorization.slice('Bearer '.length);
    }
};
exports.OrdersController = OrdersController;
__decorate([
    (0, common_1.Post)(),
    __param(0, (0, common_1.Headers)('authorization')),
    __param(1, (0, common_1.Headers)('x-idempotency-key')),
    __param(2, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object, Object, create_order_request_1.CreateOrderRequestDto]),
    __metadata("design:returntype", void 0)
], OrdersController.prototype, "createOrder", null);
__decorate([
    (0, common_1.Get)(':orderNo'),
    __param(0, (0, common_1.Headers)('authorization')),
    __param(1, (0, common_1.Param)('orderNo')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object, String]),
    __metadata("design:returntype", void 0)
], OrdersController.prototype, "getOrder", null);
__decorate([
    (0, common_1.Get)(':orderNo/payment-target'),
    __param(0, (0, common_1.Headers)('authorization')),
    __param(1, (0, common_1.Param)('orderNo')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object, String]),
    __metadata("design:returntype", void 0)
], OrdersController.prototype, "getPaymentTarget", null);
__decorate([
    (0, common_1.Post)(':orderNo/submit-client-tx'),
    __param(0, (0, common_1.Headers)('authorization')),
    __param(1, (0, common_1.Param)('orderNo')),
    __param(2, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object, String, submit_client_tx_request_1.SubmitClientTxRequestDto]),
    __metadata("design:returntype", void 0)
], OrdersController.prototype, "submitClientTx", null);
__decorate([
    (0, common_1.Post)(':orderNo/refresh-status'),
    __param(0, (0, common_1.Headers)('authorization')),
    __param(1, (0, common_1.Param)('orderNo')),
    __param(2, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object, String, refresh_order_status_request_1.RefreshOrderStatusRequestDto]),
    __metadata("design:returntype", void 0)
], OrdersController.prototype, "refreshStatus", null);
exports.OrdersController = OrdersController = __decorate([
    (0, common_1.Controller)('client/v1/orders'),
    __metadata("design:paramtypes", [orders_service_1.OrdersService])
], OrdersController);
//# sourceMappingURL=orders.controller.js.map