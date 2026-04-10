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
import { getWithdrawals } from '../api';
import { formatAmount, formatDateTime } from '../utils/format';
import type {
  Withdrawal,
  WithdrawalListResponse,
  WithdrawalQueryParams,
  WithdrawalStatus,
} from '../types';

const initialData: WithdrawalListResponse = {
  items: [],
  page: 1,
  pageSize: 20,
  total: 0,
};

const initialQueryParams: WithdrawalQueryParams = {
  page: 1,
  pageSize: 20,
};

const withdrawalStatusOptions: Array<{
  label: string;
  value: WithdrawalStatus;
}> = [
  { label: '已提交', value: 'SUBMITTED' },
  { label: '审核中', value: 'UNDER_REVIEW' },
  { label: '已通过', value: 'APPROVED' },
  { label: '已拒绝', value: 'REJECTED' },
  { label: '广播中', value: 'BROADCASTING' },
  { label: '链上确认中', value: 'CHAIN_CONFIRMING' },
  { label: '已完成', value: 'COMPLETED' },
  { label: '失败', value: 'FAILED' },
  { label: '已取消', value: 'CANCELED' },
];

const getWithdrawalStatusTag = (status: string) => {
  const statusMap: Record<string, { text: string; color: string }> = {
    SUBMITTED: { text: '已提交', color: 'default' },
    UNDER_REVIEW: { text: '审核中', color: 'warning' },
    APPROVED: { text: '已通过', color: 'processing' },
    REJECTED: { text: '已拒绝', color: 'error' },
    BROADCASTING: { text: '广播中', color: 'processing' },
    CHAIN_CONFIRMING: { text: '链上确认中', color: 'processing' },
    COMPLETED: { text: '已完成', color: 'success' },
    FAILED: { text: '失败', color: 'error' },
    CANCELED: { text: '已取消', color: 'default' },
  };
  const { text, color } = statusMap[status] ?? {
    text: status,
    color: 'default',
  };
  return <Tag color={color}>{text}</Tag>;
};

const Withdrawals: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<WithdrawalListResponse>(initialData);
  const [queryParams, setQueryParams] =
    useState<WithdrawalQueryParams>(initialQueryParams);

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
    void fetchWithdrawals();
  }, [fetchWithdrawals]);

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
      title: '申请编号',
      dataIndex: 'requestNo',
      key: 'requestNo',
      width: 180,
    },
    {
      title: '完整邮箱',
      dataIndex: 'accountEmail',
      key: 'accountEmail',
      width: 220,
      ellipsis: true,
    },
    {
      title: '提现金额',
      key: 'amount',
      width: 140,
      render: (_: unknown, record: Withdrawal) =>
        formatAmount(record.amount, record.assetCode),
    },
    {
      title: '网络',
      dataIndex: 'networkCode',
      key: 'networkCode',
      width: 120,
    },
    {
      title: '提现地址',
      dataIndex: 'payoutAddress',
      key: 'payoutAddress',
      width: 220,
      ellipsis: true,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 140,
      render: (status: string) => getWithdrawalStatusTag(status),
    },
    {
      title: '申请时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
      render: (createdAt: string) => formatDateTime(createdAt),
    },
    {
      title: '审核时间',
      dataIndex: 'reviewedAt',
      key: 'reviewedAt',
      width: 180,
      render: (reviewedAt: string | null) => formatDateTime(reviewedAt),
    },
    {
      title: '完成时间',
      dataIndex: 'completedAt',
      key: 'completedAt',
      width: 180,
      render: (completedAt: string | null) => formatDateTime(completedAt),
    },
    {
      title: '交易哈希',
      dataIndex: 'txHash',
      key: 'txHash',
      width: 220,
      ellipsis: true,
      render: (txHash: string | null) => txHash || '-',
    },
  ];

  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>提现审核</h2>

      <Card style={{ marginBottom: 24 }}>
        <Row gutter={16}>
          <Col xs={24} sm={12} lg={8}>
            <Input
              placeholder="搜索完整邮箱"
              value={queryParams.accountEmail}
              onChange={(event) =>
                setQueryParams((prev) => ({
                  ...prev,
                  accountEmail: event.target.value || undefined,
                }))
              }
              onPressEnter={handleSearch}
              allowClear
            />
          </Col>
          <Col xs={24} sm={12} lg={8}>
            <Select
              placeholder="提现状态"
              style={{ width: '100%' }}
              value={queryParams.status}
              onChange={(value) =>
                setQueryParams((prev) => ({ ...prev, status: value }))
              }
              options={withdrawalStatusOptions}
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
          rowKey="requestNo"
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
          scroll={{ x: 1700 }}
        />
      </Card>
    </div>
  );
};

export default Withdrawals;
