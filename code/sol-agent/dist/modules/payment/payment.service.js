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
var PaymentService_1;
Object.defineProperty(exports, "__esModule", { value: true });
exports.PaymentService = void 0;
const common_1 = require("@nestjs/common");
const solana_rpc_service_1 = require("../solana/solana.rpc.service");
let PaymentService = PaymentService_1 = class PaymentService {
    constructor(solanaRpc) {
        this.solanaRpc = solanaRpc;
        this.logger = new common_1.Logger(PaymentService_1.name);
        this.paymentStore = new Map();
    }
    async getPaymentStatus(address, networkCode) {
        const effectiveNetworkCode = networkCode ?? 'solana-mainnet';
        try {
            const balanceInfo = await this.solanaRpc.getBalance(address, effectiveNetworkCode);
            const txInfo = await this.solanaRpc.getRecentTransactions(address, effectiveNetworkCode, 5);
            const hasBalance = balanceInfo.balance > 0;
            const hasTransactions = txInfo.signatures.length > 0;
            let status = 'pending';
            if (hasBalance && hasTransactions) {
                status = 'received';
            }
            else if (hasBalance) {
                status = 'received';
            }
            const result = {
                address,
                networkCode: effectiveNetworkCode,
                status,
                receivedAmount: balanceInfo.balanceInSOL,
                expectedAmount: null,
                txHash: hasTransactions ? txInfo.signatures[0] : null,
                confirmations: hasTransactions ? 1 : 0,
                balance: balanceInfo.balance,
                recentTxCount: txInfo.signatures.length,
                updatedAt: new Date().toISOString(),
            };
            this.paymentStore.set(address, result);
            return result;
        }
        catch (error) {
            this.logger.error(`Failed to get payment status for ${address}:`, error);
            return {
                address,
                networkCode: effectiveNetworkCode,
                status: 'error',
                receivedAmount: '0',
                expectedAmount: null,
                txHash: null,
                confirmations: 0,
                error: error instanceof Error ? error.message : String(error),
                updatedAt: new Date().toISOString(),
            };
        }
    }
    async detectPayment(body) {
        const effectiveNetworkCode = body.networkCode ?? 'solana-mainnet';
        try {
            this.logger.log(`Detecting payment for ${body.address} on ${effectiveNetworkCode}`);
            const balanceInfo = await this.solanaRpc.getBalance(body.address, effectiveNetworkCode);
            const txInfo = await this.solanaRpc.getRecentTransactions(body.address, effectiveNetworkCode, 10);
            const receivedAmount = parseFloat(balanceInfo.balanceInSOL);
            const expectedAmount = body.expectedAmount ? parseFloat(body.expectedAmount) : null;
            let status = 'pending';
            if (expectedAmount !== null && receivedAmount >= expectedAmount) {
                status = 'confirmed';
            }
            else if (receivedAmount > 0) {
                status = 'partial';
            }
            const result = {
                address: body.address,
                networkCode: effectiveNetworkCode,
                status,
                receivedAmount: balanceInfo.balanceInSOL,
                expectedAmount: body.expectedAmount ?? null,
                txHash: txInfo.signatures.length > 0 ? txInfo.signatures[0] : null,
                confirmations: txInfo.signatures.length,
                recentTransactions: txInfo.signatures,
                updatedAt: new Date().toISOString(),
            };
            this.paymentStore.set(body.address, result);
            this.logger.log(`Payment detection result for ${body.address}: ${status}, ${balanceInfo.balanceInSOL} SOL`);
            return result;
        }
        catch (error) {
            this.logger.error(`Payment detection failed for ${body.address}:`, error);
            return {
                address: body.address,
                networkCode: effectiveNetworkCode,
                status: 'error',
                receivedAmount: '0',
                expectedAmount: body.expectedAmount ?? null,
                txHash: null,
                confirmations: 0,
                error: error instanceof Error ? error.message : String(error),
                updatedAt: new Date().toISOString(),
            };
        }
    }
};
exports.PaymentService = PaymentService;
exports.PaymentService = PaymentService = PaymentService_1 = __decorate([
    (0, common_1.Injectable)(),
    __metadata("design:paramtypes", [solana_rpc_service_1.SolanaRpcService])
], PaymentService);
//# sourceMappingURL=payment.service.js.map