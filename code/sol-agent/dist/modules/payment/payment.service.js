"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.PaymentService = void 0;
const common_1 = require("@nestjs/common");
let PaymentService = class PaymentService {
    getPaymentStatus(address, networkCode) {
        return {
            address,
            networkCode: networkCode ?? 'solana-mainnet',
            status: 'pending',
            receivedAmount: '0',
            expectedAmount: null,
            txHash: null,
            confirmations: 0,
            updatedAt: new Date().toISOString(),
            note: 'PLACEHOLDER: on-chain detection not implemented yet',
        };
    }
    detectPayment(body) {
        return {
            address: body.address,
            networkCode: body.networkCode ?? 'solana-mainnet',
            status: 'pending',
            receivedAmount: '0',
            expectedAmount: body.expectedAmount ?? null,
            txHash: null,
            confirmations: 0,
            updatedAt: new Date().toISOString(),
            note: 'PLACEHOLDER: on-chain detection not implemented yet',
        };
    }
};
exports.PaymentService = PaymentService;
exports.PaymentService = PaymentService = __decorate([
    (0, common_1.Injectable)()
], PaymentService);
//# sourceMappingURL=payment.service.js.map