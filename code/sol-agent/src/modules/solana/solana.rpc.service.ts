import { Injectable, Logger } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import {
  Connection,
  Keypair,
  ParsedTransactionWithMeta,
  PublicKey,
  ParsedAccountData,
  SendOptions,
  clusterApiUrl,
} from '@solana/web3.js';

const TOKEN_PROGRAM_ID = new PublicKey(
  'TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA',
);
const ASSOCIATED_TOKEN_PROGRAM_ID = new PublicKey(
  'ATokenGPvbdGVxr1b2hvZbsiqW5xWH25efTNsLJA8knL',
);
const LAMPORTS_PER_SOL = 1_000_000_000n;

export interface RpcSignatureInfo {
  signature: string;
  slot: number;
  err: unknown;
  memo: string | null;
  blockTime: number | null;
  confirmationStatus: string | null;
}

@Injectable()
export class SolanaRpcService {
  private readonly logger = new Logger(SolanaRpcService.name);
  private connections: Map<string, Connection> = new Map();
  private useMockMode: boolean;

  constructor(private readonly configService: ConfigService) {
    // 在测试环境中可以使用 mock 模式
    this.useMockMode =
      this.configService.get<string>('SOLANA_RPC_MODE') === 'mock';
  }

  getPreferredRpcUrl(networkCode: string = 'solana-mainnet'): string {
    return this.getOrderedRpcUrls(networkCode)[0];
  }

  getPreferredWsUrl(networkCode: string = 'solana-mainnet'): string {
    return this.getOrderedWsUrls(networkCode)[0];
  }

  getOrderedRpcUrls(networkCode: string = 'solana-mainnet'): string[] {
    const network = this.normalizeNetwork(networkCode);
    const configured = this.readUrlList(
      `SOLANA_RPC_URL_${network.toUpperCase()}S`,
      `SOLANA_RPC_URL_${network.toUpperCase()}`,
      'SOLANA_RPC_URLS',
      'SOLANA_RPC_URL',
    );

    if (configured.length > 0) {
      return configured;
    }

    return [this.getDefaultRpcUrl(network)];
  }

  getOrderedWsUrls(networkCode: string = 'solana-mainnet'): string[] {
    const network = this.normalizeNetwork(networkCode);
    const configured = this.readUrlList(
      `SOLANA_WS_URL_${network.toUpperCase()}S`,
      `SOLANA_WS_URL_${network.toUpperCase()}`,
      'SOLANA_WS_URLS',
      'SOLANA_WS_URL',
    );

    if (configured.length > 0) {
      return configured;
    }

    return this.getOrderedRpcUrls(networkCode).map((url) =>
      this.deriveWsUrlFromRpcUrl(url),
    );
  }

  /**
   * 获取 Solana RPC 连接
   * @param networkCode 网络代码，默认 solana-mainnet
   */
  getConnection(networkCode: string = 'solana-mainnet'): Connection {
    const network = this.normalizeNetwork(networkCode);

    if (this.connections.has(network)) {
      return this.connections.get(network)!;
    }

    const rpcUrl = this.getPreferredRpcUrl(networkCode);
    const wsUrl = this.getPreferredWsUrl(networkCode);
    const connection = new Connection(rpcUrl, {
      commitment: 'confirmed',
      wsEndpoint: wsUrl,
    });

    this.connections.set(network, connection);
    return connection;
  }

