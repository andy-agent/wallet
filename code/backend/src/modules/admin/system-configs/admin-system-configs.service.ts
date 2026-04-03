import { Injectable } from '@nestjs/common';

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
  private readonly configs: SystemConfig[] = [
    {
      configKey: 'WITHDRAWAL_MIN_AMOUNT',
      configValue: '10',
      valueType: 'NUMBER',
      description: 'Minimum withdrawal amount in USDT',
      scope: 'REFERRAL',
      isEditable: true,
      updatedAt: new Date().toISOString(),
      updatedBy: 'admin-001',
    },
    {
      configKey: 'VPN_SESSION_TIMEOUT_MINUTES',
      configValue: '15',
      valueType: 'NUMBER',
      description: 'VPN config validity duration in minutes',
      scope: 'VPN',
      isEditable: true,
      updatedAt: new Date(Date.now() - 86400000).toISOString(),
      updatedBy: 'admin-001',
    },
    {
      configKey: 'ORDER_EXPIRY_MINUTES',
      configValue: '30',
      valueType: 'NUMBER',
      description: 'Order payment window in minutes',
      scope: 'PAYMENT',
      isEditable: true,
      updatedAt: new Date(Date.now() - 172800000).toISOString(),
      updatedBy: 'admin-002',
    },
    {
      configKey: 'REFERRAL_COMMISSION_RATE_LEVEL1',
      configValue: '0.15',
      valueType: 'NUMBER',
      description: 'Level 1 referral commission rate',
      scope: 'REFERRAL',
      isEditable: true,
      updatedAt: new Date(Date.now() - 259200000).toISOString(),
      updatedBy: 'admin-001',
    },
    {
      configKey: 'REFERRAL_COMMISSION_RATE_LEVEL2',
      configValue: '0.05',
      valueType: 'NUMBER',
      description: 'Level 2 referral commission rate',
      scope: 'REFERRAL',
      isEditable: true,
      updatedAt: new Date(Date.now() - 259200000).toISOString(),
      updatedBy: 'admin-001',
    },
    {
      configKey: 'SYSTEM_MAINTENANCE_MODE',
      configValue: 'false',
      valueType: 'BOOLEAN',
      description: 'Global maintenance mode flag',
      scope: 'GLOBAL',
      isEditable: true,
      updatedAt: new Date().toISOString(),
      updatedBy: null,
    },
  ];

  listSystemConfigs(params: { scope?: string }) {
    let items = [...this.configs];

    if (params.scope) {
      items = items.filter((c) => c.scope === params.scope);
    }

    return {
      items,
      page: {
        page: 1,
        pageSize: items.length,
        total: items.length,
      },
    };
  }

  getConfig(configKey: string): SystemConfig | null {
    return this.configs.find((c) => c.configKey === configKey) ?? null;
  }

  updateConfig(
    configKey: string,
    updates: Partial<Pick<SystemConfig, 'configValue' | 'updatedBy'>>,
  ): SystemConfig | null {
    const config = this.configs.find((c) => c.configKey === configKey);
    if (!config || !config.isEditable) {
      return null;
    }
    Object.assign(config, updates, { updatedAt: new Date().toISOString() });
    return config;
  }
}
