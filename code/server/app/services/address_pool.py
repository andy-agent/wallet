"""
地址池管理服务 - Address Pool Management Service

负责管理收款地址的生命周期：
- 地址分配: 为订单分配可用地址
- 地址状态管理: available → allocated → expired/swept
- 地址回收: 订单过期后回收地址
- 地址导入: 批量导入新地址

地址状态:
- available: 可用
- allocated: 已分配
- expired: 过期
- swept: 已归集
- disabled: 禁用

幂等性保证:
- 同一订单只能分配一个地址
- 使用数据库唯一约束: allocated_order_id
"""
from datetime import datetime, timezone
from typing import List, Optional

from sqlalchemy import select, update
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.dialects.postgresql import insert as pg_insert

from app.models.payment_address import PaymentAddress, AddressStatus
from app.core.exceptions import AppException, ErrorCode, NotFoundException


class AddressPoolService:
    """地址池管理服务"""
    
    def __init__(self, session: AsyncSession):
        self.session = session
    
    async def allocate_address(
        self, 
        chain: str, 
        asset_code: str, 
        order_id: str
    ) -> PaymentAddress:
        """
        为订单分配一个可用地址
        
        幂等性: 如果该订单已有分配的地址，直接返回已分配的地址
        
        Args:
            chain: 链名称 (e.g., 'solana', 'tron')
            asset_code: 资产代码 (e.g., 'SOL', 'USDT_TRC20')
            order_id: 订单ID
            
        Returns:
            PaymentAddress: 分配的地址对象
            
        Raises:
            AppException: 地址池为空时抛出 ADDRESS_POOL_EMPTY 错误
            NotFoundException: 地址分配失败
        """
        # 幂等性检查: 如果订单已有分配的地址，直接返回
        existing_address = await self.get_address_by_order(order_id)
        if existing_address:
            return existing_address
        
        # 查找可用的地址并加锁 (FOR UPDATE)
        # 使用 SKIP LOCKED 避免等待其他事务持有的锁
        stmt = (
            select(PaymentAddress)
            .where(
                PaymentAddress.chain == chain,
                PaymentAddress.asset_code == asset_code,
                PaymentAddress.status == AddressStatus.AVAILABLE.value
            )
            .limit(1)
            .with_for_update(skip_locked=True)
        )
        result = await self.session.execute(stmt)
        address = result.scalar_one_or_none()
        
        if not address:
            raise AppException(
                code=ErrorCode.ADDRESS_POOL_EMPTY,
                message=f"地址池为空: chain={chain}, asset={asset_code}",
                status_code=409,
                data={"chain": chain, "asset_code": asset_code}
            )
        
        # 分配地址给订单
        address.status = AddressStatus.ALLOCATED.value
        address.allocated_order_id = order_id
        address.allocated_at = datetime.now(timezone.utc)
        
        # 注意: 不在此处 flush，让调用方控制事务提交
        # 避免在订单创建前触发外键约束
        
        return address
    
    async def release_address(self, address_id: int) -> None:
        """
        释放地址（回收地址到地址池）
        
        将地址状态从 allocated/expired 改回 available
        清除分配信息
        
        Args:
            address_id: 地址ID
            
        Raises:
            NotFoundException: 地址不存在
        """
        stmt = (
            select(PaymentAddress)
            .where(PaymentAddress.id == address_id)
            .with_for_update()
        )
        result = await self.session.execute(stmt)
        address = result.scalar_one_or_none()
        
        if not address:
            raise NotFoundException(f"地址不存在: id={address_id}")
        
        # 只有 allocated 或 expired 状态的地址才能被释放
        if address.status not in [AddressStatus.ALLOCATED.value, AddressStatus.EXPIRED.value]:
            # 已经是 available 或其他状态，无需操作
            return
        
        # 重置地址状态
        address.status = AddressStatus.AVAILABLE.value
        address.allocated_order_id = None
        address.allocated_at = None
        
        await self.session.flush()
    
    async def get_address_by_order(self, order_id: str) -> Optional[PaymentAddress]:
        """
        根据订单ID查询分配的地址
        
        Args:
            order_id: 订单ID
            
        Returns:
            Optional[PaymentAddress]: 地址对象，如果不存在则返回 None
        """
        stmt = (
            select(PaymentAddress)
            .where(PaymentAddress.allocated_order_id == order_id)
        )
        result = await self.session.execute(stmt)
        return result.scalar_one_or_none()
    
    async def get_address_by_id(self, address_id: int) -> Optional[PaymentAddress]:
        """
        根据地址ID查询地址
        
        Args:
            address_id: 地址ID
            
        Returns:
            Optional[PaymentAddress]: 地址对象，如果不存在则返回 None
        """
        stmt = select(PaymentAddress).where(PaymentAddress.id == address_id)
        result = await self.session.execute(stmt)
        return result.scalar_one_or_none()
    
    async def mark_address_expired(self, address_id: int) -> None:
        """
        将地址标记为过期状态
        
        通常在订单过期后调用
        
        Args:
            address_id: 地址ID
            
        Raises:
            NotFoundException: 地址不存在
        """
        stmt = (
            select(PaymentAddress)
            .where(PaymentAddress.id == address_id)
            .with_for_update()
        )
        result = await self.session.execute(stmt)
        address = result.scalar_one_or_none()
        
        if not address:
            raise NotFoundException(f"地址不存在: id={address_id}")
        
        if address.status != AddressStatus.ALLOCATED.value:
            # 只有已分配的地址才能标记为过期
            return
        
        address.status = AddressStatus.EXPIRED.value
        await self.session.flush()
    
    async def mark_address_swept(
        self, 
        address_id: int, 
        tx_hash: Optional[str] = None
    ) -> None:
        """
        将地址标记为已归集状态
        
        通常在资金归集后调用
        
        Args:
            address_id: 地址ID
            tx_hash: 归集交易哈希（可选）
            
        Raises:
            NotFoundException: 地址不存在
        """
        stmt = (
            select(PaymentAddress)
            .where(PaymentAddress.id == address_id)
            .with_for_update()
        )
        result = await self.session.execute(stmt)
        address = result.scalar_one_or_none()
        
        if not address:
            raise NotFoundException(f"地址不存在: id={address_id}")
        
        address.status = AddressStatus.SWEPT.value
        if tx_hash:
            address.last_seen_tx_hash = tx_hash
        
        await self.session.flush()
    
    async def import_addresses(
        self, 
        chain: str, 
        addresses: List[dict]
    ) -> int:
        """
        批量导入新地址到地址池
        
        Args:
            chain: 链名称
            addresses: 地址列表，每个地址是一个字典，包含:
                - address: 地址字符串 (必需)
                - asset_code: 资产代码 (必需)
                - encrypted_private_key: 加密的私钥 (可选)
                
        Returns:
            int: 成功导入的地址数量
            
        Raises:
            ValueError: 地址格式不正确
        """
        if not addresses:
            return 0
        
        imported_count = 0
        
        for addr_data in addresses:
            address_str = addr_data.get("address")
            asset_code = addr_data.get("asset_code")
            encrypted_private_key = addr_data.get("encrypted_private_key")
            
            if not address_str or not asset_code:
                # 跳过无效的地址数据
                continue
            
            # 使用 INSERT ... ON CONFLICT DO NOTHING 进行幂等导入
            stmt = pg_insert(PaymentAddress).values(
                chain=chain,
                asset_code=asset_code,
                address=address_str,
                encrypted_private_key=encrypted_private_key,
                status=AddressStatus.AVAILABLE.value,
            ).on_conflict_do_nothing(
                index_elements=["address"]  # 基于 address 字段的唯一约束
            )
            
            result = await self.session.execute(stmt)
            # result.rowcount 为 1 表示插入成功，0 表示已存在被跳过
            if result.rowcount == 1:
                imported_count += 1
        
        await self.session.flush()
        return imported_count
    
    async def get_available_count(
        self, 
        chain: Optional[str] = None, 
        asset_code: Optional[str] = None
    ) -> int:
        """
        获取可用地址数量
        
        Args:
            chain: 链名称（可选，为 None 则统计所有链）
            asset_code: 资产代码（可选）
            
        Returns:
            int: 可用地址数量
        """
        stmt = select(PaymentAddress).where(
            PaymentAddress.status == AddressStatus.AVAILABLE
        )
        
        if chain:
            stmt = stmt.where(PaymentAddress.chain == chain)
        if asset_code:
            stmt = stmt.where(PaymentAddress.asset_code == asset_code)
        
        result = await self.session.execute(stmt)
        addresses = result.scalars().all()
        return len(addresses)
    
    async def list_addresses(
        self,
        chain: Optional[str] = None,
        asset_code: Optional[str] = None,
        status: Optional[AddressStatus] = None,
        limit: int = 100,
        offset: int = 0
    ) -> List[PaymentAddress]:
        """
        列出地址
        
        Args:
            chain: 链名称（可选）
            asset_code: 资产代码（可选）
            status: 地址状态（可选）
            limit: 返回数量限制
            offset: 偏移量
            
        Returns:
            List[PaymentAddress]: 地址列表
        """
        stmt = select(PaymentAddress)
        
        if chain:
            stmt = stmt.where(PaymentAddress.chain == chain)
        if asset_code:
            stmt = stmt.where(PaymentAddress.asset_code == asset_code)
        if status:
            stmt = stmt.where(PaymentAddress.status == status)
        
        stmt = stmt.order_by(PaymentAddress.id).limit(limit).offset(offset)
        
        result = await self.session.execute(stmt)
        return list(result.scalars().all())
    
    async def disable_address(self, address_id: int) -> None:
        """
        禁用地址
        
        将地址状态设置为 disabled，禁用的地址不会再被分配
        
        Args:
            address_id: 地址ID
            
        Raises:
            NotFoundException: 地址不存在
        """
        stmt = (
            select(PaymentAddress)
            .where(PaymentAddress.id == address_id)
            .with_for_update()
        )
        result = await self.session.execute(stmt)
        address = result.scalar_one_or_none()
        
        if not address:
            raise NotFoundException(f"地址不存在: id={address_id}")
        
        address.status = AddressStatus.DISABLED.value
        await self.session.flush()
    
    async def enable_address(self, address_id: int) -> None:
        """
        启用地址（将 disabled 状态改回 available）
        
        Args:
            address_id: 地址ID
            
        Raises:
            NotFoundException: 地址不存在
        """
        stmt = (
            select(PaymentAddress)
            .where(PaymentAddress.id == address_id)
            .with_for_update()
        )
        result = await self.session.execute(stmt)
        address = result.scalar_one_or_none()
        
        if not address:
            raise NotFoundException(f"地址不存在: id={address_id}")
        
        if address.status == AddressStatus.DISABLED.value:
            address.status = AddressStatus.AVAILABLE.value
            await self.session.flush()
