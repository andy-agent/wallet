import React, { useCallback, useEffect, useState } from 'react';
import {
  EditOutlined,
  PlusOutlined,
  ReloadOutlined,
  SearchOutlined,
} from '@ant-design/icons';
import {
  Button,
  Card,
  Col,
  Form,
  Input,
  InputNumber,
  Modal,
  Row,
  Select,
  Space,
  Switch,
  Table,
  Tag,
  message,
} from 'antd';
import {
  createPlan,
  getPlans,
  getRegions,
  updatePlan,
} from '../api';
import { formatAmount } from '../utils/format';
import type {
  Plan,
  PlanListResponse,
  PlanMutationPayload,
  PlanQueryParams,
  VpnRegion,
} from '../types';

const initialData: PlanListResponse = {
  items: [],
  page: 1,
  pageSize: 0,
  total: 0,
};

const initialQueryParams: PlanQueryParams = {};

const defaultFormValues: PlanMutationPayload = {
  planCode: '',
  name: '',
  description: '',
  billingCycleMonths: 1,
  priceUsd: '9.99',
  isUnlimitedTraffic: true,
  maxActiveSessions: 1,
  regionAccessPolicy: 'BASIC_ONLY',
  includesAdvancedRegions: false,
  allowedRegionIds: [],
  displayOrder: 0,
  status: 'DRAFT',
};

