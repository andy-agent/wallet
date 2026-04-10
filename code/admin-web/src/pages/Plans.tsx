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
import { getPlans } from '../api';
import { formatAmount } from '../utils/format';
import type { PlanListResponse, PlanQueryParams } from '../types';

const initialData: PlanListResponse = {
  items: [],
  page: 1,
  pageSize: 0,
  total: 0,
};

const initialQueryParams: PlanQueryParams = {};

const Plans: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<PlanListResponse>(initialData);
  const [queryParams, setQueryParams] =
    useState<PlanQueryParams>(initialQueryParams);

  const fetchPlans = useCallback(async () => {
    setLoading(true);
    try {
      const result = await getPlans(queryParams);
      setData(result);
    } catch (error) {
      console.error('获取套餐列表失败:', error);
    } finally {
      setLoading(false);
    }
  }, [queryParams]);

  useEffect(() => {
    void fetchPlans();
  }, [fetchPlans]);

  const handleSearch = () => {
    setQueryParams((prev) => ({ ...prev }));
  };

  const handleReset = () => {
    setQueryParams(initialQueryParams);
  };

  const columns = [
    {
      title: '套餐编码',
      dataIndex: 'planCode',
      key: 'planCode',
      width: 160,
    },
    {
      title: '套餐名称',
      dataIndex: 'name',
      key: 'name',
      width: 180,
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      width: 220,
      ellipsis: true,
    },
    {
      title: '价格',
      dataIndex: 'priceUsd',
      key: 'priceUsd',
      width: 120,
      render: (priceUsd: string) => formatAmount(priceUsd, 'USD'),
    },
    {
      title: '周期',
      dataIndex: 'billingCycleMonths',
      key: 'billingCycleMonths',
      width: 100,
      render: (billingCycleMonths: number) => `${billingCycleMonths} 个月`,
    },
    {
      title: '最大会话数',
      dataIndex: 'maxActiveSessions',
      key: 'maxActiveSessions',
      width: 120,
    },
    {
      title: '区域策略',
      dataIndex: 'regionAccessPolicy',
      key: 'regionAccessPolicy',
      width: 140,
    },
    {
      title: '高级区域',
      dataIndex: 'includesAdvancedRegions',
      key: 'includesAdvancedRegions',
      width: 120,
      render: (includesAdvancedRegions: boolean) => (
        <Tag color={includesAdvancedRegions ? 'success' : 'default'}>
          {includesAdvancedRegions ? '包含' : '不包含'}
        </Tag>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 110,
      render: (status: string) => (
        <Tag color={status === 'ACTIVE' ? 'success' : 'default'}>{status}</Tag>
      ),
    },
    {
      title: '排序',
      dataIndex: 'displayOrder',
      key: 'displayOrder',
      width: 90,
    },
  ];

  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>套餐管理</h2>

      <Card style={{ marginBottom: 24 }}>
        <Row gutter={16}>
          <Col xs={24} sm={12} lg={8}>
            <Select
              placeholder="套餐状态"
              style={{ width: '100%' }}
              value={queryParams.status}
              onChange={(value) => setQueryParams({ status: value })}
              options={[
                { label: '启用', value: 'ACTIVE' },
                { label: '停用', value: 'INACTIVE' },
              ]}
              allowClear
            />
          </Col>
          <Col xs={24} sm={12} lg={16}>
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
          rowKey="planId"
          loading={loading}
          pagination={false}
          scroll={{ x: 1450 }}
        />
      </Card>
    </div>
  );
};

export default Plans;
