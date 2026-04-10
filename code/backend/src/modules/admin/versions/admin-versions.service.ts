import { Injectable } from '@nestjs/common';
import { PostgresDataAccessService } from '../../database/postgres-data-access.service';

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
  constructor(private readonly postgresDataAccessService: PostgresDataAccessService) {}

  async listAppVersions(params: {
    page?: number;
    pageSize?: number;
    status?: string;
    channel?: string;
  }) {
    return this.postgresDataAccessService.listAppVersions(params);
  }

  async getLatestVersion(platform: string, channel: string): Promise<AppVersion | null> {
    const result = await this.postgresDataAccessService.listAppVersions({
      page: 1,
      pageSize: 1,
      status: 'PUBLISHED',
      channel,
    });
    const item = result.items.find((version) => version.platform === platform);
    return (item as AppVersion | undefined) ?? null;
  }
}
