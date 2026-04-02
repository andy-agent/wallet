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
exports.VpnController = void 0;
const common_1 = require("@nestjs/common");
const issue_vpn_config_request_1 = require("./dto/issue-vpn-config.request");
const vpn_service_1 = require("./vpn.service");
let VpnController = class VpnController {
    constructor(vpnService) {
        this.vpnService = vpnService;
    }
    getRegions(authorization) {
        return this.vpnService.listRegions(this.extractBearer(authorization));
    }
    issueConfig(authorization, body) {
        return this.vpnService.issueConfig(this.extractBearer(authorization), body);
    }
    getStatus(authorization) {
        return this.vpnService.getVpnStatus(this.extractBearer(authorization));
    }
    extractBearer(authorization) {
        if (!authorization?.startsWith('Bearer ')) {
            return '';
        }
        return authorization.slice('Bearer '.length);
    }
};
exports.VpnController = VpnController;
__decorate([
    (0, common_1.Get)('regions'),
    __param(0, (0, common_1.Headers)('authorization')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", void 0)
], VpnController.prototype, "getRegions", null);
__decorate([
    (0, common_1.Post)('config/issue'),
    __param(0, (0, common_1.Headers)('authorization')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object, issue_vpn_config_request_1.IssueVpnConfigRequestDto]),
    __metadata("design:returntype", void 0)
], VpnController.prototype, "issueConfig", null);
__decorate([
    (0, common_1.Get)('status'),
    __param(0, (0, common_1.Headers)('authorization')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", void 0)
], VpnController.prototype, "getStatus", null);
exports.VpnController = VpnController = __decorate([
    (0, common_1.Controller)('client/v1/vpn'),
    __metadata("design:paramtypes", [vpn_service_1.VpnService])
], VpnController);
//# sourceMappingURL=vpn.controller.js.map