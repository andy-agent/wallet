import React, { useEffect, useState, useCallback } from 'react';
import {
  Table,
  Card,
  Input,
  Select,
  Button,
  Space,
  Tag,
  Row,
  Col,
} from 'antd';
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons';
import { getWithdrawals } from '../api';
import { formatDateTime } from '../utils/format';
import type { WithdrawalListResponse, WithdrawalQueryParams } from '../types';

const Withdrawals: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<WithdrawalListResponse>({
    items: [],
    total: 0,
    page: 1,
    pageSize: 20,
  });
  const [queryParams, setQueryParams] = useState<WithdrawalQueryParams>({
    page: 1,
    pageSize: 20,
  });

  const fetchWithdrawals = useCallback(async () => {
    setLoading(true);
    try {
      const result = await getWithdrawals(queryParams);
      setData(result);
    } catch (error) {
      console.error('获取提现列表失败:', error);
    } finally {
      setLoading(false);
    }
  }, [queryParams]);

  useEffect(() => {
    fetchWithdrawals();
  }, [fetchWithdrawals]);

  const handleSearch = () => {
    setQueryParams(prev => ({ ...prev, page: 1 }));
    fetchWithdrawals();
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
      pending: { text: '待审核', color: 'warning' },
      approved: { text: '已通过', color: 'processing' },
      rejected: { text: '已拒绝', color: 'error' },
      completed: { text: '已完成', color: 'success' },
      failed: { text: '失败', color: 'error' },
    };
    const { text, color } = statusMap[status] || { text: status, color: 'default' };
    return <Tag color={color}>{text}</Tag>;
  };

  const columns = [
    {
      title: '申请编号',
      dataIndex: 'requestNo',
      key: 'requestNo',
      width: 180,
    },
    {
      title: '用户邮箱',
      dataIndex: 'accountEmail',
      key: 'accountEmail',
      width: 200,
      ellipsis: true,
    },
    {
      title: '提现金额',
      dataIndex: 'amountUsdt',
      key: 'amountUsdt',
      width: 120,
      render: (amount: number) => `${amount} USDT`,
    },
    {
      title: '网络',
      dataIndex: 'network',
      key: 'network',
      width: 120,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => getStatusTag(status),
    },
    {
      title: '交易哈希',
      dataIndex: 'txHash',
      key: 'txHash',
      width: 200,
      ellipsis: true,
      render: (txHash: string) => txHash || '-',
    },
    {
      title: '申请时间',
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
      <h2 style={{ marginBottom: 24 }}>提现审核</h2>

      {/* 筛选区域 */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={16}>
          <Col xs={24} sm={12} lg={8}>
            <Input
              placeholder="搜索用户邮箱"
              value={queryParams.accountEmail}
              onChange={(e) => setQueryParams(prev => ({ ...prev, accountEmail: e.target.value }))}
              onPressEnter={handleSearch}
              allowClear
            />
          </Col>
          <Col xs={24} sm={12} lg={8}>
            <Select
              placeholder="提现状态"
              style={{ width: '100%' }}
              value={queryParams.status}
              onChange={(value) => setQueryParams(prev => ({ ...prev, status: value }))}
              allowClear
            >
              <Select.Option value="pending">待审核</Select.Option>
              <Select.Option value="approved">已通过</Select.Option>
              <Select.Option value="rejected">已拒绝</Select.Option>
              <Select.Option value="completed">已完成</Select.Option>
              <Select.Option value="failed">失败</Select.Option>
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

      {/* 提现列表 */}
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
          scroll={{ x: 1200 }}
        />
      </Card>
    </div>
  );
};

export default Withdrawals;
