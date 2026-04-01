import React, { useEffect, useState, useCallback } from 'react';
import {
  Table,
  Card,
  Button,
  Space,
  Tag,
  Modal,
  Form,
  Input,
  InputNumber,
  Switch,
  message,
} from 'antd';
import { PlusOutlined, EditOutlined } from '@ant-design/icons';
import { getPlans, createPlan, updatePlan } from '../api';
import { formatAmount } from '../utils/format';
import type { Plan, PlanFormData } from '../types';

const Plans: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [plans, setPlans] = useState<Plan[]>([]);
  const [modalVisible, setModalVisible] = useState(false);
  const [modalTitle, setModalTitle] = useState('创建套餐');
  const [editingPlan, setEditingPlan] = useState<Plan | null>(null);
  const [form] = Form.useForm();
  const [submitLoading, setSubmitLoading] = useState(false);

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

  const handleCreate = () => {
    setEditingPlan(null);
    setModalTitle('创建套餐');
    form.resetFields();
    setModalVisible(true);
  };

  const handleEdit = (plan: Plan) => {
    setEditingPlan(plan);
    setModalTitle('编辑套餐');
    form.setFieldsValue({
      name: plan.name,
      description: plan.description,
      price: plan.price,
      original_price: plan.original_price,
      currency: plan.currency,
      duration_days: plan.duration_days,
      is_active: plan.is_active,
      sort_order: plan.sort_order,
    });
    setModalVisible(true);
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setSubmitLoading(true);

      const formData: PlanFormData = {
        ...values,
        price: Math.round(values.price * 100), // 转换为分
        original_price: values.original_price ? Math.round(values.original_price * 100) : undefined,
      };

      if (editingPlan) {
        await updatePlan(editingPlan.id, formData);
        message.success('更新成功');
      } else {
        await createPlan(formData);
        message.success('创建成功');
      }

      setModalVisible(false);
      fetchPlans();
    } catch (error) {
      console.error('提交失败:', error);
      message.error('操作失败');
    } finally {
      setSubmitLoading(false);
    }
  };

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
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_: any, record: Plan) => (
        <Space size="small">
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            编辑
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>套餐管理</h2>

      <Card>
        <div style={{ marginBottom: 16 }}>
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
            创建套餐
          </Button>
        </div>

        <Table
          columns={columns}
          dataSource={plans}
          rowKey="id"
          loading={loading}
          pagination={false}
        />
      </Card>

      {/* 创建/编辑弹窗 */}
      <Modal
        title={modalTitle}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        confirmLoading={submitLoading}
        width={600}
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={{
            currency: 'CNY',
            is_active: true,
            sort_order: 0,
          }}
        >
          <Form.Item
            name="name"
            label="套餐名称"
            rules={[{ required: true, message: '请输入套餐名称' }]}
          >
            <Input placeholder="请输入套餐名称" />
          </Form.Item>

          <Form.Item
            name="description"
            label="描述"
          >
            <Input.TextArea rows={3} placeholder="请输入套餐描述" />
          </Form.Item>

          <Form.Item
            name="price"
            label="价格（元）"
            rules={[{ required: true, message: '请输入价格' }]}
          >
            <InputNumber
              style={{ width: '100%' }}
              min={0}
              precision={2}
              placeholder="请输入价格"
            />
          </Form.Item>

          <Form.Item
            name="original_price"
            label="原价（元）"
          >
            <InputNumber
              style={{ width: '100%' }}
              min={0}
              precision={2}
              placeholder="请输入原价（可选）"
            />
          </Form.Item>

          <Form.Item
            name="currency"
            label="货币"
            rules={[{ required: true, message: '请选择货币' }]}
          >
            <Input placeholder="CNY/USD" />
          </Form.Item>

          <Form.Item
            name="duration_days"
            label="有效期（天）"
            rules={[{ required: true, message: '请输入有效期' }]}
          >
            <InputNumber
              style={{ width: '100%' }}
              min={1}
              placeholder="请输入有效期天数"
            />
          </Form.Item>

          <Form.Item
            name="sort_order"
            label="排序"
          >
            <InputNumber
              style={{ width: '100%' }}
              min={0}
              placeholder="数字越小排序越靠前"
            />
          </Form.Item>

          <Form.Item
            name="is_active"
            label="状态"
            valuePropName="checked"
          >
            <Switch checkedChildren="启用" unCheckedChildren="禁用" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default Plans;
