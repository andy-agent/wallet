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
exports.AddressController = void 0;
const common_1 = require("@nestjs/common");
const swagger_1 = require("@nestjs/swagger");
const internal_auth_guard_1 = require("../../common/guards/internal-auth.guard");
const address_service_1 = require("./address.service");
const generate_address_request_1 = require("./dto/generate-address.request");
let AddressController = class AddressController {
    constructor(addressService) {
        this.addressService = addressService;
    }
    generateAddress(body) {
        return this.addressService.generateAddress(body);
    }
    getAddress(accountId) {
        return this.addressService.getAddress(accountId);
    }
};
exports.AddressController = AddressController;
__decorate([
    (0, common_1.Post)(),
    (0, swagger_1.ApiOperation)({ summary: '生成 Solana 地址（占位）' }),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [generate_address_request_1.GenerateAddressRequestDto]),
    __metadata("design:returntype", void 0)
], AddressController.prototype, "generateAddress", null);
__decorate([
    (0, common_1.Get)(':accountId'),
    (0, swagger_1.ApiOperation)({ summary: '查询指定账户的 Solana 地址（占位）' }),
    __param(0, (0, common_1.Param)('accountId')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", void 0)
], AddressController.prototype, "getAddress", null);
exports.AddressController = AddressController = __decorate([
    (0, swagger_1.ApiTags)('Address'),
    (0, common_1.UseGuards)(internal_auth_guard_1.InternalAuthGuard),
    (0, swagger_1.ApiBearerAuth)('x-internal-auth'),
    (0, common_1.Controller)('internal/v1/address'),
    __metadata("design:paramtypes", [address_service_1.AddressService])
], AddressController);
//# sourceMappingURL=address.controller.js.map