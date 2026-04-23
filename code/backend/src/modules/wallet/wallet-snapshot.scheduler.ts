import { Injectable, Logger } from '@nestjs/common';
import { Interval } from '@nestjs/schedule';
import { WalletSnapshotService } from './wallet-snapshot.service';

@Injectable()
export class WalletSnapshotScheduler {
  private readonly logger = new Logger(WalletSnapshotScheduler.name);
  private inFlight = false;

  constructor(private readonly walletSnapshotService: WalletSnapshotService) {}

  @Interval(30000)
  async syncTrackedWalletSnapshots() {
    if (this.inFlight) {
      return;
    }

    this.inFlight = true;
    try {
      await this.walletSnapshotService.syncTrackedWalletSnapshotsOnce();
    } catch (error) {
      this.logger.error('Wallet snapshot sync tick failed', error as Error);
    } finally {
      this.inFlight = false;
    }
  }
}
