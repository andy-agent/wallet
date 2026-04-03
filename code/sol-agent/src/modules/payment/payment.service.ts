import { Injectable } from '@nestjs/common';
import { DetectPaymentRequestDto } from './dto/detect-payment.request';

@Injectable()
export class PaymentService {
  /**
   * 查询地址收款状态占位实现
   * TODO: 接入 Solana RPC 获取 account/transaction 信息
   */
  getPaymentStatus(address: string, networkCode?: string) {
    return {
      address,
      networkCode: networkCode ?? 'solana-mainnet',
      status: 'pending',
      receivedAmount: '0',
      expectedAmount: null,
      txHash: null,
      confirmations: 0,
      updatedAt: new Date().toISOString(),
      note: 'PLACEHOLDER: on-chain detection not implemented yet',
    };
  }

  /**
   * 主动检测指定地址收款占位实现
   */
  detectPayment(body: DetectPaymentRequestDto) {
    return {
      address: body.address,
      networkCode: body.networkCode ?? 'solana-mainnet',
      status: 'pending',
      receivedAmount: '0',
      expectedAmount: body.expectedAmount ?? null,
      txHash: null,
      confirmations: 0,
      updatedAt: new Date().toISOString(),
      note: 'PLACEHOLDER: on-chain detection not implemented yet',
    };
  }
}
