import React, { useCallback, useEffect, useState } from 'react';
import {
  ReloadOutlined,
  SearchOutlined,
} from '@ant-design/icons';
import {
  Button,
  Card,
  Col,
  Row,
  Select,
  Space,
  Table,
  Tag,
} from 'antd';
import { getAppVersions } from '../api';
import { formatDateTime } from '../utils/format';
import type {
  AppVersion,
  VersionChannel,
  VersionListResponse,
  VersionQueryParams,
  VersionStatus,
} from '../types';

const initialData: VersionListResponse = {
  items: [],
  page: 1,
  pageSize: 20,
  total: 0,
};

const initialQueryParams: VersionQueryParams = {
  page: 1,
  pageSize: 20,
};

const statusOptions: Array<{ label: string; value: VersionStatus }> = [
  { label: '草稿', value: 'DRAFT' },
  { label: '已发布', value: 'PUBLISHED' },
  { label: '已弃用', value: 'DEPRECATED' },
];

const channelOptions: Array<{ label: string; value: VersionChannel }> = [
  { label: '官方', value: 'OFFICIAL' },
  { label: 'Google Play', value: 'GOOGLE_PLAY' },
  { label: 'App Store', value: 'APP_STORE' },
];

const getStatusTag = (status: string, forceUpdate: boolean) => {
  const statusMap: Record<string, { text: string; color: string }> = {
    DRAFT: { text: '草稿', color: 'default' },
    PUBLISHED: { text: '已发布', color: 'success' },
    DEPRECATED: { text: '已弃用', color: 'warning' },
  };
  const { text, color } = statusMap[status] ?? {
    text: status,
    color: 'default',
  };
  return (
    <Space size={4}>
      <Tag color={color}>{text}</Tag>
      {forceUpdate ? <Tag color="red">强更</Tag> : null}
    </Space>
  );
};

const getPlatformTag = (platform: string) => {
  const platformMap: Record<string, { text: string; color: string }> = {
    ANDROID: { text: 'Android', color: 'green' },
    IOS: { text: 'iOS', color: 'blue' },
  };
  const { text, color } = platformMap[platform] ?? {
    text: platform,
    color: 'default',
  };
  return <Tag color={color}>{text}</Tag>;
};

const getChannelTag = (channel: string) => {
  const channelMap: Record<string, { text: string; color: string }> = {
    OFFICIAL: { text: '官方', color: 'default' },
    GOOGLE_PLAY: { text: 'Google Play', color: 'green' },
    APP_STORE: { text: 'App Store', color: 'blue' },
  };
  const { text, color } = channelMap[channel] ?? {
    text: channel,
    color: 'default',
  };
  return <Tag color={color}>{text}</Tag>;
};

const Versions: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<VersionListResponse>(initialData);
  const [queryParams, setQueryParams] =
    useState<VersionQueryParams>(initialQueryParams);

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
    void fetchVersions();
  }, [fetchVersions]);

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

  const columns = [
    {
      title: '版本号',
      dataIndex: 'versionName',
      key: 'versionName',
      width: 140,
      render: (versionName: string, record: AppVersion) => (
        <span>
          {versionName} ({record.versionCode})
        </span>
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
      width: 140,
      render: (channel: string) => getChannelTag(channel),
    },
    {
      title: '状态',
      key: 'status',
      width: 170,
      render: (_: unknown, record: AppVersion) =>
        getStatusTag(record.status, record.forceUpdate),
    },
    {
      title: '最低版本要求',
      key: 'minVersion',
      width: 180,
      render: (_: unknown, record: AppVersion) => {
        if (record.platform === 'ANDROID') {
          return record.minAndroidVersionCode ?? '-';
        }
        return record.minIosVersionCode ?? '-';
      },
    },
    {
      title: '下载地址',
      dataIndex: 'downloadUrl',
      key: 'downloadUrl',
      width: 180,
      ellipsis: true,
      render: (downloadUrl: string | null) =>
        downloadUrl ? (
          <a href={downloadUrl} target="_blank" rel="noopener noreferrer">
            链接
          </a>
        ) : (
          '-'
        ),
    },
    {
      title: '发布时间',
      dataIndex: 'publishedAt',
      key: 'publishedAt',
      width: 180,
      render: (publishedAt: string | null) => formatDateTime(publishedAt),
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
      <h2 style={{ marginBottom: 24 }}>版本管理</h2>

      <Card style={{ marginBottom: 24 }}>
        <Row gutter={16}>
          <Col xs={24} sm={12} lg={8}>
            <Select
              placeholder="版本状态"
              style={{ width: '100%' }}
              value={queryParams.status}
              onChange={(value) =>
                setQueryParams((prev) => ({ ...prev, status: value }))
              }
              options={statusOptions}
              allowClear
            />
          </Col>
          <Col xs={24} sm={12} lg={8}>
            <Select
              placeholder="渠道"
              style={{ width: '100%' }}
              value={queryParams.channel}
              onChange={(value) =>
                setQueryParams((prev) => ({ ...prev, channel: value }))
              }
              options={channelOptions}
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
          rowKey="versionId"
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
          scroll={{ x: 1450 }}
        />
      </Card>
    </div>
  );
};

export default Versions;
