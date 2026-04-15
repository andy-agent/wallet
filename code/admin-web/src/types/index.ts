export interface PaginatedResult<T> {
  items: T[];
  page: number;
  pageSize: number;
  total: number;
}

export type OrderStatus =
  | 'AWAITING_PAYMENT'
  | 'PAYMENT_DETECTED'
  | 'CONFIRMING'
  | 'PAID'
  | 'PROVISIONING'
  | 'COMPLETED'
  | 'EXPIRED'
  | 'UNDERPAID_REVIEW'
  | 'OVERPAID_REVIEW'
  | 'FAILED'
  | 'CANCELED';

export interface Order {
  orderId: string;
  orderNo: string;
  accountId: string;
  accountEmail: string;
  planCode: string;
  planName: string;
  orderType: 'NEW' | 'RENEWAL';
  quoteAssetCode: 'SOL' | 'USDT';
  quoteNetworkCode: 'SOLANA' | 'TRON';
  quoteUsdAmount: string;
  payableAmount: string;
  status: OrderStatus;
  expiresAt: string;
  confirmedAt: string | null;
  completedAt: string | null;
  failureReason: string | null;
  submittedClientTxHash: string | null;
}

export type OrderListResponse = PaginatedResult<Order>;

export interface OrderQueryParams {
  page?: number;
  pageSize?: number;
  status?: OrderStatus;
  orderNo?: string;
  email?: string;
}

export interface DashboardSummary {
  activeAccounts: number;
  activeSubscriptions: number;
  awaitingOrders: number;
  reviewOrders: number;
  pendingWithdrawals: number;
  todayPaidOrders: number;
}

export type AccountStatus = 'ACTIVE' | 'FROZEN' | 'CLOSED';

export type SubscriptionStatus =
  | 'PENDING_ACTIVATION'
  | 'ACTIVE'
  | 'EXPIRED'
  | 'SUSPENDED'
  | 'CANCELED'
  | 'NONE';

export interface Subscription {
  subscriptionId: string;
  planCode: string;
  status: SubscriptionStatus;
  startedAt: string | null;
  expireAt: string | null;
  daysRemaining: number | null;
  isUnlimitedTraffic: boolean;
  maxActiveSessions: number;
}

export interface Account {
  accountId: string;
  email: string;
  status: AccountStatus;
  referralCode: string;
  subscription?: Subscription | null;
}

export type AccountListResponse = PaginatedResult<Account>;

export interface AccountQueryParams {
  page?: number;
  pageSize?: number;
  email?: string;
  status?: AccountStatus;
}

export type AccountDetail = Account;

export type WithdrawalStatus =
  | 'SUBMITTED'
  | 'UNDER_REVIEW'
  | 'APPROVED'
  | 'REJECTED'
  | 'BROADCASTING'
  | 'CHAIN_CONFIRMING'
  | 'COMPLETED'
  | 'FAILED'
  | 'CANCELED';

export interface Withdrawal {
  requestNo: string;
  accountId: string;
  accountEmail: string;
  amount: string;
  assetCode: 'USDT';
  networkCode: 'SOLANA';
  payoutAddress: string;
  status: WithdrawalStatus;
  failReason: string | null;
  txHash: string | null;
  createdAt: string;
  reviewedAt: string | null;
  completedAt: string | null;
}

export type WithdrawalListResponse = PaginatedResult<Withdrawal>;

export interface WithdrawalQueryParams {
  page?: number;
  pageSize?: number;
  status?: WithdrawalStatus;
  accountEmail?: string;
}

