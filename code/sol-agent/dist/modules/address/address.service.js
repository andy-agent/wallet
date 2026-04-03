"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.AddressService = void 0;
const common_1 = require("@nestjs/common");
let AddressService = class AddressService {
    generateAddress(body) {
        return {
            accountId: body.accountId,
            networkCode: body.networkCode ?? 'solana-mainnet',
            address: `sol_placeholder_${body.accountId}_${Date.now()}`,
            publicKey: null,
            createdAt: new Date().toISOString(),
            note: 'PLACEHOLDER: not a real on-chain address yet',
        };
    }
    getAddress(accountId) {
        return {
            accountId,
            networkCode: 'solana-mainnet',
            address: `sol_placeholder_${accountId}`,
            publicKey: null,
            createdAt: new Date().toISOString(),
            note: 'PLACEHOLDER: not a real on-chain address yet',
        };
    }
};
exports.AddressService = AddressService;
exports.AddressService = AddressService = __decorate([
    (0, common_1.Injectable)()
], AddressService);
//# sourceMappingURL=address.service.js.map