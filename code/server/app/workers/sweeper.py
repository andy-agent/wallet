"""
资金归集任务模块

定期扫描已 fulfilled 订单的收款地址，将余额归集到主钱包。
"""
import logging
from decimal import Decimal
from typing import Optional, List, Dict, Any
from datetime import datetime, timezone, timedelta

from sqlalchemy import select, and_, or_
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.config import get_settings
from app.core.database import get_db_context
from app.core.encryption import decrypt_private_key
from app.models.order import Order
from app.models.payment_address import PaymentAddress, AddressStatus
from app.models.sweep_record import SweepRecord, SweepStatus

# 链客户端导入
from app.integrations.solana import SolanaClient
from app.integrations.tron import TronClient

logger = logging.getLogger(__name__)

# 全局配置（从 settings 加载）
_sweep_config: Dict[str, Any] = {}


def _get_config() -> Dict[str, Any]:
    """获取归集配置（懒加载）"""
    global _sweep_config
    if not _sweep_config:
        settings = get_settings()
        _sweep_config = {
            "enabled": getattr(settings, "sweeper_enabled", True),
            "interval_minutes": getattr(settings, "sweeper_interval_minutes", 5),
            "threshold_usd": Decimal(str(getattr(settings, "sweep_threshold_usd", 1.0))),
            "reserve_amount_sol": Decimal(str(getattr(settings, "sweep_reserve_amount_sol", 0.005))),
            "reserve_amount_trx": Decimal(str(getattr(settings, "sweep_reserve_amount_trx", 1.0))),
            "max_retry_count": getattr(settings, "sweep_max_retry_count", 3),
            "retry_delay_minutes": getattr(settings, "sweep_retry_delay_minutes", 10),
            # 主钱包地址配置
            "solana_master_wallet": getattr(settings, "solana_master_wallet", None),
            "tron_master_wallet": getattr(settings, "tron_master_wallet", None),
            # 链配置
            "solana_rpc_url": settings.solana_rpc_url,
            "solana_mock_mode": settings.solana_mock_mode,
            "tron_rpc_url": settings.tron_rpc_url,
            "tron_usdt_contract": settings.tron_usdt_contract,
            "tron_mock_mode": settings.tron_mock_mode,
        }
    return _sweep_config


def _clear_config_cache():
    """清除配置缓存（用于测试）"""
    global _sweep_config
    _sweep_config = {}


async def _get_solana_client() -> SolanaClient:
    """获取 Solana 客户端"""
    config = _get_config()
    return SolanaClient(
        rpc_url=config["solana_rpc_url"],
        mock_mode=config["solana_mock_mode"]
    )


async def _get_tron_client() -> TronClient:
    """获取 Tron 客户端"""
    config = _get_config()
    return TronClient(
        rpc_url=config["tron_rpc_url"],
        usdt_contract=config["tron_usdt_contract"],
        mock_mode=config["tron_mock_mode"]
    )


async def _get_address_balance(
    chain: str,
    address: str,
    asset_code: str,
    solana_client: Optional[SolanaClient] = None,
    tron_client: Optional[TronClient] = None
) -> Decimal:
    """
    获取地址余额
    
    Args:
        chain: 链类型 (solana, tron)
        address: 钱包地址
        asset_code: 资产代码
        solana_client: Solana 客户端（可选）
        tron_client: Tron 客户端（可选）
    
    Returns:
        余额（Decimal）
    """
    try:
        if chain == "solana":
            client = solana_client or await _get_solana_client()
            if asset_code == "SOL":
                balance = await client.get_balance(address)
            else:
                # SPL Token
                settings = get_settings()
                balance = await client.get_spl_token_balance(
                    address, 
                    settings.spl_token_mint
                )
            return Decimal(str(balance))
            
        elif chain == "tron":
            client = tron_client or await _get_tron_client()
            if asset_code == "USDT_TRC20":
                balance = await client.get_trc20_balance(address)
            else:
                # TRX 余额（暂未实现，返回0）
                balance = 0.0
            return Decimal(str(balance))
            
        else:
            logger.warning(f"Unsupported chain for balance check: {chain}")
            return Decimal("0")
            
    except Exception as e:
        logger.error(f"Error getting balance for {chain}:{address}: {e}")
        return Decimal("0")


