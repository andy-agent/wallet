"""
扫描任务实现模块

负责链上支付检测、交易确认、订单过期处理、地址释放等任务。
所有任务都必须是幂等的。
"""
import logging
from datetime import datetime, timedelta, timezone
from decimal import Decimal
from typing import Optional

from sqlalchemy import select, and_, or_
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.database import get_db_context
from app.core.state_machine import (
    OrderStatus,
    state_machine,
    transition_to_seen_onchain,
    transition_to_confirming,
    transition_to_paid_success,
    transition_to_expired,
    transition_to_underpaid,
    transition_to_overpaid,
    DuplicateTransitionError,
    StateTransitionError,
)
from app.core.config import get_settings
from app.models.order import Order
from app.models.payment_address import PaymentAddress
from app.services.address_pool import AddressPoolService
from app.integrations.solana import SolanaClient
from app.integrations.tron import TronClient

logger = logging.getLogger(__name__)


# ==================== Helper Functions ====================

def _get_chain_client(chain: str, asset_code: str = None):
    """
    根据链名称和资产代码获取对应的区块链客户端
    
    Args:
        chain: 链名称 (solana, tron)
        asset_code: 资产代码 (SOL, SPL_TOKEN, USDT_TRC20)
        
    Returns:
        区块链客户端实例
    """
    settings = get_settings()
    
    if chain == "solana":
        # For SPL_TOKEN, include mint and decimals
        if asset_code == "SPL_TOKEN":
            return SolanaClient(
                rpc_url=settings.solana_rpc_url,
                mock_mode=settings.solana_mock_mode,
                spl_token_mint=settings.spl_token_mint,
                spl_token_decimals=settings.spl_token_decimals
            )
        else:
            return SolanaClient(
                rpc_url=settings.solana_rpc_url,
                mock_mode=settings.solana_mock_mode
            )
    elif chain == "tron":
        return TronClient(
            rpc_url=settings.tron_rpc_url,
            usdt_contract=settings.tron_usdt_contract,
            mock_mode=settings.tron_mock_mode
        )
    else:
        raise ValueError(f"Unsupported chain: {chain}")


def _get_required_confirmations(chain: str) -> int:
    """
    获取链所需的确认数
    
    Args:
        chain: 链名称
        
    Returns:
        所需确认数
    """
    settings = get_settings()
    
    if chain == "solana":
        return settings.solana_confirmations
    elif chain == "tron":
        return settings.tron_confirmations
    else:
        return 12  # 默认值


# ==================== Task 1: Scan Pending Orders ====================

async def scan_pending_orders():
    """
    扫描待支付订单，检测链上支付
    
    执行频率: 每10秒
    
    流程:
    1. 查询 pending_payment 状态的订单
    2. 对每个订单，调用对应链的客户端检测支付
    3. 发现支付后，更新订单状态为 seen_onchain
    
    幂等性保证:
    - 使用数据库事务
    - 状态转换使用状态机的幂等性检查
    - 异常时回滚事务，不重试（下次扫描再处理）
    """
    logger.debug("Starting scan_pending_orders task")
    
    async with get_db_context() as session:
        try:
            # 查询 pending_payment 状态的订单
            stmt = (
                select(Order)
                .where(Order.status == OrderStatus.PENDING_PAYMENT.value)
                .where(Order.expires_at > datetime.now(timezone.utc))  # 未过期的
                .limit(100)  # 批次处理
            )
            result = await session.execute(stmt)
            orders = result.scalars().all()
            
            if not orders:
                logger.debug("No pending payment orders found")
                return
            
            logger.info(f"Found {len(orders)} pending payment orders to scan")
            
            # 按链和资产分组订单，减少客户端创建开销
            orders_by_chain_asset: dict = {}
            for order in orders:
                key = (order.chain, order.asset_code)
                if key not in orders_by_chain_asset:
                    orders_by_chain_asset[key] = []
                orders_by_chain_asset[key].append(order)
            
            # 对每个链和资产组合的订单进行检测
            for (chain, asset_code), chain_orders in orders_by_chain_asset.items():
                client = None
                try:
                    client = _get_chain_client(chain, asset_code)
                    
                    for order in chain_orders:
                        try:
                            await _detect_payment_for_order(session, order, client)
                        except DuplicateTransitionError:
                            # 幂等性：已经处理过，忽略
                            logger.debug(f"Order {order.id} already processed")
                        except Exception as e:
                            # 记录错误，继续处理下一个订单
                            logger.error(f"Error detecting payment for order {order.id}: {e}")
                            continue
                    
                except Exception as e:
                    logger.error(f"Error processing chain {chain} asset {asset_code}: {e}")
                    continue
                finally:
                    if client:
                        await client.close()
            
            await session.commit()
            logger.info(f"Completed scan_pending_orders, processed {len(orders)} orders")
            
        except Exception as e:
            logger.error(f"Error in scan_pending_orders: {e}", exc_info=True)
            await session.rollback()
            raise


