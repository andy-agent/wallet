import { AccountsService } from './accounts.service';
export declare class AccountsController {
    private readonly accountsService;
    constructor(accountsService: AccountsService);
    getMe(authorization?: string): {
        accountId: string;
        email: string;
        status: import("../auth/auth.types").AccountStatus;
        referralCode: string;
        subscription: null;
    };
    getSessionSummary(authorization?: string): {
        sessionId: string;
        status: import("../auth/auth.types").SessionStatus;
        installationId: string | null;
        expiresAt: string;
    };
    private extractBearer;
}
