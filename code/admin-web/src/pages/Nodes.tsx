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
import { getNodes } from '../api';
import { formatDateTime } from '../utils/format';
import type {
  NodeHealthStatus,
  NodeListResponse,
  NodeQueryParams,
  NodeStatus,
  VpnNode,
} from '../types';

const initialData: NodeListResponse = {
  items: [],
  page: 1,
  pageSize: 20,
  total: 0,
};

const initialQueryParams: NodeQueryParams = {
  page: 1,
  pageSize: 20,
};

const statusOptions: Array<{ label: string; value: NodeStatus }> = [
  { label: '启用', value: 'ACTIVE' },
  { label: '维护中', value: 'MAINTENANCE' },
  { label: '禁用', value: 'INACTIVE' },
];

const healthStatusOptions: Array<{ label: string; value: NodeHealthStatus }> = [
  { label: '健康', value: 'HEALTHY' },
  { label: '降级', value: 'DEGRADED' },
  { label: '异常', value: 'UNHEALTHY' },
];

const getStatusTag = (status: string) => {
  const statusMap: Record<string, { text: string; color: string }> = {
    ACTIVE: { text: '启用', color: 'success' },
    MAINTENANCE: { text: '维护中', color: 'warning' },
    INACTIVE: { text: '禁用', color: 'default' },
  };
  const { text, color } = statusMap[status] ?? {
    text: status,
    color: 'default',
  };
  return <Tag color={color}>{text}</Tag>;
};

const getHealthStatusTag = (healthStatus: string) => {
  const statusMap: Record<string, { text: string; color: string }> = {
    HEALTHY: { text: '健康', color: 'success' },
    DEGRADED: { text: '降级', color: 'warning' },
    UNHEALTHY: { text: '异常', color: 'error' },
  };
  const { text, color } = statusMap[healthStatus] ?? {
    text: healthStatus,
    color: 'default',
  };
  return <Tag color={color}>{text}</Tag>;
};

const Nodes: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<NodeListResponse>(initialData);
  const [queryParams, setQueryParams] =
    useState<NodeQueryParams>(initialQueryParams);

  const fetchNodes = useCallback(async () => {
    setLoading(true);
    try {
      const result = await getNodes(queryParams);
      setData(result);
    } catch (error) {
      console.error('获取节点列表失败:', error);
    } finally {
      setLoading(false);
    }
  }, [queryParams]);

  useEffect(() => {
    void fetchNodes();
  }, [fetchNodes]);

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
      title: '节点编码',
      dataIndex: 'nodeCode',
      key: 'nodeCode',
      width: 140,
    },
    {
      title: '显示名称',
      dataIndex: 'displayName',
      key: 'displayName',
      width: 180,
    },
    {
      title: '区域 ID',
      dataIndex: 'regionId',
      key: 'regionId',
      width: 180,
      ellipsis: true,
    },
    {
      title: '协议',
      dataIndex: 'protocol',
      key: 'protocol',
      width: 100,
    },
    {
      title: '主机',
      dataIndex: 'host',
      key: 'host',
      width: 220,
      ellipsis: true,
    },
    {
      title: '端口',
      dataIndex: 'port',
      key: 'port',
      width: 90,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (status: string) => getStatusTag(status),
    },
    {
      title: '健康状态',
      dataIndex: 'healthStatus',
      key: 'healthStatus',
      width: 120,
      render: (healthStatus: string) => getHealthStatusTag(healthStatus),
    },
    {
      title: '负载',
      key: 'currentLoad',
      width: 120,
      render: (_: unknown, record: VpnNode) =>
        `${record.currentLoad}/${record.maxCapacity}`,
    },
    {
      title: '最近健康检查',
      dataIndex: 'lastHealthCheckAt',
      key: 'lastHealthCheckAt',
      width: 180,
      render: (lastHealthCheckAt: string | null) =>
        formatDateTime(lastHealthCheckAt),
    },
    {
      title: '更新时间',
      dataIndex: 'updatedAt',
      key: 'updatedAt',
      width: 180,
      render: (updatedAt: string) => formatDateTime(updatedAt),
    },
  ];

  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>节点管理</h2>

      <Card style={{ marginBottom: 24 }}>
        <Row gutter={16}>
          <Col xs={24} sm={12} lg={8}>
            <Input
              placeholder="区域 ID"
              value={queryParams.regionId}
              onChange={(event) =>
                setQueryParams((prev) => ({
                  ...prev,
                  regionId: event.target.value || undefined,
                }))
              }
              onPressEnter={handleSearch}
              allowClear
            />
          </Col>
          <Col xs={24} sm={12} lg={8}>
            <Select
              placeholder="节点状态"
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
            <Select
              placeholder="健康状态"
              style={{ width: '100%' }}
              value={queryParams.healthStatus}
              onChange={(value) =>
                setQueryParams((prev) => ({ ...prev, healthStatus: value }))
              }
              options={healthStatusOptions}
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
          rowKey="nodeId"
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
          scroll={{ x: 1650 }}
        />
      </Card>
    </div>
  );
};

export default Nodes;