async def _detect_payment_for_order(
    session: AsyncSession,
    order: Order,
    client
) -> bool:
    """
    检测单个订单的支付
    
    Args:
        session: 数据库会话
        order: 订单对象
        client: 区块链客户端
        
    Returns:
        bool: 是否检测到支付
    """
    # 根据资产代码选择检测方法
    if order.asset_code == "SPL_TOKEN":
        return await _detect_spl_token_payment(session, order, client)
    else:
        return await _detect_native_payment(session, order, client)


async def _detect_native_payment(
    session: AsyncSession,
    order: Order,
    client
) -> bool:
    """
    检测原生代币（SOL/TRX）支付
    
    Args:
        session: 数据库会话
        order: 订单对象
        client: 区块链客户端
        
    Returns:
        bool: 是否检测到支付
    """
    # 检测支付
    expected_amount = Decimal(str(order.amount_crypto))
    detection_result = await client.detect_payment(
        address=order.receive_address,
        expected_amount=expected_amount
    )
    
    if not detection_result or not detection_result.found:
        return False
    
    # 检测到支付，执行状态转换
    logger.info(
        f"Payment detected for order {order.order_no}: "
        f"tx={detection_result.tx_hash}, "
        f"amount={detection_result.amount}, "
        f"confirmations={detection_result.confirmations}"
    )
    
    # 使用状态机执行转换
    transition_to_seen_onchain(
        order_id=order.id,
        current_status=OrderStatus(order.status),
        tx_hash=detection_result.tx_hash,
        tx_from=detection_result.from_address,
        amount=str(detection_result.amount),
        triggered_by="worker"
    )
    
    # 更新订单信息
    order.status = OrderStatus.SEEN_ONCHAIN.value
    order.tx_hash = detection_result.tx_hash
    order.tx_from = detection_result.from_address
    order.confirm_count = detection_result.confirmations
    order.paid_at = datetime.now(timezone.utc)
    
    await session.flush()
    return True


async def _detect_spl_token_payment(
    session: AsyncSession,
    order: Order,
    client: SolanaClient
) -> bool:
    """
    检测SPL代币支付
    
    Args:
        session: 数据库会话
        order: 订单对象
        client: Solana客户端（已配置SPL代币参数）
        
    Returns:
        bool: 是否检测到支付
    """
    settings = get_settings()
    
    # 使用SPL代币检测方法
    expected_amount = float(order.amount_crypto)
    detection_result = await client.detect_spl_token_payment(
        wallet_address=order.receive_address,
        mint=settings.spl_token_mint,
        expected_amount=expected_amount,
        min_confirmations=1,
        amount_tolerance=0.01  # 1% tolerance
    )
    
    if not detection_result or not detection_result.found:
        return False
    
    # 检测到支付，执行状态转换
    logger.info(
        f"SPL Token payment detected for order {order.order_no}: "
        f"tx={detection_result.tx_hash}, "
        f"amount={detection_result.amount}, "
        f"confirmations={detection_result.confirmations}"
    )
    
    # 使用状态机执行转换
    transition_to_seen_onchain(
        order_id=order.id,
        current_status=OrderStatus(order.status),
        tx_hash=detection_result.tx_hash,
        tx_from=detection_result.from_address,
        amount=str(detection_result.amount),
        triggered_by="worker"
    )
    
    # 更新订单信息
    order.status = OrderStatus.SEEN_ONCHAIN.value
    order.tx_hash =detection_result.tx_hash
    order.tx_from = detection_result.from_address
    order.confirm_count = detection_result.confirmations
    order.paid_at = datetime.now(timezone.utc)
    
    await session.flush()
    return True


# ==================== Task 2: Confirm Seen Transactions ====================

