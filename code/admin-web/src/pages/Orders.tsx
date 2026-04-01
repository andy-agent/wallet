import React, { useEffect, useState, useCallback } from 'react';
import {
  Table,
  Card,
  Input,
  Select,
  DatePicker,
  Button,
  Space,
  Tag,
  Modal,
  Descriptions,
  message,
  Popconfirm,
  Row,
  Col,
} from 'antd';
import { SearchOutlined, ReloadOutlined, EyeOutlined, CheckOutlined, RedoOutlined, StopOutlined } from '@ant-design/icons';
import { getOrders, getOrderDetail, manualFulfillOrder, retryFulfillOrder, ignoreOrder } from '../api';
import { formatDateTime, formatAmount, getOrderStatusText } from '../utils/format';
import type { Order, OrderListResponse, OrderQueryParams } from '../types';

const { RangePicker } = DatePicker;

const Orders: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<OrderListResponse>({
    items: [],
    total: 0,
    page: 1,
    page_size: 20,
  });
  const [queryParams, setQueryParams] = useState<OrderQueryParams>({
    page: 1,
    page_size: 20,
  });
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);
  const [actionLoading, setActionLoading] = useState(false);

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
      page_size: 20,
    });
  };

  const handleTableChange = (pagination: any) => {
    setQueryParams(prev => ({
      ...prev,
      page: pagination.current,
      page_size: pagination.pageSize,
    }));
  };

  const showDetail = async (id: string) => {
    try {
      const order = await getOrderDetail(id);
      setSelectedOrder(order);
      setDetailModalVisible(true);
    } catch (error) {
      message.error('获取订单详情失败');
    }
  };

  const handleManualFulfill = async (id: string) => {
    setActionLoading(true);
    try {
      await manualFulfillOrder(id);
      message.success('人工确认成功');
      fetchOrders();
    } catch (error) {
      message.error('操作失败');
    } finally {
      setActionLoading(false);
    }
  };

  const handleRetryFulfill = async (id: string) => {
    setActionLoading(true);
    try {
      await retryFulfillOrder(id);
      message.success('重试发货成功');
      fetchOrders();
    } catch (error) {
      message.error('操作失败');
    } finally {
      setActionLoading(false);
    }
  };

  const handleIgnore = async (id: string) => {
    setActionLoading(true);
    try {
      await ignoreOrder(id);
      message.success('已标记为忽略');
      fetchOrders();
    } catch (error) {
      message.error('操作失败');
    } finally {
      setActionLoading(false);
    }
  };

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
      render: (paid_at: string) => formatDateTime(paid_at),
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
      width: 250,
      fixed: 'right' as const,
      render: (_: any, record: Order) => (
        <Space size="small">
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => showDetail(record.id)}
          >
            详情
          </Button>
          {record.status === 'paid' && (
            <Popconfirm
              title="确认人工发货"
              description="确定要人工确认此订单吗？"
              onConfirm={() => handleManualFulfill(record.id)}
              okButtonProps={{ loading: actionLoading }}
            >
              <Button type="link" size="small" icon={<CheckOutlined />}>
                确认
              </Button>
            </Popconfirm>
          )}
          {record.status === 'failed' && (
            <Popconfirm
              title="确认重试"
              description="确定要重试发货此订单吗？"
              onConfirm={() => handleRetryFulfill(record.id)}
              okButtonProps={{ loading: actionLoading }}
            >
              <Button type="link" size="small" icon={<RedoOutlined />}>
                重试
              </Button>
            </Popconfirm>
          )}
          {(record.status === 'pending' || record.status === 'failed') && (
            <Popconfirm
              title="确认忽略"
              description="确定要忽略此订单吗？"
              onConfirm={() => handleIgnore(record.id)}
              okButtonProps={{ loading: actionLoading, danger: true }}
            >
              <Button type="link" size="small" danger icon={<StopOutlined />}>
                忽略
              </Button>
            </Popconfirm>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>订单管理</h2>

      {/* 筛选区域 */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={16}>
          <Col xs={24} sm={12} lg={6}>
            <Input
              placeholder="搜索订单号/邮箱"
              value={queryParams.keyword}
              onChange={(e) => setQueryParams(prev => ({ ...prev, keyword: e.target.value }))}
              onPressEnter={handleSearch}
              allowClear
            />
          </Col>
          <Col xs={24} sm={12} lg={6}>
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
          <Col xs={24} sm={12} lg={6}>
            <RangePicker
              style={{ width: '100%' }}
              onChange={(dates) => {
                if (dates) {
                  setQueryParams(prev => ({
                    ...prev,
                    start_date: dates[0]?.format('YYYY-MM-DD'),
                    end_date: dates[1]?.format('YYYY-MM-DD'),
                  }));
                } else {
                  setQueryParams(prev => ({
                    ...prev,
                    start_date: undefined,
                    end_date: undefined,
                  }));
                }
              }}
            />
          </Col>
          <Col xs={24} sm={12} lg={6}>
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
          scroll={{ x: 1200 }}
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
