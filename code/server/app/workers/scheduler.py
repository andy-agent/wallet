"""
APScheduler 配置和启动模块

负责初始化和启动所有定时任务。
"""
import logging
from typing import Optional

from apscheduler.schedulers.asyncio import AsyncIOScheduler
from apscheduler.triggers.interval import IntervalTrigger
from apscheduler.events import EVENT_JOB_ERROR, EVENT_JOB_EXECUTED

from app.core.config import get_settings

logger = logging.getLogger(__name__)

# 全局调度器实例
_scheduler: Optional[AsyncIOScheduler] = None


def get_scheduler() -> Optional[AsyncIOScheduler]:
    """获取全局调度器实例"""
    return _scheduler


def _on_job_executed(event):
    """任务执行完成回调"""
    if event.exception:
        logger.error(f"Job {event.job_id} failed: {event.exception}")
    else:
        logger.debug(f"Job {event.job_id} executed successfully")


def _on_job_error(event):
    """任务执行错误回调"""
    logger.error(f"Job {event.job_id} raised an exception: {event.exception}", exc_info=event.exception)


async def start_scheduler() -> AsyncIOScheduler:
    """
    启动 APScheduler 调度器
    
    注册所有定时任务：
    - scan_pending_orders: 每10秒扫描待支付订单
    - confirm_seen_transactions: 每10秒确认链上交易
    - fulfill_paid_orders: 每5秒开通已支付订单
    - expire_orders: 每60秒过期超时订单
    - release_expired_addresses: 每300秒释放过期地址
    
    Returns:
        AsyncIOScheduler: 启动后的调度器实例
    """
    global _scheduler
    
    settings = get_settings()
    
    if not settings.worker_enabled:
        logger.info("Worker is disabled by configuration")
        return None
    
    if _scheduler and _scheduler.running:
        logger.warning("Scheduler is already running")
        return _scheduler
    
    # 创建调度器
    _scheduler = AsyncIOScheduler(
        timezone="UTC",
        job_defaults={
            "coalesce": True,  # 错过执行时间的任务合并为一次执行
            "max_instances": 1,  # 同一任务同时只能有一个实例运行
            "misfire_grace_time": 60,  # 错过执行时间后的宽限期（秒）
        }
    )
    
    # 延迟导入任务函数（避免循环依赖）
    from app.workers.scanner import (
        scan_pending_orders,
        confirm_seen_transactions,
        expire_orders,
        release_expired_addresses,
    )
    from app.workers.fulfillment import fulfill_paid_orders
    
    # 注册任务
    
    # 1. 扫描待支付订单 (每10秒)
    _scheduler.add_job(
        scan_pending_orders,
        trigger=IntervalTrigger(seconds=10),
        id="scan_pending_orders",
        name="Scan Pending Payment Orders",
        replace_existing=True,
    )
    
    # 2. 确认链上交易 (每10秒)
    _scheduler.add_job(
        confirm_seen_transactions,
        trigger=IntervalTrigger(seconds=10),
        id="confirm_seen_transactions",
        name="Confirm Seen Transactions",
        replace_existing=True,
    )
    
    # 3. 开通已支付订单 (每5秒)
    _scheduler.add_job(
        fulfill_paid_orders,
        trigger=IntervalTrigger(seconds=5),
        id="fulfill_paid_orders",
        name="Fulfill Paid Orders",
        replace_existing=True,
    )
    
    # 4. 过期超时订单 (每60秒)
    _scheduler.add_job(
        expire_orders,
        trigger=IntervalTrigger(seconds=60),
        id="expire_orders",
        name="Expire Timeout Orders",
        replace_existing=True,
    )
    
    # 5. 释放过期地址 (每300秒 = 5分钟)
    _scheduler.add_job(
        release_expired_addresses,
        trigger=IntervalTrigger(seconds=300),
        id="release_expired_addresses",
        name="Release Expired Addresses",
        replace_existing=True,
    )
    
    # 注册事件监听
    _scheduler.add_listener(_on_job_executed, EVENT_JOB_EXECUTED)
    _scheduler.add_listener(_on_job_error, EVENT_JOB_ERROR)
    
    # 启动调度器
    _scheduler.start()
    logger.info("Scheduler started with %d jobs", len(_scheduler.get_jobs()))
    
    return _scheduler


async def stop_scheduler():
    """
    停止 APScheduler 调度器
    
    优雅地关闭所有任务。
    """
    global _scheduler
    
    if _scheduler and _scheduler.running:
        _scheduler.shutdown(wait=True)
        logger.info("Scheduler stopped")
        _scheduler = None
    else:
        logger.debug("Scheduler is not running")


def get_job_status() -> dict:
    """
    获取所有任务状态
    
    Returns:
        dict: 任务状态信息
    """
    if not _scheduler:
        return {"status": "not_initialized", "jobs": []}
    
    jobs = []
    for job in _scheduler.get_jobs():
        jobs.append({
            "id": job.id,
            "name": job.name,
            "next_run_time": str(job.next_run_time) if job.next_run_time else None,
            "trigger": str(job.trigger),
        })
    
    return {
        "status": "running" if _scheduler.running else "stopped",
        "jobs": jobs,
    }
