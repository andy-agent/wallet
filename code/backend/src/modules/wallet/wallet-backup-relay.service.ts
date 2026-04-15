import { Injectable } from '@nestjs/common';
import type { PersistedWalletSecretBackupRecord } from './wallet.types';

interface WalletBackupRelayResult {
  replicatedToBackupServer: boolean;
  backupServerReference: string | null;
  lastReplicationError: string | null;
}

@Injectable()
export class WalletBackupRelayService {
  async replicate(
    record: PersistedWalletSecretBackupRecord,
  ): Promise<WalletBackupRelayResult> {
    const baseUrl = process.env.WALLET_BACKUP_SERVER_URL?.trim();
    if (!baseUrl) {
      return {
        replicatedToBackupServer: false,
        backupServerReference: null,
        lastReplicationError: 'backup_server_not_configured',
      };
    }

    try {
      const response = await fetch(`${baseUrl.replace(/\/$/, '')}/internal/v1/wallet-secret-backups`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(process.env.WALLET_BACKUP_SERVER_API_KEY
            ? { 'X-Internal-Api-Key': process.env.WALLET_BACKUP_SERVER_API_KEY }
            : {}),
        },
        body: JSON.stringify(record),
      });

      if (!response.ok) {
        const text = await response.text();
        return {
          replicatedToBackupServer: false,
          backupServerReference: null,
          lastReplicationError: `backup_server_http_${response.status}:${text}`.slice(0, 512),
        };
      }

      const payload = (await response.json().catch(() => ({}))) as {
        data?: { backupServerReference?: string };
      };
      return {
        replicatedToBackupServer: true,
        backupServerReference:
          payload?.data?.backupServerReference ?? `${baseUrl}#${record.backupId}`,
        lastReplicationError: null,
      };
    } catch (error) {
      return {
        replicatedToBackupServer: false,
        backupServerReference: null,
        lastReplicationError:
          error instanceof Error ? error.message.slice(0, 512) : 'backup_server_request_failed',
      };
    }
  }
}
