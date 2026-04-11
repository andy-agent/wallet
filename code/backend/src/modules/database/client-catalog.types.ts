export interface ClientCatalogPlan {
  planId: string;
  planCode: string;
  name: string;
  description: string | null;
  billingCycleMonths: number;
  priceUsd: string;
  isUnlimitedTraffic: boolean;
  maxActiveSessions: number;
  regionAccessPolicy: string;
  includesAdvancedRegions: boolean;
  allowedRegionIds: string[];
  displayOrder: number;
  status: string;
}

export interface ClientCatalogVpnRegion {
  regionId: string;
  regionCode: string;
  displayName: string;
  tier: string;
  status: string;
  sortOrder: number;
  remark: string | null;
}

export interface ClientCatalogVpnNode {
  nodeId: string;
  regionId: string;
  nodeCode: string;
  host: string;
  port: number;
  protocol: string;
  transportProtocol: string;
  securityType: string;
  realityPublicKey: string | null;
  serverName: string | null;
  shortId: string | null;
  flow: string | null;
  weight: number;
  status: string;
  healthStatus: string;
}
