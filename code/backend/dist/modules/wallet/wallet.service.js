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
exports.WalletService = void 0;
const common_1 = require("@nestjs/common");
const crypto_1 = require("crypto");
const auth_service_1 = require("../auth/auth.service");
let WalletService = class WalletService {
    constructor(authService) {
        this.authService = authService;
        this.publicAddresses = new Map();
    }
    getChains(accessToken) {
        this.authService.getMe(accessToken);
        return {
            items: [
                {
                    networkCode: 'SOLANA',
                    displayName: 'Solana Mainnet',
                    nativeAssetCode: 'SOL',
                    directBroadcastEnabled: true,
                    proxyBroadcastEnabled: true,
                    requiredConfirmations: 1,
                    publicRpcUrl: 'https://api.mainnet-beta.solana.com',
                },
                {
                    networkCode: 'TRON',
                    displayName: 'TRON Mainnet',
                    nativeAssetCode: 'TRX',
                    directBroadcastEnabled: true,
                    proxyBroadcastEnabled: false,
                    requiredConfirmations: 20,
                    publicRpcUrl: 'https://api.trongrid.io',
                },
            ],
        };
    }
    getAssetCatalog(accessToken, networkCode) {
        this.authService.getMe(accessToken);
        const items = [
            {
                assetId: (0, crypto_1.randomUUID)(),
                networkCode: 'SOLANA',
                assetCode: 'SOL',
                displayName: 'Solana',
                symbol: 'SOL',
                decimals: 9,
                isNative: true,
                contractAddress: null,
                walletVisible: true,
                orderPayable: true,
            },
            {
                assetId: (0, crypto_1.randomUUID)(),
                networkCode: 'SOLANA',
                assetCode: 'USDT',
                displayName: 'Tether USD (Solana)',
                symbol: 'USDT',
                decimals: 6,
                isNative: false,
                contractAddress: '<SOLANA_USDT_CONTRACT>',
                walletVisible: true,
                orderPayable: true,
            },
            {
                assetId: (0, crypto_1.randomUUID)(),
                networkCode: 'TRON',
                assetCode: 'TRX',
                displayName: 'TRON',
                symbol: 'TRX',
                decimals: 6,
                isNative: true,
                contractAddress: null,
                walletVisible: true,
                orderPayable: false,
            },
            {
                assetId: (0, crypto_1.randomUUID)(),
                networkCode: 'TRON',
                assetCode: 'USDT',
                displayName: 'Tether USD (TRC20)',
                symbol: 'USDT',
                decimals: 6,
                isNative: false,
                contractAddress: '<TRON_USDT_CONTRACT>',
                walletVisible: true,
                orderPayable: true,
            },
        ];
        return {
            items: networkCode ? items.filter((item) => item.networkCode === networkCode) : items,
        };
    }
    upsertPublicAddress(accessToken, dto) {
        const account = this.authService.getMe(accessToken);
        const existing = this.publicAddresses.get(account.accountId) ?? [];
        const found = existing.find((item) => item.networkCode === dto.networkCode &&
            item.assetCode === dto.assetCode &&
            item.address === dto.address);
        if (dto.isDefault) {
            for (const item of existing) {
                if (item.networkCode === dto.networkCode && item.assetCode === dto.assetCode) {
                    item.isDefault = false;
                }
            }
        }
        if (found) {
            found.isDefault = dto.isDefault;
            return found;
        }
        const created = {
            addressId: (0, crypto_1.randomUUID)(),
            accountId: account.accountId,
            networkCode: dto.networkCode,
            assetCode: dto.assetCode,
            address: dto.address,
            isDefault: dto.isDefault,
            createdAt: new Date().toISOString(),
        };
        existing.push(created);
        this.publicAddresses.set(account.accountId, existing);
        return created;
    }
    listPublicAddresses(accessToken, networkCode, assetCode) {
        const account = this.authService.getMe(accessToken);
        let items = this.publicAddresses.get(account.accountId) ?? [];
        if (networkCode) {
            items = items.filter((item) => item.networkCode === networkCode);
        }
        if (assetCode) {
            items = items.filter((item) => item.assetCode === assetCode);
        }
        return { items };
    }
    transferPrecheck(accessToken, dto) {
        this.authService.getMe(accessToken);
        if (!this.isAddressValid(dto.networkCode, dto.toAddress)) {
            throw new common_1.BadRequestException({
                code: 'WALLET_INVALID_ADDRESS',
                message: 'Wallet address invalid',
            });
        }
        const chain = this.getChains(accessToken).items.find((item) => item.networkCode === dto.networkCode);
        const asset = this.getAssetCatalog(accessToken, dto.networkCode).items.find((item) => item.assetCode === dto.assetCode);
        if (!chain || !asset) {
            throw new common_1.BadRequestException({
                code: 'WALLET_UNSUPPORTED_ASSET',
                message: 'Unsupported asset',
            });
        }
        return {
            networkCode: dto.networkCode,
            assetCode: dto.assetCode,
            toAddressNormalized: dto.toAddress.trim(),
            amount: dto.amount,
            estimatedFee: dto.networkCode === 'SOLANA' ? '0.000005' : '1.000000',
            directBroadcastEnabled: chain.directBroadcastEnabled,
            proxyBroadcastEnabled: chain.proxyBroadcastEnabled,
            warnings: dto.orderNo ? ['ORDER_PAYMENT_CONTEXT'] : [],
        };
    }
    proxyBroadcast(accessToken, dto) {
        this.authService.getMe(accessToken);
        const chain = this.getChains(accessToken).items.find((item) => item.networkCode === dto.networkCode);
        if (!chain?.proxyBroadcastEnabled) {
            throw new common_1.ConflictException({
                code: 'WALLET_PROXY_BROADCAST_DISABLED',
                message: 'Proxy broadcast disabled',
            });
        }
        return {
            networkCode: dto.networkCode,
            broadcasted: true,
            txHash: dto.clientTxHash ?? `proxy_${(0, crypto_1.randomUUID)()}`,
            acceptedAt: new Date().toISOString(),
        };
    }
    isAddressValid(networkCode, address) {
        const trimmed = address.trim();
        if (networkCode === 'SOLANA') {
            return trimmed.length >= 32;
        }
        return trimmed.startsWith('T') && trimmed.length >= 20;
    }
};
exports.WalletService = WalletService;
exports.WalletService = WalletService = __decorate([
    (0, common_1.Injectable)(),
    __metadata("design:paramtypes", [auth_service_1.AuthService])
], WalletService);
//# sourceMappingURL=wallet.service.js.map