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
  PlanMutationPayload,
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

  const pageSize = 'pageSize' in result ? result.pageSize : undefined;
  const total = 'total' in result ? result.total : undefined;

  return {
    items: result.items,
    page: result.page ?? 1,
    pageSize: pageSize ?? result.items.length,
    total: total ?? result.items.length,
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
  const result = (await request.get('/admin/v1/orders', {
    params,
  })) as RawPaginatedResult<Order>;
  return normalizePaginatedResult<Order>(result);
};

export const getOrderDetail = (orderNo: string): Promise<Order> => {
  return request.get(`/admin/v1/orders/${orderNo}`);
};

export const getPlans = async (
  params?: PlanQueryParams,
): Promise<PlanListResponse> => {
  const result = (await request.get('/admin/v1/plans', {
    params,
  })) as RawPaginatedResult<Plan>;
  return normalizePaginatedResult<Plan>(result);
};

export const createPlan = (data: PlanMutationPayload): Promise<Plan> => {
  return request.post('/admin/v1/plans', data);
};

export const updatePlan = (
  planId: string,
  data: PlanMutationPayload,
): Promise<Plan> => {
  return request.put(`/admin/v1/plans/${planId}`, data);
};

export const getAuditLogs = async (
  params?: AuditLogQueryParams,
): Promise<AuditLogListResponse> => {
  const result = (await request.get('/admin/v1/audit-logs', {
    params,
  })) as RawPaginatedResult<AuditLog>;
  return normalizePaginatedResult<AuditLog>(result);
};

export const getDashboardStats = (): Promise<DashboardSummary> => {
  return request.get('/admin/v1/dashboard/summary');
};

export const getAccounts = async (
  params?: AccountQueryParams,
): Promise<AccountListResponse> => {
  const result = (await request.get('/admin/v1/accounts', {
    params,
  })) as RawPaginatedResult<AccountDetail>;
  return normalizePaginatedResult<AccountDetail>(result);
};

export const getAccountDetail = (accountId: string): Promise<AccountDetail> => {
  return request.get(`/admin/v1/accounts/${accountId}`);
};

export const getWithdrawals = async (
  params?: WithdrawalQueryParams,
): Promise<WithdrawalListResponse> => {
  const result = (await request.get('/admin/v1/withdrawals', {
    params,
  })) as RawPaginatedResult<WithdrawalListResponse['items'][number]>;
  return normalizePaginatedResult(result);
};

export const getRegions = async (
  params?: RegionQueryParams,
): Promise<RegionListResponse> => {
  const result = (await request.get('/admin/v1/vpn/regions', {
    params,
  })) as RawPaginatedResult<RegionListResponse['items'][number]>;
  return normalizePaginatedResult(result);
};

export const getNodes = async (
  params?: NodeQueryParams,
): Promise<NodeListResponse> => {
  const result = (await request.get('/admin/v1/vpn/nodes', {
    params,
  })) as RawPaginatedResult<NodeListResponse['items'][number]>;
  return normalizePaginatedResult(result);
};

export const getAppVersions = async (
  params?: VersionQueryParams,
): Promise<VersionListResponse> => {
  const result = (await request.get('/admin/v1/app-versions', {
    params,
  })) as RawPaginatedResult<AppVersion>;
  return normalizePaginatedResult<AppVersion>(result);
};

export const getLegalDocuments = async (
  params?: LegalDocumentQueryParams,
): Promise<LegalDocumentListResponse> => {
  const result = (await request.get('/admin/v1/legal-documents', {
    params,
  })) as RawPaginatedResult<LegalDocument>;
  return normalizePaginatedResult<LegalDocument>(result);
};

export const getSystemConfigs = async (
  params?: SystemConfigQueryParams,
): Promise<SystemConfigListResponse> => {
  const result = (await request.get('/admin/v1/system-configs', {
    params,
  })) as RawPaginatedResult<SystemConfig>;
  return normalizePaginatedResult<SystemConfig>(result);
};
