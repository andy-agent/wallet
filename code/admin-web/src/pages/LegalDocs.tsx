import React, { useCallback, useEffect, useState } from 'react';
import {
  EyeOutlined,
  ReloadOutlined,
  SearchOutlined,
} from '@ant-design/icons';
import {
  Button,
  Card,
  Col,
  Descriptions,
  Modal,
  Row,
  Select,
  Space,
  Table,
  Tag,
} from 'antd';
import { getLegalDocuments } from '../api';
import { formatDateTime } from '../utils/format';
import type {
  LegalDocument,
  LegalDocumentListResponse,
  LegalDocumentQueryParams,
  LegalDocumentStatus,
  LegalDocumentType,
} from '../types';

const initialData: LegalDocumentListResponse = {
  items: [],
  page: 1,
  pageSize: 20,
  total: 0,
};

const initialQueryParams: LegalDocumentQueryParams = {
  page: 1,
  pageSize: 20,
};

const docTypeOptions: Array<{ label: string; value: LegalDocumentType }> = [
  { label: '服务条款', value: 'TERMS_OF_SERVICE' },
  { label: '隐私政策', value: 'PRIVACY_POLICY' },
  { label: '退款政策', value: 'REFUND_POLICY' },
  { label: '风险披露', value: 'RISK_DISCLOSURE' },
];

const statusOptions: Array<{ label: string; value: LegalDocumentStatus }> = [
  { label: '草稿', value: 'DRAFT' },
  { label: '已发布', value: 'PUBLISHED' },
  { label: '已归档', value: 'ARCHIVED' },
];

const getDocTypeText = (docType: string) => {
  const typeMap: Record<string, string> = {
    TERMS_OF_SERVICE: '服务条款',
    PRIVACY_POLICY: '隐私政策',
    REFUND_POLICY: '退款政策',
    RISK_DISCLOSURE: '风险披露',
  };
  return typeMap[docType] ?? docType;
};

const getStatusTag = (status: string) => {
  const statusMap: Record<string, { text: string; color: string }> = {
    DRAFT: { text: '草稿', color: 'default' },
    PUBLISHED: { text: '已发布', color: 'success' },
    ARCHIVED: { text: '已归档', color: 'warning' },
  };
  const { text, color } = statusMap[status] ?? {
    text: status,
    color: 'default',
  };
  return <Tag color={color}>{text}</Tag>;
};

const LegalDocs: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<LegalDocumentListResponse>(initialData);
  const [queryParams, setQueryParams] =
    useState<LegalDocumentQueryParams>(initialQueryParams);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [selectedDoc, setSelectedDoc] = useState<LegalDocument | null>(null);

  const fetchLegalDocuments = useCallback(async () => {
    setLoading(true);
    try {
      const result = await getLegalDocuments(queryParams);
      setData(result);
    } catch (error) {
      console.error('获取法务文档列表失败:', error);
    } finally {
      setLoading(false);
    }
  }, [queryParams]);

  useEffect(() => {
    void fetchLegalDocuments();
  }, [fetchLegalDocuments]);

  const handleSearch = () => {
    setQueryParams((prev) => ({ ...prev, page: 1 }));
  };

  const handleReset = () => {
    setQueryParams(initialQueryParams);
  };

  const handleTableChange = (pagination: {
    current?: number;
    pageSize?: number;
  }) => {
    setQueryParams((prev) => ({
      ...prev,
      page: pagination.current ?? 1,
      pageSize: pagination.pageSize ?? prev.pageSize,
    }));
  };

  const columns = [
    {
      title: '文档类型',
      dataIndex: 'docType',
      key: 'docType',
      width: 150,
      render: (docType: string) => getDocTypeText(docType),
    },
    {
      title: '标题',
      dataIndex: 'title',
      key: 'title',
      width: 220,
      ellipsis: true,
    },
    {
      title: '版本',
      dataIndex: 'versionNo',
      key: 'versionNo',
      width: 100,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (status: string) => getStatusTag(status),
    },
    {
      title: '生效时间',
      dataIndex: 'effectiveAt',
      key: 'effectiveAt',
      width: 180,
      render: (effectiveAt: string | null) => formatDateTime(effectiveAt),
    },
    {
      title: '更新人',
      dataIndex: 'updatedBy',
      key: 'updatedBy',
      width: 120,
      render: (updatedBy: string | null) => updatedBy || '-',
    },
    {
      title: '更新时间',
      dataIndex: 'updatedAt',
      key: 'updatedAt',
      width: 180,
      render: (updatedAt: string) => formatDateTime(updatedAt),
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      fixed: 'right' as const,
      render: (_: unknown, record: LegalDocument) => (
        <Button
          type="link"
          size="small"
          icon={<EyeOutlined />}
          onClick={() => {
            setSelectedDoc(record);
            setDetailModalVisible(true);
          }}
        >
          详情
        </Button>
      ),
    },
  ];

  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>法务文档</h2>

      <Card style={{ marginBottom: 24 }}>
        <Row gutter={16}>
          <Col xs={24} sm={12} lg={8}>
            <Select
              placeholder="文档类型"
              style={{ width: '100%' }}
              value={queryParams.docType}
              onChange={(value) =>
                setQueryParams((prev) => ({ ...prev, docType: value }))
              }
              options={docTypeOptions}
              allowClear
            />
          </Col>
          <Col xs={24} sm={12} lg={8}>
            <Select
              placeholder="文档状态"
              style={{ width: '100%' }}
              value={queryParams.status}
              onChange={(value) =>
                setQueryParams((prev) => ({ ...prev, status: value }))
              }
              options={statusOptions}
              allowClear
            />
          </Col>
          <Col xs={24} sm={12} lg={8}>
            <Space>
              <Button
                type="primary"
                icon={<SearchOutlined />}
                onClick={handleSearch}
              >
                搜索
              </Button>
              <Button icon={<ReloadOutlined />} onClick={handleReset}>
                重置
              </Button>
            </Space>
          </Col>
        </Row>
      </Card>

      <Card>
        <Table
          columns={columns}
          dataSource={data.items}
          rowKey="docId"
          loading={loading}
          pagination={{
            current: data.page,
            pageSize: data.pageSize,
            total: data.total,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条`,
          }}
          onChange={handleTableChange}
          scroll={{ x: 1350 }}
        />
      </Card>

      <Modal
        title="法务文档详情"
        open={detailModalVisible}
        onCancel={() => setDetailModalVisible(false)}
        footer={null}
        width={760}
      >
        {selectedDoc && (
          <Descriptions bordered column={1}>
            <Descriptions.Item label="文档类型">
              {getDocTypeText(selectedDoc.docType)}
            </Descriptions.Item>
            <Descriptions.Item label="标题">
              {selectedDoc.title}
            </Descriptions.Item>
            <Descriptions.Item label="版本">
              {selectedDoc.versionNo}
            </Descriptions.Item>
            <Descriptions.Item label="状态">
              {getStatusTag(selectedDoc.status)}
            </Descriptions.Item>
            <Descriptions.Item label="生效时间">
              {formatDateTime(selectedDoc.effectiveAt)}
            </Descriptions.Item>
            <Descriptions.Item label="创建时间">
              {formatDateTime(selectedDoc.createdAt)}
            </Descriptions.Item>
            <Descriptions.Item label="更新时间">
              {formatDateTime(selectedDoc.updatedAt)}
            </Descriptions.Item>
            <Descriptions.Item label="更新人">
              {selectedDoc.updatedBy || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="内容">
              <div style={{ whiteSpace: 'pre-wrap' }}>{selectedDoc.content}</div>
            </Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
    </div>
  );
};

export default LegalDocs;
