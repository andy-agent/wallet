import request from './request';
import type { 
  Order, OrderListResponse, OrderQueryParams,
  Plan,
  AuditLogListResponse, AuditLogQueryParams,
  DashboardStats,
  AccountListResponse, AccountQueryParams, AccountDetail,
  WithdrawalListResponse, WithdrawalQueryParams,
  RegionListResponse, RegionQueryParams,
  NodeListResponse, NodeQueryParams,
  VersionListResponse, VersionQueryParams,
  LegalDocumentListResponse, LegalDocumentQueryParams,
  SystemConfigListResponse,
  AdminLoginRequest, AdminLoginResponse,
} from '../types';

// ========== 认证 API ==========

// 管理员登录
export const adminLogin = (data: AdminLoginRequest): Promise<AdminLoginResponse> => {
  return request.post('/admin/v1/auth/login', data);
};

// ========== 订单 API ==========

// 获取订单列表 - aligned with backend: page, pageSize, orderNo, status, email
export const getOrders = (params?: OrderQueryParams): Promise<OrderListResponse> => {
  return request.get('/admin/v1/orders', { params });
};

// 获取订单详情
export const getOrderDetail = (orderNo: string): Promise<Order> => {
  return request.get(`/admin/v1/orders/${orderNo}`);
};

// NOTE: 以下写操作后端暂未提供，暂时注释掉，避免调用不存在的接口
// 人工确认订单
// export const manualFulfillOrder = (id: string): Promise<void> => {
//   return request.post(`/admin/v1/orders/${id}/manual-fulfill`);
// };

// 重试 fulfill 订单
// export const retryFulfillOrder = (id: string): Promise<void> => {
//   return request.post(`/admin/v1/orders/${id}/retry-fulfill`);
// };

// 标记订单为忽略
// export const ignoreOrder = (id: string): Promise<void> => {
//   return request.post(`/admin/v1/orders/${id}/ignore`);
// };

// ========== 套餐 API ==========

// 获取套餐列表
export const getPlans = (status?: string): Promise<Plan[]> => {
  return request.get('/admin/v1/plans', { params: { status } });
};

// NOTE: 以下写操作后端暂未提供，暂时注释掉
// 创建套餐
// export const createPlan = (data: PlanFormData): Promise<Plan> => {
//   return request.post('/admin/v1/plans', data);
// };

// 更新套餐
// export const updatePlan = (id: number, data: PlanFormData): Promise<Plan> => {
//   return request.put(`/admin/v1/plans/${id}`, data);
// };

// ========== 审计日志 API ==========

// 获取审计日志列表 - aligned with backend: page, pageSize, module, actorType, targetType, dateRange
export const getAuditLogs = (params?: AuditLogQueryParams): Promise<AuditLogListResponse> => {
  return request.get('/admin/v1/audit-logs', { params });
};

// NOTE: 实体追踪接口后端暂未提供
// 获取实体追踪日志
// export const getEntityAuditLogs = (entityType: string, entityId: string): Promise<AuditLog[]> => {
//   return request.get(`/admin/v1/audit-logs/${entityType}/${entityId}`);
// };

// ========== 仪表板 API ==========

// 获取统计数据 - aligned with backend: /dashboard/summary
export const getDashboardStats = (): Promise<DashboardStats> => {
  return request.get('/admin/v1/dashboard/summary');
};

// ========== 账号 API ==========

// 获取账号列表 - GET /api/admin/v1/accounts
export const getAccounts = (params?: AccountQueryParams): Promise<AccountListResponse> => {
  return request.get('/admin/v1/accounts', { params });
};

// 获取账号详情 - GET /api/admin/v1/accounts/:accountId
export const getAccountDetail = (accountId: string): Promise<AccountDetail> => {
  return request.get(`/admin/v1/accounts/${accountId}`);
};

// NOTE: 以下写操作后端暂未提供
// 冻结账号
// export const freezeAccount = (accountId: string): Promise<void> => {
//   return request.post(`/admin/v1/accounts/${accountId}/freeze`);
// };

// 解冻账号
// export const unfreezeAccount = (accountId: string): Promise<void> => {
//   return request.post(`/admin/v1/accounts/${accountId}/unfreeze`);
// };

// 驱逐会话
// export const evictAccountSessions = (accountId: string): Promise<void> => {
//   return request.post(`/admin/v1/accounts/${accountId}/evict-sessions`);
// };

// ========== 提现 API ==========

// 获取提现列表 - GET /api/admin/v1/withdrawals
export const getWithdrawals = (params?: WithdrawalQueryParams): Promise<WithdrawalListResponse> => {
  return request.get('/admin/v1/withdrawals', { params });
};

// NOTE: 以下写操作后端暂未提供
// 通过提现
// export const approveWithdrawal = (requestNo: string): Promise<void> => {
//   return request.post(`/admin/v1/withdrawals/${requestNo}/approve`);
// };

// 拒绝提现
// export const rejectWithdrawal = (requestNo: string, reason: string): Promise<void> => {
//   return request.post(`/admin/v1/withdrawals/${requestNo}/reject`, { reason });
// };

// ========== VPN 区域 API ==========

// 获取区域列表 - GET /api/admin/v1/vpn/regions
export const getRegions = (params?: RegionQueryParams): Promise<RegionListResponse> => {
  return request.get('/admin/v1/vpn/regions', { params });
};

// ========== VPN 节点 API ==========

// 获取节点列表 - GET /api/admin/v1/vpn/nodes
export const getNodes = (params?: NodeQueryParams): Promise<NodeListResponse> => {
  return request.get('/admin/v1/vpn/nodes', { params });
};

// ========== 版本管理 API ==========

// 获取版本列表 - GET /api/admin/v1/app-versions
export const getAppVersions = (params?: VersionQueryParams): Promise<VersionListResponse> => {
  return request.get('/admin/v1/app-versions', { params });
};

// ========== 法务文档 API ==========

// 获取法务文档列表 - GET /api/admin/v1/legal-documents
export const getLegalDocuments = (params?: LegalDocumentQueryParams): Promise<LegalDocumentListResponse> => {
  return request.get('/admin/v1/legal-documents', { params });
};

// ========== 系统配置 API ==========

// 获取系统配置列表 - GET /api/admin/v1/system-configs
export const getSystemConfigs = (scope?: string): Promise<SystemConfigListResponse> => {
  return request.get('/admin/v1/system-configs', { params: { scope } });
};
