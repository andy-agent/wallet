import React, { useEffect, useState, useCallback } from 'react';
import {
  Table,
  Card,
  Input,
  Select,
  Button,
  Space,
  Tag,
  Modal,
  Descriptions,
  Row,
  Col,
} from 'antd';
import { SearchOutlined, ReloadOutlined, EyeOutlined } from '@ant-design/icons';
import { getAccounts, getAccountDetail } from '../api';
import { formatDateTime } from '../utils/format';
import type { Account, AccountListResponse, AccountQueryParams, AccountDetail } from '../types';

const Accounts: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<AccountListResponse>({
    items: [],
    total: 0,
    page: 1,
    pageSize: 20,
  });
  const [queryParams, setQueryParams] = useState<AccountQueryParams>({
    page: 1,
    pageSize: 20,
  });
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [selectedAccount, setSelectedAccount] = useState<AccountDetail | null>(null);
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
    fetchAccounts();
  }, [fetchAccounts]);

  const handleSearch = () => {
    setQueryParams(prev => ({ ...prev, page: 1 }));
    fetchAccounts();
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

  const getStatusTag = (status: string) => {
    const statusMap: Record<string, { text: string; color: string }> = {
      active: { text: '正常', color: 'success' },
      frozen: { text: '冻结', color: 'error' },
      pending: { text: '待激活', color: 'warning' },
    };
    const { text, color } = statusMap[status] || { text: status, color: 'default' };
    return <Tag color={color}>{text}</Tag>;
  };

  const columns = [
    {
      title: '账号编号',
      dataIndex: 'accountId',
      key: 'accountId',
      width: 150,
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email',
      width: 200,
      ellipsis: true,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => getStatusTag(status),
    },
    {
      title: '当前套餐',
      dataIndex: 'planCode',
      key: 'planCode',
      width: 120,
      render: (planCode: string) => planCode || '-',
    },
    {
      title: '到期时间',
      dataIndex: 'expireAt',
      key: 'expireAt',
      width: 170,
      render: (expireAt: string) => expireAt ? formatDateTime(expireAt) : '-',
    },
    {
      title: '最后登录',
      dataIndex: 'lastLoginAt',
      key: 'lastLoginAt',
      width: 170,
      render: (lastLoginAt: string) => lastLoginAt ? formatDateTime(lastLoginAt) : '-',
    },
    {
      title: '注册时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 170,
      render: (createdAt: string) => formatDateTime(createdAt),
    },
    {
      title: '操作',
      key: 'action',
      width: 120,
      fixed: 'right' as const,
      render: (_: any, record: Account) => (
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

      {/* 筛选区域 */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={16}>
          <Col xs={24} sm={12} lg={8}>
            <Input
              placeholder="搜索邮箱"
              value={queryParams.email}
              onChange={(e) => setQueryParams(prev => ({ ...prev, email: e.target.value }))}
              onPressEnter={handleSearch}
              allowClear
            />
          </Col>
          <Col xs={24} sm={12} lg={8}>
            <Select
              placeholder="账号状态"
              style={{ width: '100%' }}
              value={queryParams.status}
              onChange={(value) => setQueryParams(prev => ({ ...prev, status: value }))}
              allowClear
            >
              <Select.Option value="active">正常</Select.Option>
              <Select.Option value="frozen">冻结</Select.Option>
              <Select.Option value="pending">待激活</Select.Option>
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

      {/* 账号列表 */}
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
          scroll={{ x: 1100 }}
        />
      </Card>

      {/* 账号详情弹窗 */}
      <Modal
        title="账号详情"
        open={detailModalVisible}
        onCancel={() => setDetailModalVisible(false)}
        footer={null}
        width={700}
      >
        {detailLoading ? (
          <div style={{ textAlign: 'center', padding: 40 }}>加载中...</div>
        ) : selectedAccount ? (
          <Descriptions bordered column={2}>
            <Descriptions.Item label="账号编号" span={2}>
              {selectedAccount.accountId}
            </Descriptions.Item>
            <Descriptions.Item label="邮箱" span={2}>
              {selectedAccount.email}
            </Descriptions.Item>
            <Descriptions.Item label="状态">
              {getStatusTag(selectedAccount.status)}
            </Descriptions.Item>
            <Descriptions.Item label="注册时间">
              {formatDateTime(selectedAccount.createdAt)}
            </Descriptions.Item>
            {selectedAccount.subscription && (
              <>
                <Descriptions.Item label="套餐名称">
                  {selectedAccount.subscription.planName}
                </Descriptions.Item>
                <Descriptions.Item label="订阅状态">
                  {selectedAccount.subscription.status}
                </Descriptions.Item>
                <Descriptions.Item label="到期时间" span={2}>
                  {formatDateTime(selectedAccount.subscription.expiresAt)}
                </Descriptions.Item>
              </>
            )}
            {selectedAccount.referral && (
              <>
                <Descriptions.Item label="邀请码">
                  {selectedAccount.referral.code}
                </Descriptions.Item>
                <Descriptions.Item label="邀请人数">
                  {selectedAccount.referral.inviteCount}
                </Descriptions.Item>
              </>
            )}
            {selectedAccount.commission && (
              <>
                <Descriptions.Item label="可提佣金">
                  {selectedAccount.commission.availableAmount} USDT
                </Descriptions.Item>
                <Descriptions.Item label="冻结佣金">
                  {selectedAccount.commission.frozenAmount} USDT
                </Descriptions.Item>
              </>
            )}
            <Descriptions.Item label="最后登录" span={2}>
              {selectedAccount.lastLoginAt ? formatDateTime(selectedAccount.lastLoginAt) : '-'}
            </Descriptions.Item>
          </Descriptions>
        ) : null}
      </Modal>
    </div>
  );
};

export default Accounts;
