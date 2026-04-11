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
var AddressService_1;
Object.defineProperty(exports, "__esModule", { value: true });
exports.AddressService = void 0;
const common_1 = require("@nestjs/common");
const solana_rpc_service_1 = require("../solana/solana.rpc.service");
let AddressService = AddressService_1 = class AddressService {
    constructor(solanaRpc) {
        this.solanaRpc = solanaRpc;
        this.logger = new common_1.Logger(AddressService_1.name);
        this.addressStore = new Map();
    }
    generateAddress(body) {
        const keypair = this.solanaRpc.generateKeypair();
        const networkCode = body.networkCode ?? 'solana-mainnet';
        const addressData = {
            accountId: body.accountId,
            networkCode,
            address: keypair.address,
            publicKey: keypair.publicKey,
            secretKey: keypair.secretKey,
            createdAt: new Date().toISOString(),
        };
        this.addressStore.set(body.accountId, addressData);
        this.logger.log(`Generated Solana address for account ${body.accountId}: ${keypair.address}`);
        return this.sanitizeAddressData(addressData);
    }
    getAddress(accountId) {
        const stored = this.addressStore.get(accountId);
        if (stored) {
            return this.sanitizeAddressData(stored);
        }
        return {
            accountId,
            networkCode: 'solana-mainnet',
            address: null,
            publicKey: null,
            createdAt: null,
            note: 'Address not found for this account',
        };
    }
    getAddressInternal(accountId) {
        return this.addressStore.get(accountId) || null;
    }
    sanitizeAddressData(addressData) {
        const sanitized = { ...addressData };
        delete sanitized.secretKey;
        return sanitized;
    }
};
exports.AddressService = AddressService;
exports.AddressService = AddressService = AddressService_1 = __decorate([
    (0, common_1.Injectable)(),
    __metadata("design:paramtypes", [solana_rpc_service_1.SolanaRpcService])
], AddressService);
//# sourceMappingURL=address.service.js.map