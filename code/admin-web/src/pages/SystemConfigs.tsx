import React, { useEffect, useState } from 'react';
import {
  Table,
  Card,
  Select,
  Button,
  Tag,
  Row,
  Col,
} from 'antd';
import { ReloadOutlined } from '@ant-design/icons';
import { getSystemConfigs } from '../api';
import { formatDateTime } from '../utils/format';
import type { SystemConfigListResponse, SystemConfigQueryParams } from '../types';

const SystemConfigs: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<SystemConfigListResponse>({
    items: [],
    total: 0,
  });
  const [queryParams, setQueryParams] = useState<SystemConfigQueryParams>({});

  const fetchSystemConfigs = async () => {
    setLoading(true);
    try {
      const result = await getSystemConfigs(queryParams.scope);
      setData(result);
    } catch (error) {
      console.error('获取系统配置失败:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSystemConfigs();
  }, [queryParams.scope]);

  const handleReset = () => {
    setQueryParams({});
  };

  const getScopeTag = (scope: string) => {
    const scopeMap: Record<string, { text: string; color: string }> = {
      system: { text: '系统', color: 'blue' },
      payment: { text: '支付', color: 'green' },
      vpn: { text: 'VPN', color: 'purple' },
      referral: { text: '邀请', color: 'orange' },
    };
    const { text, color } = scopeMap[scope] || { text: scope, color: 'default' };
    return <Tag color={color}>{text}</Tag>;
  };

  const columns = [
    {
      title: '配置键',
      dataIndex: 'key',
      key: 'key',
      width: 250,
      ellipsis: true,
    },
    {
      title: '配置值',
      dataIndex: 'value',
      key: 'value',
      width: 300,
      ellipsis: true,
    },
    {
      title: '作用域',
      dataIndex: 'scope',
      key: 'scope',
      width: 120,
      render: (scope: string) => getScopeTag(scope),
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
      render: (description: string) => description || '-',
    },
    {
      title: '更新时间',
      dataIndex: 'updatedAt',
      key: 'updatedAt',
      width: 170,
      render: (updatedAt: string) => formatDateTime(updatedAt),
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 170,
      render: (createdAt: string) => formatDateTime(createdAt),
    },
  ];

  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>系统配置</h2>

      {/* 筛选区域 */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={16} align="middle">
          <Col xs={24} sm={12} lg={8}>
            <Select
              placeholder="作用域筛选"
              style={{ width: '100%' }}
              value={queryParams.scope}
              onChange={(value) => setQueryParams({ scope: value })}
              allowClear
            >
              <Select.Option value="system">系统</Select.Option>
              <Select.Option value="payment">支付</Select.Option>
              <Select.Option value="vpn">VPN</Select.Option>
              <Select.Option value="referral">邀请</Select.Option>
            </Select>
          </Col>
          <Col xs={24} sm={12} lg={8}>
            <Button icon={<ReloadOutlined />} onClick={handleReset}>
              重置筛选
            </Button>
          </Col>
        </Row>
      </Card>

      {/* 配置列表 */}
      <Card>
        <Table
          columns={columns}
          dataSource={data.items}
          rowKey="id"
          loading={loading}
          pagination={{
            total: data.total,
            showTotal: (total) => `共 ${total} 条`,
          }}
          scroll={{ x: 1000 }}
        />
      </Card>
    </div>
  );
};

export default SystemConfigs;
