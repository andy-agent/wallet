export interface MarzbanInbound {
  tag: string;
  protocol: string;
  network: string;
  tls: string;
  port: number;
}

export interface MarzbanUser {
  username: string;
  status: string;
  expireAt: string | null;
  subscriptionUrl: string;
}

export interface EnsureMarzbanUserInput {
  subscriptionId: string;
  existingUsername?: string | null;
  expireAt?: string | null;
  isUnlimitedTraffic?: boolean;
}

export interface MarzbanHostSetting {
  inboundTag: string;
  remark: string;
  address: string;
  port: number;
  sni: string | null;
  host: string | null;
  security: string | null;
  allowInsecure: boolean;
  isDisabled: boolean;
}
