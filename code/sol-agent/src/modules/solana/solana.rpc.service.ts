import { Injectable, Logger } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import {
  Connection,
  Keypair,
  ParsedTransactionWithMeta,
  PublicKey,
  clusterApiUrl,
} from '@solana/web3.js';

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

  /**
   * 获取 Solana RPC 连接
   * @param networkCode 网络代码，默认 solana-mainnet
   */
  getConnection(networkCode: string = 'solana-mainnet'): Connection {
    const network = networkCode.replace('solana-', '');

    if (this.connections.has(network)) {
      return this.connections.get(network)!;
    }

    const rpcUrl = this.configService.get<string>(
      `SOLANA_RPC_URL_${network.toUpperCase()}`,
    );

    let connection: Connection;
    if (rpcUrl) {
      connection = new Connection(rpcUrl, 'confirmed');
    } else {
      // 使用公共 RPC 端点（生产环境应使用私有 RPC）
      const clusterUrl =
        network === 'mainnet'
          ? clusterApiUrl('mainnet-beta')
          : network === 'devnet'
            ? clusterApiUrl('devnet')
            : clusterApiUrl('testnet');
      connection = new Connection(clusterUrl, 'confirmed');
    }

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
    error?: string;
  }> {
    // Mock 模式：返回模拟的健康状态
    if (this.useMockMode) {
      return {
        healthy: true,
        network: networkCode,
        slot: 123456789,
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
      };
    } catch (error) {
      this.logger.error(`RPC health check failed for ${networkCode}:`, error);
      return {
        healthy: false,
        network: networkCode,
        error: error instanceof Error ? error.message : String(error),
      };
    }
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
  ): Promise<{
    address: string;
    balance: number;
    balanceInSOL: string;
    networkCode: string;
  }> {
    // Mock 模式：返回模拟余额
    if (this.useMockMode) {
      return {
        address,
        balance: 0,
        balanceInSOL: '0',
        networkCode,
      };
    }

    const connection = this.getConnection(networkCode);
    const publicKey = new PublicKey(address);

    // 添加 10 秒超时
    const balance = await Promise.race([
      connection.getBalance(publicKey),
      new Promise<never>((_, reject) =>
        setTimeout(() => reject(new Error('RPC timeout')), 10000),
      ),
    ]);

    return {
      address,
      balance,
      balanceInSOL: (balance / 1e9).toFixed(9),
      networkCode,
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
}
