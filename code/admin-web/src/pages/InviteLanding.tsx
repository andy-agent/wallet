import React, { useMemo } from 'react';
import { Alert, Button, Card, Col, Divider, message, Row, Space, Tag, Typography } from 'antd';
import {
  CopyOutlined,
  DownloadOutlined,
  LinkOutlined,
  MobileOutlined,
} from '@ant-design/icons';
import { useSearchParams } from 'react-router-dom';

const { Paragraph, Text, Title } = Typography;

const APK_DOWNLOAD_URL = (import.meta.env.VITE_PUBLIC_ANDROID_APK_URL ?? '').trim();

const appendInviteCode = (baseUrl: string, code: string) => {
  if (!baseUrl) {
    return '';
  }

  try {
    const url = new URL(baseUrl);
    if (code) {
      url.searchParams.set('invite_code', code);
    }
    return url.toString();
  } catch {
    return baseUrl;
  }
};

const InviteLanding: React.FC = () => {
  const [searchParams] = useSearchParams();

  const code = useMemo(
    () => searchParams.get('code')?.trim().toUpperCase() ?? '',
    [searchParams],
  );

  const inviteLink = useMemo(() => window.location.href, []);
  const downloadHref = useMemo(
    () => appendInviteCode(APK_DOWNLOAD_URL, code),
    [code],
  );
  const openAppHref = useMemo(
    () => `v2rayng://invite?code=${encodeURIComponent(code)}`,
    [code],
  );
  const hasCode = code.length > 0;
  const hasDownloadUrl = downloadHref.length > 0;

  const copyText = async (value: string, successText: string) => {
    try {
      await navigator.clipboard.writeText(value);
      message.success(successText);
    } catch {
      message.error('复制失败，请手动复制');
    }
  };

  return (
    <div
      style={{
        minHeight: '100vh',
        padding: '32px 16px',
        background:
          'linear-gradient(180deg, #f6f8fb 0%, #eef3ff 48%, #ffffff 100%)',
      }}
    >
      <div style={{ maxWidth: 1080, margin: '0 auto' }}>
        <Row gutter={[24, 24]} align="middle">
          <Col xs={24} lg={13}>
            <Space direction="vertical" size={20} style={{ width: '100%' }}>
              <Tag color="blue" style={{ width: 'fit-content', margin: 0 }}>
                CryptoVPN 邀请页
              </Tag>
              <Title level={1} style={{ margin: 0 }}>
                接受邀请，下载并打开 CryptoVPN
              </Title>
              <Paragraph style={{ fontSize: 16, margin: 0 }}>
                这是直装 APK 的邀请落地页。先下载并安装 App，安装完成后返回本页点击“打开 App”，即可把当前邀请码带进 App，后续在登录或注册后自动绑定推广关系。
              </Paragraph>
              <Space wrap size={[12, 12]}>
                <Button
                  type="primary"
                  size="large"
                  icon={<DownloadOutlined />}
                  href={downloadHref}
                  disabled={!hasDownloadUrl}
                >
                  下载 App
                </Button>
                <Button
                  size="large"
                  icon={<MobileOutlined />}
                  href={openAppHref}
                  disabled={!hasCode}
                >
                  打开 App
                </Button>
              </Space>
              <Text type="secondary">
                Android deep link 使用 `v2rayng://invite?code=...`。如果你已安装 App，可以直接点“打开 App”；如果尚未安装，请先下载，再返回此页点击打开。
              </Text>
            </Space>
          </Col>

          <Col xs={24} lg={11}>
            <Card
              style={{
                borderRadius: 24,
                boxShadow: '0 16px 48px rgba(31, 56, 120, 0.12)',
              }}
            >
              <Space direction="vertical" size={18} style={{ width: '100%' }}>
                {!hasCode ? (
                  <Alert
                    type="warning"
                    showIcon
                    message="链接缺少邀请码"
                    description="当前链接没有携带 code 参数。请重新获取正确的分享链接。"
                  />
                ) : null}

                <div>
                  <Text type="secondary">邀请码</Text>
                  <Title level={2} style={{ margin: '8px 0 0' }}>
                    {code || '未提供'}
                  </Title>
                </div>

                <div>
                  <Text type="secondary">产品简介</Text>
                  <Paragraph style={{ margin: '8px 0 0', fontSize: 16 }}>
                    当前页面负责承接公开邀请链接、提供 APK 下载入口，并在安装完成后通过 deep link 把邀请码传进 App。
                  </Paragraph>
                </div>

                <Divider style={{ margin: 0 }} />

                <Alert
                  type="info"
                  showIcon
                  message="使用步骤"
                  description={
                    <Space direction="vertical" size={4}>
                      <Text>1. 下载并安装 Android APK。</Text>
                      <Text>2. 安装完成后返回本页，点击“打开 App”。</Text>
                      <Text>3. 在 App 内登录或注册成功后，系统会继续处理推广绑定。</Text>
                    </Space>
                  }
                />

                {!hasDownloadUrl ? (
                  <Alert
                    type="warning"
                    showIcon
                    message="未配置 APK 下载地址"
                    description="部署时请注入 VITE_PUBLIC_ANDROID_APK_URL，并确保该地址可直接下载最新 Android 安装包。"
                  />
                ) : null}

                <Space direction="vertical" size={12} style={{ width: '100%' }}>
                  <Button
                    block
                    icon={<CopyOutlined />}
                    onClick={() => copyText(code, '邀请码已复制')}
                    disabled={!hasCode}
                  >
                    复制邀请码
                  </Button>
                  <Button
                    block
                    icon={<LinkOutlined />}
                    onClick={() => copyText(inviteLink, '邀请链接已复制')}
                  >
                    复制邀请链接
                  </Button>
                </Space>

                <Alert
                  type="info"
                  showIcon
                  message="直接下载 APK 的限制"
                  description="如果用户只是下载并安装 APK、随后从桌面直接打开 App，而没有返回本页点击“打开 App”，系统无法自动把邀请码带进 App。这个场景下需要用户重新回到本页完成一次打开 App 动作。"
                />
              </Space>
            </Card>
          </Col>
        </Row>
      </div>
    </div>
  );
};

export default InviteLanding;
