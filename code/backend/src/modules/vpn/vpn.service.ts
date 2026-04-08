import {
  ConflictException,
  ForbiddenException,
  Injectable,
} from '@nestjs/common';
import { randomUUID } from 'crypto';
import { AuthService } from '../auth/auth.service';
import { RuntimeStateRepository } from '../database/runtime-state.repository';
import {
  PersistedSubscriptionRecord,
  SubscriptionState,
} from './vpn.types';

const BASIC_REGION_ID = '33333333-3333-3333-3333-333333333333';
const ADVANCED_REGION_ID = '22222222-2222-2222-2222-222222222222';

@Injectable()
export class VpnService {
  constructor(
    private readonly authService: AuthService,
    private readonly runtimeStateRepository: RuntimeStateRepository,
  ) {}

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
    return this.runtimeStateRepository.countActiveSubscriptions();
  }

  activateSubscription(accountId: string, planCode: string, orderNo: string) {
    const existing = this.runtimeStateRepository.findCurrentSubscriptionByAccountId(accountId);
    if (existing?.orderNo === orderNo) {
      return this.toSubscriptionState(existing);
    }

    const now = new Date();
    const expireAt = new Date(now.getTime() + 30 * 24 * 60 * 60 * 1000);
    const subscription: PersistedSubscriptionRecord = {
      accountId,
      orderNo,
      createdAt: existing?.createdAt ?? now.toISOString(),
      updatedAt: now.toISOString(),
      subscriptionId: randomUUID(),
      planCode,
      status: 'ACTIVE',
      startedAt: now.toISOString(),
      expireAt: expireAt.toISOString(),
      daysRemaining: 30,
      isUnlimitedTraffic: true,
      maxActiveSessions: 1,
    };

    this.runtimeStateRepository.upsertSubscription(subscription);
    return this.toSubscriptionState(subscription);
  }

  private getSubscriptionByAccountId(accountId: string): SubscriptionState {
    const subscription =
      this.runtimeStateRepository.findCurrentSubscriptionByAccountId(accountId);

    if (!subscription) {
      return this.getEmptySubscription();
    }

    if (subscription.expireAt && new Date(subscription.expireAt).getTime() <= Date.now()) {
      const expiredSubscription: PersistedSubscriptionRecord = {
        ...subscription,
        status: 'EXPIRED',
        daysRemaining: 0,
        updatedAt: new Date().toISOString(),
      };
      this.runtimeStateRepository.upsertSubscription(expiredSubscription);
      return this.toSubscriptionState(expiredSubscription);
    }

    return this.toSubscriptionState(subscription);
  }

  getSubscriptionByAccountIdForAdmin(accountId: string): SubscriptionState | null {
    const subscription =
      this.runtimeStateRepository.findCurrentSubscriptionByAccountId(accountId);
    if (!subscription || subscription.status === 'NONE') {
      return null;
    }
    return this.toSubscriptionState(subscription);
  }

  private toSubscriptionState(
    subscription: PersistedSubscriptionRecord,
  ): SubscriptionState {
    const { accountId: _accountId, createdAt: _createdAt, orderNo: _orderNo, updatedAt: _updatedAt, ...publicSubscription } =
      subscription;
    return publicSubscription;
  }

  private getEmptySubscription(): SubscriptionState {
    return {
      subscriptionId: '',
      planCode: '',
      status: 'NONE',
      startedAt: null,
      expireAt: null,
      daysRemaining: null,
      isUnlimitedTraffic: true,
      maxActiveSessions: 1,
    };
  }
}
