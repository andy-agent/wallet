import { AuthService } from '../auth/auth.service';
export declare class AccountsService {
    private readonly authService;
    constructor(authService: AuthService);
    getMe(accessToken: string): {
        accountId: string;
        email: string;
        status: import("../auth/auth.types").AccountStatus;
        referralCode: string;
        subscription: null;
    };
    getSessionSummary(accessToken: string): {
        sessionId: string;
        status: import("../auth/auth.types").SessionStatus;
        installationId: string | null;
        expiresAt: string;
    };
}
