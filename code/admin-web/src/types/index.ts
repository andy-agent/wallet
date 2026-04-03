// 订单状态
export type OrderStatus = 'pending' | 'paid' | 'fulfilled' | 'failed' | 'ignored';

// 订单
export interface Order {
  id: string;
  out_trade_no: string;
  plan_id: number;
  plan_name: string;
  amount: number;
  currency: string;
  status: OrderStatus;
  user_email: string;
  user_id?: string;
  payer_name?: string;
  telegram_id?: string;
  payment_method?: string;
  transaction_id?: string;
  paid_at?: string;
  fulfilled_at?: string;
  created_at: string;
  updated_at: string;
  remark?: string;
}

// 订单列表响应
export interface OrderListResponse {
  items: Order[];
  total: number;
  page: number;
  page_size: number;
}

// 订单查询参数 - aligned with backend: page, pageSize, orderNo, status, email
export interface OrderQueryParams {
  page?: number;
  pageSize?: number;
  status?: string;
  orderNo?: string;
  email?: string;
  start_date?: string;
  end_date?: string;
  keyword?: string;
}

// 套餐
export interface Plan {
  id: number;
  name: string;
  description?: string;
  price: number;
  original_price?: number;
  currency: string;
  duration_days: number;
  is_active: boolean;
  sort_order: number;
  created_at: string;
  updated_at: string;
}

// 创建/更新套餐请求
export interface PlanFormData {
  name: string;
  description?: string;
  price: number;
  original_price?: number;
  currency: string;
  duration_days: number;
  is_active: boolean;
  sort_order: number;
}

// 审计日志
export interface AuditLog {
  id: number;
  action: string;
  entity_type: string;
  entity_id: string;
  operator: string;
  operator_id?: string;
  old_value?: string;
  new_value?: string;
  ip_address?: string;
  created_at: string;
}

// 审计日志列表响应
export interface AuditLogListResponse {
  items: AuditLog[];
  total: number;
  page: number;
  page_size: number;
}

// 审计日志查询参数 - aligned with backend: page, pageSize, module, actorType, targetType, dateRange
export interface AuditLogQueryParams {
  page?: number;
  pageSize?: number;
  entity_type?: string;
  entity_id?: string;
  action?: string;
  module?: string;
  actorType?: string;
  targetType?: string;
  dateRange?: string;
  start_date?: string;
  end_date?: string;
}

// 统计数据
export interface DashboardStats {
  today_orders: number;
  today_revenue: number;
  pending_orders: number;
  status_distribution: {
    status: string;
    count: number;
  }[];
  recent_trend: {
    date: string;
    orders: number;
    revenue: number;
  }[];
}

// ==================== 账号相关类型 ====================

export interface Account {
  accountId: string;
  email: string;
  status: 'active' | 'frozen' | 'pending';
  planCode?: string;
  expireAt?: string;
  lastLoginAt?: string;
  createdAt: string;
}

export interface AccountListResponse {
  items: Account[];
  total: number;
  page: number;
  pageSize: number;
}

export interface AccountQueryParams {
  page?: number;
  pageSize?: number;
  email?: string;
  status?: string;
}

export interface AccountDetail extends Account {
  subscription?: {
    planName: string;
    status: string;
    expiresAt: string;
  };
  referral?: {
    code: string;
    inviteCount: number;
  };
  commission?: {
    availableAmount: number;
    frozenAmount: number;
  };
}

// ==================== 提现相关类型 ====================

export interface Withdrawal {
  id: string;
  requestNo: string;
  accountEmail: string;
  amountUsdt: number;
  network: string;
  address: string;
  status: 'pending' | 'approved' | 'rejected' | 'completed' | 'failed';
  txHash?: string;
  createdAt: string;
  updatedAt: string;
}

export interface WithdrawalListResponse {
  items: Withdrawal[];
  total: number;
  page: number;
  pageSize: number;
}

export interface WithdrawalQueryParams {
  page?: number;
  pageSize?: number;
  status?: string;
  accountEmail?: string;
}

// ==================== VPN 区域相关类型 ====================

export interface VpnRegion {
  id: string;
  regionCode: string;
  displayName: string;
  tier: 'standard' | 'premium';
  status: 'active' | 'inactive' | 'maintenance';
  sortOrder: number;
  createdAt: string;
  updatedAt: string;
}

export interface RegionListResponse {
  items: VpnRegion[];
  total: number;
  page: number;
  pageSize: number;
}

export interface RegionQueryParams {
  page?: number;
  pageSize?: number;
  tier?: string;
  status?: string;
}

// ==================== VPN 节点相关类型 ====================

export interface VpnNode {
  id: string;
  nodeCode: string;
  regionCode: string;
  host: string;
  port: number;
  status: 'active' | 'inactive';
  healthStatus: 'healthy' | 'unhealthy' | 'unknown';
  weight: number;
  createdAt: string;
  updatedAt: string;
}

export interface NodeListResponse {
  items: VpnNode[];
  total: number;
  page: number;
  pageSize: number;
}

export interface NodeQueryParams {
  page?: number;
  pageSize?: number;
  regionId?: string;
  status?: string;
  healthStatus?: string;
}

// ==================== 版本管理相关类型 ====================

export interface AppVersion {
  id: string;
  versionCode: number;
  versionName: string;
  platform: 'android' | 'ios';
  channel: string;
  status: 'active' | 'deprecated' | 'force_update';
  releaseNotes?: string;
  downloadUrl?: string;
  createdAt: string;
  updatedAt: string;
}

export interface VersionListResponse {
  items: AppVersion[];
  total: number;
  page: number;
  pageSize: number;
}

export interface VersionQueryParams {
  page?: number;
  pageSize?: number;
  status?: string;
  channel?: string;
}

// ==================== 法务文档相关类型 ====================

export interface LegalDocument {
  id: string;
  docType: string;
  title: string;
  content?: string;
  version: string;
  status: 'active' | 'draft' | 'archived';
  effectiveAt?: string;
  createdAt: string;
  updatedAt: string;
}

export interface LegalDocumentListResponse {
  items: LegalDocument[];
  total: number;
  page: number;
  pageSize: number;
}

export interface LegalDocumentQueryParams {
  page?: number;
  pageSize?: number;
  docType?: string;
  status?: string;
}

// ==================== 系统配置相关类型 ====================

export interface SystemConfig {
  id: string;
  key: string;
  value: string;
  scope: string;
  description?: string;
  createdAt: string;
  updatedAt: string;
}

export interface SystemConfigListResponse {
  items: SystemConfig[];
  total: number;
}

export interface SystemConfigQueryParams {
  scope?: string;
}

// ==================== 登录相关类型 ====================

export interface AdminLoginRequest {
  username: string;
  password: string;
}

export interface AdminLoginResponse {
  accessToken: string;
  expiresIn: number;
}

// API 响应格式
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}