async def confirm_seen_transactions():
    """
    确认已发现的链上交易
    
    执行频率: 每10秒
    
    流程:
    1. 查询 seen_onchain 或 confirming 状态的订单
    2. 检查确认数是否达标
    3. 根据金额匹配情况更新状态:
       - 金额匹配: paid_success
       - 少付: underpaid
       - 多付: overpaid
    
    幂等性保证:
    - 使用数据库事务
    - 状态转换使用状态机的幂等性检查
    """
    logger.debug("Starting confirm_seen_transactions task")
    
    async with get_db_context() as session:
        try:
            # 查询需要确认的订单
            stmt = (
                select(Order)
                .where(
                    or_(
                        Order.status == OrderStatus.SEEN_ONCHAIN.value,
                        Order.status == OrderStatus.CONFIRMING.value
                    )
                )
                .limit(100)
            )
            result = await session.execute(stmt)
            orders = result.scalars().all()
            
            if not orders:
                logger.debug("No orders to confirm")
                return
            
            logger.info(f"Found {len(orders)} orders to confirm")
            
            # 按链和资产分组
            orders_by_chain_asset: dict = {}
            for order in orders:
                key = (order.chain, order.asset_code)
                if key not in orders_by_chain_asset:
                    orders_by_chain_asset[key] = []
                orders_by_chain_asset[key].append(order)
            
            # 处理每个链和资产的订单
            for (chain, asset_code), chain_orders in orders_by_chain_asset.items():
                client = None
                try:
                    client = _get_chain_client(chain, asset_code)
                    required_confirmations = _get_required_confirmations(chain)
                    
                    for order in chain_orders:
                        try:
                            await _confirm_order(session, order, client, required_confirmations)
                        except DuplicateTransitionError:
                            logger.debug(f"Order {order.id} already confirmed")
                        except Exception as e:
                            logger.error(f"Error confirming order {order.id}: {e}")
                            continue
                    
                except Exception as e:
                    logger.error(f"Error processing chain {chain} asset {asset_code}: {e}")
                    continue
                finally:
                    if client:
                        await client.close()
            
            await session.commit()
            logger.info(f"Completed confirm_seen_transactions, processed {len(orders)} orders")
            
        except Exception as e:
            logger.error(f"Error in confirm_seen_transactions: {e}", exc_info=True)
            await session.rollback()
            raise


async def _confirm_order(
    session: AsyncSession,
    order: Order,
    client,
    required_confirmations: int
):
    """
    确认单个订单的交易
    
    Args:
        session: 数据库会话
        order: 订单对象
        client: 区块链客户端
        required_confirmations: 所需确认数
    """
    # 获取交易详情
    tx_hash = order.tx_hash
    if not tx_hash:
        logger.warning(f"Order {order.id} has no tx_hash")
        return
    
    # 获取最新确认数
    if order.chain == "solana":
        if order.asset_code == "SPL_TOKEN":
            # For SPL token, get transaction via client
            tx = await client._get_spl_transaction(tx_hash, order.receive_address, client.spl_token_mint)
            if tx:
                current_confirmations = tx.confirmations
            else:
                logger.warning(f"SPL Token transaction {tx_hash} not found")
                return
        else:
            tx = await client.get_transaction(tx_hash)
            if tx:
                current_confirmations = tx.confirmations
            else:
                logger.warning(f"Transaction {tx_hash} not found on Solana")
                return
    elif order.chain == "tron":
        # TronClient 没有 get_transaction 方法，使用 detect_payment
        expected_amount = Decimal(str(order.amount_crypto))
        detection = await client.detect_payment(
            address=order.receive_address,
            expected_amount=expected_amount
        )
        if detection and detection.found and detection.tx_hash == tx_hash:
            current_confirmations = detection.confirmations
        else:
            # 如果没找到相同交易，使用现有确认数
            current_confirmations = order.confirm_count
    else:
        return
    
    # 更新确认数
    order.confirm_count = current_confirmations
    
    # 检查是否达到确认数要求
    if current_confirmations < required_confirmations:
        # 未达到确认数，更新为 confirming 状态
        if order.status != OrderStatus.CONFIRMING.value:
            try:
                transition_to_confirming(
                    order_id=order.id,
                    current_status=OrderStatus(order.status),
                    confirm_count=current_confirmations,
                    triggered_by="worker"
                )
                order.status = OrderStatus.CONFIRMING.value
                await session.flush()
                logger.debug(f"Order {order.order_no} updated to confirming ({current_confirmations}/{required_confirmations})")
            except StateTransitionError:
                # 状态转换不允许，忽略
                pass
        return
    
    # 已达到确认数，检查金额匹配
    expected_amount = Decimal(str(order.amount_crypto))
    settings = get_settings()
    tolerance = Decimal(str(settings.order_amount_tolerance))
    
    # 获取实际支付金额（从链上重新查询或从记录中获取）
    if order.asset_code == "SPL_TOKEN":
        # For SPL token, re-detect payment
        detection = await client.detect_spl_token_payment(
            wallet_address=order.receive_address,
            mint=settings.spl_token_mint,
            expected_amount=float(expected_amount),
            min_confirmations=1
        )
    else:
        detection = await client.detect_payment(
            address=order.receive_address,
            expected_amount=expected_amount
        )
    
    if detection and detection.found:
        actual_amount = Decimal(str(detection.amount))
        
        # 计算容差范围
        min_acceptable = expected_amount * (Decimal("1") - tolerance)
        max_acceptable = expected_amount * (Decimal("1") + tolerance)
        
        current_status = OrderStatus(order.status)
        
        if min_acceptable <= actual_amount <= max_acceptable:
            # 金额匹配
            transition_to_paid_success(
                order_id=order.id,
                current_status=current_status,
                confirm_count=current_confirmations,
                paid_at=datetime.now(timezone.utc),
                triggered_by="worker"
            )
            order.status = OrderStatus.PAID_SUCCESS.value
            order.confirmed_at = datetime.now(timezone.utc)
            logger.info(f"Order {order.order_no} confirmed as paid_success")
            
        elif actual_amount < min_acceptable:
            # 少付
            transition_to_underpaid(
                order_id=order.id,
                current_status=current_status,
                expected=str(expected_amount),
                actual=str(actual_amount),
                triggered_by="worker"
            )
            order.status = OrderStatus.UNDERPAID.value
            logger.warning(f"Order {order.order_no} underpaid: expected={expected_amount}, actual={actual_amount}")
            
        else:
            # 多付
            transition_to_overpaid(
                order_id=order.id,
                current_status=current_status,
                expected=str(expected_amount),
                actual=str(actual_amount),
                triggered_by="worker"
            )
            order.status = OrderStatus.OVERPAID.value
            logger.warning(f"Order {order.order_no} overpaid: expected={expected_amount}, actual={actual_amount}")
        
        await session.flush()