async def _estimate_sweep_fee(
    chain: str,
    asset_code: str,
    solana_client: Optional[SolanaClient] = None,
    tron_client: Optional[TronClient] = None
) -> Dict[str, Decimal]:
    """
    估算归集手续费
    
    Args:
        chain: 链类型
        asset_code: 资产代码
        solana_client: Solana 客户端
        tron_client: Tron 客户端
    
    Returns:
        {
            "fee_asset": 手续费代币,
            "estimated_fee": 预估手续费金额,
            "reserve_amount": 保留金额
        }
    """
    config = _get_config()
    
    if chain == "solana":
        # Solana 手续费估算
        # 基础交易费约 0.000005 SOL，优先费根据网络情况
        # SPL Token 转账需要更多计算单元
        if asset_code == "SOL":
            estimated_fee = Decimal("0.00001")  # 基础转账
        else:
            # SPL Token 转账（创建 ATA 可能额外费用）
            estimated_fee = Decimal("0.0005")  # 包含优先费
        
        return {
            "fee_asset": "SOL",
            "estimated_fee": estimated_fee,
            "reserve_amount": config["reserve_amount_sol"]
        }
        
    elif chain == "tron":
        # Tron 手续费估算
        # TRC20 转账需要能量和带宽
        # 预估能量消耗约 30,000-65,000，带宽约 350
        # 如果没有足够的资源，需要燃烧 TRX
        if asset_code == "USDT_TRC20":
            # TRC20 转账可能需要燃烧约 10-30 TRX（如果没有能量）
            estimated_fee = Decimal("20")  # 保守估计
        else:
            estimated_fee = Decimal("1")
        
        return {
            "fee_asset": "TRX",
            "estimated_fee": estimated_fee,
            "reserve_amount": config["reserve_amount_trx"]
        }
        
    else:
        return {
            "fee_asset": "",
            "estimated_fee": Decimal("0"),
            "reserve_amount": Decimal("0")
        }


async def _calculate_sweep_amount(
    chain: str,
    asset_code: str,
    balance: Decimal,
    solana_client: Optional[SolanaClient] = None,
    tron_client: Optional[TronClient] = None
) -> Optional[Decimal]:
    """
    计算实际可归集金额
    
    Args:
        chain: 链类型
        asset_code: 资产代码
        balance: 当前余额
        solana_client: Solana 客户端
        tron_client: Tron 客户端
    
    Returns:
        可归集金额，如果不足则返回 None
    """
    config = _get_config()
    
    # 检查是否超过阈值
    # 注意：这里简化处理，实际应该获取资产价格换算成 USD
    # 暂时假设余额就是价值（对于稳定币）
    if balance < config["threshold_usd"]:
        return None
    
    # 获取手续费估算
    fee_info = await _estimate_sweep_fee(chain, asset_code, solana_client, tron_client)
    reserve = fee_info["reserve_amount"]
    
    # 计算可归集金额
    if chain == "solana" and asset_code == "SOL":
        # SOL 转账需要保留一些作为手续费
        sweep_amount = balance - reserve
    elif chain == "tron" and asset_code == "TRX":
        # TRX 转账需要保留一些作为手续费
        sweep_amount = balance - reserve
    else:
        # 代币转账（USDT 等），可以转移全部余额
        # 手续费从原生代币扣除
        sweep_amount = balance
    
    if sweep_amount <= 0:
        return None
        
    return sweep_amount


async def _create_sweep_transaction(
    session: AsyncSession,
    address: PaymentAddress,
    order: Order,
    to_address: str,
    amount: Decimal,
    fee_info: Dict[str, Decimal]
) -> SweepRecord:
    """
    创建归集记录
    
    Args:
        session: 数据库会话
        address: 支付地址
        order: 关联订单
        to_address: 目标主钱包地址
        amount: 归集金额
        fee_info: 手续费信息
    
    Returns:
        SweepRecord 实例
    """
    # 检查是否已有待处理的归集记录
    existing = await session.execute(
        select(SweepRecord).where(
            and_(
                SweepRecord.address_id == address.id,
                SweepRecord.status.in_([
                    SweepStatus.PENDING.value,
                    SweepStatus.PROCESSING.value,
                    SweepStatus.RETRYING.value
                ])
            )
        )
    )
    if existing.scalar_one_or_none():
        logger.info(f"Sweep record already exists for address {address.address}")
        return None
    
    sweep_record = SweepRecord(
        address_id=address.id,
        order_id=order.id,
        chain=address.chain,
        asset_code=address.asset_code,
        from_address=address.address,
        to_address=to_address,
        amount=amount,
        amount_usd=order.amount_usd_locked if order else None,
        fee_asset=fee_info.get("fee_asset"),
        status=SweepStatus.PENDING.value
    )
    
    session.add(sweep_record)
    await session.flush()
    
    logger.info(
        f"Created sweep record #{sweep_record.id}: "
        f"{address.chain}:{address.address[:8]}... -> {to_address[:8]}... "
        f"amount={amount}"
    )
    
    return sweep_record


