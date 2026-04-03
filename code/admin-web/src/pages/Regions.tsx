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
} from 'antd';
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons';
import { getRegions } from '../api';
import { formatDateTime } from '../utils/format';
import type { RegionListResponse, RegionQueryParams } from '../types';

const Regions: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<RegionListResponse>({
    items: [],
    total: 0,
    page: 1,
    pageSize: 20,
  });
  const [queryParams, setQueryParams] = useState<RegionQueryParams>({
    page: 1,
    pageSize: 20,
  });

  const fetchRegions = useCallback(async () => {
    setLoading(true);
    try {
      const result = await getRegions(queryParams);
      setData(result);
    } catch (error) {
      console.error('获取区域列表失败:', error);
    } finally {
      setLoading(false);
    }
  }, [queryParams]);

  useEffect(() => {
    fetchRegions();
  }, [fetchRegions]);

  const handleSearch = () => {
    setQueryParams(prev => ({ ...prev, page: 1 }));
    fetchRegions();
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

  const getTierTag = (tier: string) => {
    const tierMap: Record<string, { text: string; color: string }> = {
      standard: { text: '标准区', color: 'blue' },
      premium: { text: '高级区', color: 'purple' },
    };
    const { text, color } = tierMap[tier] || { text: tier, color: 'default' };
    return <Tag color={color}>{text}</Tag>;
  };

  const getStatusTag = (status: string) => {
    const statusMap: Record<string, { text: string; color: string }> = {
      active: { text: '启用', color: 'success' },
      inactive: { text: '禁用', color: 'default' },
      maintenance: { text: '维护中', color: 'warning' },
    };
    const { text, color } = statusMap[status] || { text: status, color: 'default' };
    return <Tag color={color}>{text}</Tag>;
  };

  const columns = [
    {
      title: '区域编码',
      dataIndex: 'regionCode',
      key: 'regionCode',
      width: 120,
    },
    {
      title: '显示名称',
      dataIndex: 'displayName',
      key: 'displayName',
      width: 150,
    },
    {
      title: '等级',
      dataIndex: 'tier',
      key: 'tier',
      width: 100,
      render: (tier: string) => getTierTag(tier),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => getStatusTag(status),
    },
    {
      title: '排序',
      dataIndex: 'sortOrder',
      key: 'sortOrder',
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
      <h2 style={{ marginBottom: 24 }}>区域管理</h2>

      {/* 筛选区域 */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={16}>
          <Col xs={24} sm={12} lg={8}>
            <Select
              placeholder="区域等级"
              style={{ width: '100%' }}
              value={queryParams.tier}
              onChange={(value) => setQueryParams(prev => ({ ...prev, tier: value }))}
              allowClear
            >
              <Select.Option value="standard">标准区</Select.Option>
              <Select.Option value="premium">高级区</Select.Option>
            </Select>
          </Col>
          <Col xs={24} sm={12} lg={8}>
            <Select
              placeholder="区域状态"
              style={{ width: '100%' }}
              value={queryParams.status}
              onChange={(value) => setQueryParams(prev => ({ ...prev, status: value }))}
              allowClear
            >
              <Select.Option value="active">启用</Select.Option>
              <Select.Option value="inactive">禁用</Select.Option>
              <Select.Option value="maintenance">维护中</Select.Option>
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

      {/* 区域列表 */}
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
          scroll={{ x: 800 }}
        />
      </Card>
    </div>
  );
};

export default Regions;
