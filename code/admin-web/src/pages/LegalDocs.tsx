import React, { useEffect, useState, useCallback } from 'react';
import {
  Table,
  Card,
  Select,
  Button,
  Space,
  Tag,
  Row,
  Col,
  Modal,
  Descriptions,
} from 'antd';
import { SearchOutlined, ReloadOutlined, EyeOutlined } from '@ant-design/icons';
import { getLegalDocuments } from '../api';
import { formatDateTime } from '../utils/format';
import type { LegalDocument, LegalDocumentListResponse, LegalDocumentQueryParams } from '../types';

const LegalDocs: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<LegalDocumentListResponse>({
    items: [],
    total: 0,
    page: 1,
    pageSize: 20,
  });
  const [queryParams, setQueryParams] = useState<LegalDocumentQueryParams>({
    page: 1,
    pageSize: 20,
  });
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
    fetchLegalDocuments();
  }, [fetchLegalDocuments]);

  const handleSearch = () => {
    setQueryParams(prev => ({ ...prev, page: 1 }));
    fetchLegalDocuments();
  };

  const handleReset = () => {
    setQueryParams({
      page: 1,
      pageSize: 20,
    });
  };

  const handleTableChange = (pagination: any) => {
    setQueryParams(prev => ({
      ...prev,
      page: pagination.current,
      pageSize: pagination.pageSize,
    }));
  };

  const showDetail = (doc: LegalDocument) => {
    setSelectedDoc(doc);
    setDetailModalVisible(true);
  };

  const getStatusTag = (status: string) => {
    const statusMap: Record<string, { text: string; color: string }> = {
      active: { text: '生效中', color: 'success' },
      draft: { text: '草稿', color: 'default' },
      archived: { text: '已归档', color: 'warning' },
    };
    const { text, color } = statusMap[status] || { text: status, color: 'default' };
    return <Tag color={color}>{text}</Tag>;
  };

  const getDocTypeText = (docType: string) => {
    const typeMap: Record<string, string> = {
      terms_of_service: '服务条款',
      privacy_policy: '隐私政策',
      refund_policy: '退款政策',
      user_agreement: '用户协议',
    };
    return typeMap[docType] || docType;
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
      width: 200,
      ellipsis: true,
    },
    {
      title: '版本',
      dataIndex: 'version',
      key: 'version',
      width: 100,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => getStatusTag(status),
    },
    {
      title: '生效时间',
      dataIndex: 'effectiveAt',
      key: 'effectiveAt',
      width: 170,
      render: (effectiveAt: string) => effectiveAt ? formatDateTime(effectiveAt) : '-',
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 170,
      render: (createdAt: string) => formatDateTime(createdAt),
    },
    {
      title: '更新时间',
      dataIndex: 'updatedAt',
      key: 'updatedAt',
      width: 170,
      render: (updatedAt: string) => formatDateTime(updatedAt),
    },
    {
      title: '操作',
      key: 'action',
      width: 120,
      fixed: 'right' as const,
      render: (_: any, record: LegalDocument) => (
        <Button
          type="link"
          size="small"
          icon={<EyeOutlined />}
          onClick={() => showDetail(record)}
        >
          详情
        </Button>
      ),
    },
  ];

  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>法务文档</h2>

      {/* 筛选区域 */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={16}>
          <Col xs={24} sm={12} lg={8}>
            <Select
              placeholder="文档类型"
              style={{ width: '100%' }}
              value={queryParams.docType}
              onChange={(value) => setQueryParams(prev => ({ ...prev, docType: value }))}
              allowClear
            >
              <Select.Option value="terms_of_service">服务条款</Select.Option>
              <Select.Option value="privacy_policy">隐私政策</Select.Option>
              <Select.Option value="refund_policy">退款政策</Select.Option>
              <Select.Option value="user_agreement">用户协议</Select.Option>
            </Select>
          </Col>
          <Col xs={24} sm={12} lg={8}>
            <Select
              placeholder="文档状态"
              style={{ width: '100%' }}
              value={queryParams.status}
              onChange={(value) => setQueryParams(prev => ({ ...prev, status: value }))}
              allowClear
            >
              <Select.Option value="active">生效中</Select.Option>
              <Select.Option value="draft">草稿</Select.Option>
              <Select.Option value="archived">已归档</Select.Option>
            </Select>
          </Col>
          <Col xs={24} sm={12} lg={8}>
            <Space>
              <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
                搜索
              </Button>
              <Button icon={<ReloadOutlined />} onClick={handleReset}>
                重置
              </Button>
            </Space>
          </Col>
        </Row>
      </Card>

      {/* 文档列表 */}
      <Card>
        <Table
          columns={columns}
          dataSource={data.items}
          rowKey="id"
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
          scroll={{ x: 1100 }}
        />
      </Card>

      {/* 文档详情弹窗 */}
      <Modal
        title="法务文档详情"
        open={detailModalVisible}
        onCancel={() => setDetailModalVisible(false)}
        footer={null}
        width={700}
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
              {selectedDoc.version}
            </Descriptions.Item>
            <Descriptions.Item label="状态">
              {getStatusTag(selectedDoc.status)}
            </Descriptions.Item>
            <Descriptions.Item label="生效时间">
              {selectedDoc.effectiveAt ? formatDateTime(selectedDoc.effectiveAt) : '-'}
            </Descriptions.Item>
            <Descriptions.Item label="创建时间">
              {formatDateTime(selectedDoc.createdAt)}
            </Descriptions.Item>
            <Descriptions.Item label="更新时间">
              {formatDateTime(selectedDoc.updatedAt)}
            </Descriptions.Item>
            <Descriptions.Item label="内容">
              <div style={{ maxHeight: 300, overflow: 'auto', whiteSpace: 'pre-wrap' }}>
                {selectedDoc.content || '暂无内容'}
              </div>
            </Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
    </div>
  );
};

export default LegalDocs;
