import { Injectable, Logger } from '@nestjs/common';
import { SolanaRpcService } from '../solana/solana.rpc.service';
import { DetectPaymentRequestDto } from './dto/detect-payment.request';

@Injectable()
export class PaymentService {
  private readonly logger = new Logger(PaymentService.name);
  // 存储检测到的支付状态
  private paymentStore: Map<string, any> = new Map();

  constructor(private readonly solanaRpc: SolanaRpcService) {}

  /**
   * 查询地址收款状态
   * 调用真实 Solana RPC 获取余额和交易信息
   */
  async getPaymentStatus(address: string, networkCode?: string) {
    const effectiveNetworkCode = networkCode ?? 'solana-mainnet';
    
    try {
      // 查询余额
      const balanceInfo = await this.solanaRpc.getBalance(address, effectiveNetworkCode);
      
      // 查询最近交易
      const txInfo = await this.solanaRpc.getRecentTransactions(address, effectiveNetworkCode, 5);
      
      // 判断收款状态
      const hasBalance = balanceInfo.balance > 0;
      const hasTransactions = txInfo.signatures.length > 0;
      
      let status = 'pending';
      if (hasBalance && hasTransactions) {
        status = 'received';
      } else if (hasBalance) {
        status = 'received';
      }

      const result = {
        address,
        networkCode: effectiveNetworkCode,
        status,
        receivedAmount: balanceInfo.balanceInSOL,
        expectedAmount: null,
        txHash: hasTransactions ? txInfo.signatures[0] : null,
        confirmations: hasTransactions ? 1 : 0, // 简化处理
        balance: balanceInfo.balance,
        recentTxCount: txInfo.signatures.length,
        updatedAt: new Date().toISOString(),
      };

      // 存储状态
      this.paymentStore.set(address, result);
      
      return result;
    } catch (error) {
      this.logger.error(`Failed to get payment status for ${address}:`, error);
      
      // 返回错误状态
      return {
        address,
        networkCode: effectiveNetworkCode,
        status: 'error',
        receivedAmount: '0',
        expectedAmount: null,
        txHash: null,
        confirmations: 0,
        error: error instanceof Error ? error.message : String(error),
        updatedAt: new Date().toISOString(),
      };
    }
  }

  /**
   * 主动检测指定地址收款
   * 调用真实 Solana RPC 进行余额和交易检测
   */
  async detectPayment(body: DetectPaymentRequestDto) {
    const effectiveNetworkCode = body.networkCode ?? 'solana-mainnet';
    
    try {
      this.logger.log(`Detecting payment for ${body.address} on ${effectiveNetworkCode}`);
      
      // 获取余额信息
      const balanceInfo = await this.solanaRpc.getBalance(body.address, effectiveNetworkCode);
      
      // 获取最近交易
      const txInfo = await this.solanaRpc.getRecentTransactions(
        body.address, 
        effectiveNetworkCode, 
        10
      );

      // 判断是否收到款项
      const receivedAmount = parseFloat(balanceInfo.balanceInSOL);
      const expectedAmount = body.expectedAmount ? parseFloat(body.expectedAmount) : null;
      
      let status = 'pending';
      if (expectedAmount !== null && receivedAmount >= expectedAmount) {
        status = 'confirmed';
      } else if (receivedAmount > 0) {
        status = 'partial';
      }

      const result = {
        address: body.address,
        networkCode: effectiveNetworkCode,
        status,
        receivedAmount: balanceInfo.balanceInSOL,
        expectedAmount: body.expectedAmount ?? null,
        txHash: txInfo.signatures.length > 0 ? txInfo.signatures[0] : null,
        confirmations: txInfo.signatures.length,
        recentTransactions: txInfo.signatures,
        updatedAt: new Date().toISOString(),
      };

      // 存储检测结果
      this.paymentStore.set(body.address, result);
      
      this.logger.log(`Payment detection result for ${body.address}: ${status}, ${balanceInfo.balanceInSOL} SOL`);
      
      return result;
    } catch (error) {
      this.logger.error(`Payment detection failed for ${body.address}:`, error);
      
      return {
        address: body.address,
        networkCode: effectiveNetworkCode,
        status: 'error',
        receivedAmount: '0',
        expectedAmount: body.expectedAmount ?? null,
        txHash: null,
        confirmations: 0,
        error: error instanceof Error ? error.message : String(error),
        updatedAt: new Date().toISOString(),
      };
    }
  }
}
