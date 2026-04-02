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
exports.ProvisioningService = void 0;
const common_1 = require("@nestjs/common");
const vpn_service_1 = require("../vpn/vpn.service");
let ProvisioningService = class ProvisioningService {
    constructor(vpnService) {
        this.vpnService = vpnService;
    }
    provisionPaidOrder(input) {
        const subscription = this.vpnService.activateSubscription(input.accountId, input.planCode);
        return {
            subscriptionId: subscription.subscriptionId,
            status: 'COMPLETED',
        };
    }
};
exports.ProvisioningService = ProvisioningService;
exports.ProvisioningService = ProvisioningService = __decorate([
    (0, common_1.Injectable)(),
    __metadata("design:paramtypes", [vpn_service_1.VpnService])
], ProvisioningService);
//# sourceMappingURL=provisioning.service.js.map