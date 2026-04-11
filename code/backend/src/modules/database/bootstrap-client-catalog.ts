import {
  ClientCatalogPlan,
  ClientCatalogVpnNode,
  ClientCatalogVpnRegion,
} from './client-catalog.types';

export const BOOTSTRAP_CONFIG_ISSUE_MINUTES = 15;

export const BOOTSTRAP_CLIENT_PLANS: ClientCatalogPlan[] = [
  {
    planId: '11111111-1111-1111-1111-111111111111',
    planCode: 'BASIC_1M',
    name: '基础版-1个月',
    description: '来自 bootstrap 种子的基础套餐',
    billingCycleMonths: 1,
    priceUsd: '9.99',
    isUnlimitedTraffic: true,
    maxActiveSessions: 1,
    regionAccessPolicy: 'BASIC_ONLY',
    includesAdvancedRegions: false,
    allowedRegionIds: ['33333333-3333-3333-3333-333333333333'],
    displayOrder: 1,
    status: 'ACTIVE',
  },
];

export const BOOTSTRAP_CLIENT_VPN_REGIONS: ClientCatalogVpnRegion[] = [
  {
    regionId: '33333333-3333-3333-3333-333333333333',
    regionCode: 'JP_BASIC',
    displayName: '日本-基础线路',
    tier: 'BASIC',
    status: 'ACTIVE',
    sortOrder: 1,
    remark: '来自 bootstrap 种子的基础区域',
  },
];

export const BOOTSTRAP_CLIENT_VPN_NODES: ClientCatalogVpnNode[] = [
  {
    nodeId: '44444444-4444-4444-4444-444444444444',
    regionId: '33333333-3333-3333-3333-333333333333',
    nodeCode: 'NODE_JP_01',
    host: 'bootstrap.jp.example.com',
    port: 443,
    protocol: 'VLESS',
    transportProtocol: 'tcp',
    securityType: 'REALITY',
    realityPublicKey: 'bootstrap-jp-public-key',
    serverName: 'bootstrap.jp.example.com',
    shortId: 'jpbasic',
    flow: 'XTLS_VISION',
    weight: 100,
    status: 'ACTIVE',
    healthStatus: 'HEALTHY',
  },
];
