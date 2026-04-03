import { Injectable } from '@nestjs/common';

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

@Injectable()
export class AdminAuditService {
  private readonly auditLogs: AuditLog[] = [
    {
      auditId: 'AUD-001',
      requestId: 'req-001',
      module: 'ORDERS',
      action: 'MARK_EXCEPTION',
      actorType: 'ADMIN',
      actorId: 'admin-001',
      targetType: 'ORDER',
      targetId: 'ORD-001',
      oldValue: { status: 'PENDING' },
      newValue: { status: 'EXCEPTION' },
      ipAddress: '192.168.1.1',
      userAgent: 'Mozilla/5.0',
      createdAt: new Date().toISOString(),
    },
    {
      auditId: 'AUD-002',
      requestId: 'req-002',
      module: 'PLANS',
      action: 'CREATE',
      actorType: 'ADMIN',
      actorId: 'admin-001',
      targetType: 'PLAN',
      targetId: 'PLAN-001',
      oldValue: null,
      newValue: { planCode: 'BASIC_1M', name: '基础版-1个月' },
      ipAddress: '192.168.1.1',
      userAgent: 'Mozilla/5.0',
      createdAt: new Date(Date.now() - 86400000).toISOString(),
    },
    {
      auditId: 'AUD-003',
      requestId: 'req-003',
      module: 'WITHDRAWALS',
      action: 'APPROVE',
      actorType: 'ADMIN',
      actorId: 'admin-002',
      targetType: 'WITHDRAWAL',
      targetId: 'WDR-001',
      oldValue: { status: 'SUBMITTED' },
      newValue: { status: 'APPROVED' },
      ipAddress: '192.168.1.2',
      userAgent: 'Mozilla/5.0',
      createdAt: new Date(Date.now() - 172800000).toISOString(),
    },
  ];

  listAuditLogs(params: {
    page?: number;
    pageSize?: number;
    module?: string;
    actorType?: string;
    targetType?: string;
    dateRange?: string;
  }) {
    let items = [...this.auditLogs];

    if (params.module) {
      items = items.filter((log) => log.module === params.module);
    }

    if (params.actorType) {
      items = items.filter((log) => log.actorType === params.actorType);
    }

    if (params.targetType) {
      items = items.filter((log) => log.targetType === params.targetType);
    }

    // Sort by createdAt desc
    items = items.sort((a, b) => b.createdAt.localeCompare(a.createdAt));

    const page = Math.max(1, params.page ?? 1);
    const pageSize = Math.min(100, Math.max(1, params.pageSize ?? 20));
    const total = items.length;
    const start = (page - 1) * pageSize;
    const end = start + pageSize;
    const paginatedItems = items.slice(start, end);

    return {
      items: paginatedItems,
      page: {
        page,
        pageSize,
        total,
      },
    };
  }

  createAuditLog(log: Omit<AuditLog, 'auditId' | 'createdAt'>): AuditLog {
    const auditLog: AuditLog = {
      ...log,
      auditId: `AUD-${Date.now()}`,
      createdAt: new Date().toISOString(),
    };
    this.auditLogs.push(auditLog);
    return auditLog;
  }
}
