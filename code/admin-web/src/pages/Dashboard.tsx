import React, { useEffect, useState } from 'react';
import {
  CheckCircleOutlined,
  ClockCircleOutlined,
  ShoppingCartOutlined,
  TeamOutlined,
  UserOutlined,
  WalletOutlined,
} from '@ant-design/icons';
import { Card, Col, Row, Spin, Statistic } from 'antd';
import { getDashboardStats } from '../api';
import type { DashboardSummary } from '../types';

const Dashboard: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [summary, setSummary] = useState<DashboardSummary | null>(null);

  useEffect(() => {
    const fetchSummary = async () => {
      setLoading(true);
      try {
        const result = await getDashboardStats();
        setSummary(result);
      } catch (error) {
        console.error('获取仪表板汇总失败:', error);
      } finally {
        setLoading(false);
      }
    };

    void fetchSummary();
  }, []);

  const cards = [
    {
      key: 'activeAccounts',
      title: '活跃账号',
      value: summary?.activeAccounts ?? 0,
      color: '#1677ff',
      icon: <UserOutlined />,
    },
    {
      key: 'activeSubscriptions',
      title: '活跃订阅',
      value: summary?.activeSubscriptions ?? 0,
      color: '#52c41a',
      icon: <TeamOutlined />,
    },
    {
      key: 'awaitingOrders',
      title: '待支付订单',
      value: summary?.awaitingOrders ?? 0,
      color: '#faad14',
      icon: <ClockCircleOutlined />,
    },
    {
      key: 'reviewOrders',
      title: '待复核订单',
      value: summary?.reviewOrders ?? 0,
      color: '#722ed1',
      icon: <ShoppingCartOutlined />,
    },
    {
      key: 'pendingWithdrawals',
      title: '待处理提现',
      value: summary?.pendingWithdrawals ?? 0,
      color: '#eb2f96',
      icon: <WalletOutlined />,
    },
    {
      key: 'todayPaidOrders',
      title: '今日已支付订单',
      value: summary?.todayPaidOrders ?? 0,
      color: '#13c2c2',
      icon: <CheckCircleOutlined />,
    },
  ];

  return (
    <Spin spinning={loading}>
      <div>
        <h2 style={{ marginBottom: 24 }}>数据概览</h2>
        <Row gutter={[16, 16]}>
          {cards.map((card) => (
            <Col key={card.key} xs={24} sm={12} lg={8}>
              <Card>
                <Statistic
                  title={card.title}
                  value={card.value}
                  prefix={card.icon}
                  valueStyle={{ color: card.color }}
                />
              </Card>
            </Col>
          ))}
        </Row>
      </div>
    </Spin>
  );
};

export default Dashboard;
