import { Injectable } from '@nestjs/common';

export interface AppVersion {
  versionId: string;
  platform: 'ANDROID' | 'IOS';
  channel: 'GOOGLE_PLAY' | 'APP_STORE' | 'OFFICIAL';
  versionName: string;
  versionCode: number;
  minAndroidVersionCode: number | null;
  minIosVersionCode: number | null;
  downloadUrl: string | null;
  forceUpdate: boolean;
  releaseNotes: string;
  status: 'DRAFT' | 'PUBLISHED' | 'DEPRECATED';
  publishedAt: string | null;
  createdAt: string;
  updatedAt: string;
}

@Injectable()
export class AdminVersionsService {
  private readonly versions: AppVersion[] = [
    {
      versionId: 'VER-001',
      platform: 'ANDROID',
      channel: 'GOOGLE_PLAY',
      versionName: '1.0.0',
      versionCode: 100,
      minAndroidVersionCode: 21,
      minIosVersionCode: null,
      downloadUrl: null,
      forceUpdate: false,
      releaseNotes: '初始版本发布',
      status: 'PUBLISHED',
      publishedAt: new Date(Date.now() - 2592000000).toISOString(),
      createdAt: new Date(Date.now() - 2592000000).toISOString(),
      updatedAt: new Date(Date.now() - 2592000000).toISOString(),
    },
    {
      versionId: 'VER-002',
      platform: 'ANDROID',
      channel: 'GOOGLE_PLAY',
      versionName: '1.1.0',
      versionCode: 110,
      minAndroidVersionCode: 21,
      minIosVersionCode: null,
      downloadUrl: null,
      forceUpdate: false,
      releaseNotes: '优化连接稳定性，新增多语言支持',
      status: 'PUBLISHED',
      publishedAt: new Date(Date.now() - 1296000000).toISOString(),
      createdAt: new Date(Date.now() - 1296000000).toISOString(),
      updatedAt: new Date(Date.now() - 1296000000).toISOString(),
    },
    {
      versionId: 'VER-003',
      platform: 'ANDROID',
      channel: 'GOOGLE_PLAY',
      versionName: '1.2.0',
      versionCode: 120,
      minAndroidVersionCode: 21,
      minIosVersionCode: null,
      downloadUrl: null,
      forceUpdate: true,
      releaseNotes: '安全更新，修复已知问题',
      status: 'DRAFT',
      publishedAt: null,
      createdAt: new Date(Date.now() - 86400000).toISOString(),
      updatedAt: new Date(Date.now() - 86400000).toISOString(),
    },
    {
      versionId: 'VER-004',
      platform: 'IOS',
      channel: 'APP_STORE',
      versionName: '1.0.0',
      versionCode: 100,
      minAndroidVersionCode: null,
      minIosVersionCode: 14,
      downloadUrl: null,
      forceUpdate: false,
      releaseNotes: 'iOS 初始版本',
      status: 'PUBLISHED',
      publishedAt: new Date(Date.now() - 1728000000).toISOString(),
      createdAt: new Date(Date.now() - 1728000000).toISOString(),
      updatedAt: new Date(Date.now() - 1728000000).toISOString(),
    },
  ];

  listAppVersions(params: {
    page?: number;
    pageSize?: number;
    status?: string;
    channel?: string;
  }) {
    let items = [...this.versions];

    if (params.status) {
      items = items.filter((v) => v.status === params.status);
    }

    if (params.channel) {
      items = items.filter((v) => v.channel === params.channel);
    }

    // Sort by createdAt desc
    items = items.sort((a, b) => b.createdAt.localeCompare(a.createdAt));

    const page = Math.max(1, params.page ?? 1);
    const pageSize = Math.min(100, Math.max(1, params.pageSize ?? 20));
    const total = items.length;
    const start = (page - 1) * pageSize;
    const end = start + pageSize;
    const paginatedItems = items.slice(start, end);

    return {
      items: paginatedItems,
      page: {
        page,
        pageSize,
        total,
      },
    };
  }

  getLatestVersion(platform: string, channel: string): AppVersion | null {
    return (
      this.versions.find(
        (v) =>
          v.platform === platform &&
          v.channel === channel &&
          v.status === 'PUBLISHED',
      ) ?? null
    );
  }
}