# ==================== Task 4: Expire Orders ====================

async def expire_orders():
    """
    过期超时订单
    
    执行频率: 每60秒
    
    流程:
    1. 查询 pending_payment 状态的订单
    2. 检查是否超过过期时间（15分钟）
    3. 超时的订单更新为 expired 状态
    
    幂等性保证:
    - 使用数据库事务
    - 状态转换使用状态机的幂等性检查
    """
    logger.debug("Starting expire_orders task")
    
    async with get_db_context() as session:
        try:
            now = datetime.now(timezone.utc)
            
            # 查询已超时的 pending_payment 订单
            stmt = (
                select(Order)
                .where(Order.status == OrderStatus.PENDING_PAYMENT.value)
                .where(Order.expires_at < now)
                .limit(100)
            )
            result = await session.execute(stmt)
            orders = result.scalars().all()
            
            if not orders:
                logger.debug("No orders to expire")
                return
            
            logger.info(f"Found {len(orders)} orders to expire")
            
            for order in orders:
                try:
                    transition_to_expired(
                        order_id=order.id,
                        current_status=OrderStatus(order.status),
                        reason="timeout",
                        triggered_by="worker"
                    )
                    order.status = OrderStatus.EXPIRED.value
                    logger.info(f"Order {order.order_no} expired")
                    
                except DuplicateTransitionError:
                    logger.debug(f"Order {order.id} already expired")
                except StateTransitionError as e:
                    logger.error(f"State transition error for order {order.id}: {e}")
                    continue
            
            await session.commit()
            logger.info(f"Completed expire_orders, expired {len(orders)} orders")
            
        except Exception as e:
            logger.error(f"Error in expire_orders: {e}", exc_info=True)
            await session.rollback()
            raise


# ==================== Task 5: Release Expired Addresses ====================

async def release_expired_addresses():
    """
    释放过期订单的地址回地址池
    
    执行频率: 每300秒 (5分钟)
    
    流程:
    1. 查询 expired/underpaid/overpaid/failed 状态的订单
    2. 释放这些订单关联的地址
    3. 将地址状态改回 available
    
    幂等性保证:
    - AddressPoolService.release_address 是幂等的
    - 使用数据库事务
    """
    logger.debug("Starting release_expired_addresses task")
    
    async with get_db_context() as session:
        try:
            address_service = AddressPoolService(session)
            
            # 查询需要释放地址的订单状态
            releasable_statuses = [
                OrderStatus.EXPIRED.value,
                OrderStatus.UNDERPAID.value,
                OrderStatus.OVERPAID.value,
                OrderStatus.FAILED.value,
                OrderStatus.LATE_PAID.value,
            ]
            
            # 查询这些状态的订单
            stmt = (
                select(Order)
                .where(Order.status.in_(releasable_statuses))
                .limit(100)
            )
            result = await session.execute(stmt)
            orders = result.scalars().all()
            
            if not orders:
                logger.debug("No addresses to release")
                return
            
            logger.info(f"Found {len(orders)} orders with addresses to release")
            
            released_count = 0
            for order in orders:
                try:
                    # 获取订单关联的地址
                    address = await address_service.get_address_by_order(order.id)
                    if address:
                        await address_service.release_address(address.id)
                        released_count += 1
                        logger.info(f"Released address {address.address[:8]}... for order {order.order_no}")
                except Exception as e:
                    logger.error(f"Error releasing address for order {order.id}: {e}")
                    continue
            
            await session.commit()
            logger.info(f"Completed release_expired_addresses, released {released_count} addresses")
            
        except Exception as e:
            logger.error(f"Error in release_expired_addresses: {e}", exc_info=True)
            await session.rollback()
            raise
