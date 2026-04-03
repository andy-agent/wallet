import { Injectable } from '@nestjs/common';

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
  private readonly documents: LegalDocument[] = [
    {
      docId: 'LEGAL-001',
      docType: 'TERMS_OF_SERVICE',
      versionNo: '1.0.0',
      title: '服务条款',
      content: '本服务条款规定了用户使用 CryptoVPN 服务的条件和规则...',
      status: 'PUBLISHED',
      effectiveAt: new Date(Date.now() - 2592000000).toISOString(),
      createdAt: new Date(Date.now() - 2592000000).toISOString(),
      updatedAt: new Date(Date.now() - 2592000000).toISOString(),
      updatedBy: 'admin-001',
    },
    {
      docId: 'LEGAL-002',
      docType: 'PRIVACY_POLICY',
      versionNo: '1.0.0',
      title: '隐私政策',
      content: '本隐私政策说明了我们如何收集、使用和保护用户的个人信息...',
      status: 'PUBLISHED',
      effectiveAt: new Date(Date.now() - 2592000000).toISOString(),
      createdAt: new Date(Date.now() - 2592000000).toISOString(),
      updatedAt: new Date(Date.now() - 2592000000).toISOString(),
      updatedBy: 'admin-001',
    },
    {
      docId: 'LEGAL-003',
      docType: 'REFUND_POLICY',
      versionNo: '1.0.0',
      title: '退款政策',
      content: '本退款政策说明了用户在何种情况下可以申请退款...',
      status: 'DRAFT',
      effectiveAt: null,
      createdAt: new Date(Date.now() - 86400000).toISOString(),
      updatedAt: new Date(Date.now() - 86400000).toISOString(),
      updatedBy: 'admin-002',
    },
    {
      docId: 'LEGAL-004',
      docType: 'RISK_DISCLOSURE',
      versionNo: '1.0.0',
      title: '风险披露声明',
      content: '使用加密货币支付存在市场波动风险，用户应自行承担...',
      status: 'PUBLISHED',
      effectiveAt: new Date(Date.now() - 1296000000).toISOString(),
      createdAt: new Date(Date.now() - 1296000000).toISOString(),
      updatedAt: new Date(Date.now() - 1296000000).toISOString(),
      updatedBy: 'admin-001',
    },
  ];

  listLegalDocuments(params: {
    page?: number;
    pageSize?: number;
    docType?: string;
    status?: string;
  }) {
    let items = [...this.documents];

    if (params.docType) {
      items = items.filter((d) => d.docType === params.docType);
    }

    if (params.status) {
      items = items.filter((d) => d.status === params.status);
    }

    // Sort by updatedAt desc
    items = items.sort((a, b) => b.updatedAt.localeCompare(a.updatedAt));

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

  getLegalDocument(docType: string): LegalDocument | null {
    return (
      this.documents.find((d) => d.docType === docType && d.status === 'PUBLISHED') ??
      null
    );
  }
}
