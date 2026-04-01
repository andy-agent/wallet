"""
Worker 入口点 - 直接运行: python -m app.workers
"""
import asyncio
import logging
import os

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


async def main():
    """启动 Worker Scheduler"""
    from app.workers.scheduler import start_scheduler
    
    logger.info("Starting Worker...")
    
    scheduler = await start_scheduler()
    
    if scheduler is None:
        logger.error("Failed to start scheduler (returned None)")
        return 1
    
    logger.info("Worker started successfully!")
    logger.info(f"Jobs registered: {[job.id for job in scheduler.get_jobs()]}")
    
    # 保持运行
    try:
        while True:
            await asyncio.sleep(60)
            logger.debug("Worker is alive...")
    except KeyboardInterrupt:
        logger.info("Worker stopped by user")
    except Exception as e:
        logger.error(f"Worker error: {e}")
        return 1
    
    return 0


if __name__ == "__main__":
    exit_code = asyncio.run(main())
    exit(exit_code)
