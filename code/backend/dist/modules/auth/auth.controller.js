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
exports.AuthController = void 0;
const common_1 = require("@nestjs/common");
const register_email_code_request_1 = require("./dto/register-email-code.request");
const register_email_request_1 = require("./dto/register-email.request");
const login_password_request_1 = require("./dto/login-password.request");
const refresh_token_request_1 = require("./dto/refresh-token.request");
const logout_request_1 = require("./dto/logout.request");
const password_reset_code_request_1 = require("./dto/password-reset-code.request");
const password_reset_request_1 = require("./dto/password-reset.request");
const auth_service_1 = require("./auth.service");
let AuthController = class AuthController {
    constructor(authService) {
        this.authService = authService;
    }
    requestRegisterCode(body) {
        this.authService.requestRegisterCode(body.email);
        return {};
    }
    register(body, idempotencyKey) {
        return this.authService.register(body, idempotencyKey ?? 'missing');
    }
    login(body) {
        return this.authService.login(body);
    }
    refresh(body) {
        return this.authService.refresh(body);
    }
    logout(_body, authorization) {
        const accessToken = this.extractBearer(authorization);
        this.authService.logout(accessToken);
        return {};
    }
    requestResetCode(body) {
        this.authService.requestResetCode(body.email);
        return {};
    }
    resetPassword(body, _idempotencyKey) {
        this.authService.resetPassword(body);
        return {};
    }
    extractBearer(authorization) {
        if (!authorization?.startsWith('Bearer ')) {
            return '';
        }
        return authorization.slice('Bearer '.length);
    }
};
exports.AuthController = AuthController;
__decorate([
    (0, common_1.Post)('register/email/request-code'),
    (0, common_1.HttpCode)(200),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [register_email_code_request_1.RegisterEmailCodeRequestDto]),
    __metadata("design:returntype", void 0)
], AuthController.prototype, "requestRegisterCode", null);
__decorate([
    (0, common_1.Post)('register/email'),
    (0, common_1.HttpCode)(200),
    __param(0, (0, common_1.Body)()),
    __param(1, (0, common_1.Headers)('x-idempotency-key')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [register_email_request_1.RegisterEmailRequestDto, String]),
    __metadata("design:returntype", void 0)
], AuthController.prototype, "register", null);
__decorate([
    (0, common_1.Post)('login/password'),
    (0, common_1.HttpCode)(200),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [login_password_request_1.LoginPasswordRequestDto]),
    __metadata("design:returntype", void 0)
], AuthController.prototype, "login", null);
__decorate([
    (0, common_1.Post)('refresh'),
    (0, common_1.HttpCode)(200),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [refresh_token_request_1.RefreshTokenRequestDto]),
    __metadata("design:returntype", void 0)
], AuthController.prototype, "refresh", null);
__decorate([
    (0, common_1.Post)('logout'),
    (0, common_1.HttpCode)(200),
    __param(0, (0, common_1.Body)()),
    __param(1, (0, common_1.Headers)('authorization')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [logout_request_1.LogoutRequestDto, String]),
    __metadata("design:returntype", void 0)
], AuthController.prototype, "logout", null);
__decorate([
    (0, common_1.Post)('password/forgot/request-code'),
    (0, common_1.HttpCode)(200),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [password_reset_code_request_1.PasswordResetCodeRequestDto]),
    __metadata("design:returntype", void 0)
], AuthController.prototype, "requestResetCode", null);
__decorate([
    (0, common_1.Post)('password/reset'),
    (0, common_1.HttpCode)(200),
    __param(0, (0, common_1.Body)()),
    __param(1, (0, common_1.Headers)('x-idempotency-key')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [password_reset_request_1.PasswordResetRequestDto, String]),
    __metadata("design:returntype", void 0)
], AuthController.prototype, "resetPassword", null);
exports.AuthController = AuthController = __decorate([
    (0, common_1.Controller)('client/v1/auth'),
    __metadata("design:paramtypes", [auth_service_1.AuthService])
], AuthController);
//# sourceMappingURL=auth.controller.js.map