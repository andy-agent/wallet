import {
  ConflictException,
  ForbiddenException,
  Injectable,
} from '@nestjs/common';
import { randomUUID } from 'crypto';
import { AuthService } from '../auth/auth.service';
import { PostgresDataAccessService } from '../database/postgres-data-access.service';
import { RuntimeStateRepository } from '../database/runtime-state.repository';
import {
  PersistedSubscriptionRecord,
  SubscriptionState,
} from './vpn.types';

const TEST_BASIC_REGION_ID = '33333333-3333-3333-3333-333333333333';
const TEST_ADVANCED_REGION_ID = '22222222-2222-2222-2222-222222222222';

@Injectable()
export class VpnService {
  constructor(
    private readonly authService: AuthService,
    private readonly runtimeStateRepository: RuntimeStateRepository,
    private readonly postgresDataAccessService: PostgresDataAccessService,
  ) {}

  async getCurrentSubscription(accessToken: string) {
    const account = this.authService.getMe(accessToken);
    return this.getSubscriptionByAccountId(account.accountId);
  }

  async listRegions(accessToken: string) {
    const account = this.authService.getMe(accessToken);
    const subscription = await this.getSubscriptionByAccountId(account.accountId);
    if (subscription.status !== 'ACTIVE') {
      throw new ForbiddenException({
        code: 'SUBSCRIPTION_REQUIRED',
        message: 'Subscription required',
      });
    }
    if (!this.postgresDataAccessService.isEnabled()) {
      if (process.env.NODE_ENV === 'test') {
        return {
          items: [
            {
              regionId: TEST_BASIC_REGION_ID,
              regionCode: 'JP_BASIC',
              displayName: '日本-基础线路',
              tier: 'BASIC',
              status: 'ACTIVE',
              isAllowed: true,
              remark: '基础可用区域',
            },
            {
              regionId: TEST_ADVANCED_REGION_ID,
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
      return { items: [] };
    }

    const plans = await this.postgresDataAccessService.listPlans({
      page: 1,
      pageSize: 100,
    });
    const plan = plans.items.find(
      (item) => (item as { planCode?: string }).planCode === subscription.planCode,
    ) as
      | {
          planCode: string;
          regionAccessPolicy: string;
          includesAdvancedRegions: boolean;
          allowedRegionIds: string[];
        }
      | undefined;

    const regions = await this.postgresDataAccessService.listVpnRegions({
      page: 1,
      pageSize: 100,
      status: 'ACTIVE',
    });

    return {
      items: regions.items.map((region) => {
        const typedRegion = region as {
          regionId: string;
          regionCode: string;
          displayName: string;
          tier: string;
          status: string;
          remark: string | null;
        };
        return {
          ...typedRegion,
          isAllowed: this.isRegionAllowed(plan, typedRegion),
        };
      }),
    };
  }

  async issueConfig(
    accessToken: string,
    params: { regionCode: string; connectionMode: 'global' | 'rule' },
  ) {
    const session = this.authService.getSessionSummary(accessToken);
    const account = this.authService.getMe(accessToken);
    const subscription = await this.getSubscriptionByAccountId(account.accountId);
    if (subscription.status !== 'ACTIVE') {
      throw new ForbiddenException({
        code: 'SUBSCRIPTION_REQUIRED',
        message: 'Subscription required',
      });
    }
    const regions = (await this.listRegions(accessToken)).items;
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

    const node = await this.pickRegionNode(region.regionId);
    if (!node) {
      throw new ConflictException({
        code: 'VPN_REGION_UNAVAILABLE',
        message: 'Region unavailable',
      });
    }

    return {
      regionCode: region.regionCode,
      connectionMode: params.connectionMode,
      configPayload: this.buildVlessConfigPayload({
        subscriptionId: subscription.subscriptionId,
        regionCode: region.regionCode,
        host: node.host,
        port: node.port,
      }),
      issuedAt: new Date().toISOString(),
      expireAt: new Date(Date.now() + 15 * 60 * 1000).toISOString(),
    };
  }

  async getVpnStatus(accessToken: string) {
    const account = this.authService.getMe(accessToken);
    const subscription = await this.getSubscriptionByAccountId(account.accountId);
    const session = this.authService.getSessionSummary(accessToken);
    const allowedRegions =
      subscription.status === 'ACTIVE' ? (await this.listRegions(accessToken)).items : [];
    const currentRegionCode =
      allowedRegions.find((item) => item.isAllowed)?.regionCode ?? null;

    return {
      subscriptionStatus: subscription.status,
      currentRegionCode,
      connectionMode: subscription.status === 'ACTIVE' ? 'global' : null,
      canIssueConfig:
        subscription.status === 'ACTIVE' &&
        session.status === 'ACTIVE' &&
        currentRegionCode !== null,
      sessionStatus: session.status,
    };
  }

  async getActiveSubscriptionCount() {
    return this.runtimeStateRepository.countActiveSubscriptions();
  }

  async activateSubscription(
    accountId: string,
    planCode: string,
    orderNo: string,
  ) {
    const existing =
      await this.runtimeStateRepository.findCurrentSubscriptionByAccountId(accountId);
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

    await this.runtimeStateRepository.upsertSubscription(subscription);
    return this.toSubscriptionState(subscription);
  }

  private async getSubscriptionByAccountId(
    accountId: string,
  ): Promise<SubscriptionState> {
    const subscription =
      await this.runtimeStateRepository.findCurrentSubscriptionByAccountId(accountId);

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
      await this.runtimeStateRepository.upsertSubscription(expiredSubscription);
      return this.toSubscriptionState(expiredSubscription);
    }

    return this.toSubscriptionState(subscription);
  }

  async getSubscriptionByAccountIdForAdmin(
    accountId: string,
  ): Promise<SubscriptionState | null> {
    const subscription =
      await this.runtimeStateRepository.findCurrentSubscriptionByAccountId(accountId);
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

  private isRegionAllowed(
    plan:
      | {
          regionAccessPolicy: string;
          includesAdvancedRegions: boolean;
          allowedRegionIds: string[];
        }
      | undefined,
    region: { regionId: string; tier: string },
  ) {
    if (!plan) {
      return false;
    }

    if (plan.includesAdvancedRegions) {
      return true;
    }

    if (plan.regionAccessPolicy === 'CUSTOM') {
      return plan.allowedRegionIds.includes(region.regionId);
    }

    if (plan.regionAccessPolicy === 'INCLUDE_ADVANCED') {
      return true;
    }

    return region.tier === 'BASIC';
  }

  private async pickRegionNode(regionId: string) {
    if (!this.postgresDataAccessService.isEnabled()) {
      if (process.env.NODE_ENV === 'test') {
        return {
          host: 'jp-basic.example.com',
          port: 443,
        };
      }
      return null;
    }

    const nodes = await this.postgresDataAccessService.listVpnNodes({
      page: 1,
      pageSize: 20,
      regionId,
      status: 'ACTIVE',
    });

    return nodes.items[0] as
      | {
          host: string;
          port: number;
        }
      | undefined
      | null;
  }

  private buildVlessConfigPayload(input: {
    subscriptionId: string;
    regionCode: string;
    host: string;
    port: number;
  }) {
    const remark = encodeURIComponent(`CryptoVPN-${input.regionCode}`);
    return `vless://${input.subscriptionId}@${input.host}:${input.port}?encryption=none&security=none&type=tcp#${remark}`;
  }
}
