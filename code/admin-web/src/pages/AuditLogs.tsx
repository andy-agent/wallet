import React, { useEffect, useState, useCallback } from 'react';
import {
  Table,
  Card,
  Select,
  DatePicker,
  Button,
  Space,
  Tag,
  Modal,
  Timeline,
  Empty,
  message,
  Row,
  Col,
  Input,
} from 'antd';
import { SearchOutlined, ReloadOutlined, HistoryOutlined } from '@ant-design/icons';
import { getAuditLogs, getEntityAuditLogs } from '../api';
import { formatDateTime, getActionText } from '../utils/format';
import type { AuditLog, AuditLogListResponse, AuditLogQueryParams } from '../types';

const { RangePicker } = DatePicker;

const AuditLogs: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<AuditLogListResponse>({
    items: [],
    total: 0,
    page: 1,
    page_size: 20,
  });
  const [queryParams, setQueryParams] = useState<AuditLogQueryParams>({
    page: 1,
    page_size: 20,
  });
  const [traceModalVisible, setTraceModalVisible] = useState(false);
  const [traceLogs, setTraceLogs] = useState<AuditLog[]>([]);
  const [traceLoading, setTraceLoading] = useState(false);
  const [traceEntity, setTraceEntity] = useState<{ type: string; id: string } | null>(null);

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

  const handleTrace = async (entityType: string, entityId: string) => {
    setTraceEntity({ type: entityType, id: entityId });
    setTraceModalVisible(true);
    setTraceLoading(true);
    try {
      const logs = await getEntityAuditLogs(entityType, entityId);
      setTraceLogs(logs);
    } catch (error) {
      message.error('获取实体追踪失败');
    } finally {
      setTraceLoading(false);
    }
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
    {
      title: '操作',
      key: 'action_col',
      width: 120,
      render: (_: any, record: AuditLog) => (
        <Button
          type="link"
          size="small"
          icon={<HistoryOutlined />}
          onClick={() => handleTrace(record.entity_type, record.entity_id)}
        >
          追踪
        </Button>
      ),
    },
  ];

  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>审计日志</h2>

      {/* 筛选区域 */}
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

      {/* 实体追踪弹窗 */}
      <Modal
        title={`实体追踪 - ${traceEntity?.type} #${traceEntity?.id}`}
        open={traceModalVisible}
        onCancel={() => setTraceModalVisible(false)}
        footer={null}
        width={700}
      >
        {traceLoading ? (
          <div style={{ textAlign: 'center', padding: 40 }}>加载中...</div>
        ) : traceLogs.length === 0 ? (
          <Empty description="暂无追踪记录" />
        ) : (
          <Timeline mode="left">
            {traceLogs.map((log) => (
              <Timeline.Item
                key={log.id}
                color={getActionColor(log.action)}
                label={formatDateTime(log.created_at)}
              >
                <div>
                  <Tag color={getActionColor(log.action)}>
                    {getActionText(log.action)}
                  </Tag>
                  <span style={{ marginLeft: 8, color: '#666' }}>
                    操作人: {log.operator}
                  </span>
                </div>
                {(log.old_value || log.new_value) && (
                  <div style={{ marginTop: 8, padding: 8, background: '#f5f5f5', borderRadius: 4 }}>
                    {log.old_value && (
                      <div>
                        <span style={{ color: '#999' }}>变更前:</span>
                        <pre style={{ margin: '4px 0', fontSize: 12 }}>
                          {JSON.stringify(JSON.parse(log.old_value), null, 2)}
                        </pre>
                      </div>
                    )}
                    {log.new_value && (
                      <div>
                        <span style={{ color: '#999' }}>变更后:</span>
                        <pre style={{ margin: '4px 0', fontSize: 12 }}>
                          {JSON.stringify(JSON.parse(log.new_value), null, 2)}
                        </pre>
                      </div>
                    )}
                  </div>
                )}
              </Timeline.Item>
            ))}
          </Timeline>
        )}
      </Modal>
    </div>
  );
};

export default AuditLogs;
