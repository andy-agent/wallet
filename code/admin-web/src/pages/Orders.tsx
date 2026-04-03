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
import { getOrders, getOrderDetail } from '../api';
import { formatDateTime, formatAmount, getOrderStatusText } from '../utils/format';
import type { Order, OrderListResponse, OrderQueryParams } from '../types';

const Orders: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<OrderListResponse>({
    items: [],
    total: 0,
    page: 1,
    page_size: 20,
  });
  // NOTE: 对齐后端参数命名: page, pageSize, orderNo, status, email
  const [queryParams, setQueryParams] = useState<OrderQueryParams>({
    page: 1,
    pageSize: 20,
  });
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
    fetchOrders();
  }, [fetchOrders]);

  const handleSearch = () => {
    setQueryParams(prev => ({ ...prev, page: 1 }));
    fetchOrders();
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

  const showDetail = async (orderNo: string) => {
    try {
      const order = await getOrderDetail(orderNo);
      setSelectedOrder(order);
      setDetailModalVisible(true);
    } catch (error) {
      console.error('获取订单详情失败:', error);
    }
  };

  // NOTE: 以下写操作后端暂未提供，暂时注释掉
  // const handleManualFulfill = async (id: string) => { ... }
  // const handleRetryFulfill = async (id: string) => { ... }
  // const handleIgnore = async (id: string) => { ... }

  const columns = [
    {
      title: '订单号',
      dataIndex: 'out_trade_no',
      key: 'out_trade_no',
      width: 180,
    },
    {
      title: '套餐',
      dataIndex: 'plan_name',
      key: 'plan_name',
      width: 150,
    },
    {
      title: '金额',
      dataIndex: 'amount',
      key: 'amount',
      width: 120,
      render: (amount: number, record: Order) => formatAmount(amount, record.currency),
    },
    {
      title: '用户',
      dataIndex: 'user_email',
      key: 'user_email',
      width: 200,
      ellipsis: true,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => {
        const { text, color } = getOrderStatusText(status);
        return <Tag color={color}>{text}</Tag>;
      },
    },
    {
      title: '支付时间',
      dataIndex: 'paid_at',
      key: 'paid_at',
      width: 170,
      render: (paid_at: string) => paid_at ? formatDateTime(paid_at) : '-',
    },
    {
      title: '创建时间',
      dataIndex: 'created_at',
      key: 'created_at',
      width: 170,
      render: (created_at: string) => formatDateTime(created_at),
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      fixed: 'right' as const,
      render: (_: any, record: Order) => (
        <Button
          type="link"
          size="small"
          icon={<EyeOutlined />}
          onClick={() => showDetail(record.out_trade_no)}
        >
          详情
        </Button>
        /*
          NOTE: 人工确认、重试发货、标记忽略等写操作后端暂未提供
          待后端实现后再开启以下功能
        */
      ),
    },
  ];

  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>订单管理</h2>

      {/* 筛选区域 - 对齐后端参数: orderNo, status, email */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={16}>
          <Col xs={24} sm={12} lg={8}>
            <Input
              placeholder="搜索订单号"
              value={queryParams.orderNo}
              onChange={(e) => setQueryParams(prev => ({ ...prev, orderNo: e.target.value }))}
              onPressEnter={handleSearch}
              allowClear
            />
          </Col>
          <Col xs={24} sm={12} lg={8}>
            <Input
              placeholder="搜索用户邮箱"
              value={queryParams.email}
              onChange={(e) => setQueryParams(prev => ({ ...prev, email: e.target.value }))}
              onPressEnter={handleSearch}
              allowClear
            />
          </Col>
          <Col xs={24} sm={12} lg={8}>
            <Select
              placeholder="订单状态"
              style={{ width: '100%' }}
              value={queryParams.status}
              onChange={(value) => setQueryParams(prev => ({ ...prev, status: value }))}
              allowClear
            >
              <Select.Option value="pending">待支付</Select.Option>
              <Select.Option value="paid">已支付</Select.Option>
              <Select.Option value="fulfilled">已完成</Select.Option>
              <Select.Option value="failed">失败</Select.Option>
              <Select.Option value="ignored">已忽略</Select.Option>
            </Select>
          </Col>
        </Row>
        <Row gutter={16} style={{ marginTop: 16 }}>
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

      {/* 订单列表 */}
      <Card>
        <Table
          columns={columns}
          dataSource={data.items}
          rowKey="id"
          loading={loading}
          pagination={{
            current: data.page,
            pageSize: data.page_size,
            total: data.total,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条`,
          }}
          onChange={handleTableChange}
          scroll={{ x: 1100 }}
        />
      </Card>

      {/* 订单详情弹窗 */}
      <Modal
        title="订单详情"
        open={detailModalVisible}
        onCancel={() => setDetailModalVisible(false)}
        footer={null}
        width={700}
      >
        {selectedOrder && (
          <Descriptions bordered column={2}>
            <Descriptions.Item label="订单号" span={2}>
              {selectedOrder.out_trade_no}
            </Descriptions.Item>
            <Descriptions.Item label="套餐名称">{selectedOrder.plan_name}</Descriptions.Item>
            <Descriptions.Item label="订单金额">
              {formatAmount(selectedOrder.amount, selectedOrder.currency)}
            </Descriptions.Item>
            <Descriptions.Item label="订单状态">
              <Tag color={getOrderStatusText(selectedOrder.status).color}>
                {getOrderStatusText(selectedOrder.status).text}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="支付方式">
              {selectedOrder.payment_method || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="用户邮箱" span={2}>
              {selectedOrder.user_email}
            </Descriptions.Item>
            <Descriptions.Item label="Telegram ID">
              {selectedOrder.telegram_id || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="付款人">
              {selectedOrder.payer_name || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="交易号" span={2}>
              {selectedOrder.transaction_id || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="创建时间">
              {formatDateTime(selectedOrder.created_at)}
            </Descriptions.Item>
            <Descriptions.Item label="支付时间">
              {selectedOrder.paid_at ? formatDateTime(selectedOrder.paid_at) : '-'}
            </Descriptions.Item>
            <Descriptions.Item label="完成时间">
              {selectedOrder.fulfilled_at ? formatDateTime(selectedOrder.fulfilled_at) : '-'}
            </Descriptions.Item>
            <Descriptions.Item label="备注" span={2}>
              {selectedOrder.remark || '-'}
            </Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
    </div>
  );
};

export default Orders;