  /**
   * 检查 RPC 连接健康状态
   */
  async checkHealth(networkCode: string = 'solana-mainnet'): Promise<{
    healthy: boolean;
    network: string;
    slot?: number;
    rpcUrl?: string;
    wsUrl?: string;
    error?: string;
  }> {
    // Mock 模式：返回模拟的健康状态
    if (this.useMockMode) {
      return {
        healthy: true,
        network: networkCode,
        slot: 123456789,
        rpcUrl: this.getPreferredRpcUrl(networkCode),
        wsUrl: this.getPreferredWsUrl(networkCode),
      };
    }

    try {
      const connection = this.getConnection(networkCode);
      // 添加 5 秒超时
      const slot = await Promise.race([
        connection.getSlot(),
        new Promise<never>((_, reject) =>
          setTimeout(() => reject(new Error('RPC timeout')), 5000),
        ),
      ]);
      return {
        healthy: true,
        network: networkCode,
        slot,
        rpcUrl: this.getPreferredRpcUrl(networkCode),
        wsUrl: this.getPreferredWsUrl(networkCode),
      };
    } catch (error) {
      this.logger.error(`RPC health check failed for ${networkCode}:`, error);
      return {
        healthy: false,
        network: networkCode,
        rpcUrl: this.getPreferredRpcUrl(networkCode),
        wsUrl: this.getPreferredWsUrl(networkCode),
        error: error instanceof Error ? error.message : String(error),
      };
    }
  }

  async checkRealtimeHealth(networkCode: string = 'solana-mainnet'): Promise<{
    healthy: boolean;
    network: string;
    slot?: number;
    wsUrl?: string;
    error?: string;
  }> {
    if (this.useMockMode) {
      return {
        healthy: true,
        network: networkCode,
        slot: 123456789,
        wsUrl: this.getPreferredWsUrl(networkCode),
      };
    }

    const connection = this.getConnection(networkCode);
    const wsUrl = this.getPreferredWsUrl(networkCode);

    return new Promise((resolve) => {
      let subscriptionId: number | null = null;
      const cleanup = async () => {
        if (subscriptionId !== null) {
          try {
            await connection.removeSlotChangeListener(subscriptionId);
          } catch {
            // Ignore cleanup errors.
          }
        }
      };

      const timeout = setTimeout(async () => {
        await cleanup();
        resolve({
          healthy: false,
          network: networkCode,
          wsUrl,
          error: 'WSS timeout',
        });
      }, 5000);

      try {
        subscriptionId = connection.onSlotChange(async (slotInfo) => {
          clearTimeout(timeout);
          await cleanup();
          resolve({
            healthy: true,
            network: networkCode,
            slot: slotInfo.slot,
            wsUrl,
          });
        });
      } catch (error) {
        clearTimeout(timeout);
        resolve({
          healthy: false,
          network: networkCode,
          wsUrl,
          error: error instanceof Error ? error.message : String(error),
        });
      }
    });
  }

  /**
   * 生成新的 Solana 地址
   */
  generateKeypair(): {
    publicKey: string;
    secretKey: string;
    address: string;
  } {
    const keypair = Keypair.generate();
    return {
      publicKey: keypair.publicKey.toBase58(),
      secretKey: Buffer.from(keypair.secretKey).toString('base64'),
      address: keypair.publicKey.toBase58(),
    };
  }

  /**
   * 从 secret key 恢复 keypair
   */
  restoreKeypair(secretKeyBase64: string): Keypair {
    const secretKey = Buffer.from(secretKeyBase64, 'base64');
    return Keypair.fromSecretKey(secretKey);
  }

