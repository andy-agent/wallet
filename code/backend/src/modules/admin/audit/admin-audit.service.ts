import { Injectable } from '@nestjs/common';
import { PostgresDataAccessService } from '../../database/postgres-data-access.service';

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
  constructor(private readonly postgresDataAccessService: PostgresDataAccessService) {}

  async listAuditLogs(params: {
    page?: number;
    pageSize?: number;
    module?: string;
    actorType?: string;
    targetType?: string;
    dateRange?: string;
  }) {
    return this.postgresDataAccessService.listAuditLogs(params);
  }

  createAuditLog(log: Omit<AuditLog, 'auditId' | 'createdAt'>): AuditLog {
    return {
      ...log,
      auditId: `AUD-${Date.now()}`,
      createdAt: new Date().toISOString(),
    };
  }
}
