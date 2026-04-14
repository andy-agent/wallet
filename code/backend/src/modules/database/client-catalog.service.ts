import { Injectable } from '@nestjs/common';
import {
  BOOTSTRAP_CLIENT_PLANS,
  BOOTSTRAP_CLIENT_VPN_NODES,
  BOOTSTRAP_CLIENT_VPN_REGIONS,
  BOOTSTRAP_CONFIG_ISSUE_MINUTES,
} from './bootstrap-client-catalog';
import {
  ClientCatalogPlan,
  ClientCatalogVpnNode,
  ClientCatalogVpnRegion,
} from './client-catalog.types';
import { PostgresDataAccessService } from './postgres-data-access.service';

@Injectable()
export class ClientCatalogService {
  constructor(
    private readonly postgresDataAccessService: PostgresDataAccessService,
  ) {}

  async listPlans(params: {
    status?: string;
  } = {}): Promise<ClientCatalogPlan[]> {
    if (this.postgresDataAccessService.isEnabled()) {
      const result = await this.postgresDataAccessService.listPlans({
        page: 1,
        pageSize: 100,
        status: params.status,
      });
      return result.items as unknown as ClientCatalogPlan[];
    }

    return BOOTSTRAP_CLIENT_PLANS.filter((plan) =>
      this.matchesStatus(plan.status, params.status),
    ).sort((left, right) => left.displayOrder - right.displayOrder);
  }

  async findPlanByCode(
    planCode: string,
    params: {
      status?: string;
    } = {},
  ): Promise<ClientCatalogPlan | null> {
    const plans = await this.listPlans(params);
    return plans.find((plan) => plan.planCode === planCode) ?? null;
  }

  async listRegions(params: {
    status?: string;
  } = {}): Promise<ClientCatalogVpnRegion[]> {
    if (this.postgresDataAccessService.isEnabled()) {
      const result = await this.postgresDataAccessService.listVpnRegions({
        page: 1,
        pageSize: 100,
        status: params.status,
      });
      return result.items as unknown as ClientCatalogVpnRegion[];
    }

    return BOOTSTRAP_CLIENT_VPN_REGIONS.filter((region) =>
      this.matchesStatus(region.status, params.status),
    ).sort((left, right) => left.sortOrder - right.sortOrder);
  }

  async selectIssueNode(regionId: string): Promise<ClientCatalogVpnNode | null> {
    const nodes = await this.listNodes({
      regionIds: [regionId],
      status: 'ACTIVE',
    });

    const candidates = nodes
      .filter((node) => this.isAndroidImportableNode(node))
      .filter((node) => node.healthStatus !== 'UNHEALTHY')
      .sort((left, right) => {
        const healthDelta =
          this.healthScore(right.healthStatus) - this.healthScore(left.healthStatus);
        if (healthDelta !== 0) {
          return healthDelta;
        }
        return right.weight - left.weight;
      });

    return candidates[0] ?? null;
  }

  async listNodes(params: {
    regionIds?: string[];
    status?: string;
    healthStatus?: string;
  } = {}): Promise<ClientCatalogVpnNode[]> {
    const regionIds = (params.regionIds ?? []).filter(Boolean);
    if (this.postgresDataAccessService.isEnabled()) {
      if (regionIds.length === 0) {
        const result = await this.postgresDataAccessService.listVpnNodes({
          page: 1,
          pageSize: 100,
          status: params.status,
          healthStatus: params.healthStatus,
        });
        return result.items as unknown as ClientCatalogVpnNode[];
      }

      const batches = await Promise.all(
        regionIds.map((regionId) =>
          this.postgresDataAccessService.listVpnNodes({
            page: 1,
            pageSize: 100,
            regionId,
            status: params.status,
            healthStatus: params.healthStatus,
          }),
        ),
      );
      return batches.flatMap(
        (result) => result.items as unknown as ClientCatalogVpnNode[],
      );
    }

    return BOOTSTRAP_CLIENT_VPN_NODES.filter((node) => {
      if (regionIds.length > 0 && !regionIds.includes(node.regionId)) {
        return false;
      }
      if (!this.matchesStatus(node.status, params.status)) {
        return false;
      }
      if (params.healthStatus && !this.matchesStatus(node.healthStatus, params.healthStatus)) {
        return false;
      }
      return true;
    });
  }

  async getConfigIssueMinutes(): Promise<number> {
    if (!this.postgresDataAccessService.isEnabled()) {
      return BOOTSTRAP_CONFIG_ISSUE_MINUTES;
    }

    const rawValue = await this.postgresDataAccessService.getSystemConfigValue(
      'VPN',
      'CONFIG_ISSUE_MINUTES',
    );
    const parsed = Number.parseInt(rawValue ?? '', 10);

    if (!Number.isFinite(parsed) || parsed <= 0) {
      return BOOTSTRAP_CONFIG_ISSUE_MINUTES;
    }

    return parsed;
  }

  private matchesStatus(actual: string, expected?: string) {
    if (!expected) {
      return true;
    }
    return actual.toUpperCase() === expected.toUpperCase();
  }

  private isAndroidImportableNode(node: ClientCatalogVpnNode) {
    return node.protocol.toUpperCase() === 'VLESS';
  }

  private healthScore(status: string) {
    const normalized = status.toUpperCase();
    if (normalized === 'HEALTHY') {
      return 2;
    }
    if (normalized === 'DEGRADED') {
      return 1;
    }
    return 0;
  }
}