export interface Plan {
  planId: string;
  planCode: string;
  name: string;
  description: string;
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

export type PlanListResponse = PaginatedResult<Plan>;

export type PlanStatus = 'DRAFT' | 'ACTIVE' | 'DISABLED';

export type RegionAccessPolicy = 'BASIC_ONLY' | 'INCLUDE_ADVANCED' | 'CUSTOM';

export interface PlanQueryParams {
  status?: string;
}

export interface PlanMutationPayload {
  planCode: string;
  name: string;
  description?: string;
  billingCycleMonths: number;
  priceUsd: string;
  isUnlimitedTraffic: boolean;
  maxActiveSessions: number;
  regionAccessPolicy: RegionAccessPolicy;
  includesAdvancedRegions: boolean;
  allowedRegionIds: string[];
  displayOrder: number;
  status: PlanStatus;
}

export interface AuditLog {
  auditId: string;
  requestId: string;
  module: string;
  action: string;
  actorType: 'ADMIN' | 'SYSTEM' | 'USER';
  actorId: string;
  targetType: string;
  targetId: string;
  oldValue: Record<string, unknown> | null;
  newValue: Record<string, unknown> | null;
  ipAddress: string | null;
  userAgent: string | null;
  createdAt: string;
}

export type AuditLogListResponse = PaginatedResult<AuditLog>;

export interface AuditLogQueryParams {
  page?: number;
  pageSize?: number;
  module?: string;
  actorType?: AuditLog['actorType'];
  targetType?: string;
  dateRange?: string;
}

export type RegionTier = 'BASIC' | 'ADVANCED';
export type RegionStatus = 'ACTIVE' | 'MAINTENANCE' | 'INACTIVE';

export interface VpnRegion {
  regionId: string;
  regionCode: string;
  displayName: string;
  tier: RegionTier;
  status: RegionStatus;
  sortOrder: number;
  remark: string | null;
  createdAt: string;
  updatedAt: string;
}

export type RegionListResponse = PaginatedResult<VpnRegion>;

export interface RegionQueryParams {
  page?: number;
  pageSize?: number;
  tier?: RegionTier;
  status?: RegionStatus;
}

export type NodeProtocol = 'VLESS' | 'VMESS' | 'TROJAN';
export type NodeStatus = 'ACTIVE' | 'MAINTENANCE' | 'INACTIVE';
export type NodeHealthStatus = 'HEALTHY' | 'DEGRADED' | 'UNHEALTHY';

export interface VpnNode {
  nodeId: string;
  regionId: string;
  nodeCode: string;
  displayName: string;
  host: string;
  port: number;
  protocol: NodeProtocol;
  status: NodeStatus;
  healthStatus: NodeHealthStatus;
  currentLoad: number;
  maxCapacity: number;
  lastHealthCheckAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export type NodeListResponse = PaginatedResult<VpnNode>;

export interface NodeQueryParams {
  page?: number;
  pageSize?: number;
  regionId?: string;
  status?: NodeStatus;
  healthStatus?: NodeHealthStatus;
}

export type VersionPlatform = 'ANDROID' | 'IOS';
export type VersionChannel = 'GOOGLE_PLAY' | 'APP_STORE' | 'OFFICIAL';
export type VersionStatus = 'DRAFT' | 'PUBLISHED' | 'DEPRECATED';

export interface AppVersion {
  versionId: string;
  platform: VersionPlatform;
  channel: VersionChannel;
  versionName: string;
  versionCode: number;
  minAndroidVersionCode: number | null;
  minIosVersionCode: number | null;
  downloadUrl: string | null;
  forceUpdate: boolean;
  releaseNotes: string;
  status: VersionStatus;
  publishedAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export type VersionListResponse = PaginatedResult<AppVersion>;

export interface VersionQueryParams {
  page?: number;
  pageSize?: number;
  status?: VersionStatus;
  channel?: VersionChannel;
}

export type LegalDocumentType =
  | 'TERMS_OF_SERVICE'
  | 'PRIVACY_POLICY'
  | 'REFUND_POLICY'
  | 'RISK_DISCLOSURE';

export type LegalDocumentStatus = 'DRAFT' | 'PUBLISHED' | 'ARCHIVED';

export interface LegalDocument {
  docId: string;
  docType: LegalDocumentType;
  versionNo: string;
  title: string;
  content: string;
  status: LegalDocumentStatus;
  effectiveAt: string | null;
  createdAt: string;
  updatedAt: string;
  updatedBy: string | null;
}

export type LegalDocumentListResponse = PaginatedResult<LegalDocument>;

export interface LegalDocumentQueryParams {
  page?: number;
  pageSize?: number;
  docType?: LegalDocumentType;
  status?: LegalDocumentStatus;
}

export type SystemConfigValueType = 'STRING' | 'NUMBER' | 'BOOLEAN' | 'JSON';
export type SystemConfigScope = 'GLOBAL' | 'VPN' | 'PAYMENT' | 'REFERRAL';

export interface SystemConfig {
  configKey: string;
  configValue: string;
  valueType: SystemConfigValueType;
  description: string;
  scope: SystemConfigScope;
  isEditable: boolean;
  updatedAt: string;
  updatedBy: string | null;
}

export type SystemConfigListResponse = PaginatedResult<SystemConfig>;

export interface SystemConfigQueryParams {
  scope?: SystemConfigScope;
}

export interface AdminLoginRequest {
  username: string;
  password: string;
}

export interface AdminLoginResponse {
  accessToken: string;
  accessTokenExpiresAt: string;
  adminId: string;
  role: string;
}

export interface ApiResponse<T> {
  code: number | string;
  message: string;
  data: T;
}
