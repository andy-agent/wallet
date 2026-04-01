#!/usr/bin/env python3
"""
初始化套餐脚本 - 从 JSON/CSV 文件导入套餐

用法:
    python scripts/init_plans.py --file examples/plans.json
    python scripts/init_plans.py --file examples/plans.csv --update-existing

环境变量:
    DATABASE_URL: 数据库连接字符串 (必需)
"""

import argparse
import asyncio
import csv
import json
import os
import sys
from decimal import Decimal
from typing import List, Dict, Any

# 添加项目路径
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import ulid
from sqlalchemy import select
from sqlalchemy.dialects.postgresql import insert as pg_insert

from app.core.database import init_db, get_db_context
from app.models.plan import Plan


def parse_args() -> argparse.Namespace:
    """解析命令行参数"""
    parser = argparse.ArgumentParser(
        prog="init_plans",
        description="从 JSON/CSV 文件导入套餐到数据库",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
示例:
  # 从 JSON 导入套餐
  python scripts/init_plans.py --file examples/plans.json

  # 从 CSV 导入套餐并更新已存在的套餐
  python scripts/init_plans.py --file examples/plans.csv --update-existing

  # 显示帮助
  python scripts/init_plans.py --help
        """
    )
    parser.add_argument(
        "--file", "-f",
        required=True,
        help="套餐数据文件路径 (JSON 或 CSV 格式)"
    )
    parser.add_argument(
        "--update-existing", "-u",
        action="store_true",
        help="如果套餐已存在，更新现有套餐信息"
    )
    parser.add_argument(
        "--dry-run", "-d",
        action="store_true",
        help="试运行模式，不实际写入数据库"
    )
    return parser.parse_args()


def load_plans_from_json(file_path: str) -> List[Dict[str, Any]]:
    """从 JSON 文件加载套餐数据"""
    with open(file_path, "r", encoding="utf-8") as f:
        data = json.load(f)
    
    if isinstance(data, dict) and "plans" in data:
        return data["plans"]
    elif isinstance(data, list):
        return data
    else:
        raise ValueError("JSON 格式错误: 期望对象包含 'plans' 数组或直接是数组")


def load_plans_from_csv(file_path: str) -> List[Dict[str, Any]]:
    """从 CSV 文件加载套餐数据"""
    plans = []
    with open(file_path, "r", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            # 转换字段类型
            plan = {
                "code": row["code"],
                "name": row["name"],
                "description": row.get("description", ""),
                "traffic_bytes": int(row["traffic_bytes"]),
                "duration_days": int(row["duration_days"]),
                "price_usd": Decimal(row["price_usd"]),
                "enabled": row.get("enabled", "true").lower() in ("true", "1", "yes"),
                "sort_order": int(row.get("sort_order", 0)),
            }
            # 处理 supported_assets (JSON 数组字符串或逗号分隔)
            supported_assets = row.get("supported_assets", "SOL,USDT_TRC20")
            if supported_assets.startswith("["):
                plan["supported_assets"] = json.loads(supported_assets)
            else:
                plan["supported_assets"] = [a.strip() for a in supported_assets.split(",")]
            plans.append(plan)
    return plans


def load_plans(file_path: str) -> List[Dict[str, Any]]:
    """根据文件扩展名加载套餐数据"""
    ext = os.path.splitext(file_path)[1].lower()
    if ext == ".json":
        return load_plans_from_json(file_path)
    elif ext == ".csv":
        return load_plans_from_csv(file_path)
    else:
        raise ValueError(f"不支持的文件格式: {ext} (仅支持 .json 和 .csv)")


async def import_plans(plans_data: List[Dict[str, Any]], update_existing: bool = False, dry_run: bool = False) -> Dict[str, int]:
    """
    导入套餐到数据库
    
    Returns:
        统计信息: {"created": x, "updated": y, "skipped": z}
    """
    stats = {"created": 0, "updated": 0, "skipped": 0, "errors": 0}
    
    async with get_db_context() as session:
        for plan_data in plans_data:
            code = plan_data.get("code")
            if not code:
                print(f"  ⚠️  跳过: 缺少 code 字段")
                stats["skipped"] += 1
                continue
            
            try:
                # 检查是否已存在
                result = await session.execute(
                    select(Plan).where(Plan.code == code)
                )
                existing = result.scalar_one_or_none()
                
                if existing and not update_existing:
                    print(f"  ⏭️  跳过已存在: {code}")
                    stats["skipped"] += 1
                    continue
                
                if dry_run:
                    if existing:
                        print(f"  📝 [DRY RUN] 将更新: {code}")
                        stats["updated"] += 1
                    else:
                        print(f"  📝 [DRY RUN] 将创建: {code}")
                        stats["created"] += 1
                    continue
                
                if existing:
                    # 更新现有套餐
                    existing.name = plan_data.get("name", existing.name)
                    existing.description = plan_data.get("description", existing.description)
                    existing.traffic_bytes = plan_data.get("traffic_bytes", existing.traffic_bytes)
                    existing.duration_days = plan_data.get("duration_days", existing.duration_days)
                    existing.price_usd = plan_data.get("price_usd", existing.price_usd)
                    existing.supported_assets = plan_data.get("supported_assets", existing.supported_assets)
                    existing.enabled = plan_data.get("enabled", existing.enabled)
                    existing.sort_order = plan_data.get("sort_order", existing.sort_order)
                    print(f"  ✅ 更新: {code} - {existing.name}")
                    stats["updated"] += 1
                else:
                    # 创建新套餐
                    plan_id = plan_data.get("id") or str(ulid.new().str)
                    new_plan = Plan(
                        id=plan_id,
                        code=code,
                        name=plan_data["name"],
                        description=plan_data.get("description", ""),
                        traffic_bytes=plan_data["traffic_bytes"],
                        duration_days=plan_data["duration_days"],
                        price_usd=plan_data["price_usd"],
                        supported_assets=plan_data.get("supported_assets", ["SOL", "USDT_TRC20"]),
                        enabled=plan_data.get("enabled", True),
                        sort_order=plan_data.get("sort_order", 0),
                    )
                    session.add(new_plan)
                    print(f"  ✅ 创建: {code} - {new_plan.name}")
                    stats["created"] += 1
                    
            except Exception as e:
                print(f"  ❌ 错误处理 {code}: {e}")
                stats["errors"] += 1
        
        if not dry_run:
            await session.commit()
    
    return stats


async def main():
    """主函数"""
    args = parse_args()
    
    # 检查文件是否存在
    if not os.path.exists(args.file):
        print(f"❌ 错误: 文件不存在: {args.file}")
        sys.exit(1)
    
    print(f"📂 加载套餐数据: {args.file}")
    
    try:
        plans = load_plans(args.file)
        print(f"📊 找到 {len(plans)} 个套餐")
    except Exception as e:
        print(f"❌ 加载文件失败: {e}")
        sys.exit(1)
    
    if args.dry_run:
        print("🔍 试运行模式 (不写入数据库)")
    
    # 初始化数据库
    print("🔌 连接数据库...")
    init_db()
    
    # 导入套餐
    print("🚀 开始导入...")
    stats = await import_plans(plans, update_existing=args.update_existing, dry_run=args.dry_run)
    
    # 打印统计
    print("\n" + "=" * 50)
    print("📈 导入统计:")
    print(f"   创建: {stats['created']}")
    print(f"   更新: {stats['updated']}")
    print(f"   跳过: {stats['skipped']}")
    if stats['errors'] > 0:
        print(f"   错误: {stats['errors']}")
    print("=" * 50)
    
    if stats['errors'] > 0:
        sys.exit(1)


if __name__ == "__main__":
    asyncio.run(main())