  /**
   * 查询地址余额（lamports）
   */
  async getBalance(
    address: string,
    networkCode: string = 'solana-mainnet',
    mintAddress?: string | null,
  ): Promise<{
    address: string;
    balance: number;
    balanceUiAmount?: string;
    balanceInSOL: string;
    decimals?: number;
    networkCode: string;
    mintAddress?: string | null;
  }> {
    // Mock 模式：返回模拟余额
    if (this.useMockMode) {
      return {
        address,
        balance: 0,
        balanceUiAmount: '0',
        balanceInSOL: '0',
        decimals: mintAddress ? 6 : 9,
        networkCode,
        mintAddress: mintAddress ?? null,
      };
    }

    const connection = this.getConnection(networkCode);
    const owner = new PublicKey(address);

    if (!mintAddress) {
      const balance = await Promise.race([
        connection.getBalance(owner),
        new Promise<never>((_, reject) =>
          setTimeout(() => reject(new Error('RPC timeout')), 10000),
        ),
      ]);

      return {
        address,
        balance,
        balanceUiAmount: (balance / 1e9).toFixed(9),
        balanceInSOL: (balance / 1e9).toFixed(9),
        decimals: 9,
        networkCode,
        mintAddress: null,
      };
    }

    const mint = new PublicKey(mintAddress);
    const tokenAccounts = await Promise.race([
      connection.getParsedTokenAccountsByOwner(
        owner,
        { programId: TOKEN_PROGRAM_ID },
        'confirmed',
      ),
      new Promise<never>((_, reject) =>
        setTimeout(() => reject(new Error('RPC timeout')), 10000),
      ),
    ]);

    const matchingAccounts = tokenAccounts.value.filter((accountInfo) => {
      const parsed = accountInfo.account.data as ParsedAccountData;
      const accountMint =
        (parsed.parsed as { info?: { mint?: string } } | undefined)?.info?.mint;
      return accountMint === mint.toBase58();
    });

    if (matchingAccounts.length === 0) {
      return {
        address,
        balance: 0,
        balanceUiAmount: '0',
        balanceInSOL: '0',
        decimals: 6,
        networkCode,
        mintAddress: mint.toBase58(),
      };
    }

    let totalRaw = 0n;
    let decimals = 6;

    for (const accountInfo of matchingAccounts) {
      const parsed = accountInfo.account.data as ParsedAccountData;
      const tokenAmount = (
        parsed.parsed as {
          info?: {
            tokenAmount?: {
              amount?: string;
              decimals?: number;
            };
          };
        }
      )?.info?.tokenAmount;

      if (!tokenAmount?.amount) {
        continue;
      }

      totalRaw += BigInt(tokenAmount.amount);
      if (Number.isFinite(tokenAmount.decimals)) {
        decimals = Number(tokenAmount.decimals);
      }
    }

    const rawAmount =
      totalRaw > BigInt(Number.MAX_SAFE_INTEGER)
        ? Number.MAX_SAFE_INTEGER
        : Number(totalRaw);

    return {
      address,
      balance: Number.isFinite(rawAmount) ? rawAmount : 0,
      balanceUiAmount: this.formatUiAmount(totalRaw, decimals),
      balanceInSOL: this.formatUiAmount(totalRaw, decimals),
      decimals,
      networkCode,
      mintAddress: mint.toBase58(),
    };
  }

  /**
   * 查询地址最近的交易签名
   */
  async getRecentTransactions(
    address: string,
    networkCode: string = 'solana-mainnet',
    limit: number = 10,
  ): Promise<{
    address: string;
    networkCode: string;
    signatures: string[];
  }> {
    // Mock 模式：返回空交易列表
    if (this.useMockMode) {
      return {
        address,
        networkCode,
        signatures: [],
      };
    }

    const signatureInfo = await this.getSignatureInfos(address, networkCode, {
      limit,
    });

    return {
      address,
      networkCode,
      signatures: signatureInfo.signatures.map((sig) => sig.signature),
    };
  }

  /**
   * 查询地址最近的交易签名元数据
   */
  async getSignatureInfos(
    address: string,
    networkCode: string = 'solana-mainnet',
    options?: {
      limit?: number;
      beforeSignature?: string;
    },
  ): Promise<{
    address: string;
    networkCode: string;
    signatures: RpcSignatureInfo[];
  }> {
    if (this.useMockMode) {
      return {
        address,
        networkCode,
        signatures: [],
      };
    }

    const connection = this.getConnection(networkCode);
    const publicKey = new PublicKey(address);

    const signatures = await Promise.race([
      connection.getSignaturesForAddress(publicKey, {
        limit: options?.limit,
        before: options?.beforeSignature,
      }),
      new Promise<never>((_, reject) =>
        setTimeout(() => reject(new Error('RPC timeout')), 10000),
      ),
    ]);

    return {
      address,
      networkCode,
      signatures: signatures.map((signatureInfo) => ({
        signature: signatureInfo.signature,
        slot: signatureInfo.slot,
        err: signatureInfo.err,
        memo: signatureInfo.memo ?? null,
        blockTime: signatureInfo.blockTime ?? null,
        confirmationStatus: signatureInfo.confirmationStatus ?? null,
      })),
    };
  }

