import { Injectable } from '@nestjs/common';
import { GenerateAddressRequestDto } from './dto/generate-address.request';

@Injectable()
export class AddressService {
  /**
   * 生成 Solana 地址占位实现
   * TODO: 接入 @solana/web3.js Keypair.generate() 并持久化
   */
  generateAddress(body: GenerateAddressRequestDto) {
    return {
      accountId: body.accountId,
      networkCode: body.networkCode ?? 'solana-mainnet',
      address: `sol_placeholder_${body.accountId}_${Date.now()}`,
      publicKey: null,
      createdAt: new Date().toISOString(),
      note: 'PLACEHOLDER: not a real on-chain address yet',
    };
  }

  /**
   * 查询 Solana 地址占位实现
   */
  getAddress(accountId: string) {
    return {
      accountId,
      networkCode: 'solana-mainnet',
      address: `sol_placeholder_${accountId}`,
      publicKey: null,
      createdAt: new Date().toISOString(),
      note: 'PLACEHOLDER: not a real on-chain address yet',
    };
  }
}
