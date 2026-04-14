import {
  ConflictException,
  ForbiddenException,
  Injectable,
} from '@nestjs/common';
import { randomUUID } from 'crypto';
import { AuthService } from '../auth/auth.service';
import { ClientCatalogService } from '../database/client-catalog.service';
import { MarzbanService } from '../marzban/marzban.service';
import {
  ClientCatalogPlan,
  ClientCatalogVpnNode,
  ClientCatalogVpnRegion,
} from '../database/client-catalog.types';
import { RuntimeStateRepository } from '../database/runtime-state.repository';
import {
  PersistedSubscriptionRecord,
  SubscriptionState,
} from './vpn.types';

@Injectable()
export class VpnService {
  constructor(
    private readonly authService: AuthService,
    private readonly clientCatalogService: ClientCatalogService,
    private readonly runtimeStateRepository: RuntimeStateRepository,
    private readonly marzbanService: MarzbanService,
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

    const [plan, regions] = await Promise.all([
      this.clientCatalogService.findPlanByCode(subscription.planCode, {
        status: 'ACTIVE',
      }),
      this.clientCatalogService.listRegions({
        status: 'ACTIVE',
      }),
    ]);

    return {
      items: regions.map((region) => ({
        ...region,
        isAllowed: this.isRegionAllowed(plan, region),
      })),
    };
  }

