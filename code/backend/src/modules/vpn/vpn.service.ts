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

interface AllowedLineView extends ClientCatalogVpnRegion {
  lineCode: string;
  lineName: string;
  regionGroupCode: string;
  regionGroupName: string;
  isAllowed: boolean;
}

export interface VpnNodeView {
  nodeId: string;
  nodeName: string;
  lineCode: string;
  lineName: string;
  regionCode: string;
  regionName: string;
  host: string;
  port: number;
  status: string;
  healthStatus: string;
  selected: boolean;
  source: 'marzban-host' | 'catalog-node';
}

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
      this.clientCatalogService.findPlanByCode(subscription.planCode),
      this.clientCatalogService.listRegions({
        status: 'ACTIVE',
      }),
    ]);

    return {
      items: regions.map((region) => {
        const identity = this.toLineIdentity(region);
        return {
          ...region,
          ...identity,
          isAllowed: this.isRegionAllowed(plan, region),
        };
      }),
    };
  }

  async listNodes(
    accessToken: string,
    params: { lineCode?: string } = {},
  ) {
    const account = this.authService.getMe(accessToken);
    const subscription = await this.getSubscriptionByAccountId(account.accountId);
    if (subscription.status !== 'ACTIVE') {
      throw new ForbiddenException({
        code: 'SUBSCRIPTION_REQUIRED',
        message: 'Subscription required',
      });
    }

    const allowedLines = await this.getAllowedLines(accessToken);
    const targetLines = params.lineCode
      ? allowedLines.filter((line) => line.lineCode === params.lineCode)
      : allowedLines;

    if (params.lineCode && targetLines.length === 0) {
      throw new ForbiddenException({
        code: 'VPN_LINE_FORBIDDEN',
        message: 'Current plan does not include this line',
      });
    }

    const items = (
      await Promise.all(
        targetLines.map((line) =>
          this.resolveNodesForLine(
            line,
            subscription,
            allowedLines.length,
          ),
        ),
      )
    ).flat();

    return { items };
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
    const allowedLines =
      subscription.status === 'ACTIVE' ? await this.getAllowedLines(accessToken) : [];
    const selectedLine =
      allowedLines.find((item) => item.lineCode === subscription.selectedLineCode) ??
      allowedLines.find((item) => item.isAllowed) ??
      null;
    const selectedLineNodes = selectedLine
      ? await this.resolveNodesForLine(selectedLine, subscription, allowedLines.length)
      : [];
    const selectedNode =
      selectedLineNodes.find((item) => item.nodeId === subscription.selectedNodeId) ??
      selectedLineNodes[0] ??
      null;

    return {
      subscriptionStatus: subscription.status,
      currentRegionCode: selectedLine?.lineCode ?? null,
      connectionMode: subscription.status === 'ACTIVE' ? 'global' : null,
      canIssueConfig:
        subscription.status === 'ACTIVE' &&
        session.status === 'ACTIVE' &&
        selectedLine !== null,
      sessionStatus: session.status,
      selectedRegionCode: selectedLine?.regionGroupCode ?? null,
      selectedRegionName: selectedLine?.regionGroupName ?? null,
      selectedLineCode: selectedLine?.lineCode ?? null,
      selectedLineName: selectedLine?.lineName ?? null,
      selectedNodeId: selectedNode?.nodeId ?? null,
      selectedNodeName: selectedNode?.nodeName ?? null,
    };
  }

  async selectNode(
    accessToken: string,
    params: { lineCode: string; nodeId: string },
  ) {
    const account = this.authService.getMe(accessToken);
    const persistedSubscription =
      await this.runtimeStateRepository.findCurrentSubscriptionByAccountId(account.accountId);
    if (!persistedSubscription || persistedSubscription.status !== 'ACTIVE') {
      throw new ForbiddenException({
        code: 'SUBSCRIPTION_REQUIRED',
        message: 'Subscription required',
      });
    }

    const allowedLines = await this.getAllowedLines(accessToken);
    const selectedLine = allowedLines.find((line) => line.lineCode === params.lineCode);
    if (!selectedLine) {
      throw new ForbiddenException({
        code: 'VPN_LINE_FORBIDDEN',
        message: 'Current plan does not include this line',
      });
    }

    const candidateNodes = await this.resolveNodesForLine(
      selectedLine,
      persistedSubscription,
      allowedLines.length,
    );
    const selectedNode = candidateNodes.find((node) => node.nodeId === params.nodeId);
    if (!selectedNode) {
      throw new ConflictException({
        code: 'VPN_NODE_UNAVAILABLE',
        message: 'Node unavailable',
      });
    }

    await this.runtimeStateRepository.upsertSubscription({
      ...persistedSubscription,
      updatedAt: new Date().toISOString(),
      selectedLineCode: selectedLine.lineCode,
      selectedNodeId: selectedNode.nodeId,
    });

    return this.getVpnStatus(accessToken);
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
      selectedLineCode: existing?.selectedLineCode ?? null,
      selectedNodeId: existing?.selectedNodeId ?? null,
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
      subscriptionUrl: this.marzbanService.normalizeSubscriptionUrl(
        input.subscriptionUrl,
      ),
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

    const normalized = await this.normalizePersistedSubscriptionUrl(subscription);
    return this.toSubscriptionState(normalized);
  }

  private async normalizePersistedSubscriptionUrl(
    subscription: PersistedSubscriptionRecord,
  ): Promise<PersistedSubscriptionRecord> {
    if (
      subscription.status !== 'ACTIVE' ||
      !subscription.subscriptionUrl?.trim()
    ) {
      return subscription;
    }

    const normalizedUrl = this.marzbanService.normalizeSubscriptionUrl(
      subscription.subscriptionUrl,
    );
    if (normalizedUrl === subscription.subscriptionUrl) {
      return subscription;
    }

    const updated: PersistedSubscriptionRecord = {
      ...subscription,
      updatedAt: new Date().toISOString(),
      subscriptionUrl: normalizedUrl,
    };
    await this.runtimeStateRepository.upsertSubscription(updated);
    return updated;
  }

  private async toSubscriptionState(
    subscription: PersistedSubscriptionRecord,
  ): Promise<SubscriptionState> {
    const plan = subscription.planCode
      ? await this.clientCatalogService.findPlanByCode(subscription.planCode)
      : null;
    const {
      accountId: _accountId,
      createdAt: _createdAt,
      orderNo: _orderNo,
      updatedAt: _updatedAt,
      ...publicSubscription
    } = subscription;
    return {
      ...publicSubscription,
      planName: plan?.name ?? null,
    };
  }

  private getEmptySubscription(): SubscriptionState {
    return {
      subscriptionId: '',
      planCode: '',
      planName: null,
      status: 'NONE',
      startedAt: null,
      expireAt: null,
      daysRemaining: null,
      isUnlimitedTraffic: true,
      maxActiveSessions: 1,
      marzbanUsername: null,
      subscriptionUrl: null,
      selectedLineCode: null,
      selectedNodeId: null,
    };
  }

  private async getAllowedLines(accessToken: string): Promise<AllowedLineView[]> {
    const regions = (await this.listRegions(accessToken)).items as AllowedLineView[];
    return regions.filter((item) => item.isAllowed);
  }

  private async resolveNodesForLine(
    line: AllowedLineView,
    subscription: SubscriptionState,
    _allowedLineCount: number,
  ): Promise<VpnNodeView[]> {
    const catalogNodes = await this.clientCatalogService.listNodes({
      regionIds: [line.regionId],
      status: 'ACTIVE',
    });
    return catalogNodes.map((node) =>
      this.toCatalogNodeView(line, node, subscription.selectedNodeId),
    );
  }

  private toCatalogNodeView(
    line: AllowedLineView,
    node: ClientCatalogVpnNode,
    selectedNodeId: string | null,
  ): VpnNodeView {
    return {
      nodeId: node.nodeId,
      nodeName: node.nodeCode,
      lineCode: line.lineCode,
      lineName: line.lineName,
      regionCode: line.regionGroupCode,
      regionName: line.regionGroupName,
      host: node.host,
      port: node.port,
      status: node.status,
      healthStatus: node.healthStatus,
      selected: selectedNodeId === node.nodeId,
      source: 'catalog-node',
    };
  }

  private toLineIdentity(region: ClientCatalogVpnRegion) {
    const lineCode = region.regionCode;
    const lineName = region.displayName;
    const regionGroupCode = lineCode.split('_')[0] || lineCode;
    const regionGroupName = lineName.split('-')[0]?.trim() || lineName;
    return {
      lineCode,
      lineName,
      regionGroupCode,
      regionGroupName,
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
