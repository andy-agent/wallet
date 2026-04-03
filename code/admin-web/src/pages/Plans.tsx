import React, { useEffect, useState, useCallback } from 'react';
import {
  Table,
  Card,
  Tag,
} from 'antd';
import { getPlans } from '../api';
import { formatAmount } from '../utils/format';
import type { Plan } from '../types';

const Plans: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [plans, setPlans] = useState<Plan[]>([]);

  const fetchPlans = useCallback(async () => {
    setLoading(true);
    try {
      const data = await getPlans();
      setPlans(data);
    } catch (error) {
      console.error('获取套餐列表失败:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchPlans();
  }, [fetchPlans]);

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 60,
    },
    {
      title: '套餐名称',
      dataIndex: 'name',
      key: 'name',
      width: 150,
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
    {
      title: '价格',
      dataIndex: 'price',
      key: 'price',
      width: 120,
      render: (price: number, record: Plan) => (
        <span>
          {formatAmount(price, record.currency)}
          {record.original_price && record.original_price > price && (
            <span style={{ textDecoration: 'line-through', color: '#999', marginLeft: 8 }}>
              {formatAmount(record.original_price, record.currency)}
            </span>
          )}
        </span>
      ),
    },
    {
      title: '有效期',
      dataIndex: 'duration_days',
      key: 'duration_days',
      width: 100,
      render: (days: number) => `${days} 天`,
    },
    {
      title: '状态',
      dataIndex: 'is_active',
      key: 'is_active',
      width: 80,
      render: (isActive: boolean) => (
        <Tag color={isActive ? 'success' : 'default'}>
          {isActive ? '启用' : '禁用'}
        </Tag>
      ),
    },
    {
      title: '排序',
      dataIndex: 'sort_order',
      key: 'sort_order',
      width: 80,
    },
  ];

  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>套餐管理</h2>

      <Card>
        {/* 
          NOTE: 创建/编辑功能暂时禁用
          后端目前仅支持 GET /admin/v1/plans
          写操作（创建、编辑、发布、禁用）待后端实现后再开启
        */}
        <Table
          columns={columns}
          dataSource={plans}
          rowKey="id"
          loading={loading}
          pagination={false}
        />
      </Card>
    </div>
  );
};

export default Plans;
