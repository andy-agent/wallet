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

// 订单查询参数
export interface OrderQueryParams {
  page?: number;
  page_size?: number;
  status?: string;
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

// 审计日志查询参数
export interface AuditLogQueryParams {
  page?: number;
  page_size?: number;
  entity_type?: string;
  entity_id?: string;
  action?: string;
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

// API 响应格式
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}
