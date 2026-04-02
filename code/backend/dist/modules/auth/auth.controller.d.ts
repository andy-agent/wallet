import { RegisterEmailCodeRequestDto } from './dto/register-email-code.request';
import { RegisterEmailRequestDto } from './dto/register-email.request';
import { LoginPasswordRequestDto } from './dto/login-password.request';
import { RefreshTokenRequestDto } from './dto/refresh-token.request';
import { LogoutRequestDto } from './dto/logout.request';
import { PasswordResetCodeRequestDto } from './dto/password-reset-code.request';
import { PasswordResetRequestDto } from './dto/password-reset.request';
import { AuthService } from './auth.service';
export declare class AuthController {
    private readonly authService;
    constructor(authService: AuthService);
    requestRegisterCode(body: RegisterEmailCodeRequestDto): {};
    register(body: RegisterEmailRequestDto, idempotencyKey?: string): {
        accessToken: string;
        refreshToken: string;
        accessTokenExpiresAt: string;
        refreshTokenExpiresAt: string;
        accountId: string;
        accountStatus: import("./auth.types").AccountStatus;
    };
    login(body: LoginPasswordRequestDto): {
        accessToken: string;
        refreshToken: string;
        accessTokenExpiresAt: string;
        refreshTokenExpiresAt: string;
        accountId: string;
        accountStatus: import("./auth.types").AccountStatus;
    };
    refresh(body: RefreshTokenRequestDto): {
        accessToken: string;
        refreshToken: string;
        accessTokenExpiresAt: string;
        refreshTokenExpiresAt: string;
        accountId: string;
        accountStatus: import("./auth.types").AccountStatus;
    };
    logout(_body: LogoutRequestDto, authorization?: string): {};
    requestResetCode(body: PasswordResetCodeRequestDto): {};
    resetPassword(body: PasswordResetRequestDto, _idempotencyKey?: string): {};
    private extractBearer;
}