async def _execute_sweep(
    session: AsyncSession,
    sweep_record: SweepRecord,
    address: PaymentAddress,
    solana_client: Optional[SolanaClient] = None,
    tron_client: Optional[TronClient] = None
) -> bool:
    """
    执行归集转账
    
    Args:
        session: 数据库会话
        sweep_record: 归集记录
        address: 支付地址
        solana_client: Solana 客户端
        tron_client: Tron 客户端
    
    Returns:
        是否成功
    """
    # 更新状态为处理中
    sweep_record.status = SweepStatus.PROCESSING.value
    sweep_record.started_at = datetime.now(timezone.utc)
    await session.flush()
    
    try:
        # 解密私钥
        if not address.encrypted_private_key:
            raise ValueError("No private key available for address")
        
        private_key = decrypt_private_key(address.encrypted_private_key)
        
        # 根据链类型执行转账
        if sweep_record.chain == "solana":
            # TODO: 实现 Solana 转账
            # 需要使用私钥签名并发送交易
            # 这里先模拟成功
            tx_hash = await _execute_solana_sweep(
                sweep_record, private_key, solana_client
            )
            
        elif sweep_record.chain == "tron":
            # TODO: 实现 Tron 转账
            # 需要使用私钥签名并发送交易
            tx_hash = await _execute_tron_sweep(
                sweep_record, private_key, tron_client
            )
            
        else:
            raise ValueError(f"Unsupported chain: {sweep_record.chain}")
        
        if tx_hash:
            sweep_record.tx_hash = tx_hash
            sweep_record.status = SweepStatus.COMPLETED.value
            sweep_record.completed_at = datetime.now(timezone.utc)
            
            # 更新地址状态为已归集
            address.status = AddressStatus.SWEPT.value
            
            logger.info(
                f"Sweep #{sweep_record.id} completed: tx_hash={tx_hash}"
            )
            return True
        else:
            raise RuntimeError("Failed to get transaction hash")
            
    except Exception as e:
        logger.error(f"Sweep #{sweep_record.id} failed: {e}")
        sweep_record.status = SweepStatus.FAILED.value
        sweep_record.error_message = str(e)[:500]
        sweep_record.retry_count += 1
        
        # 如果未达到最大重试次数，设置为重试状态
        config = _get_config()
        if sweep_record.retry_count < config["max_retry_count"]:
            sweep_record.status = SweepStatus.RETRYING.value
        
        return False


async def _execute_solana_sweep(
    sweep_record: SweepRecord,
    private_key: str,
    solana_client: Optional[SolanaClient] = None
) -> Optional[str]:
    """
    执行 Solana 归集转账
    
    Args:
        sweep_record: 归集记录
        private_key: 解密后的私钥
        solana_client: Solana 客户端
    
    Returns:
        交易哈希，失败返回 None
    """
    # TODO: 实现实际的 Solana 转账逻辑
    # 需要使用 solders 或类似库来签名和发送交易
    # 1. 构建转账指令
    # 2. 创建交易
    # 3. 签名交易
    # 4. 发送交易
    # 5. 等待确认
    
    # 临时返回模拟的交易哈希（开发/测试阶段）
    client = solana_client or await _get_solana_client()
    
    if client.mock_mode:
        # Mock 模式下生成假交易哈希
        import random
        import string
        tx_hash = ''.join(random.choices(string.ascii_lowercase + string.digits, k=87))
        return tx_hash
    else:
        # 真实转账逻辑待实现
        logger.warning("Real Solana sweep not yet implemented")
        return None


