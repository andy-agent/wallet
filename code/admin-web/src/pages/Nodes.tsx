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
  Input,
} from 'antd';
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons';
import { getNodes } from '../api';
import { formatDateTime } from '../utils/format';
import type { NodeListResponse, NodeQueryParams } from '../types';

const Nodes: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<NodeListResponse>({
    items: [],
    total: 0,
    page: 1,
    pageSize: 20,
  });
  const [queryParams, setQueryParams] = useState<NodeQueryParams>({
    page: 1,
    pageSize: 20,
  });

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
    fetchNodes();
  }, [fetchNodes]);

  const handleSearch = () => {
    setQueryParams(prev => ({ ...prev, page: 1 }));
    fetchNodes();
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

  const getStatusTag = (status: string) => {
    const statusMap: Record<string, { text: string; color: string }> = {
      active: { text: '启用', color: 'success' },
      inactive: { text: '禁用', color: 'default' },
    };
    const { text, color } = statusMap[status] || { text: status, color: 'default' };
    return <Tag color={color}>{text}</Tag>;
  };

  const getHealthStatusTag = (healthStatus: string) => {
    const statusMap: Record<string, { text: string; color: string }> = {
      healthy: { text: '健康', color: 'success' },
      unhealthy: { text: '异常', color: 'error' },
      unknown: { text: '未知', color: 'default' },
    };
    const { text, color } = statusMap[healthStatus] || { text: healthStatus, color: 'default' };
    return <Tag color={color}>{text}</Tag>;
  };

  const columns = [
    {
      title: '节点编码',
      dataIndex: 'nodeCode',
      key: 'nodeCode',
      width: 120,
    },
    {
      title: '区域编码',
      dataIndex: 'regionCode',
      key: 'regionCode',
      width: 100,
    },
    {
      title: '主机',
      dataIndex: 'host',
      key: 'host',
      width: 180,
      ellipsis: true,
    },
    {
      title: '端口',
      dataIndex: 'port',
      key: 'port',
      width: 80,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => getStatusTag(status),
    },
    {
      title: '健康状态',
      dataIndex: 'healthStatus',
      key: 'healthStatus',
      width: 100,
      render: (healthStatus: string) => getHealthStatusTag(healthStatus),
    },
    {
      title: '权重',
      dataIndex: 'weight',
      key: 'weight',
      width: 80,
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
  ];

  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>节点管理</h2>

      {/* 筛选区域 */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={16}>
          <Col xs={24} sm={12} lg={6}>
            <Input
              placeholder="区域ID"
              value={queryParams.regionId}
              onChange={(e) => setQueryParams(prev => ({ ...prev, regionId: e.target.value }))}
              onPressEnter={handleSearch}
              allowClear
            />
          </Col>
          <Col xs={24} sm={12} lg={6}>
            <Select
              placeholder="节点状态"
              style={{ width: '100%' }}
              value={queryParams.status}
              onChange={(value) => setQueryParams(prev => ({ ...prev, status: value }))}
              allowClear
            >
              <Select.Option value="active">启用</Select.Option>
              <Select.Option value="inactive">禁用</Select.Option>
            </Select>
          </Col>
          <Col xs={24} sm={12} lg={6}>
            <Select
              placeholder="健康状态"
              style={{ width: '100%' }}
              value={queryParams.healthStatus}
              onChange={(value) => setQueryParams(prev => ({ ...prev, healthStatus: value }))}
              allowClear
            >
              <Select.Option value="healthy">健康</Select.Option>
              <Select.Option value="unhealthy">异常</Select.Option>
              <Select.Option value="unknown">未知</Select.Option>
            </Select>
          </Col>
          <Col xs={24} sm={12} lg={6}>
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

      {/* 节点列表 */}
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
    </div>
  );
};

export default Nodes;
