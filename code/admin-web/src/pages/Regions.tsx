import React, { useCallback, useEffect, useState } from 'react';
import {
  ReloadOutlined,
  SearchOutlined,
} from '@ant-design/icons';
import {
  Button,
  Card,
  Col,
  Row,
  Select,
  Space,
  Table,
  Tag,
} from 'antd';
import { getRegions } from '../api';
import { formatDateTime } from '../utils/format';
import type {
  RegionListResponse,
  RegionQueryParams,
  RegionStatus,
  RegionTier,
} from '../types';

const initialData: RegionListResponse = {
  items: [],
  page: 1,
  pageSize: 20,
  total: 0,
};

const initialQueryParams: RegionQueryParams = {
  page: 1,
  pageSize: 20,
};

const tierOptions: Array<{ label: string; value: RegionTier }> = [
  { label: '基础区', value: 'BASIC' },
  { label: '高级区', value: 'ADVANCED' },
];

const statusOptions: Array<{ label: string; value: RegionStatus }> = [
  { label: '启用', value: 'ACTIVE' },
  { label: '维护中', value: 'MAINTENANCE' },
  { label: '禁用', value: 'INACTIVE' },
];

const getTierTag = (tier: string) => {
  const tierMap: Record<string, { text: string; color: string }> = {
    BASIC: { text: '基础区', color: 'blue' },
    ADVANCED: { text: '高级区', color: 'purple' },
  };
  const { text, color } = tierMap[tier] ?? {
    text: tier,
    color: 'default',
  };
  return <Tag color={color}>{text}</Tag>;
};

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

const Regions: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<RegionListResponse>(initialData);
  const [queryParams, setQueryParams] =
    useState<RegionQueryParams>(initialQueryParams);

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
    void fetchRegions();
  }, [fetchRegions]);

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
      title: '区域编码',
      dataIndex: 'regionCode',
      key: 'regionCode',
      width: 150,
    },
    {
      title: '显示名称',
      dataIndex: 'displayName',
      key: 'displayName',
      width: 180,
    },
    {
      title: '等级',
      dataIndex: 'tier',
      key: 'tier',
      width: 110,
      render: (tier: string) => getTierTag(tier),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (status: string) => getStatusTag(status),
    },
    {
      title: '备注',
      dataIndex: 'remark',
      key: 'remark',
      width: 180,
      ellipsis: true,
      render: (remark: string | null) => remark || '-',
    },
    {
      title: '排序',
      dataIndex: 'sortOrder',
      key: 'sortOrder',
      width: 90,
    },
    {
      title: '更新时间',
      dataIndex: 'updatedAt',
      key: 'updatedAt',
      width: 180,
      render: (updatedAt: string) => formatDateTime(updatedAt),
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
      render: (createdAt: string) => formatDateTime(createdAt),
    },
  ];

  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>区域管理</h2>

      <Card style={{ marginBottom: 24 }}>
        <Row gutter={16}>
          <Col xs={24} sm={12} lg={8}>
            <Select
              placeholder="区域等级"
              style={{ width: '100%' }}
              value={queryParams.tier}
              onChange={(value) =>
                setQueryParams((prev) => ({ ...prev, tier: value }))
              }
              options={tierOptions}
              allowClear
            />
          </Col>
          <Col xs={24} sm={12} lg={8}>
            <Select
              placeholder="区域状态"
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
          rowKey="regionId"
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
          scroll={{ x: 1300 }}
        />
      </Card>
    </div>
  );
};

export default Regions;
