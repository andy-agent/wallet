import { Injectable } from '@nestjs/common';
import { PostgresDataAccessService } from '../../database/postgres-data-access.service';

export interface VpnRegion {
  regionId: string;
  regionCode: string;
  displayName: string;
  tier: 'BASIC' | 'ADVANCED';
  status: 'ACTIVE' | 'MAINTENANCE' | 'INACTIVE';
  sortOrder: number;
  remark: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface VpnNode {
  nodeId: string;
  regionId: string;
  nodeCode: string;
  displayName: string;
  host: string;
  port: number;
  protocol: 'VLESS' | 'VMESS' | 'TROJAN';
  status: 'ACTIVE' | 'MAINTENANCE' | 'INACTIVE';
  healthStatus: 'HEALTHY' | 'DEGRADED' | 'UNHEALTHY';
  currentLoad: number;
  maxCapacity: number;
  lastHealthCheckAt: string | null;
  createdAt: string;
  updatedAt: string;
}

@Injectable()
export class AdminVpnService {
  constructor(private readonly postgresDataAccessService: PostgresDataAccessService) {}

  async listRegions(params: {
    page?: number;
    pageSize?: number;
    tier?: string;
    status?: string;
  }) {
    return this.postgresDataAccessService.listVpnRegions(params);
  }

  async listNodes(params: {
    page?: number;
    pageSize?: number;
    regionId?: string;
    status?: string;
    healthStatus?: string;
  }) {
    return this.postgresDataAccessService.listVpnNodes(params);
  }

  async getRegionById(regionId: string): Promise<VpnRegion | null> {
    const result = await this.postgresDataAccessService.listVpnRegions({
      page: 1,
      pageSize: 1,
    });
    return (result.items.find((item) => item.regionId === regionId) as VpnRegion | undefined) ?? null;
  }

  async getNodeById(nodeId: string): Promise<VpnNode | null> {
    const result = await this.postgresDataAccessService.listVpnNodes({
      page: 1,
      pageSize: 1,
    });
    return (result.items.find((item) => item.nodeId === nodeId) as VpnNode | undefined) ?? null;
  }

  async getActiveRegions(): Promise<VpnRegion[]> {
    const result = await this.postgresDataAccessService.listVpnRegions({
      page: 1,
      pageSize: 100,
      status: 'ACTIVE',
    });
    return result.items as unknown as VpnRegion[];
  }
}
