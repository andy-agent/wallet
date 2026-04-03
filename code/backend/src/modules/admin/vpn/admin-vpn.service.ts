import { Injectable } from '@nestjs/common';

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
  private readonly regions: VpnRegion[] = [
    {
      regionId: '33333333-3333-3333-3333-333333333333',
      regionCode: 'JP_BASIC',
      displayName: '日本-基础线路',
      tier: 'BASIC',
      status: 'ACTIVE',
      sortOrder: 1,
      remark: '基础可用区域',
      createdAt: new Date(Date.now() - 2592000000).toISOString(),
      updatedAt: new Date(Date.now() - 86400000).toISOString(),
    },
    {
      regionId: '22222222-2222-2222-2222-222222222222',
      regionCode: 'US_LOW_LATENCY',
      displayName: '美国-低延迟',
      tier: 'ADVANCED',
      status: 'ACTIVE',
      sortOrder: 2,
      remark: '高级区域',
      createdAt: new Date(Date.now() - 2592000000).toISOString(),
      updatedAt: new Date(Date.now() - 86400000).toISOString(),
    },
    {
      regionId: '44444444-4444-4444-4444-444444444444',
      regionCode: 'SG_STANDARD',
      displayName: '新加坡-标准',
      tier: 'BASIC',
      status: 'MAINTENANCE',
      sortOrder: 3,
      remark: '维护中',
      createdAt: new Date(Date.now() - 1296000000).toISOString(),
      updatedAt: new Date(Date.now() - 43200000).toISOString(),
    },
  ];

  private readonly nodes: VpnNode[] = [
    {
      nodeId: 'NODE-001',
      regionId: '33333333-3333-3333-3333-333333333333',
      nodeCode: 'JP-TOK-01',
      displayName: '日本东京-01',
      host: 'jp-tok-01.cryptovpn.example',
      port: 443,
      protocol: 'VLESS',
      status: 'ACTIVE',
      healthStatus: 'HEALTHY',
      currentLoad: 45,
      maxCapacity: 1000,
      lastHealthCheckAt: new Date(Date.now() - 300000).toISOString(),
      createdAt: new Date(Date.now() - 2592000000).toISOString(),
      updatedAt: new Date(Date.now() - 86400000).toISOString(),
    },
    {
      nodeId: 'NODE-002',
      regionId: '33333333-3333-3333-3333-333333333333',
      nodeCode: 'JP-OSA-01',
      displayName: '日本大阪-01',
      host: 'jp-osa-01.cryptovpn.example',
      port: 443,
      protocol: 'VLESS',
      status: 'ACTIVE',
      healthStatus: 'HEALTHY',
      currentLoad: 32,
      maxCapacity: 1000,
      lastHealthCheckAt: new Date(Date.now() - 300000).toISOString(),
      createdAt: new Date(Date.now() - 2592000000).toISOString(),
      updatedAt: new Date(Date.now() - 86400000).toISOString(),
    },
    {
      nodeId: 'NODE-003',
      regionId: '22222222-2222-2222-2222-222222222222',
      nodeCode: 'US-LA-01',
      displayName: '美国洛杉矶-01',
      host: 'us-la-01.cryptovpn.example',
      port: 443,
      protocol: 'VLESS',
      status: 'ACTIVE',
      healthStatus: 'DEGRADED',
      currentLoad: 78,
      maxCapacity: 1000,
      lastHealthCheckAt: new Date(Date.now() - 300000).toISOString(),
      createdAt: new Date(Date.now() - 2592000000).toISOString(),
      updatedAt: new Date(Date.now() - 43200000).toISOString(),
    },
    {
      nodeId: 'NODE-004',
      regionId: '22222222-2222-2222-2222-222222222222',
      nodeCode: 'US-NY-01',
      displayName: '美国纽约-01',
      host: 'us-ny-01.cryptovpn.example',
      port: 443,
      protocol: 'VLESS',
      status: 'MAINTENANCE',
      healthStatus: 'UNHEALTHY',
      currentLoad: 0,
      maxCapacity: 1000,
      lastHealthCheckAt: new Date(Date.now() - 3600000).toISOString(),
      createdAt: new Date(Date.now() - 1296000000).toISOString(),
      updatedAt: new Date(Date.now() - 3600000).toISOString(),
    },
    {
      nodeId: 'NODE-005',
      regionId: '44444444-4444-4444-4444-444444444444',
      nodeCode: 'SG-01',
      displayName: '新加坡-01',
      host: 'sg-01.cryptovpn.example',
      port: 443,
      protocol: 'VLESS',
      status: 'INACTIVE',
      healthStatus: 'UNHEALTHY',
      currentLoad: 0,
      maxCapacity: 1000,
      lastHealthCheckAt: new Date(Date.now() - 7200000).toISOString(),
      createdAt: new Date(Date.now() - 1296000000).toISOString(),
      updatedAt: new Date(Date.now() - 43200000).toISOString(),
    },
  ];

  listRegions(params: {
    page?: number;
    pageSize?: number;
    tier?: string;
    status?: string;
  }) {
    let items = [...this.regions];

    if (params.tier) {
      items = items.filter((r) => r.tier === params.tier);
    }

    if (params.status) {
      items = items.filter((r) => r.status === params.status);
    }

    // Sort by sortOrder asc
    items = items.sort((a, b) => a.sortOrder - b.sortOrder);

    const page = Math.max(1, params.page ?? 1);
    const pageSize = Math.min(100, Math.max(1, params.pageSize ?? 20));
    const total = items.length;
    const start = (page - 1) * pageSize;
    const end = start + pageSize;
    const paginatedItems = items.slice(start, end);

    return {
      items: paginatedItems,
      page: {
        page,
        pageSize,
        total,
      },
    };
  }

  listNodes(params: {
    page?: number;
    pageSize?: number;
    regionId?: string;
    status?: string;
    healthStatus?: string;
  }) {
    let items = [...this.nodes];

    if (params.regionId) {
      items = items.filter((n) => n.regionId === params.regionId);
    }

    if (params.status) {
      items = items.filter((n) => n.status === params.status);
    }

    if (params.healthStatus) {
      items = items.filter((n) => n.healthStatus === params.healthStatus);
    }

    // Sort by createdAt desc
    items = items.sort((a, b) => b.createdAt.localeCompare(a.createdAt));

    const page = Math.max(1, params.page ?? 1);
    const pageSize = Math.min(100, Math.max(1, params.pageSize ?? 20));
    const total = items.length;
    const start = (page - 1) * pageSize;
    const end = start + pageSize;
    const paginatedItems = items.slice(start, end);

    return {
      items: paginatedItems,
      page: {
        page,
        pageSize,
        total,
      },
    };
  }

  getRegionById(regionId: string): VpnRegion | null {
    return this.regions.find((r) => r.regionId === regionId) ?? null;
  }

  getNodeById(nodeId: string): VpnNode | null {
    return this.nodes.find((n) => n.nodeId === nodeId) ?? null;
  }

  getActiveRegions(): VpnRegion[] {
    return this.regions.filter((r) => r.status === 'ACTIVE');
  }
}
