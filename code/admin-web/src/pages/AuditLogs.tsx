import React, { useEffect, useState, useCallback } from 'react';
import {
  Table,
  Card,
  Select,
  DatePicker,
  Button,
  Space,
  Tag,
  Row,
  Col,
  Input,
} from 'antd';
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons';
import { getAuditLogs } from '../api';
import { formatDateTime, getActionText } from '../utils/format';
import type { AuditLogListResponse, AuditLogQueryParams } from '../types';

const { RangePicker } = DatePicker;

const AuditLogs: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<AuditLogListResponse>({
    items: [],
    total: 0,
    page: 1,
    page_size: 20,
  });
  // NOTE: 对齐后端参数命名: page, pageSize, module, actorType, targetType, dateRange
  const [queryParams, setQueryParams] = useState<AuditLogQueryParams>({
    page: 1,
    pageSize: 20,
  });

  const fetchLogs = useCallback(async () => {
    setLoading(true);
    try {
      const result = await getAuditLogs(queryParams);
      setData(result);
    } catch (error) {
      console.error('获取审计日志失败:', error);
    } finally {
      setLoading(false);
    }
  }, [queryParams]);

  useEffect(() => {
    fetchLogs();
  }, [fetchLogs]);

  const handleSearch = () => {
    setQueryParams(prev => ({ ...prev, page: 1 }));
    fetchLogs();
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

  const getActionColor = (action: string): string => {
    const colorMap: Record<string, string> = {
      create: 'green',
      update: 'blue',
      delete: 'red',
      manual_fulfill: 'purple',
      retry_fulfill: 'orange',
      ignore: 'warning',
      login: 'cyan',
      logout: 'default',
    };
    return colorMap[action] || 'default';
  };

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 60,
    },
    {
      title: '操作',
      dataIndex: 'action',
      key: 'action',
      width: 120,
      render: (action: string) => (
        <Tag color={getActionColor(action)}>{getActionText(action)}</Tag>
      ),
    },
    {
      title: '实体类型',
      dataIndex: 'entity_type',
      key: 'entity_type',
      width: 100,
    },
    {
      title: '实体ID',
      dataIndex: 'entity_id',
      key: 'entity_id',
      width: 150,
      ellipsis: true,
    },
    {
      title: '操作人',
      dataIndex: 'operator',
      key: 'operator',
      width: 120,
    },
    {
      title: 'IP地址',
      dataIndex: 'ip_address',
      key: 'ip_address',
      width: 120,
      render: (ip: string) => ip || '-',
    },
    {
      title: '操作时间',
      dataIndex: 'created_at',
      key: 'created_at',
      width: 170,
      render: (created_at: string) => formatDateTime(created_at),
    },
    // NOTE: 实体追踪功能后端暂未提供
    // {
    //   title: '操作',
    //   key: 'action_col',
    //   width: 120,
    //   render: (_: any, record: AuditLog) => (...),
    // },
  ];

  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>审计日志</h2>

      {/* 筛选区域 - 对齐后端参数: module, actorType, targetType, dateRange */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={16}>
          <Col xs={24} sm={12} lg={6}>
            <Select
              placeholder="操作类型"
              style={{ width: '100%' }}
              value={queryParams.action}
              onChange={(value) => setQueryParams(prev => ({ ...prev, action: value }))}
              allowClear
            >
              <Select.Option value="create">创建</Select.Option>
              <Select.Option value="update">更新</Select.Option>
              <Select.Option value="delete">删除</Select.Option>
              <Select.Option value="manual_fulfill">人工确认</Select.Option>
              <Select.Option value="retry_fulfill">重试发货</Select.Option>
              <Select.Option value="ignore">标记忽略</Select.Option>
              <Select.Option value="login">登录</Select.Option>
              <Select.Option value="logout">登出</Select.Option>
            </Select>
          </Col>
          <Col xs={24} sm={12} lg={6}>
            <Input
              placeholder="模块 (module)"
              value={queryParams.module}
              onChange={(e) => setQueryParams(prev => ({ ...prev, module: e.target.value }))}
              allowClear
            />
          </Col>
          <Col xs={24} sm={12} lg={6}>
            <Input
              placeholder="操作者类型 (actorType)"
              value={queryParams.actorType}
              onChange={(e) => setQueryParams(prev => ({ ...prev, actorType: e.target.value }))}
              allowClear
            />
          </Col>
          <Col xs={24} sm={12} lg={6}>
            <Input
              placeholder="目标类型 (targetType)"
              value={queryParams.targetType}
              onChange={(e) => setQueryParams(prev => ({ ...prev, targetType: e.target.value }))}
              allowClear
            />
          </Col>
        </Row>
        <Row gutter={16} style={{ marginTop: 16 }}>
          <Col xs={24} sm={12} lg={6}>
            <Input
              placeholder="实体类型"
              value={queryParams.entity_type}
              onChange={(e) => setQueryParams(prev => ({ ...prev, entity_type: e.target.value }))}
              allowClear
            />
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

      {/* 日志列表 */}
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
        />
      </Card>

      {/* 
        NOTE: 实体追踪弹窗功能后端暂未提供
        待后端实现 /admin/v1/audit-logs/:entityType/:entityId 后再开启
      */}
    </div>
  );
};

export default AuditLogs;