const Plans: React.FC = () => {
  const [form] = Form.useForm<PlanMutationPayload>();
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [data, setData] = useState<PlanListResponse>(initialData);
  const [regions, setRegions] = useState<VpnRegion[]>([]);
  const [queryParams, setQueryParams] =
    useState<PlanQueryParams>(initialQueryParams);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingPlan, setEditingPlan] = useState<Plan | null>(null);
  const regionAccessPolicy = Form.useWatch('regionAccessPolicy', form);

  const fetchPlans = useCallback(async () => {
    setLoading(true);
    try {
      const result = await getPlans(queryParams);
      setData(result);
    } catch (error) {
      console.error('获取套餐列表失败:', error);
      message.error('获取套餐列表失败');
    } finally {
      setLoading(false);
    }
  }, [queryParams]);

  const fetchRegions = useCallback(async () => {
    try {
      const result = await getRegions({ page: 1, pageSize: 100 });
      setRegions(result.items);
    } catch (error) {
      console.error('获取区域列表失败:', error);
      message.error('获取区域列表失败');
    }
  }, []);

  useEffect(() => {
    void fetchPlans();
  }, [fetchPlans]);

  useEffect(() => {
    void fetchRegions();
  }, [fetchRegions]);

  const regionOptions = regions.map((region) => ({
    label: `${region.displayName} (${region.regionCode})`,
    value: region.regionId,
  }));

  const openCreateModal = () => {
    setEditingPlan(null);
    form.setFieldsValue(defaultFormValues);
    setIsModalOpen(true);
  };

  const openEditModal = (plan: Plan) => {
    setEditingPlan(plan);
    form.setFieldsValue({
      planCode: plan.planCode,
      name: plan.name,
      description: plan.description,
      billingCycleMonths: plan.billingCycleMonths,
      priceUsd: plan.priceUsd,
      isUnlimitedTraffic: plan.isUnlimitedTraffic,
      maxActiveSessions: plan.maxActiveSessions,
      regionAccessPolicy: plan.regionAccessPolicy as PlanMutationPayload['regionAccessPolicy'],
      includesAdvancedRegions: plan.includesAdvancedRegions,
      allowedRegionIds: plan.allowedRegionIds,
      displayOrder: plan.displayOrder,
      status: plan.status as PlanMutationPayload['status'],
    });
    setIsModalOpen(true);
  };

  const handleSearch = () => {
    setQueryParams((prev) => ({ ...prev }));
  };

  const handleReset = () => {
    setQueryParams(initialQueryParams);
  };

  const handleModalCancel = () => {
    setIsModalOpen(false);
    setEditingPlan(null);
    form.resetFields();
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const payload: PlanMutationPayload = {
        ...values,
        allowedRegionIds:
          values.regionAccessPolicy === 'CUSTOM' ? values.allowedRegionIds : [],
      };

      setSubmitting(true);
      if (editingPlan) {
        await updatePlan(editingPlan.planId, payload);
        message.success('套餐已更新');
      } else {
        await createPlan(payload);
        message.success('套餐已创建');
      }
      handleModalCancel();
      await fetchPlans();
    } catch (error) {
      if ((error as { errorFields?: unknown[] })?.errorFields) {
        return;
      }
      console.error('保存套餐失败:', error);
      message.error('保存套餐失败');
    } finally {
      setSubmitting(false);
    }
  };

  const columns = [
    {
      title: '套餐编码',
      dataIndex: 'planCode',
      key: 'planCode',
      width: 160,
    },
    {
      title: '套餐名称',
      dataIndex: 'name',
      key: 'name',
      width: 180,
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      width: 220,
      ellipsis: true,
      render: (description: string) => description || '-',
    },
    {
      title: '价格',
      dataIndex: 'priceUsd',
      key: 'priceUsd',
      width: 120,
      render: (priceUsd: string) => formatAmount(priceUsd, 'USD'),
    },
    {
      title: '周期',
      dataIndex: 'billingCycleMonths',
      key: 'billingCycleMonths',
      width: 100,
      render: (billingCycleMonths: number) => `${billingCycleMonths} 个月`,
    },
    {
      title: '流量',
      dataIndex: 'isUnlimitedTraffic',
      key: 'isUnlimitedTraffic',
      width: 100,
      render: (isUnlimitedTraffic: boolean) => (
        <Tag color={isUnlimitedTraffic ? 'success' : 'default'}>
          {isUnlimitedTraffic ? '不限' : '有限'}
        </Tag>
      ),
    },
    {
      title: '最大会话数',
      dataIndex: 'maxActiveSessions',
      key: 'maxActiveSessions',
      width: 120,
    },
    {
      title: '区域策略',
      dataIndex: 'regionAccessPolicy',
      key: 'regionAccessPolicy',
      width: 160,
    },
    {
      title: '允许区域',
      dataIndex: 'allowedRegionIds',
      key: 'allowedRegionIds',
      width: 260,
      render: (allowedRegionIds: string[]) =>
        allowedRegionIds.length > 0
          ? allowedRegionIds
              .map(
                (regionId) =>
                  regions.find((region) => region.regionId === regionId)
                    ?.displayName || regionId,
              )
              .join(' / ')
          : '-',
    },
    {
      title: '高级区域',
      dataIndex: 'includesAdvancedRegions',
      key: 'includesAdvancedRegions',
      width: 120,
      render: (includesAdvancedRegions: boolean) => (
        <Tag color={includesAdvancedRegions ? 'success' : 'default'}>
          {includesAdvancedRegions ? '包含' : '不包含'}
        </Tag>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 110,
      render: (status: string) => {
        const color =
          status === 'ACTIVE'
            ? 'success'
            : status === 'DRAFT'
              ? 'processing'
              : 'default';
        return <Tag color={color}>{status}</Tag>;
      },
    },
    {
      title: '排序',
      dataIndex: 'displayOrder',
      key: 'displayOrder',
      width: 90,
    },
    {
      title: '操作',
      key: 'actions',
      width: 100,
      fixed: 'right' as const,
      render: (_: unknown, record: Plan) => (
        <Button
          icon={<EditOutlined />}
          onClick={() => openEditModal(record)}
        >
          编辑
        </Button>
      ),
    },
  ];

  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>套餐管理</h2>

      <Card style={{ marginBottom: 24 }}>
        <Row gutter={16}>
          <Col xs={24} sm={12} lg={8}>
            <Select
              placeholder="套餐状态"
              style={{ width: '100%' }}
              value={queryParams.status}
              onChange={(value) => setQueryParams({ status: value })}
              options={[
                { label: '草稿', value: 'DRAFT' },
                { label: '启用', value: 'ACTIVE' },
                { label: '停用', value: 'DISABLED' },
              ]}
              allowClear
            />
          </Col>
          <Col xs={24} sm={12} lg={16}>
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
              <Button
                type="dashed"
                icon={<PlusOutlined />}
                onClick={openCreateModal}
              >
                新建套餐
              </Button>
            </Space>
          </Col>
        </Row>
      </Card>

      <Card>
        <Table
          columns={columns}
          dataSource={data.items}
          rowKey="planId"
          loading={loading}
          pagination={false}
          scroll={{ x: 1900 }}
        />
      </Card>

      <Modal
        title={editingPlan ? '编辑套餐' : '新建套餐'}
        open={isModalOpen}
        onCancel={handleModalCancel}
        onOk={() => void handleSubmit()}
        confirmLoading={submitting}
        width={760}
        destroyOnHidden
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={defaultFormValues}
        >
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="套餐编码"
                name="planCode"
                rules={[{ required: true, message: '请输入套餐编码' }]}
              >
                <Input placeholder="例如 BASIC_1M" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="套餐名称"
                name="name"
                rules={[{ required: true, message: '请输入套餐名称' }]}
              >
                <Input placeholder="例如 基础版-1个月" />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item label="描述" name="description">
            <Input.TextArea rows={3} placeholder="输入套餐说明" />
          </Form.Item>

          <Row gutter={16}>
            <Col span={8}>
              <Form.Item
                label="周期(月)"
                name="billingCycleMonths"
                rules={[{ required: true, message: '请输入周期' }]}
              >
                <InputNumber min={1} style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                label="价格(USD)"
                name="priceUsd"
                rules={[{ required: true, message: '请输入价格' }]}
              >
                <Input placeholder="例如 9.99" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                label="最大会话数"
                name="maxActiveSessions"
                rules={[{ required: true, message: '请输入会话数' }]}
              >
                <InputNumber min={1} style={{ width: '100%' }} />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={8}>
              <Form.Item
                label="区域策略"
                name="regionAccessPolicy"
                rules={[{ required: true, message: '请选择区域策略' }]}
              >
                <Select
                  options={[
                    { label: '仅基础区域', value: 'BASIC_ONLY' },
                    { label: '包含高级区域', value: 'INCLUDE_ADVANCED' },
                    { label: '自定义区域', value: 'CUSTOM' },
                  ]}
                />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                label="显示排序"
                name="displayOrder"
                rules={[{ required: true, message: '请输入排序值' }]}
              >
                <InputNumber min={0} style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                label="状态"
                name="status"
                rules={[{ required: true, message: '请选择状态' }]}
              >
                <Select
                  options={[
                    { label: '草稿', value: 'DRAFT' },
                    { label: '启用', value: 'ACTIVE' },
                    { label: '停用', value: 'DISABLED' },
                  ]}
                />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="不限流量"
                name="isUnlimitedTraffic"
                valuePropName="checked"
              >
                <Switch checkedChildren="是" unCheckedChildren="否" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="包含高级区域"
                name="includesAdvancedRegions"
                valuePropName="checked"
              >
                <Switch checkedChildren="是" unCheckedChildren="否" />
              </Form.Item>
            </Col>
          </Row>

          {regionAccessPolicy === 'CUSTOM' ? (
            <Form.Item
              label="允许区域"
              name="allowedRegionIds"
              rules={[{ required: true, message: '请选择允许区域' }]}
            >
              <Select
                mode="multiple"
                placeholder="选择允许的节点区域"
                options={regionOptions}
              />
            </Form.Item>
          ) : null}
        </Form>
      </Modal>
    </div>
  );
};

export default Plans;
