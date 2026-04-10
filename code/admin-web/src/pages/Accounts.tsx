import React, { useCallback, useEffect, useState } from 'react';
import {
  EyeOutlined,
  ReloadOutlined,
  SearchOutlined,
} from '@ant-design/icons';
import {
  Button,
  Card,
  Col,
  Descriptions,
  Input,
  Modal,
  Row,
  Select,
  Space,
  Table,
  Tag,
} from 'antd';
import { getAccountDetail, getAccounts } from '../api';
import { formatDateTime } from '../utils/format';
import type {
  Account,
  AccountDetail,
  AccountListResponse,
  AccountQueryParams,
  AccountStatus,
  SubscriptionStatus,
} from '../types';

const initialData: AccountListResponse = {
  items: [],
  page: 1,
  pageSize: 20,
  total: 0,
};

const initialQueryParams: AccountQueryParams = {
  page: 1,
  pageSize: 20,
};

const accountStatusOptions: Array<{ label: string; value: AccountStatus }> = [
  { label: '正常', value: 'ACTIVE' },
  { label: '冻结', value: 'FROZEN' },
  { label: '关闭', value: 'CLOSED' },
];

const getAccountStatusTag = (status: string) => {
  const statusMap: Record<string, { text: string; color: string }> = {
    ACTIVE: { text: '正常', color: 'success' },
    FROZEN: { text: '冻结', color: 'error' },
    CLOSED: { text: '关闭', color: 'default' },
  };
  const { text, color } = statusMap[status] ?? {
    text: status,
    color: 'default',
  };
  return <Tag color={color}>{text}</Tag>;
};

const getSubscriptionStatusTag = (status: SubscriptionStatus) => {
  const statusMap: Record<SubscriptionStatus, { text: string; color: string }> = {
    PENDING_ACTIVATION: { text: '待激活', color: 'warning' },
    ACTIVE: { text: '生效中', color: 'success' },
    EXPIRED: { text: '已过期', color: 'default' },
    SUSPENDED: { text: '已暂停', color: 'warning' },
    CANCELED: { text: '已取消', color: 'default' },
    NONE: { text: '无订阅', color: 'default' },
  };
  const { text, color } = statusMap[status];
  return <Tag color={color}>{text}</Tag>;
};

const Accounts: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<AccountListResponse>(initialData);
  const [queryParams, setQueryParams] =
    useState<AccountQueryParams>(initialQueryParams);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [selectedAccount, setSelectedAccount] = useState<AccountDetail | null>(
    null,
  );
  const [detailLoading, setDetailLoading] = useState(false);

  const fetchAccounts = useCallback(async () => {
    setLoading(true);
    try {
      const result = await getAccounts(queryParams);
      setData(result);
    } catch (error) {
      console.error('获取账号列表失败:', error);
    } finally {
      setLoading(false);
    }
  }, [queryParams]);

  useEffect(() => {
    void fetchAccounts();
  }, [fetchAccounts]);

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

  const showDetail = async (accountId: string) => {
    setDetailLoading(true);
    try {
      const account = await getAccountDetail(accountId);
      setSelectedAccount(account);
      setDetailModalVisible(true);
    } catch (error) {
      console.error('获取账号详情失败:', error);
    } finally {
      setDetailLoading(false);
    }
  };

  const columns = [
    {
      title: '账号编号',
      dataIndex: 'accountId',
      key: 'accountId',
      width: 180,
    },
    {
      title: '完整邮箱',
      dataIndex: 'email',
      key: 'email',
      width: 240,
      ellipsis: true,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 110,
      render: (status: string) => getAccountStatusTag(status),
    },
    {
      title: '邀请码',
      dataIndex: 'referralCode',
      key: 'referralCode',
      width: 140,
    },
    {
      title: '当前套餐',
      key: 'subscriptionPlanCode',
      width: 150,
      render: (_: unknown, record: Account) =>
        record.subscription?.planCode || '-',
    },
    {
      title: '订阅状态',
      key: 'subscriptionStatus',
      width: 130,
      render: (_: unknown, record: Account) =>
        record.subscription
          ? getSubscriptionStatusTag(record.subscription.status)
          : getSubscriptionStatusTag('NONE'),
    },
    {
      title: '到期时间',
      key: 'expireAt',
      width: 180,
      render: (_: unknown, record: Account) =>
        formatDateTime(record.subscription?.expireAt),
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      fixed: 'right' as const,
      render: (_: unknown, record: Account) => (
        <Button
          type="link"
          size="small"
          icon={<EyeOutlined />}
          onClick={() => showDetail(record.accountId)}
        >
          详情
        </Button>
      ),
    },
  ];

  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>用户管理</h2>

      <Card style={{ marginBottom: 24 }}>
        <Row gutter={16}>
          <Col xs={24} sm={12} lg={8}>
            <Input
              placeholder="搜索完整邮箱"
              value={queryParams.email}
              onChange={(event) =>
                setQueryParams((prev) => ({
                  ...prev,
                  email: event.target.value || undefined,
                }))
              }
              onPressEnter={handleSearch}
              allowClear
            />
          </Col>
          <Col xs={24} sm={12} lg={8}>
            <Select
              placeholder="账号状态"
              style={{ width: '100%' }}
              value={queryParams.status}
              onChange={(value) =>
                setQueryParams((prev) => ({ ...prev, status: value }))
              }
              options={accountStatusOptions}
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
          rowKey="accountId"
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

      <Modal
        title="账号详情"
        open={detailModalVisible}
        onCancel={() => setDetailModalVisible(false)}
        footer={null}
        width={760}
      >
        {detailLoading ? (
          <div style={{ textAlign: 'center', padding: 40 }}>加载中...</div>
        ) : selectedAccount ? (
          <Descriptions bordered column={2}>
            <Descriptions.Item label="账号编号" span={2}>
              {selectedAccount.accountId}
            </Descriptions.Item>
            <Descriptions.Item label="完整邮箱" span={2}>
              {selectedAccount.email}
            </Descriptions.Item>
            <Descriptions.Item label="状态">
              {getAccountStatusTag(selectedAccount.status)}
            </Descriptions.Item>
            <Descriptions.Item label="邀请码">
              {selectedAccount.referralCode}
            </Descriptions.Item>
            <Descriptions.Item label="订阅套餐">
              {selectedAccount.subscription?.planCode || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="订阅状态">
              {selectedAccount.subscription
                ? getSubscriptionStatusTag(selectedAccount.subscription.status)
                : getSubscriptionStatusTag('NONE')}
            </Descriptions.Item>
            <Descriptions.Item label="开始时间">
              {formatDateTime(selectedAccount.subscription?.startedAt)}
            </Descriptions.Item>
            <Descriptions.Item label="到期时间">
              {formatDateTime(selectedAccount.subscription?.expireAt)}
            </Descriptions.Item>
            <Descriptions.Item label="剩余天数">
              {selectedAccount.subscription?.daysRemaining ?? '-'}
            </Descriptions.Item>
            <Descriptions.Item label="最大会话数">
              {selectedAccount.subscription?.maxActiveSessions ?? '-'}
            </Descriptions.Item>
          </Descriptions>
        ) : null}
      </Modal>
    </div>
  );
};

export default Accounts;
