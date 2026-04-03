import {
  ConflictException,
  ForbiddenException,
  Injectable,
} from '@nestjs/common';
import { randomUUID } from 'crypto';
import { AuthService } from '../auth/auth.service';

const BASIC_REGION_ID = '33333333-3333-3333-3333-333333333333';
const ADVANCED_REGION_ID = '22222222-2222-2222-2222-222222222222';

export interface SubscriptionState {
  subscriptionId: string;
  planCode: string;
  status:
    | 'PENDING_ACTIVATION'
    | 'ACTIVE'
    | 'EXPIRED'
    | 'SUSPENDED'
    | 'CANCELED'
    | 'NONE';
  startedAt: string | null;
  expireAt: string | null;
  daysRemaining: number | null;
  isUnlimitedTraffic: boolean;
  maxActiveSessions: number;
}

@Injectable()
export class VpnService {
  private readonly subscriptionsByAccountId = new Map<string, SubscriptionState>();

  constructor(private readonly authService: AuthService) {}

  getCurrentSubscription(accessToken: string) {
    const account = this.authService.getMe(accessToken);
    return this.getSubscriptionByAccountId(account.accountId);
  }

  listRegions(accessToken: string) {
    const account = this.authService.getMe(accessToken);
    const subscription = this.getSubscriptionByAccountId(account.accountId);
    if (subscription.status !== 'ACTIVE') {
      throw new ForbiddenException({
        code: 'SUBSCRIPTION_REQUIRED',
        message: 'Subscription required',
      });
    }
    return {
      items: [
        {
          regionId: BASIC_REGION_ID,
          regionCode: 'JP_BASIC',
          displayName: '日本-基础线路',
          tier: 'BASIC',
          status: 'ACTIVE',
          isAllowed: true,
          remark: '基础可用区域',
        },
        {
          regionId: ADVANCED_REGION_ID,
          regionCode: 'US_LOW_LATENCY',
          displayName: '美国-低延迟',
          tier: 'ADVANCED',
          status: 'ACTIVE',
          isAllowed: false,
          remark: '当前套餐无权限',
        },
      ],
    };
  }

  issueConfig(
    accessToken: string,
    params: { regionCode: string; connectionMode: 'global' | 'rule' },
  ) {
    const session = this.authService.getSessionSummary(accessToken);
    const account = this.authService.getMe(accessToken);
    const subscription = this.getSubscriptionByAccountId(account.accountId);
    if (subscription.status !== 'ACTIVE') {
      throw new ForbiddenException({
        code: 'SUBSCRIPTION_REQUIRED',
        message: 'Subscription required',
      });
    }
    const regions = this.listRegions(accessToken).items;
    const region = regions.find((item) => item.regionCode === params.regionCode);

    if (!region) {
      throw new ConflictException({
        code: 'VPN_REGION_UNAVAILABLE',
        message: 'Region unavailable',
      });
    }

    if (region.status !== 'ACTIVE') {
      throw new ConflictException({
        code: 'VPN_REGION_UNAVAILABLE',
        message: 'Region unavailable',
      });
    }

    if (!region.isAllowed) {
      throw new ForbiddenException({
        code: 'VPN_REGION_FORBIDDEN',
        message: 'Region forbidden',
      });
    }

    return {
      regionCode: region.regionCode,
      connectionMode: params.connectionMode,
      configPayload: `vless://issued-${region.regionCode.toLowerCase()}-${session.sessionId}`,
      issuedAt: new Date().toISOString(),
      expireAt: new Date(Date.now() + 15 * 60 * 1000).toISOString(),
    };
  }

  getVpnStatus(accessToken: string) {
    const account = this.authService.getMe(accessToken);
    const subscription = this.getSubscriptionByAccountId(account.accountId);
    const session = this.authService.getSessionSummary(accessToken);

    return {
      subscriptionStatus: subscription.status,
      currentRegionCode: subscription.status === 'ACTIVE' ? 'JP_BASIC' : null,
      connectionMode: subscription.status === 'ACTIVE' ? 'global' : null,
      canIssueConfig: subscription.status === 'ACTIVE' && session.status === 'ACTIVE',
      sessionStatus: session.status,
    };
  }

  getActiveSubscriptionCount() {
    let count = 0;
    for (const sub of this.subscriptionsByAccountId.values()) {
      if (sub.status === 'ACTIVE') {
        count++;
      }
    }
    return count;
  }

  activateSubscription(accountId: string, planCode: string) {
    const subscription: SubscriptionState = {
      subscriptionId: randomUUID(),
      planCode,
      status: 'ACTIVE',
      startedAt: new Date().toISOString(),
      expireAt: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(),
      daysRemaining: 30,
      isUnlimitedTraffic: true,
      maxActiveSessions: 1,
    };
    this.subscriptionsByAccountId.set(accountId, subscription);
    return subscription;
  }

  private getSubscriptionByAccountId(accountId: string): SubscriptionState {
    return (
      this.subscriptionsByAccountId.get(accountId) ?? {
        subscriptionId: '' ,
        planCode: '',
        status: 'NONE',
        startedAt: null,
        expireAt: null,
        daysRemaining: null,
        isUnlimitedTraffic: true,
        maxActiveSessions: 1,
      }
    );
  }
}
