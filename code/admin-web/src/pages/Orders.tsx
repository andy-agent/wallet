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
import { getOrderDetail, getOrders } from '../api';
import { formatAmount, formatDateTime, getOrderStatusText } from '../utils/format';
import type {
  Order,
  OrderListResponse,
  OrderQueryParams,
  OrderStatus,
} from '../types';

const initialData: OrderListResponse = {
  items: [],
  page: 1,
  pageSize: 20,
  total: 0,
};

const initialQueryParams: OrderQueryParams = {
  page: 1,
  pageSize: 20,
};

const orderStatusOptions: Array<{ label: string; value: OrderStatus }> = [
  { label: '待支付', value: 'AWAITING_PAYMENT' },
  { label: '已检测支付', value: 'PAYMENT_DETECTED' },
  { label: '链上确认中', value: 'CONFIRMING' },
  { label: '已支付', value: 'PAID' },
  { label: '开通中', value: 'PROVISIONING' },
  { label: '已完成', value: 'COMPLETED' },
  { label: '少付待复核', value: 'UNDERPAID_REVIEW' },
  { label: '多付待复核', value: 'OVERPAID_REVIEW' },
  { label: '失败', value: 'FAILED' },
  { label: '已过期', value: 'EXPIRED' },
  { label: '已取消', value: 'CANCELED' },
];

const Orders: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<OrderListResponse>(initialData);
  const [queryParams, setQueryParams] =
    useState<OrderQueryParams>(initialQueryParams);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);

  const fetchOrders = useCallback(async () => {
    setLoading(true);
    try {
      const result = await getOrders(queryParams);
      setData(result);
    } catch (error) {
      console.error('获取订单列表失败:', error);
    } finally {
      setLoading(false);
    }
  }, [queryParams]);

  useEffect(() => {
    void fetchOrders();
  }, [fetchOrders]);

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

  const showDetail = async (orderNo: string) => {
    try {
      const order = await getOrderDetail(orderNo);
      setSelectedOrder(order);
      setDetailModalVisible(true);
    } catch (error) {
      console.error('获取订单详情失败:', error);
    }
  };

  const columns = [
    {
      title: '订单号',
      dataIndex: 'orderNo',
      key: 'orderNo',
      width: 220,
    },
    {
      title: '账号邮箱',
      dataIndex: 'accountEmail',
      key: 'accountEmail',
      width: 220,
      ellipsis: true,
    },
    {
      title: '套餐',
      dataIndex: 'planName',
      key: 'planName',
      width: 180,
    },
    {
      title: '支付金额',
      key: 'payableAmount',
      width: 150,
      render: (_: unknown, record: Order) =>
        formatAmount(record.payableAmount, record.quoteAssetCode),
    },
    {
      title: '计价金额',
      dataIndex: 'quoteUsdAmount',
      key: 'quoteUsdAmount',
      width: 130,
      render: (quoteUsdAmount: string) => formatAmount(quoteUsdAmount, 'USD'),
    },
    {
      title: '订单类型',
      dataIndex: 'orderType',
      key: 'orderType',
      width: 110,
      render: (orderType: string) => (
        <Tag color={orderType === 'NEW' ? 'blue' : 'purple'}>
          {orderType === 'NEW' ? '新购' : '续费'}
        </Tag>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 150,
      render: (status: string) => {
        const { text, color } = getOrderStatusText(status);
        return <Tag color={color}>{text}</Tag>;
      },
    },
    {
      title: '确认时间',
      dataIndex: 'confirmedAt',
      key: 'confirmedAt',
      width: 180,
      render: (confirmedAt: string | null) => formatDateTime(confirmedAt),
    },
    {
      title: '支付截止',
      dataIndex: 'expiresAt',
      key: 'expiresAt',
      width: 180,
      render: (expiresAt: string) => formatDateTime(expiresAt),
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      fixed: 'right' as const,
      render: (_: unknown, record: Order) => (
        <Button
          type="link"
          size="small"
          icon={<EyeOutlined />}
          onClick={() => showDetail(record.orderNo)}
        >
          详情
        </Button>
      ),
    },
  ];

  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>订单管理</h2>

      <Card style={{ marginBottom: 24 }}>
        <Row gutter={16}>
          <Col xs={24} sm={12} lg={8}>
            <Input
              placeholder="搜索订单号"
              value={queryParams.orderNo}
              onChange={(event) =>
                setQueryParams((prev) => ({
                  ...prev,
                  orderNo: event.target.value || undefined,
                }))
              }
              onPressEnter={handleSearch}
              allowClear
            />
          </Col>
          <Col xs={24} sm={12} lg={8}>
            <Input
              placeholder="搜索账号邮箱"
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
              placeholder="订单状态"
              style={{ width: '100%' }}
              value={queryParams.status}
              onChange={(value) =>
                setQueryParams((prev) => ({ ...prev, status: value }))
              }
              options={orderStatusOptions}
              allowClear
            />
          </Col>
        </Row>
        <Row gutter={16} style={{ marginTop: 16 }}>
          <Col xs={24}>
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
          rowKey="orderId"
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
          scroll={{ x: 1500 }}
        />
      </Card>

      <Modal
        title="订单详情"
        open={detailModalVisible}
        onCancel={() => setDetailModalVisible(false)}
        footer={null}
        width={760}
      >
        {selectedOrder && (
          <Descriptions bordered column={2}>
            <Descriptions.Item label="订单号" span={2}>
              {selectedOrder.orderNo}
            </Descriptions.Item>
            <Descriptions.Item label="账号邮箱" span={2}>
              {selectedOrder.accountEmail}
            </Descriptions.Item>
            <Descriptions.Item label="账号 ID">
              {selectedOrder.accountId}
            </Descriptions.Item>
            <Descriptions.Item label="订单类型">
              {selectedOrder.orderType === 'NEW' ? '新购' : '续费'}
            </Descriptions.Item>
            <Descriptions.Item label="套餐名称">
              {selectedOrder.planName}
            </Descriptions.Item>
            <Descriptions.Item label="套餐编码">
              {selectedOrder.planCode}
            </Descriptions.Item>
            <Descriptions.Item label="支付金额">
              {formatAmount(
                selectedOrder.payableAmount,
                selectedOrder.quoteAssetCode,
              )}
            </Descriptions.Item>
            <Descriptions.Item label="计价金额">
              {formatAmount(selectedOrder.quoteUsdAmount, 'USD')}
            </Descriptions.Item>
            <Descriptions.Item label="支付网络">
              {selectedOrder.quoteNetworkCode}
            </Descriptions.Item>
            <Descriptions.Item label="状态">
              <Tag color={getOrderStatusText(selectedOrder.status).color}>
                {getOrderStatusText(selectedOrder.status).text}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="支付截止">
              {formatDateTime(selectedOrder.expiresAt)}
            </Descriptions.Item>
            <Descriptions.Item label="确认时间">
              {formatDateTime(selectedOrder.confirmedAt)}
            </Descriptions.Item>
            <Descriptions.Item label="完成时间">
              {formatDateTime(selectedOrder.completedAt)}
            </Descriptions.Item>
            <Descriptions.Item label="客户端交易哈希">
              {selectedOrder.submittedClientTxHash || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="失败原因" span={2}>
              {selectedOrder.failureReason || '-'}
            </Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
    </div>
  );
};

export default Orders;
