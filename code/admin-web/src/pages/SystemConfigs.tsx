import React, { useCallback, useEffect, useState } from 'react';
import { ReloadOutlined } from '@ant-design/icons';
import {
  Button,
  Card,
  Col,
  Row,
  Select,
  Table,
  Tag,
} from 'antd';
import { getSystemConfigs } from '../api';
import { formatDateTime } from '../utils/format';
import type {
  SystemConfigListResponse,
  SystemConfigQueryParams,
  SystemConfigScope,
} from '../types';

const initialData: SystemConfigListResponse = {
  items: [],
  page: 1,
  pageSize: 0,
  total: 0,
};

const getScopeTag = (scope: string) => {
  const scopeMap: Record<string, { text: string; color: string }> = {
    GLOBAL: { text: '全局', color: 'blue' },
    PAYMENT: { text: '支付', color: 'green' },
    VPN: { text: 'VPN', color: 'purple' },
    REFERRAL: { text: '邀请', color: 'orange' },
  };
  const { text, color } = scopeMap[scope] ?? {
    text: scope,
    color: 'default',
  };
  return <Tag color={color}>{text}</Tag>;
};

const SystemConfigs: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<SystemConfigListResponse>(initialData);
  const [queryParams, setQueryParams] = useState<SystemConfigQueryParams>({});

  const fetchSystemConfigs = useCallback(async () => {
    setLoading(true);
    try {
      const result = await getSystemConfigs(queryParams);
      setData(result);
    } catch (error) {
      console.error('获取系统配置失败:', error);
    } finally {
      setLoading(false);
    }
  }, [queryParams]);

  useEffect(() => {
    void fetchSystemConfigs();
  }, [fetchSystemConfigs]);

  const columns = [
    {
      title: '配置键',
      dataIndex: 'configKey',
      key: 'configKey',
      width: 260,
      ellipsis: true,
    },
    {
      title: '配置值',
      dataIndex: 'configValue',
      key: 'configValue',
      width: 220,
      ellipsis: true,
    },
    {
      title: '值类型',
      dataIndex: 'valueType',
      key: 'valueType',
      width: 120,
      render: (valueType: string) => <Tag>{valueType}</Tag>,
    },
    {
      title: '作用域',
      dataIndex: 'scope',
      key: 'scope',
      width: 120,
      render: (scope: string) => getScopeTag(scope),
    },
    {
      title: '可编辑',
      dataIndex: 'isEditable',
      key: 'isEditable',
      width: 100,
      render: (isEditable: boolean) => (
        <Tag color={isEditable ? 'success' : 'default'}>
          {isEditable ? '是' : '否'}
        </Tag>
      ),
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      width: 260,
      ellipsis: true,
    },
    {
      title: '更新人',
      dataIndex: 'updatedBy',
      key: 'updatedBy',
      width: 120,
      render: (updatedBy: string | null) => updatedBy || '-',
    },
    {
      title: '更新时间',
      dataIndex: 'updatedAt',
      key: 'updatedAt',
      width: 180,
      render: (updatedAt: string) => formatDateTime(updatedAt),
    },
  ];

  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>系统配置</h2>

      <Card style={{ marginBottom: 24 }}>
        <Row gutter={16} align="middle">
          <Col xs={24} sm={12} lg={8}>
            <Select
              placeholder="作用域筛选"
              style={{ width: '100%' }}
              value={queryParams.scope}
              onChange={(value: SystemConfigScope | undefined) =>
                setQueryParams(value ? { scope: value } : {})
              }
              options={[
                { label: '全局', value: 'GLOBAL' },
                { label: '支付', value: 'PAYMENT' },
                { label: 'VPN', value: 'VPN' },
                { label: '邀请', value: 'REFERRAL' },
              ]}
              allowClear
            />
          </Col>
          <Col xs={24} sm={12} lg={8}>
            <Button icon={<ReloadOutlined />} onClick={() => setQueryParams({})}>
              重置筛选
            </Button>
          </Col>
        </Row>
      </Card>

      <Card>
        <Table
          columns={columns}
          dataSource={data.items}
          rowKey="configKey"
          loading={loading}
          pagination={false}
          scroll={{ x: 1600 }}
        />
      </Card>
    </div>
  );
};

export default SystemConfigs;
