import { Injectable } from '@nestjs/common';
import { PostgresDataAccessService } from '../../database/postgres-data-access.service';

export interface LegalDocument {
  docId: string;
  docType: 'TERMS_OF_SERVICE' | 'PRIVACY_POLICY' | 'REFUND_POLICY' | 'RISK_DISCLOSURE';
  versionNo: string;
  title: string;
  content: string;
  status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED';
  effectiveAt: string | null;
  createdAt: string;
  updatedAt: string;
  updatedBy: string | null;
}

@Injectable()
export class AdminLegalService {
  constructor(private readonly postgresDataAccessService: PostgresDataAccessService) {}

  async listLegalDocuments(params: {
    page?: number;
    pageSize?: number;
    docType?: string;
    status?: string;
  }) {
    return this.postgresDataAccessService.listLegalDocuments(params);
  }

  async getLegalDocument(docType: string): Promise<LegalDocument | null> {
    const result = await this.postgresDataAccessService.listLegalDocuments({
      page: 1,
      pageSize: 1,
      docType,
      status: 'PUBLISHED',
    });
    return (result.items[0] as unknown as LegalDocument | undefined) ?? null;
  }
}
