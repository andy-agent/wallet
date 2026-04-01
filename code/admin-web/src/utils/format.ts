import dayjs from 'dayjs';

// 格式化日期时间
export const formatDateTime = (date: string | Date, format = 'YYYY-MM-DD HH:mm:ss'): string => {
  if (!date) return '-';
  return dayjs(date).format(format);
};

// 格式化日期
export const formatDate = (date: string | Date): string => {
  if (!date) return '-';
  return dayjs(date).format('YYYY-MM-DD');
};

// 格式化金额
export const formatAmount = (amount: number, currency = 'CNY'): string => {
  if (amount === undefined || amount === null) return '-';
  const symbol = currency === 'CNY' ? '¥' : currency === 'USD' ? '$' : currency;
  return `${symbol}${(amount / 100).toFixed(2)}`;
};

// 获取订单状态显示
export const getOrderStatusText = (status: string): { text: string; color: string } => {
  const statusMap: Record<string, { text: string; color: string }> = {
    pending: { text: '待支付', color: 'default' },
    paid: { text: '已支付', color: 'processing' },
    fulfilled: { text: '已完成', color: 'success' },
    failed: { text: '失败', color: 'error' },
    ignored: { text: '已忽略', color: 'warning' },
  };
  return statusMap[status] || { text: status, color: 'default' };
};

// 获取操作类型显示
export const getActionText = (action: string): string => {
  const actionMap: Record<string, string> = {
    create: '创建',
    update: '更新',
    delete: '删除',
    manual_fulfill: '人工确认',
    retry_fulfill: '重试发货',
    ignore: '标记忽略',
    login: '登录',
    logout: '登出',
  };
  return actionMap[action] || action;
};