  /**
   * 获取交易详情
   */
  async getTransaction(
    signature: string,
    networkCode: string = 'solana-mainnet',
  ): Promise<{
    signature: string;
    networkCode: string;
    transaction: any | null;
  }> {
    // Mock 模式：返回 null
    if (this.useMockMode) {
      return {
        signature,
        networkCode,
        transaction: null,
      };
    }

    const connection = this.getConnection(networkCode);

    // 添加 10 秒超时
    const transaction = await Promise.race([
      connection.getTransaction(signature, {
        maxSupportedTransactionVersion: 0,
      }),
      new Promise<never>((_, reject) =>
        setTimeout(() => reject(new Error('RPC timeout')), 10000),
      ),
    ]);

    return {
      signature,
      networkCode,
      transaction,
    };
  }

  /**
   * 获取已解析的交易详情
   */
  async getParsedTransaction(
    signature: string,
    networkCode: string = 'solana-mainnet',
  ): Promise<{
    signature: string;
    networkCode: string;
    transaction: ParsedTransactionWithMeta | null;
  }> {
    if (this.useMockMode) {
      return {
        signature,
        networkCode,
        transaction: null,
      };
    }

    const connection = this.getConnection(networkCode);

    const transaction = await Promise.race([
      connection.getParsedTransaction(signature, {
        commitment: 'confirmed',
        maxSupportedTransactionVersion: 0,
      }),
      new Promise<never>((_, reject) =>
        setTimeout(() => reject(new Error('RPC timeout')), 10000),
      ),
    ]);

    return {
      signature,
      networkCode,
      transaction,
    };
  }

  /**
   * 批量获取已解析的交易详情，适合扫描场景
   */
  async getParsedTransactions(
    signatures: string[],
    networkCode: string = 'solana-mainnet',
  ): Promise<{
    networkCode: string;
    transactions: Array<{
      signature: string;
      transaction: ParsedTransactionWithMeta | null;
    }>;
  }> {
    if (this.useMockMode) {
      return {
        networkCode,
        transactions: signatures.map((signature) => ({
          signature,
          transaction: null,
        })),
      };
    }

    const connection = this.getConnection(networkCode);

    const transactions = await Promise.race([
      connection.getParsedTransactions(signatures, {
        commitment: 'confirmed',
        maxSupportedTransactionVersion: 0,
      }),
      new Promise<never>((_, reject) =>
        setTimeout(() => reject(new Error('RPC timeout')), 10000),
      ),
    ]);

    return {
      networkCode,
      transactions: signatures.map((signature, index) => ({
        signature,
        transaction: transactions[index] ?? null,
      })),
    };
  }

  deriveAssociatedTokenAddress(
    ownerAddress: string,
    mintAddress: string,
  ): string {
    const owner = new PublicKey(ownerAddress);
    const mint = new PublicKey(mintAddress);
    const [associatedTokenAddress] = PublicKey.findProgramAddressSync(
      [owner.toBuffer(), TOKEN_PROGRAM_ID.toBuffer(), mint.toBuffer()],
      ASSOCIATED_TOKEN_PROGRAM_ID,
    );
    return associatedTokenAddress.toBase58();
  }

