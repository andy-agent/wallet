import dayjs from 'dayjs';

export const formatDateTime = (
  date: string | Date | null | undefined,
  format = 'YYYY-MM-DD HH:mm:ss',
): string => {
  if (!date) {
    return '-';
  }
  return dayjs(date).format(format);
};

export const formatDate = (date: string | Date | null | undefined): string => {
  if (!date) {
    return '-';
  }
  return dayjs(date).format('YYYY-MM-DD');
};

export const formatAmount = (
  amount: number | string | null | undefined,
  currency = 'USD',
): string => {
  if (amount === undefined || amount === null || amount === '') {
    return '-';
  }

  const numericAmount =
    typeof amount === 'number' ? amount : Number.parseFloat(amount);
  const fallbackText = String(amount);
  const formatted =
    Number.isFinite(numericAmount)
      ? numericAmount.toLocaleString('zh-CN', {
          minimumFractionDigits: 0,
          maximumFractionDigits: 8,
        })
      : fallbackText;

  if (currency === 'USD') {
    return `$${formatted}`;
  }

  if (currency === 'CNY') {
    return `¥${formatted}`;
  }

  return `${formatted} ${currency}`;
};

export const getOrderStatusText = (
  status: string,
): { text: string; color: string } => {
  const statusMap: Record<string, { text: string; color: string }> = {
    AWAITING_PAYMENT: { text: '待支付', color: 'default' },
    PAYMENT_DETECTED: { text: '已检测支付', color: 'processing' },
    CONFIRMING: { text: '链上确认中', color: 'processing' },
    PAID: { text: '已支付', color: 'processing' },
    PROVISIONING: { text: '开通中', color: 'warning' },
    COMPLETED: { text: '已完成', color: 'success' },
    EXPIRED: { text: '已过期', color: 'default' },
    UNDERPAID_REVIEW: { text: '少付待复核', color: 'warning' },
    OVERPAID_REVIEW: { text: '多付待复核', color: 'warning' },
    FAILED: { text: '失败', color: 'error' },
    CANCELED: { text: '已取消', color: 'default' },
  };

  return statusMap[status] ?? { text: status, color: 'default' };
};

export const getActionText = (action: string): string => {
  const actionMap: Record<string, string> = {
    CREATE: '创建',
    UPDATE: '更新',
    DELETE: '删除',
    MARK_EXCEPTION: '标记异常',
    APPROVE: '通过',
    LOGIN: '登录',
    LOGOUT: '登出',
  };

  return actionMap[action] ?? action;
};
