import request from './request';
import type {
  AccountDetail,
  AccountListResponse,
  AccountQueryParams,
  AdminLoginRequest,
  AdminLoginResponse,
  AppVersion,
  AuditLog,
  AuditLogListResponse,
  AuditLogQueryParams,
  DashboardSummary,
  LegalDocument,
  LegalDocumentListResponse,
  LegalDocumentQueryParams,
  NodeListResponse,
  NodeQueryParams,
  Order,
  OrderListResponse,
  OrderQueryParams,
  PaginatedResult,
  Plan,
  PlanListResponse,
  PlanQueryParams,
  RegionListResponse,
  RegionQueryParams,
  SystemConfig,
  SystemConfigListResponse,
  SystemConfigQueryParams,
  VersionListResponse,
  VersionQueryParams,
  WithdrawalListResponse,
  WithdrawalQueryParams,
} from '../types';

type NestedPage = {
  page: number;
  pageSize: number;
  total: number;
};

type RawPaginatedResult<T> =
  | PaginatedResult<T>
  | {
      items: T[];
      page: NestedPage;
    }
  | {
      items: T[];
      page?: number;
      pageSize?: number;
      total?: number;
    };

const normalizePaginatedResult = <T>(
  result: RawPaginatedResult<T> | T[],
): PaginatedResult<T> => {
  if (Array.isArray(result)) {
    return {
      items: result,
      page: 1,
      pageSize: result.length,
      total: result.length,
    };
  }

  if (typeof result.page === 'object') {
    return {
      items: result.items,
      page: result.page.page,
      pageSize: result.page.pageSize,
      total: result.page.total,
    };
  }

  return {
    items: result.items,
    page: result.page ?? 1,
    pageSize: result.pageSize ?? result.items.length,
    total: result.total ?? result.items.length,
  };
};

export const adminLogin = (
  data: AdminLoginRequest,
): Promise<AdminLoginResponse> => {
  return request.post('/admin/v1/auth/login', data);
};

export const getOrders = async (
  params?: OrderQueryParams,
): Promise<OrderListResponse> => {
  const result = await request.get('/admin/v1/orders', { params });
  return normalizePaginatedResult<Order>(result);
};

export const getOrderDetail = (orderNo: string): Promise<Order> => {
  return request.get(`/admin/v1/orders/${orderNo}`);
};

export const getPlans = async (
  params?: PlanQueryParams,
): Promise<PlanListResponse> => {
  const result = await request.get('/admin/v1/plans', { params });
  return normalizePaginatedResult<Plan>(result);
};

export const getAuditLogs = async (
  params?: AuditLogQueryParams,
): Promise<AuditLogListResponse> => {
  const result = await request.get('/admin/v1/audit-logs', { params });
  return normalizePaginatedResult<AuditLog>(result);
};

export const getDashboardStats = (): Promise<DashboardSummary> => {
  return request.get('/admin/v1/dashboard/summary');
};

export const getAccounts = async (
  params?: AccountQueryParams,
): Promise<AccountListResponse> => {
  const result = await request.get('/admin/v1/accounts', { params });
  return normalizePaginatedResult<AccountDetail>(result);
};

export const getAccountDetail = (accountId: string): Promise<AccountDetail> => {
  return request.get(`/admin/v1/accounts/${accountId}`);
};

export const getWithdrawals = async (
  params?: WithdrawalQueryParams,
): Promise<WithdrawalListResponse> => {
  const result = await request.get('/admin/v1/withdrawals', { params });
  return normalizePaginatedResult(result);
};

export const getRegions = async (
  params?: RegionQueryParams,
): Promise<RegionListResponse> => {
  const result = await request.get('/admin/v1/vpn/regions', { params });
  return normalizePaginatedResult(result);
};

export const getNodes = async (
  params?: NodeQueryParams,
): Promise<NodeListResponse> => {
  const result = await request.get('/admin/v1/vpn/nodes', { params });
  return normalizePaginatedResult(result);
};

export const getAppVersions = async (
  params?: VersionQueryParams,
): Promise<VersionListResponse> => {
  const result = await request.get('/admin/v1/app-versions', { params });
  return normalizePaginatedResult<AppVersion>(result);
};

export const getLegalDocuments = async (
  params?: LegalDocumentQueryParams,
): Promise<LegalDocumentListResponse> => {
  const result = await request.get('/admin/v1/legal-documents', { params });
  return normalizePaginatedResult<LegalDocument>(result);
};

export const getSystemConfigs = async (
  params?: SystemConfigQueryParams,
): Promise<SystemConfigListResponse> => {
  const result = await request.get('/admin/v1/system-configs', { params });
  return normalizePaginatedResult<SystemConfig>(result);
};