  async precheckTransfer(
    address: string,
    networkCode: string = 'solana-mainnet',
    mintAddress?: string | null,
  ): Promise<{
    toAddressNormalized: string;
    estimatedFee: string;
    mintAddress: string | null;
    networkCode: string;
  }> {
    const connection = this.getConnection(networkCode);
    const toPubkey = new PublicKey(address.trim());
    await Promise.race([
      connection.getLatestBlockhash('confirmed'),
      new Promise<never>((_, reject) =>
        setTimeout(() => reject(new Error('RPC timeout')), 10000),
      ),
    ]);
    if (mintAddress) {
      new PublicKey(mintAddress.trim());
    }
    return {
      toAddressNormalized: toPubkey.toBase58(),
      estimatedFee: this.formatUiAmount(5000n, 9),
      mintAddress: mintAddress?.trim() || null,
      networkCode,
    };
  }

  async broadcastTransaction(
    serializedTx: string,
    networkCode: string = 'solana-mainnet',
    options?: SendOptions,
  ): Promise<{
    signature: string;
    confirmed: boolean;
    slot?: number;
  }> {
    const connection = this.getConnection(networkCode);
    const raw = Buffer.from(serializedTx, 'base64');
    const signature = await Promise.race([
      connection.sendRawTransaction(raw, {
        skipPreflight: false,
        maxRetries: options?.maxRetries,
      }),
      new Promise<never>((_, reject) =>
        setTimeout(() => reject(new Error('RPC timeout')), 10000),
      ),
    ]);
    const confirmation = await Promise.race([
      connection.confirmTransaction(signature, 'confirmed'),
      new Promise<never>((_, reject) =>
        setTimeout(() => reject(new Error('RPC timeout')), 15000),
      ),
    ]);

    let slot: number | undefined;
    try {
      const statuses = await Promise.race([
        connection.getSignatureStatuses([signature], {
          searchTransactionHistory: true,
        }),
        new Promise<never>((_, reject) =>
          setTimeout(() => reject(new Error('RPC timeout')), 10000),
        ),
      ]);
      slot = statuses.value[0]?.slot;
    } catch (error) {
      this.logger.warn(
        `Failed to fetch signature status for ${signature}: ${
          error instanceof Error ? error.message : String(error)
        }`,
      );
    }

    return {
      signature,
      confirmed: !confirmation.value.err,
      slot,
    };
  }

  private formatUiAmount(amount: bigint, decimals: number): string {
    if (decimals <= 0) {
      return amount.toString();
    }

    const divisor = 10n ** BigInt(decimals);
    const whole = amount / divisor;
    const fraction = amount % divisor;

    if (fraction === 0n) {
      return whole.toString();
    }

    return `${whole}.${fraction
      .toString()
      .padStart(decimals, '0')
      .replace(/0+$/, '')}`;
  }

  private normalizeNetwork(networkCode: string): 'mainnet' | 'devnet' | 'testnet' {
    const normalized = networkCode.replace('solana-', '').toLowerCase();
    if (normalized === 'devnet' || normalized === 'testnet') {
      return normalized;
    }
    return 'mainnet';
  }

  private getDefaultRpcUrl(network: 'mainnet' | 'devnet' | 'testnet') {
    return network === 'mainnet'
      ? clusterApiUrl('mainnet-beta')
      : network === 'devnet'
        ? clusterApiUrl('devnet')
        : clusterApiUrl('testnet');
  }

  private deriveWsUrlFromRpcUrl(rpcUrl: string) {
    if (rpcUrl.startsWith('https://')) {
      return `wss://${rpcUrl.slice('https://'.length)}`;
    }
    if (rpcUrl.startsWith('http://')) {
      return `ws://${rpcUrl.slice('http://'.length)}`;
    }
    return rpcUrl;
  }

  private readUrlList(...keys: string[]): string[] {
    return Array.from(
      new Set(
        keys.flatMap((key) =>
          (this.configService.get<string>(key) ?? '')
            .split(',')
            .map((item) => item.trim())
            .filter((item) => item.length > 0),
        ),
      ),
    );
  }
}
