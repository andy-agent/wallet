#!/usr/bin/env python3
"""
导入收款地址脚本 - 从 JSON/CSV 文件导入地址和加密私钥

用法:
    python scripts/import_addresses.py --file examples/addresses.json --chain solana
    python scripts/import_addresses.py --file examples/addresses.csv --chain tron --dry-run

环境变量:
    DATABASE_URL: 数据库连接字符串 (必需)
"""

import argparse
import asyncio
import csv
import json
import os
import re
import sys
from typing import List, Dict, Any, Tuple

# 添加项目路径
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from sqlalchemy import select
from sqlalchemy.dialects.postgresql import insert as pg_insert

from app.core.database import init_db, get_db_context, sync_engine
from app.models.payment_address import PaymentAddress, AddressStatus
from app.core.database import Base


def parse_args() -> argparse.Namespace:
    """解析命令行参数"""
    parser = argparse.ArgumentParser(
        prog="import_addresses",
        description="从 JSON/CSV 文件导入收款地址到地址池",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
示例:
  # 导入 Solana 地址
  python scripts/import_addresses.py --file examples/solana_addresses.json --chain solana

  # 导入 Tron 地址 (CSV 格式)
  python scripts/import_addresses.py --file examples/tron_addresses.csv --chain tron

  # 试运行模式
  python scripts/import_addresses.py --file examples/addresses.json --chain solana --dry-run

  # 显示帮助
  python scripts/import_addresses.py --help

地址格式验证:
  - Solana: Base58 编码, 32-44 字符
  - Tron: Base58 编码, 以 'T' 开头, 34 字符
        """
    )
    parser.add_argument(
        "--file", "-f",
        required=True,
        help="地址数据文件路径 (JSON 或 CSV 格式)"
    )
    parser.add_argument(
        "--chain", "-c",
        required=True,
        choices=["solana", "tron"],
        help="区块链类型: solana 或 tron"
    )
    parser.add_argument(
        "--dry-run", "-d",
        action="store_true",
        help="试运行模式，不实际写入数据库"
    )
    parser.add_argument(
        "--skip-validation",
        action="store_true",
        help="跳过地址格式验证"
    )
    return parser.parse_args()


def validate_solana_address(address: str) -> Tuple[bool, str]:
    """
    验证 Solana 地址格式
    - Base58 编码
    - 32-44 字符长度
    """
    if not address:
        return False, "地址为空"
    
    # Base58 字符集
    base58_chars = set("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz")
    if not all(c in base58_chars for c in address):
        return False, "包含非 Base58 字符"
    
    if len(address) < 32 or len(address) > 44:
        return False, f"长度错误: {len(address)} (期望 32-44)"
    
    return True, ""


def validate_tron_address(address: str) -> Tuple[bool, str]:
    """
    验证 Tron 地址格式
    - Base58 编码
    - 以 'T' 开头
    - 34 字符长度
    """
    if not address:
        return False, "地址为空"
    
    if not address.startswith("T"):
        return False, "Tron 地址必须以 'T' 开头"
    
    # Base58 字符集
    base58_chars = set("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz")
    if not all(c in base58_chars for c in address):
        return False, "包含非 Base58 字符"
    
    if len(address) != 34:
        return False, f"长度错误: {len(address)} (期望 34)"
    
    return True, ""


def validate_address(address: str, chain: str) -> Tuple[bool, str]:
    """根据链类型验证地址格式"""
    if chain == "solana":
        return validate_solana_address(address)
    elif chain == "tron":
        return validate_tron_address(address)
    else:
        return False, f"不支持的链类型: {chain}"


def load_addresses_from_json(file_path: str) -> List[Dict[str, Any]]:
    """从 JSON 文件加载地址数据"""
    with open(file_path, "r", encoding="utf-8") as f:
        data = json.load(f)
    
    if isinstance(data, dict) and "addresses" in data:
        return data["addresses"]
    elif isinstance(data, list):
        return data
    else:
        raise ValueError("JSON 格式错误: 期望对象包含 'addresses' 数组或直接是数组")


def load_addresses_from_csv(file_path: str) -> List[Dict[str, Any]]:
    """从 CSV 文件加载地址数据"""
    addresses = []
    with open(file_path, "r", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            addr = {
                "address": row["address"].strip(),
                "asset_code": row.get("asset_code", "").strip(),
                "encrypted_private_key": row.get("encrypted_private_key", "").strip() or None,
            }
            addresses.append(addr)
    return addresses


def load_addresses(file_path: str) -> List[Dict[str, Any]]:
    """根据文件扩展名加载地址数据"""
    ext = os.path.splitext(file_path)[1].lower()
    if ext == ".json":
        return load_addresses_from_json(file_path)
    elif ext == ".csv":
        return load_addresses_from_csv(file_path)
    else:
        raise ValueError(f"不支持的文件格式: {ext} (仅支持 .json 和 .csv)")


def infer_asset_code(chain: str, asset_code: str) -> str:
    """推断资产代码"""
    if asset_code:
        return asset_code
    
    if chain == "solana":
        return "SOL"
    elif chain == "tron":
        return "USDT_TRC20"
    else:
        return "UNKNOWN"


async def import_addresses(
    chain: str,
    addresses_data: List[Dict[str, Any]],
    skip_validation: bool = False,
    dry_run: bool = False
) -> Dict[str, int]:
    """
    导入地址到数据库
    
    Returns:
        统计信息
    """
    stats = {
        "total": len(addresses_data),
        "valid": 0,
        "invalid": 0,
        "imported": 0,
        "skipped": 0,
        "errors": 0
    }
    
    valid_addresses = []
    
    # 第一遍：验证所有地址
    print("🔍 验证地址格式...")
    for addr_data in addresses_data:
        address = addr_data.get("address", "").strip()
        
        if not address:
            print(f"  ⚠️  跳过: 地址为空")
            stats["invalid"] += 1
            continue
        
        if not skip_validation:
            is_valid, error_msg = validate_address(address, chain)
            if not is_valid:
                print(f"  ⚠️  无效地址: {address[:20]}... - {error_msg}")
                stats["invalid"] += 1
                continue
        
        stats["valid"] += 1
        valid_addresses.append({
            "address": address,
            "asset_code": infer_asset_code(chain, addr_data.get("asset_code", "")),
            "encrypted_private_key": addr_data.get("encrypted_private_key") or None,
        })
    
    if dry_run:
        print(f"\n📝 [DRY RUN] 将导入 {len(valid_addresses)} 个地址")
        return stats
    
    if not valid_addresses:
        print("❌ 没有有效的地址可导入")
        return stats
    
    # 第二遍：导入到数据库
    print(f"\n📥 导入到数据库...")
    
    async with get_db_context() as session:
        for addr in valid_addresses:
            try:
                # 使用 INSERT ... ON CONFLICT DO NOTHING 进行幂等导入
                stmt = pg_insert(PaymentAddress).values(
                    chain=chain,
                    asset_code=addr["asset_code"],
                    address=addr["address"],
                    encrypted_private_key=addr["encrypted_private_key"],
                    status=AddressStatus.AVAILABLE.value,
                ).on_conflict_do_nothing(
                    index_elements=["address"]  # 基于 address 字段的唯一约束
                )
                
                result = await session.execute(stmt)
                
                if result.rowcount == 1:
                    print(f"  ✅ 导入: {addr['address'][:20]}... ({addr['asset_code']})")
                    stats["imported"] += 1
                else:
                    print(f"  ⏭️  跳过已存在: {addr['address'][:20]}...")
                    stats["skipped"] += 1
                    
            except Exception as e:
                print(f"  ❌ 导入失败 {addr['address'][:20]}...: {e}")
                stats["errors"] += 1
        
        await session.commit()
    
    return stats


async def main():
    """主函数"""
    args = parse_args()
    
    # 检查文件是否存在
    if not os.path.exists(args.file):
        print(f"❌ 错误: 文件不存在: {args.file}")
        sys.exit(1)
    
    print(f"📂 加载地址数据: {args.file}")
    print(f"🔗 链类型: {args.chain}")
    
    try:
        addresses = load_addresses(args.file)
        print(f"📊 找到 {len(addresses)} 个地址")
    except Exception as e:
        print(f"❌ 加载文件失败: {e}")
        sys.exit(1)
    
    if args.dry_run:
        print("🔍 试运行模式 (不写入数据库)")
    
    # 初始化数据库
    print("🔌 连接数据库...")
    init_db()
    
    # 导入地址
    stats = await import_addresses(
        chain=args.chain,
        addresses_data=addresses,
        skip_validation=args.skip_validation,
        dry_run=args.dry_run
    )
    
    # 打印统计
    print("\n" + "=" * 50)
    print("📈 导入统计:")
    print(f"   总计: {stats['total']}")
    print(f"   有效: {stats['valid']}")
    print(f"   无效: {stats['invalid']}")
    if not args.dry_run:
        print(f"   导入: {stats['imported']}")
        print(f"   跳过(已存在): {stats['skipped']}")
    if stats['errors'] > 0:
        print(f"   错误: {stats['errors']}")
    print("=" * 50)
    
    if stats['errors'] > 0 or stats['valid'] == 0:
        sys.exit(1)


if __name__ == "__main__":
    asyncio.run(main())
