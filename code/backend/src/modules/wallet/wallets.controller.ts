import {
  Body,
  Controller,
  Delete,
  Get,
  Headers,
  Param,
  Patch,
  Post,
  Query,
} from '@nestjs/common';
import { CreateCustomTokenRequestDto } from './dto/create-custom-token.request';
import { CreateMnemonicWalletRequestDto } from './dto/create-mnemonic-wallet.request';
import { ImportWatchWalletRequestDto } from './dto/import-watch-wallet.request';
import { UpsertWalletSecretBackupRequestDto } from './dto/upsert-wallet-secret-backup.request';
import { UpdateWalletRequestDto } from './dto/update-wallet.request';
import { WalletService } from './wallet.service';

@Controller('client/v1/wallets')
export class WalletsController {
  constructor(private readonly walletService: WalletService) {}

  @Get()
  listWallets(@Headers('authorization') authorization?: string) {
    return this.walletService.listWallets(this.extractBearer(authorization));
  }

  @Get(':walletId')
  getWallet(
    @Headers('authorization') authorization: string | undefined,
    @Param('walletId') walletId: string,
  ) {
    return this.walletService.getWallet(this.extractBearer(authorization), walletId);
  }

  @Get(':walletId/chain-accounts')
  listWalletChainAccounts(
    @Headers('authorization') authorization: string | undefined,
    @Param('walletId') walletId: string,
  ) {
    return this.walletService.listWalletChainAccounts(
      this.extractBearer(authorization),
      walletId,
    );
  }

  @Get(':walletId/custom-tokens')
  listCustomTokens(
    @Headers('authorization') authorization: string | undefined,
    @Param('walletId') walletId: string,
    @Query('chainId') chainId?: string,
  ) {
    return this.walletService.listCustomTokens(
      this.extractBearer(authorization),
      walletId,
      chainId,
    );
  }

  @Post(':walletId/custom-tokens')
  createCustomToken(
    @Headers('authorization') authorization: string | undefined,
    @Param('walletId') walletId: string,
    @Body() body: CreateCustomTokenRequestDto,
  ) {
    return this.walletService.createCustomToken(
      this.extractBearer(authorization),
      walletId,
      body,
    );
  }

  @Delete(':walletId/custom-tokens/:customTokenId')
  deleteCustomToken(
    @Headers('authorization') authorization: string | undefined,
    @Param('walletId') walletId: string,
    @Param('customTokenId') customTokenId: string,
  ) {
    return this.walletService.deleteCustomToken(
      this.extractBearer(authorization),
      walletId,
      customTokenId,
    );
  }

  @Post(':walletId/secret-backup')
  upsertWalletSecretBackup(
    @Headers('authorization') authorization: string | undefined,
    @Param('walletId') walletId: string,
    @Body() body: UpsertWalletSecretBackupRequestDto,
  ) {
    return this.walletService.upsertWalletSecretBackupV2(
      this.extractBearer(authorization),
      walletId,
      body,
    );
  }

  @Get(':walletId/secret-backup')
  getWalletSecretBackupMetadata(
    @Headers('authorization') authorization: string | undefined,
    @Param('walletId') walletId: string,
  ) {
    return this.walletService.getWalletSecretBackupMetadataByWalletId(
      this.extractBearer(authorization),
      walletId,
    );
  }

  @Post('create-mnemonic')
  createMnemonicWallet(
    @Headers('authorization') authorization: string | undefined,
    @Body() body: CreateMnemonicWalletRequestDto,
  ) {
    return this.walletService.createMnemonicWallet(
      this.extractBearer(authorization),
      body,
    );
  }

  @Post('import/mnemonic')
  importMnemonicWallet(
    @Headers('authorization') authorization: string | undefined,
    @Body() body: CreateMnemonicWalletRequestDto,
  ) {
    return this.walletService.importMnemonicWallet(
      this.extractBearer(authorization),
      body,
    );
  }

  @Post('import/watch-only')
  importWatchOnlyWallet(
    @Headers('authorization') authorization: string | undefined,
    @Body() body: ImportWatchWalletRequestDto,
  ) {
    return this.walletService.importWatchOnlyWallet(
      this.extractBearer(authorization),
      body,
    );
  }

  @Patch(':walletId')
  updateWallet(
    @Headers('authorization') authorization: string | undefined,
    @Param('walletId') walletId: string,
    @Body() body: UpdateWalletRequestDto,
  ) {
    return this.walletService.updateWallet(
      this.extractBearer(authorization),
      walletId,
      body,
    );
  }

  @Post(':walletId/set-default')
  setDefaultWallet(
    @Headers('authorization') authorization: string | undefined,
    @Param('walletId') walletId: string,
  ) {
    return this.walletService.setDefaultWallet(
      this.extractBearer(authorization),
      walletId,
    );
  }

  @Post('reset')
  resetWalletDomain(@Headers('authorization') authorization: string | undefined) {
    return this.walletService.resetWalletDomain(this.extractBearer(authorization));
  }

  private extractBearer(authorization?: string) {
    if (!authorization?.startsWith('Bearer ')) {
      return '';
    }
    return authorization.slice('Bearer '.length);
  }
}
