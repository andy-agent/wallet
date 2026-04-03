import React, { useEffect, useState, useCallback } from 'react';
import {
  Table,
  Card,
  Select,
  Button,
  Space,
  Tag,
  Row,
  Col,
} from 'antd';
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons';
import { getAppVersions } from '../api';
import { formatDateTime } from '../utils/format';
import type { AppVersion, VersionListResponse, VersionQueryParams } from '../types';

const Versions: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<VersionListResponse>({
    items: [],
    total: 0,
    page: 1,
    pageSize: 20,
  });
  const [queryParams, setQueryParams] = useState<VersionQueryParams>({
    page: 1,
    pageSize: 20,
  });

  const fetchVersions = useCallback(async () => {
    setLoading(true);
    try {
      const result = await getAppVersions(queryParams);
      setData(result);
    } catch (error) {
      console.error('获取版本列表失败:', error);
    } finally {
      setLoading(false);
    }
  }, [queryParams]);

  useEffect(() => {
    fetchVersions();
  }, [fetchVersions]);

  const handleSearch = () => {
    setQueryParams(prev => ({ ...prev, page: 1 }));
    fetchVersions();
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

  const getStatusTag = (status: string) => {
    const statusMap: Record<string, { text: string; color: string }> = {
      active: { text: '正常', color: 'success' },
      deprecated: { text: '已弃用', color: 'default' },
      force_update: { text: '强制更新', color: 'error' },
    };
    const { text, color } = statusMap[status] || { text: status, color: 'default' };
    return <Tag color={color}>{text}</Tag>;
  };

  const getPlatformTag = (platform: string) => {
    const platformMap: Record<string, { text: string; color: string }> = {
      android: { text: 'Android', color: 'green' },
      ios: { text: 'iOS', color: 'blue' },
    };
    const { text, color } = platformMap[platform] || { text: platform, color: 'default' };
    return <Tag color={color}>{text}</Tag>;
  };

  const columns = [
    {
      title: '版本号',
      dataIndex: 'versionName',
      key: 'versionName',
      width: 120,
      render: (versionName: string, record: AppVersion) => (
        <span>{versionName} ({record.versionCode})</span>
      ),
    },
    {
      title: '平台',
      dataIndex: 'platform',
      key: 'platform',
      width: 100,
      render: (platform: string) => getPlatformTag(platform),
    },
    {
      title: '渠道',
      dataIndex: 'channel',
      key: 'channel',
      width: 120,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (status: string) => getStatusTag(status),
    },
    {
      title: '下载地址',
      dataIndex: 'downloadUrl',
      key: 'downloadUrl',
      width: 200,
      ellipsis: true,
      render: (url: string) => url ? <a href={url} target="_blank" rel="noopener noreferrer">链接</a> : '-',
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 170,
      render: (createdAt: string) => formatDateTime(createdAt),
    },
    {
      title: '更新时间',
      dataIndex: 'updatedAt',
      key: 'updatedAt',
      width: 170,
      render: (updatedAt: string) => formatDateTime(updatedAt),
    },
  ];

  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>版本管理</h2>

      {/* 筛选区域 */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={16}>
          <Col xs={24} sm={12} lg={8}>
            <Select
              placeholder="版本状态"
              style={{ width: '100%' }}
              value={queryParams.status}
              onChange={(value) => setQueryParams(prev => ({ ...prev, status: value }))}
              allowClear
            >
              <Select.Option value="active">正常</Select.Option>
              <Select.Option value="deprecated">已弃用</Select.Option>
              <Select.Option value="force_update">强制更新</Select.Option>
            </Select>
          </Col>
          <Col xs={24} sm={12} lg={8}>
            <Select
              placeholder="渠道"
              style={{ width: '100%' }}
              value={queryParams.channel}
              onChange={(value) => setQueryParams(prev => ({ ...prev, channel: value }))}
              allowClear
            >
              <Select.Option value="official">官方</Select.Option>
              <Select.Option value="google_play">Google Play</Select.Option>
              <Select.Option value="app_store">App Store</Select.Option>
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

      {/* 版本列表 */}
      <Card>
        <Table
          columns={columns}
          dataSource={data.items}
          rowKey="id"
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
          scroll={{ x: 900 }}
        />
      </Card>
    </div>
  );
};

export default Versions;
