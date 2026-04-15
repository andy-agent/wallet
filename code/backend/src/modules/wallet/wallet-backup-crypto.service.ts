import {
  Injectable,
  InternalServerErrorException,
} from '@nestjs/common';
import { createHash } from 'crypto';
import { spawn } from 'child_process';
import { resolve } from 'path';

interface WalletBackupPayload {
  accountId: string;
  walletId: string;
  walletName: string | null;
  secretType: 'MNEMONIC';
  mnemonic: string;
  mnemonicHash: string;
  mnemonicWordCount: number;
  sourceType: string | null;
  publicAddresses: Array<{
    networkCode: 'SOLANA' | 'TRON';
    assetCode: 'SOL' | 'TRX' | 'USDT';
    address: string;
    isDefault: boolean;
  }>;
  exportedAt: string;
}

@Injectable()
export class WalletBackupCryptoService {
  getRecoveryKeyVersion(): string {
    return process.env.WALLET_BACKUP_RECOVERY_KEY_VERSION?.trim() || 'v1';
  }

  getRecipientFingerprint(): string {
    const recipients = this.loadRecipients();
    return createHash('sha256')
      .update(recipients.join('\n'))
      .digest('hex');
  }

  async encryptBackup(payload: WalletBackupPayload): Promise<string> {
    const result = await this.runAgeEncryptScript({
      recipients: this.loadRecipients(),
      payload,
    });
    return result.ciphertext;
  }

  private loadRecipients(): string[] {
    const inline = process.env.WALLET_BACKUP_RECIPIENTS
      ?.split(',')
      .map((item) => item.trim())
      .filter((item) => item.length > 0) ?? [];
    if (inline.length > 0) {
      return inline;
    }
    throw new InternalServerErrorException({
      code: 'WALLET_BACKUP_RECIPIENTS_MISSING',
      message: 'Wallet backup recipients are not configured',
    });
  }

  private runAgeEncryptScript(input: {
    recipients: string[];
    payload: WalletBackupPayload;
  }): Promise<{ ciphertext: string }> {
    return new Promise((resolvePromise, reject) => {
      const scriptPath = resolve(process.cwd(), 'scripts/age-encrypt.mjs');
      const child = spawn(process.execPath, [scriptPath], {
        stdio: ['pipe', 'pipe', 'pipe'],
      });

      let stdout = '';
      let stderr = '';

      child.stdout.on('data', (chunk) => {
        stdout += chunk.toString();
      });
      child.stderr.on('data', (chunk) => {
        stderr += chunk.toString();
      });
      child.on('error', (error) => reject(error));
      child.on('close', (code) => {
        if (code !== 0) {
          reject(
            new InternalServerErrorException({
              code: 'WALLET_BACKUP_ENCRYPT_FAILED',
              message: stderr.trim() || `age encrypt script exited with ${code}`,
            }),
          );
          return;
        }
        try {
          resolvePromise(JSON.parse(stdout) as { ciphertext: string });
        } catch (error) {
          reject(
            new InternalServerErrorException({
              code: 'WALLET_BACKUP_ENCRYPT_FAILED',
              message:
                error instanceof Error
                  ? error.message
                  : 'Failed to parse age encryption output',
            }),
          );
        }
      });

      child.stdin.write(JSON.stringify(input));
      child.stdin.end();
    });
  }
}