async def _execute_tron_sweep(
    sweep_record: SweepRecord,
    private_key: str,
    tron_client: Optional[TronClient] = None
) -> Optional[str]:
    """
    执行 Tron 归集转账
    
    Args:
        sweep_record: 归集记录
        private_key: 解密后的私钥
        tron_client: Tron 客户端
    
    Returns:
        交易哈希，失败返回 None
    """
    # TODO: 实现实际的 Tron 转账逻辑
    # 需要使用 tronpy 或类似库来签名和发送交易
    # 1. 构建 TRC20 转账合约调用
    # 2. 签名交易
    # 3. 广播交易
    # 4. 等待确认
    
    # 临时返回模拟的交易哈希（开发/测试阶段）
    client = tron_client or await _get_tron_client()
    
    if client.mock_mode:
        # Mock 模式下生成假交易哈希
        import random
        import string
        tx_hash = ''.join(random.choices(string.hexdigits.lower(), k=64))
        return tx_hash
    else:
        # 真实转账逻辑待实现
        logger.warning("Real Tron sweep not yet implemented")
        return None


async def _get_master_wallet(chain: str) -> Optional[str]:
    """获取主钱包地址"""
    config = _get_config()
    if chain == "solana":
        return config.get("solana_master_wallet")
    elif chain == "tron":
        return config.get("tron_master_wallet")
    return None


async def sweep_fulfilled_orders():
    """
    扫描已 fulfilled 的订单并进行资金归集
    
    主要流程：
    1. 查询状态为 allocated 且关联订单为 fulfilled 的地址
    2. 检查地址余额
    3. 如果余额超过阈值，创建归集记录
    4. 执行归集转账
    5. 更新状态
    """
    config = _get_config()
    
    if not config["enabled"]:
        logger.debug("Sweeper is disabled")
        return
    
    logger.info("Starting sweep cycle for fulfilled orders")
    
    async with get_db_context() as session:
        try:
            # 1. 查询需要归集的地址
            # 条件：
            # - 地址状态为 allocated
            # - 关联订单状态为 fulfilled
            # - 订单 fulfilled_at 在一定时间内（避免处理太旧的）
            
            cutoff_date = datetime.now(timezone.utc) - timedelta(days=30)
            
            result = await session.execute(
                select(PaymentAddress, Order)
                .join(Order, PaymentAddress.allocated_order_id == Order.id)
                .where(
                    and_(
                        PaymentAddress.status == AddressStatus.ALLOCATED.value,
                        Order.status == "fulfilled",
                        Order.fulfilled_at >= cutoff_date
                    )
                )
            )
            
            addresses_to_sweep = result.all()
            
            if not addresses_to_sweep:
                logger.debug("No addresses to sweep")
                return
            
            logger.info(f"Found {len(addresses_to_sweep)} addresses to check for sweeping")
            
            # 初始化链客户端
            solana_client = None
            tron_client = None
            
            for address, order in addresses_to_sweep:
                try:
                    # 获取主钱包地址
                    master_wallet = await _get_master_wallet(address.chain)
                    if not master_wallet:
                        logger.error(f"Master wallet not configured for {address.chain}")
                        continue
                    
                    # 获取客户端
                    if address.chain == "solana" and not solana_client:
                        solana_client = await _get_solana_client()
                    elif address.chain == "tron" and not tron_client:
                        tron_client = await _get_tron_client()
                    
                    # 检查余额
                    balance = await _get_address_balance(
                        address.chain,
                        address.address,
                        address.asset_code,
                        solana_client,
                        tron_client
                    )
                    
                    if balance <= 0:
                        logger.debug(f"Address {address.address} has zero balance")
                        continue
                    
                    # 计算可归集金额
                    sweep_amount = await _calculate_sweep_amount(
                        address.chain,
                        address.asset_code,
                        balance,
                        solana_client,
                        tron_client
                    )
                    
                    if not sweep_amount:
                        logger.info(
                            f"Address {address.address} balance {balance} "
                            f"below threshold or insufficient after reserve"
                        )
                        continue
                    
                    # 获取手续费信息
                    fee_info = await _estimate_sweep_fee(
                        address.chain,
                        address.asset_code,
                        solana_client,
                        tron_client
                    )
                    
                    # 创建归集记录
                    sweep_record = await _create_sweep_transaction(
                        session,
                        address,
                        order,
                        master_wallet,
                        sweep_amount,
                        fee_info
                    )
                    
                    if not sweep_record:
                        continue
                    
                    # 执行归集
                    success = await _execute_sweep(
                        session,
                        sweep_record,
                        address,
                        solana_client,
                        tron_client
                    )
                    
                    if success:
                        logger.info(
                            f"Successfully swept {sweep_amount} from {address.address}"
                        )
                    else:
                        logger.warning(
                            f"Failed to sweep from {address.address}, "
                            f"retry_count={sweep_record.retry_count}"
                        )
                    
                    # 提交本笔归集的事务
                    await session.commit()
                    
                except Exception as e:
                    logger.error(f"Error processing address {address.address}: {e}")
                    await session.rollback()
                    continue
            
            # 关闭客户端
            if solana_client:
                await solana_client.close()
            if tron_client:
                await tron_client.close()
                
        except Exception as e:
            logger.error(f"Error in sweep_fulfilled_orders: {e}")
            raise


