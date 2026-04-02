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
exports.WalletController = void 0;
const common_1 = require("@nestjs/common");
const proxy_broadcast_request_1 = require("./dto/proxy-broadcast.request");
const transfer_precheck_request_1 = require("./dto/transfer-precheck.request");
const upsert_wallet_public_address_request_1 = require("./dto/upsert-wallet-public-address.request");
const wallet_service_1 = require("./wallet.service");
let WalletController = class WalletController {
    constructor(walletService) {
        this.walletService = walletService;
    }
    getChains(authorization) {
        return this.walletService.getChains(this.extractBearer(authorization));
    }
    getAssetCatalog(authorization, networkCode) {
        return this.walletService.getAssetCatalog(this.extractBearer(authorization), networkCode);
    }
    upsertPublicAddress(authorization, body) {
        return this.walletService.upsertPublicAddress(this.extractBearer(authorization), body);
    }
    listPublicAddresses(authorization, networkCode, assetCode) {
        return this.walletService.listPublicAddresses(this.extractBearer(authorization), networkCode, assetCode);
    }
    transferPrecheck(authorization, body) {
        return this.walletService.transferPrecheck(this.extractBearer(authorization), body);
    }
    proxyBroadcast(authorization, body) {
        return this.walletService.proxyBroadcast(this.extractBearer(authorization), body);
    }
    extractBearer(authorization) {
        if (!authorization?.startsWith('Bearer ')) {
            return '';
        }
        return authorization.slice('Bearer '.length);
    }
};
exports.WalletController = WalletController;
__decorate([
    (0, common_1.Get)('chains'),
    __param(0, (0, common_1.Headers)('authorization')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", void 0)
], WalletController.prototype, "getChains", null);
__decorate([
    (0, common_1.Get)('assets/catalog'),
    __param(0, (0, common_1.Headers)('authorization')),
    __param(1, (0, common_1.Query)('networkCode')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, String]),
    __metadata("design:returntype", void 0)
], WalletController.prototype, "getAssetCatalog", null);
__decorate([
    (0, common_1.Post)('public-addresses'),
    __param(0, (0, common_1.Headers)('authorization')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object, upsert_wallet_public_address_request_1.UpsertWalletPublicAddressRequestDto]),
    __metadata("design:returntype", void 0)
], WalletController.prototype, "upsertPublicAddress", null);
__decorate([
    (0, common_1.Get)('public-addresses'),
    __param(0, (0, common_1.Headers)('authorization')),
    __param(1, (0, common_1.Query)('networkCode')),
    __param(2, (0, common_1.Query)('assetCode')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, String, String]),
    __metadata("design:returntype", void 0)
], WalletController.prototype, "listPublicAddresses", null);
__decorate([
    (0, common_1.Post)('transfer/precheck'),
    __param(0, (0, common_1.Headers)('authorization')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object, transfer_precheck_request_1.TransferPrecheckRequestDto]),
    __metadata("design:returntype", void 0)
], WalletController.prototype, "transferPrecheck", null);
__decorate([
    (0, common_1.Post)('transfer/proxy-broadcast'),
    __param(0, (0, common_1.Headers)('authorization')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object, proxy_broadcast_request_1.ProxyBroadcastRequestDto]),
    __metadata("design:returntype", void 0)
], WalletController.prototype, "proxyBroadcast", null);
exports.WalletController = WalletController = __decorate([
    (0, common_1.Controller)('client/v1/wallet'),
    __metadata("design:paramtypes", [wallet_service_1.WalletService])
], WalletController);
//# sourceMappingURL=wallet.controller.js.map