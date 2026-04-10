import { Injectable } from '@nestjs/common';
import { PostgresDataAccessService } from '../../database/postgres-data-access.service';

export interface SystemConfig {
  configKey: string;
  configValue: string;
  valueType: 'STRING' | 'NUMBER' | 'BOOLEAN' | 'JSON';
  description: string;
  scope: 'GLOBAL' | 'VPN' | 'PAYMENT' | 'REFERRAL';
  isEditable: boolean;
  updatedAt: string;
  updatedBy: string | null;
}

@Injectable()
export class AdminSystemConfigsService {
  constructor(private readonly postgresDataAccessService: PostgresDataAccessService) {}

  async listSystemConfigs(params: { page?: number; pageSize?: number; scope?: string }) {
    return this.postgresDataAccessService.listSystemConfigs(params);
  }

  async getConfig(configKey: string): Promise<SystemConfig | null> {
    const result = await this.postgresDataAccessService.listSystemConfigs({});
    return (
      (result.items.find((item) => item.configKey === configKey) as SystemConfig | undefined) ??
      null
    );
  }

  updateConfig(
    configKey: string,
    updates: Partial<Pick<SystemConfig, 'configValue' | 'updatedBy'>>,
  ): SystemConfig | null {
    return null;
  }
}