  async issueConfig(
    accessToken: string,
    params: { regionCode: string; connectionMode: 'global' | 'rule' },
  ) {
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
    if (!region || region.status !== 'ACTIVE') {
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

    const [node, issueMinutes] = await Promise.all([
      this.clientCatalogService.selectIssueNode(region.regionId),
      this.clientCatalogService.getConfigIssueMinutes(),
    ]);
    if (!node) {
      throw new ConflictException({
        code: 'VPN_REGION_UNAVAILABLE',
        message: 'Region unavailable',
      });
    }

    const issuedAt = new Date();

    return {
      regionCode: region.regionCode,
      connectionMode: params.connectionMode,
      configPayload: this.buildVlessConfigPayload({
        subscriptionId: subscription.subscriptionId,
        regionCode: region.regionCode,
        node,
      }),
      issuedAt: issuedAt.toISOString(),
      expireAt: new Date(issuedAt.getTime() + issueMinutes * 60 * 1000).toISOString(),
    };
  }

  async getVpnStatus(accessToken: string) {
    const account = this.authService.getMe(accessToken);
    const subscription = await this.getSubscriptionByAccountId(account.accountId);
    const session = this.authService.getSessionSummary(accessToken);
    const allowedRegions =
      subscription.status === 'ACTIVE' ? (await this.listRegions(accessToken)).items : [];
    const currentRegion = allowedRegions.find((item) => item.isAllowed) ?? null;

    return {
      subscriptionStatus: subscription.status,
      currentRegionCode: currentRegion?.regionCode ?? null,
      connectionMode: subscription.status === 'ACTIVE' ? 'global' : null,
      canIssueConfig:
        subscription.status === 'ACTIVE' &&
        session.status === 'ACTIVE' &&
        currentRegion !== null,
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

    const plan = await this.clientCatalogService.findPlanByCode(planCode);
    const now = new Date();
    const expireAt = this.addMonths(now, plan?.billingCycleMonths ?? 1);
    const subscription: PersistedSubscriptionRecord = {
      accountId,
      orderNo,
      createdAt: existing?.createdAt ?? now.toISOString(),
      updatedAt: now.toISOString(),
      subscriptionId: existing?.subscriptionId ?? randomUUID(),
      planCode,
      status: 'ACTIVE',
      startedAt: now.toISOString(),
      expireAt: expireAt.toISOString(),
      daysRemaining: this.calculateDaysRemaining(now, expireAt),
      isUnlimitedTraffic: plan?.isUnlimitedTraffic ?? true,
      maxActiveSessions: plan?.maxActiveSessions ?? 1,
      marzbanUsername: existing?.marzbanUsername ?? null,
      subscriptionUrl: existing?.subscriptionUrl ?? null,
    };

    await this.runtimeStateRepository.upsertSubscription(subscription);
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

  async attachMarzbanAccess(
    accountId: string,
    input: {
      marzbanUsername: string;
      subscriptionUrl: string;
      expireAt?: string | null;
    },
  ) {
    const subscription =
      await this.runtimeStateRepository.findCurrentSubscriptionByAccountId(accountId);
    if (!subscription) {
      return null;
    }

    const nextExpireAt = input.expireAt ?? subscription.expireAt;
    const updated: PersistedSubscriptionRecord = {
      ...subscription,
      updatedAt: new Date().toISOString(),
      expireAt: nextExpireAt,
      daysRemaining: nextExpireAt
        ? this.calculateDaysRemaining(new Date(), new Date(nextExpireAt))
        : subscription.daysRemaining,
      marzbanUsername: input.marzbanUsername,
      subscriptionUrl: input.subscriptionUrl,
    };
    await this.runtimeStateRepository.upsertSubscription(updated);
    return this.toSubscriptionState(updated);
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

    const reconciled = await this.reconcileMarzbanAccessIfNeeded(subscription);
    return this.toSubscriptionState(reconciled);
  }

  private async reconcileMarzbanAccessIfNeeded(
    subscription: PersistedSubscriptionRecord,
  ): Promise<PersistedSubscriptionRecord> {
    if (subscription.status !== 'ACTIVE') {
      return subscription;
    }

    if (
      subscription.marzbanUsername?.trim() &&
      subscription.subscriptionUrl?.trim()
    ) {
      return subscription;
    }

    const marzbanUser = await this.marzbanService.ensureUserForSubscription({
      subscriptionId: subscription.subscriptionId,
      existingUsername: subscription.marzbanUsername,
      expireAt: subscription.expireAt,
      isUnlimitedTraffic: subscription.isUnlimitedTraffic,
    });

    const nextExpireAt = marzbanUser.expireAt ?? subscription.expireAt;
    const updated: PersistedSubscriptionRecord = {
      ...subscription,
      updatedAt: new Date().toISOString(),
      expireAt: nextExpireAt,
      daysRemaining: nextExpireAt
        ? this.calculateDaysRemaining(new Date(), new Date(nextExpireAt))
        : subscription.daysRemaining,
      marzbanUsername: marzbanUser.username,
      subscriptionUrl: marzbanUser.subscriptionUrl,
    };
    await this.runtimeStateRepository.upsertSubscription(updated);
    return updated;
  }

  private toSubscriptionState(
    subscription: PersistedSubscriptionRecord,
  ): SubscriptionState {
    const {
      accountId: _accountId,
      createdAt: _createdAt,
      orderNo: _orderNo,
      updatedAt: _updatedAt,
      ...publicSubscription
    } = subscription;
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
      marzbanUsername: null,
      subscriptionUrl: null,
    };
  }

  private isRegionAllowed(
    plan: ClientCatalogPlan | null,
    region: Pick<ClientCatalogVpnRegion, 'regionId' | 'tier'>,
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

    return region.tier.toUpperCase() === 'BASIC';
  }

  private buildVlessConfigPayload(input: {
    subscriptionId: string;
    regionCode: string;
    node: ClientCatalogVpnNode;
  }) {
    const query = new URLSearchParams({
      encryption: 'none',
      security: input.node.securityType.toLowerCase(),
      type: input.node.transportProtocol.toLowerCase(),
    });

    if (input.node.realityPublicKey) {
      query.set('pbk', input.node.realityPublicKey);
      query.set('fp', 'chrome');
    }

    if (input.node.serverName) {
      query.set('sni', input.node.serverName);
    }

    if (input.node.shortId) {
      query.set('sid', input.node.shortId);
    }

    const normalizedFlow = this.normalizeFlow(input.node.flow);
    if (normalizedFlow) {
      query.set('flow', normalizedFlow);
    }

    const remark = encodeURIComponent(`CryptoVPN-${input.regionCode}`);
    return `vless://${input.subscriptionId}@${input.node.host}:${input.node.port}?${query.toString()}#${remark}`;
  }

  private normalizeFlow(flow: string | null) {
    if (!flow) {
      return null;
    }

    const normalized = flow.trim().toUpperCase();
    if (normalized === 'XTLS_VISION') {
      return 'xtls-rprx-vision';
    }

    return flow.trim().toLowerCase();
  }

  private addMonths(value: Date, months: number) {
    const next = new Date(value);
    next.setMonth(next.getMonth() + months);
    return next;
  }

  private calculateDaysRemaining(startAt: Date, expireAt: Date) {
    return Math.max(
      1,
      Math.ceil((expireAt.getTime() - startAt.getTime()) / (24 * 60 * 60 * 1000)),
    );
  }
}
