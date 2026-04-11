import { Injectable, Logger } from '@nestjs/common';
import { SolanaRpcService } from '../solana/solana.rpc.service';
import { GenerateAddressRequestDto } from './dto/generate-address.request';

@Injectable()
export class AddressService {
  private readonly logger = new Logger(AddressService.name);
  // 简单的内存存储，生产环境应使用数据库
  private addressStore: Map<string, any> = new Map();

  constructor(private readonly solanaRpc: SolanaRpcService) {}

  /**
   * 生成真实的 Solana 地址
   * 使用 @solana/web3.js Keypair.generate()
   */
  generateAddress(body: GenerateAddressRequestDto) {
    const keypair = this.solanaRpc.generateKeypair();
    const networkCode = body.networkCode ?? 'solana-mainnet';

    const addressData = {
      accountId: body.accountId,
      networkCode,
      address: keypair.address,
      publicKey: keypair.publicKey,
      // 注意：secretKey 仅用于演示，生产环境应使用安全的密钥管理系统
      secretKey: keypair.secretKey,
      createdAt: new Date().toISOString(),
    };

    // 存储地址信息（生产环境应加密存储 secretKey）
    this.addressStore.set(body.accountId, addressData);

    this.logger.log(
      `Generated Solana address for account ${body.accountId}: ${keypair.address}`,
    );

    // 返回时不包含 secretKey
    return this.sanitizeAddressData(addressData);
  }

  /**
   * 查询 Solana 地址
   */
  getAddress(accountId: string) {
    const stored = this.addressStore.get(accountId);

    if (stored) {
      return this.sanitizeAddressData(stored);
    }

    // 如果未找到，返回占位信息
    return {
      accountId,
      networkCode: 'solana-mainnet',
      address: null,
      publicKey: null,
      createdAt: null,
      note: 'Address not found for this account',
    };
  }

  /**
   * 获取地址的原始数据（包含 secretKey，仅供内部使用）
   */
  getAddressInternal(accountId: string): any | null {
    return this.addressStore.get(accountId) || null;
  }

  private sanitizeAddressData(addressData: Record<string, any>) {
    const sanitized = { ...addressData };
    delete sanitized.secretKey;
    return sanitized;
  }
}
