import request from './request';
import type { 
  Order, OrderListResponse, OrderQueryParams,
  Plan, PlanFormData,
  AuditLog, AuditLogListResponse, AuditLogQueryParams,
  DashboardStats
} from '../types';

// ========== 订单 API ==========

// 获取订单列表
export const getOrders = (params?: OrderQueryParams): Promise<OrderListResponse> => {
  return request.get('/admin/v1/orders', { params });
};

// 获取订单详情
export const getOrderDetail = (id: string): Promise<Order> => {
  return request.get(`/admin/v1/orders/${id}`);
};

// 人工确认订单
export const manualFulfillOrder = (id: string): Promise<void> => {
  return request.post(`/admin/v1/orders/${id}/manual-fulfill`);
};

// 重试 fulfill 订单
export const retryFulfillOrder = (id: string): Promise<void> => {
  return request.post(`/admin/v1/orders/${id}/retry-fulfill`);
};

// 标记订单为忽略
export const ignoreOrder = (id: string): Promise<void> => {
  return request.post(`/admin/v1/orders/${id}/ignore`);
};

// ========== 套餐 API ==========

// 获取套餐列表
export const getPlans = (): Promise<Plan[]> => {
  return request.get('/admin/v1/plans');
};

// 创建套餐
export const createPlan = (data: PlanFormData): Promise<Plan> => {
  return request.post('/admin/v1/plans', data);
};

// 更新套餐
export const updatePlan = (id: number, data: PlanFormData): Promise<Plan> => {
  return request.put(`/admin/v1/plans/${id}`, data);
};

// ========== 审计日志 API ==========

// 获取审计日志列表
export const getAuditLogs = (params?: AuditLogQueryParams): Promise<AuditLogListResponse> => {
  return request.get('/admin/v1/audit-logs', { params });
};

// 获取实体追踪日志
export const getEntityAuditLogs = (entityType: string, entityId: string): Promise<AuditLog[]> => {
  return request.get(`/admin/v1/audit-logs/${entityType}/${entityId}`);
};

// ========== 仪表板 API ==========

// 获取统计数据
export const getDashboardStats = (): Promise<DashboardStats> => {
  return request.get('/admin/v1/dashboard/stats');
};