async def retry_failed_sweeps():
    """
    重试失败的归集任务
    
    检查状态为 retrying 的归集记录，如果超过重试延迟时间则重新尝试。
    """
    config = _get_config()
    
    if not config["enabled"]:
        return
    
    logger.info("Starting retry cycle for failed sweeps")
    
    async with get_db_context() as session:
        try:
            # 查询需要重试的记录
            retry_after = datetime.now(timezone.utc) - timedelta(
                minutes=config["retry_delay_minutes"]
            )
            
            result = await session.execute(
                select(SweepRecord, PaymentAddress)
                .join(PaymentAddress, SweepRecord.address_id == PaymentAddress.id)
                .where(
                    and_(
                        SweepRecord.status == SweepStatus.RETRYING.value,
                        SweepRecord.retry_count < config["max_retry_count"],
                        or_(
                            SweepRecord.updated_at == None,
                            SweepRecord.updated_at < retry_after
                        )
                    )
                )
            )
            
            sweeps_to_retry = result.all()
            
            if not sweeps_to_retry:
                logger.debug("No sweeps to retry")
                return
            
            logger.info(f"Found {len(sweeps_to_retry)} sweeps to retry")
            
            # 初始化链客户端
            solana_client = None
            tron_client = None
            
            for sweep_record, address in sweeps_to_retry:
                try:
                    # 重新检查余额
                    if sweep_record.chain == "solana" and not solana_client:
                        solana_client = await _get_solana_client()
                    elif sweep_record.chain == "tron" and not tron_client:
                        tron_client = await _get_tron_client()
                    
                    balance = await _get_address_balance(
                        sweep_record.chain,
                        sweep_record.from_address,
                        sweep_record.asset_code,
                        solana_client,
                        tron_client
                    )
                    
                    if balance < sweep_record.amount:
                        logger.warning(
                            f"Sweep #{sweep_record.id} balance changed, "
                            f"updating amount from {sweep_record.amount} to {balance}"
                        )
                        sweep_record.amount = min(sweep_record.amount, balance)
                    
                    if sweep_record.amount <= 0:
                        sweep_record.status = SweepStatus.FAILED.value
                        sweep_record.error_message = "Balance is zero"
                        await session.commit()
                        continue
                    
                    # 重新尝试执行
                    success = await _execute_sweep(
                        session,
                        sweep_record,
                        address,
                        solana_client,
                        tron_client
                    )
                    
                    if success:
                        logger.info(f"Sweep #{sweep_record.id} retry succeeded")
                    else:
                        logger.warning(f"Sweep #{sweep_record.id} retry failed")
                    
                    await session.commit()
                    
                except Exception as e:
                    logger.error(f"Error retrying sweep #{sweep_record.id}: {e}")
                    await session.rollback()
                    continue
            
            # 关闭客户端
            if solana_client:
                await solana_client.close()
            if tron_client:
                await tron_client.close()
                
        except Exception as e:
            logger.error(f"Error in retry_failed_sweeps: {e}")
            raise


async def run_sweeper_cycle():
    """
    运行完整的归集周期
    
    包括：
    1. 处理新的 fulfilled 订单
    2. 重试失败的归集
    """
    try:
        await sweep_fulfilled_orders()
        await retry_failed_sweeps()
    except Exception as e:
        logger.error(f"Sweeper cycle failed: {e}")
        raise
