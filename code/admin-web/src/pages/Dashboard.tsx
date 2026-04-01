import React, { useEffect, useState } from 'react';
import { Card, Row, Col, Statistic, Spin } from 'antd';
import {
  ShoppingCartOutlined,
  DollarOutlined,
  ClockCircleOutlined,
  CheckCircleOutlined,
} from '@ant-design/icons';
import { Column, Pie } from '@ant-design/charts';
import { getDashboardStats } from '../api';
import { formatAmount } from '../utils/format';
import type { DashboardStats } from '../types';

const Dashboard: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [stats, setStats] = useState<DashboardStats | null>(null);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    setLoading(true);
    try {
      const data = await getDashboardStats();
      setStats(data);
    } catch (error) {
      console.error('获取统计数据失败:', error);
    } finally {
      setLoading(false);
    }
  };

  // 状态分布图表配置
  const pieConfig = {
    data: stats?.status_distribution?.map(item => ({
      type: item.status,
      value: item.count,
    })) || [],
    angleField: 'value',
    colorField: 'type',
    radius: 0.8,
    label: {
      type: 'outer',
      content: '{name} {percentage}',
    },
    interactions: [{ type: 'element-active' }],
  };

  // 趋势图表配置
  const columnConfig = {
    data: stats?.recent_trend?.map(item => ({
      date: item.date,
      orders: item.orders,
    })) || [],
    xField: 'date',
    yField: 'orders',
    label: {
      position: 'top',
    },
    columnStyle: {
      fill: '#1890ff',
    },
  };

  return (
    <Spin spinning={loading}>
      <div>
        <h2 style={{ marginBottom: 24 }}>数据概览</h2>
        
        {/* 统计卡片 */}
        <Row gutter={16} style={{ marginBottom: 24 }}>
          <Col xs={24} sm={12} lg={6}>
            <Card>
              <Statistic
                title="今日订单"
                value={stats?.today_orders || 0}
                prefix={<ShoppingCartOutlined />}
                valueStyle={{ color: '#1890ff' }}
              />
            </Card>
          </Col>
          <Col xs={24} sm={12} lg={6}>
            <Card>
              <Statistic
                title="今日收入"
                value={formatAmount(stats?.today_revenue || 0)}
                prefix={<DollarOutlined />}
                valueStyle={{ color: '#52c41a' }}
              />
            </Card>
          </Col>
          <Col xs={24} sm={12} lg={6}>
            <Card>
              <Statistic
                title="待处理订单"
                value={stats?.pending_orders || 0}
                prefix={<ClockCircleOutlined />}
                valueStyle={{ color: '#faad14' }}
              />
            </Card>
          </Col>
          <Col xs={24} sm={12} lg={6}>
            <Card>
              <Statistic
                title="完成率"
                value={stats?.today_orders 
                  ? Math.round(((stats.today_orders - (stats.pending_orders || 0)) / stats.today_orders) * 100) 
                  : 0}
                suffix="%"
                prefix={<CheckCircleOutlined />}
                valueStyle={{ color: '#722ed1' }}
              />
            </Card>
          </Col>
        </Row>

        {/* 图表区域 */}
        <Row gutter={16}>
          <Col xs={24} lg={12}>
            <Card title="订单状态分布" style={{ marginBottom: 16 }}>
              <div style={{ height: 300 }}>
                <Pie {...pieConfig} />
              </div>
            </Card>
          </Col>
          <Col xs={24} lg={12}>
            <Card title="近期交易趋势" style={{ marginBottom: 16 }}>
              <div style={{ height: 300 }}>
                <Column {...columnConfig} />
              </div>
            </Card>
          </Col>
        </Row>
      </div>
    </Spin>
  );
};

export default Dashboard;
