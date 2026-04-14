import { Injectable, Logger, ServiceUnavailableException } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import {
  EnsureMarzbanUserInput,
  MarzbanInbound,
  MarzbanUser,
} from './marzban.types';

interface TokenResponse {
  access_token: string;
}

interface InboundsResponse {
  vless?: MarzbanInbound[];
}

interface UserResponse {
  username: string;
  status?: string;
  expire?: number | null;
  subscription_url?: string;
  token?: string;
}

@Injectable()
export class MarzbanService {
  private readonly logger = new Logger(MarzbanService.name);
  private token: string | null = null;
  private cachedVlessInboundTag: string | null = null;
  private readonly mockUsers = new Map<string, MarzbanUser>();

  constructor(private readonly configService: ConfigService) {}

  async ensureUserForSubscription(
    input: EnsureMarzbanUserInput,
  ): Promise<MarzbanUser> {
    const username =
      input.existingUsername?.trim() || this.buildUsername(input.subscriptionId);

    if (this.isMockMode()) {
      const existing = this.mockUsers.get(username);
      const next: MarzbanUser = {
        username,
        status: 'active',
        expireAt: input.expireAt ?? existing?.expireAt ?? null,
        subscriptionUrl: this.resolveSubscriptionUrl(
          existing?.subscriptionUrl,
          `${username}-mock-token`,
        ),
      };
      this.mockUsers.set(username, next);
      return next;
    }

    const existing = await this.getUser(username);
    if (existing) {
      const updated = await this.modifyUser(username, {
        expire: this.toUnix(input.expireAt),
        status: 'active',
      });
      return this.toMarzbanUser(updated);
    }

    const inboundTag = await this.getPreferredVlessInboundTag();
    const created = await this.request<UserResponse>('/api/user', {
      method: 'POST',
      body: JSON.stringify({
        username,
        status: 'active',
        expire: this.toUnix(input.expireAt),
        proxies: { vless: {} },
        inbounds: { vless: [inboundTag] },
      }),
    });
    return this.toMarzbanUser(created);
  }

  normalizeSubscriptionUrl(
    subscriptionUrl?: string | null,
    tokenFallback = '',
  ) {
    return this.resolveSubscriptionUrl(subscriptionUrl, tokenFallback);
  }

  private async getUser(username: string): Promise<MarzbanUser | null> {
    try {
      const user = await this.request<UserResponse>(`/api/user/${username}`);
      return this.toMarzbanUser(user);
    } catch (error) {
      if (this.isHttpStatus(error, 404)) {
        return null;
      }
      throw error;
    }
  }

  private async modifyUser(
    username: string,
    body: Record<string, unknown>,
  ): Promise<UserResponse> {
    return this.request<UserResponse>(`/api/user/${username}`, {
      method: 'PUT',
      body: JSON.stringify(body),
    });
  }

  private async getPreferredVlessInboundTag(): Promise<string> {
    if (this.cachedVlessInboundTag) {
      return this.cachedVlessInboundTag;
    }

    const response = await this.request<InboundsResponse>('/api/inbounds');
    const tag = response.vless?.[0]?.tag;
    if (!tag) {
      throw new ServiceUnavailableException({
        code: 'MARZBAN_VLESS_INBOUND_MISSING',
        message: 'No Marzban VLESS inbound is available',
      });
    }

    this.cachedVlessInboundTag = tag;
    return tag;
  }

  private async request<T>(
    path: string,
    init?: RequestInit,
    allowRetry = true,
  ): Promise<T> {
    const headers = new Headers(init?.headers ?? {});
    headers.set('Accept', 'application/json');
    if (init?.body) {
      headers.set('Content-Type', 'application/json');
    }

    if (path !== '/api/admin/token') {
      headers.set('Authorization', `Bearer ${await this.getToken()}`);
    }

    const response = await fetch(`${this.getApiBaseUrl()}${path.replace('/api', '')}`, {
      ...init,
      headers,
    });

    if (response.status === 401 && path !== '/api/admin/token' && allowRetry) {
      this.token = null;
      return this.request<T>(path, init, false);
    }

    if (!response.ok) {
      const body = await response.text();
      throw Object.assign(new Error(body || response.statusText), {
        status: response.status,
      });
    }

    return (await response.json()) as T;
  }

  private async getToken(): Promise<string> {
    if (this.token) {
      return this.token;
    }

    const body = new URLSearchParams({
      username: this.getAdminUsername(),
      password: this.getAdminPassword(),
    });

    const response = await fetch(`${this.getApiBaseUrl()}/admin/token`, {
      method: 'POST',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body,
    });

    if (!response.ok) {
      const error = await response.text();
      throw new ServiceUnavailableException({
        code: 'MARZBAN_AUTH_FAILED',
        message: error || 'Failed to authenticate with Marzban',
      });
    }

    const payload = (await response.json()) as TokenResponse;
    this.token = payload.access_token;
    return this.token;
  }

  private toMarzbanUser(user: UserResponse): MarzbanUser {
    return {
      username: user.username,
      status: user.status ?? 'active',
      expireAt:
        typeof user.expire === 'number' && user.expire > 0
          ? new Date(user.expire * 1000).toISOString()
          : null,
      subscriptionUrl: this.resolveSubscriptionUrl(
        user.subscription_url,
        user.token ?? '',
      ),
    };
  }

  private resolveSubscriptionUrl(
    subscriptionUrl?: string | null,
    tokenFallback = '',
  ) {
    const raw = subscriptionUrl?.trim();
    if (raw) {
      if (/^https?:\/\//i.test(raw)) {
        return raw;
      }
      return `${this.getPanelBaseUrl()}${raw.startsWith('/') ? '' : '/'}${raw}`;
    }
    return `${this.getPanelBaseUrl()}/sub/${tokenFallback}`;
  }

  private buildUsername(subscriptionId: string) {
    return `cvpn_${subscriptionId.replace(/-/g, '').slice(0, 24)}`;
  }

  private toUnix(value?: string | null) {
    if (!value) {
      return undefined;
    }
    const time = new Date(value).getTime();
    if (Number.isNaN(time)) {
      return undefined;
    }
    return Math.floor(time / 1000);
  }

  private getPanelBaseUrl() {
    const configured =
      this.configService.get<string>('MARZBAN_BASE_URL')?.trim() ||
      'https://vpn.residential-agent.com';
    return configured.replace(/\/+$/, '').replace(/\/api$/, '');
  }

  private getApiBaseUrl() {
    return `${this.getPanelBaseUrl()}/api`;
  }

  private getAdminUsername() {
    return (
      this.configService.get<string>('MARZBAN_ADMIN_USERNAME')?.trim() || 'admin'
    );
  }

  private getAdminPassword() {
    const password = this.configService.get<string>('MARZBAN_ADMIN_PASSWORD')?.trim();
    if (!password) {
      throw new ServiceUnavailableException({
        code: 'MARZBAN_CONFIG_MISSING',
        message: 'Marzban admin password is not configured',
      });
    }
    return password;
  }

  private isMockMode() {
    return (
      this.configService.get<string>('MARZBAN_MOCK_MODE')?.toLowerCase() === 'true'
    );
  }

  private isHttpStatus(error: unknown, status: number) {
    return (
      typeof error === 'object' &&
      error !== null &&
      'status' in error &&
      (error as { status?: number }).status === status
    );
  }
}
