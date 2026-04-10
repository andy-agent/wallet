import React, { useCallback, useEffect, useState } from 'react';
import {
  ReloadOutlined,
  SearchOutlined,
} from '@ant-design/icons';
import {
  Button,
  Card,
  Col,
  Input,
  Row,
  Select,
  Space,
  Table,
  Tag,
} from 'antd';
import { getAuditLogs } from '../api';
import { formatDateTime, getActionText } from '../utils/format';
import type { AuditLogListResponse, AuditLogQueryParams } from '../types';

const initialData: AuditLogListResponse = {
  items: [],
  page: 1,
  pageSize: 20,
  total: 0,
};

const initialQueryParams: AuditLogQueryParams = {
  page: 1,
  pageSize: 20,
};

const actorTypeOptions = [
  { label: '管理员', value: 'ADMIN' },
  { label: '系统', value: 'SYSTEM' },
  { label: '用户', value: 'USER' },
];

const dateRangeOptions = [
  { label: '今天', value: 'TODAY' },
  { label: '最近 7 天', value: 'LAST_7_DAYS' },
  { label: '最近 30 天', value: 'LAST_30_DAYS' },
];

const getActionColor = (action: string): string => {
  const colorMap: Record<string, string> = {
    CREATE: 'green',
    UPDATE: 'blue',
    DELETE: 'red',
    MARK_EXCEPTION: 'orange',
    APPROVE: 'processing',
    LOGIN: 'cyan',
    LOGOUT: 'default',
  };
  return colorMap[action] ?? 'default';
};

const AuditLogs: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<AuditLogListResponse>(initialData);
  const [queryParams, setQueryParams] =
    useState<AuditLogQueryParams>(initialQueryParams);

  const fetchLogs = useCallback(async () => {
    setLoading(true);
    try {
      const result = await getAuditLogs(queryParams);
      setData(result);
    } catch (error) {
      console.error('获取审计日志失败:', error);
    } finally {
      setLoading(false);
    }
  }, [queryParams]);

  useEffect(() => {
    void fetchLogs();
  }, [fetchLogs]);

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
      title: '审计 ID',
      dataIndex: 'auditId',
      key: 'auditId',
      width: 120,
    },
    {
      title: '模块',
      dataIndex: 'module',
      key: 'module',
      width: 120,
      render: (module: string) => <Tag>{module}</Tag>,
    },
    {
      title: '动作',
      dataIndex: 'action',
      key: 'action',
      width: 140,
      render: (action: string) => (
        <Tag color={getActionColor(action)}>{getActionText(action)}</Tag>
      ),
    },
    {
      title: '操作者类型',
      dataIndex: 'actorType',
      key: 'actorType',
      width: 120,
    },
    {
      title: '操作者 ID',
      dataIndex: 'actorId',
      key: 'actorId',
      width: 140,
    },
    {
      title: '目标类型',
      dataIndex: 'targetType',
      key: 'targetType',
      width: 120,
    },
    {
      title: '目标 ID',
      dataIndex: 'targetId',
      key: 'targetId',
      width: 180,
      ellipsis: true,
    },
    {
      title: '请求 ID',
      dataIndex: 'requestId',
      key: 'requestId',
      width: 160,
      ellipsis: true,
    },
    {
      title: 'IP 地址',
      dataIndex: 'ipAddress',
      key: 'ipAddress',
      width: 140,
      render: (ipAddress: string | null) => ipAddress || '-',
    },
    {
      title: '操作时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
      render: (createdAt: string) => formatDateTime(createdAt),
    },
  ];

  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>审计日志</h2>

      <Card style={{ marginBottom: 24 }}>
        <Row gutter={16}>
          <Col xs={24} sm={12} lg={6}>
            <Input
              placeholder="模块"
              value={queryParams.module}
              onChange={(event) =>
                setQueryParams((prev) => ({
                  ...prev,
                  module: event.target.value || undefined,
                }))
              }
              allowClear
            />
          </Col>
          <Col xs={24} sm={12} lg={6}>
            <Select
              placeholder="操作者类型"
              style={{ width: '100%' }}
              value={queryParams.actorType}
              onChange={(value) =>
                setQueryParams((prev) => ({ ...prev, actorType: value }))
              }
              options={actorTypeOptions}
              allowClear
            />
          </Col>
          <Col xs={24} sm={12} lg={6}>
            <Input
              placeholder="目标类型"
              value={queryParams.targetType}
              onChange={(event) =>
                setQueryParams((prev) => ({
                  ...prev,
                  targetType: event.target.value || undefined,
                }))
              }
              allowClear
            />
          </Col>
          <Col xs={24} sm={12} lg={6}>
            <Select
              placeholder="时间范围"
              style={{ width: '100%' }}
              value={queryParams.dateRange}
              onChange={(value) =>
                setQueryParams((prev) => ({ ...prev, dateRange: value }))
              }
              options={dateRangeOptions}
              allowClear
            />
          </Col>
        </Row>
        <Row gutter={16} style={{ marginTop: 16 }}>
          <Col xs={24}>
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
          rowKey="auditId"
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
          scroll={{ x: 1450 }}
        />
      </Card>
    </div>
  );
};

export default AuditLogs;
