import { Injectable, Logger } from '@nestjs/common';
import { RuntimeStateRepository } from '../database/runtime-state.repository';
import { WalletService } from './wallet.service';

@Injectable()
export class WalletSnapshotService {
  private readonly logger = new Logger(WalletSnapshotService.name);

  constructor(
    private readonly runtimeStateRepository: RuntimeStateRepository,
    private readonly walletService: WalletService,
  ) {}

  async syncTrackedWalletSnapshotsOnce() {
    const accounts = await this.runtimeStateRepository.listAccounts();
    let syncedAccounts = 0;
    let syncedSnapshots = 0;

    for (const account of accounts) {
      try {
        const upserted = await this.walletService.syncWalletAssetSnapshotsForAccount(
          account.accountId,
        );
        syncedAccounts += 1;
        syncedSnapshots += upserted;
      } catch (error) {
        this.logger.warn(
          `Wallet snapshot sync failed for account ${account.accountId}`,
          error as Error,
        );
      }
    }

    return {
      syncedAccounts,
      syncedSnapshots,
    };
  }
}
