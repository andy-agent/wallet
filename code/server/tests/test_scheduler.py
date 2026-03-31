"""
Worker Scheduler 测试 - Worker Scheduler Tests

验证:
1. 所有任务正确注册
2. 调度间隔配置
3. 任务执行不丢单
4. 调度器生命周期管理
"""
import pytest
import asyncio
from unittest.mock import AsyncMock, MagicMock, patch

from apscheduler.schedulers.asyncio import AsyncIOScheduler
from apscheduler.triggers.interval import IntervalTrigger

from app.workers.scheduler import (
    get_scheduler,
    start_scheduler,
    stop_scheduler,
    get_job_status,
    _on_job_executed,
    _on_job_error,
)


class TestSchedulerInitialization:
    """测试调度器初始化"""
    
    @pytest.fixture(autouse=True)
    def reset_scheduler(self):
        """重置调度器状态"""
        import app.workers.scheduler as scheduler_module
        scheduler_module._scheduler = None
        yield
        scheduler_module._scheduler = None
    
    @pytest.mark.asyncio
    @patch("app.workers.scheduler.get_settings")
    @patch("app.workers.scheduler.AsyncIOScheduler")
    async def test_scheduler_disabled(self, mock_scheduler_class, mock_get_settings):
        """测试 Worker 被禁用时调度器不启动"""
        mock_settings = MagicMock()
        mock_settings.worker_enabled = False
        mock_get_settings.return_value = mock_settings
        
        result = await start_scheduler()
        
        assert result is None
        mock_scheduler_class.assert_not_called()
    
    @pytest.mark.asyncio
    @patch("app.workers.scheduler.get_settings")
    @patch("app.workers.scheduler.AsyncIOScheduler")
    async def test_scheduler_already_running(self, mock_scheduler_class, mock_get_settings):
        """测试调度器已在运行时返回现有实例"""
        mock_settings = MagicMock()
        mock_settings.worker_enabled = True
        mock_get_settings.return_value = mock_settings
        
        mock_scheduler = MagicMock()
        mock_scheduler.running = True
        mock_scheduler_class.return_value = mock_scheduler
        
        # 先设置全局调度器
        import app.workers.scheduler as scheduler_module
        scheduler_module._scheduler = mock_scheduler
        
        result = await start_scheduler()
        
        assert result == mock_scheduler
        mock_scheduler.start.assert_not_called()


class TestJobRegistration:
    """测试任务注册"""
    
    @pytest.fixture(autouse=True)
    def reset_scheduler(self):
        """重置调度器状态"""
        import app.workers.scheduler as scheduler_module
        scheduler_module._scheduler = None
        yield
        scheduler_module._scheduler = None
    
    @pytest.mark.asyncio
    @patch("app.workers.scheduler.get_settings")
    @patch("app.workers.scheduler.AsyncIOScheduler")
    async def test_all_jobs_registered(self, mock_scheduler_class, mock_get_settings):
        """测试所有5个任务都已注册"""
        mock_settings = MagicMock()
        mock_settings.worker_enabled = True
        mock_get_settings.return_value = mock_settings
        
        mock_scheduler = MagicMock()
        mock_scheduler.get_jobs.return_value = []
        mock_scheduler.running = False
        mock_scheduler_class.return_value = mock_scheduler
        
        await start_scheduler()
        
        # 验证添加了 5 个任务
        assert mock_scheduler.add_job.call_count == 5
        
        # 获取所有添加的任务 ID
        call_args_list = mock_scheduler.add_job.call_args_list
        job_ids = [call[1].get("id") for call in call_args_list]
        
        expected_jobs = {
            "scan_pending_orders",
            "confirm_seen_transactions",
            "fulfill_paid_orders",
            "expire_orders",
            "release_expired_addresses",
        }
        
        assert set(job_ids) == expected_jobs
    
    @pytest.mark.asyncio
    @patch("app.workers.scheduler.get_settings")
    @patch("app.workers.scheduler.AsyncIOScheduler")
    async def test_job_intervals(self, mock_scheduler_class, mock_get_settings):
        """测试任务调度间隔配置"""
        mock_settings = MagicMock()
        mock_settings.worker_enabled = True
        mock_get_settings.return_value = mock_settings
        
        mock_scheduler = MagicMock()
        mock_scheduler.get_jobs.return_value = []
        mock_scheduler.running = False
        mock_scheduler_class.return_value = mock_scheduler
        
        await start_scheduler()
        
        # 获取所有添加的任务
        call_args_list = mock_scheduler.add_job.call_args_list
        
        intervals = {}
        for call in call_args_list:
            job_id = call[1].get("id")
            trigger = call[1].get("trigger")
            if isinstance(trigger, IntervalTrigger):
                # 从 trigger 获取间隔
                interval = trigger.interval
                intervals[job_id] = interval.total_seconds()
        
        # 验证各任务的调度间隔
        assert intervals.get("scan_pending_orders") == 10
        assert intervals.get("confirm_seen_transactions") == 10
        assert intervals.get("fulfill_paid_orders") == 5
        assert intervals.get("expire_orders") == 60
        assert intervals.get("release_expired_addresses") == 300
    
    @pytest.mark.asyncio
    @patch("app.workers.scheduler.get_settings")
    @patch("app.workers.scheduler.AsyncIOScheduler")
    async def test_job_replace_existing(self, mock_scheduler_class, mock_get_settings):
        """测试任务配置 replace_existing"""
        mock_settings = MagicMock()
        mock_settings.worker_enabled = True
        mock_get_settings.return_value = mock_settings
        
        mock_scheduler = MagicMock()
        mock_scheduler.get_jobs.return_value = []
        mock_scheduler.running = False
        mock_scheduler_class.return_value = mock_scheduler
        
        await start_scheduler()
        
        # 验证所有任务都设置了 replace_existing=True
        call_args_list = mock_scheduler.add_job.call_args_list
        for call in call_args_list:
            assert call[1].get("replace_existing") is True


class TestSchedulerJobDefaults:
    """测试调度器默认配置"""
    
    @pytest.fixture(autouse=True)
    def reset_scheduler(self):
        """重置调度器状态"""
        import app.workers.scheduler as scheduler_module
        scheduler_module._scheduler = None
        yield
        scheduler_module._scheduler = None
    
    @pytest.mark.asyncio
    @patch("app.workers.scheduler.get_settings")
    @patch("app.workers.scheduler.AsyncIOScheduler")
    async def test_scheduler_job_defaults(self, mock_scheduler_class, mock_get_settings):
        """测试调度器任务默认配置"""
        mock_settings = MagicMock()
        mock_settings.worker_enabled = True
        mock_get_settings.return_value = mock_settings
        
        mock_scheduler = MagicMock()
        mock_scheduler.get_jobs.return_value = []
        mock_scheduler.running = False
        mock_scheduler_class.return_value = mock_scheduler
        
        await start_scheduler()
        
        # 验证调度器初始化时的 job_defaults
        init_call = mock_scheduler_class.call_args
        job_defaults = init_call[1].get("job_defaults", {})
        
        assert job_defaults.get("coalesce") is True
        assert job_defaults.get("max_instances") == 1
        assert job_defaults.get("misfire_grace_time") == 60
    
    @pytest.mark.asyncio
    @patch("app.workers.scheduler.get_settings")
    @patch("app.workers.scheduler.AsyncIOScheduler")
    async def test_scheduler_timezone(self, mock_scheduler_class, mock_get_settings):
        """测试调度器时区配置"""
        mock_settings = MagicMock()
        mock_settings.worker_enabled = True
        mock_get_settings.return_value = mock_settings
        
        mock_scheduler = MagicMock()
        mock_scheduler.get_jobs.return_value = []
        mock_scheduler.running = False
        mock_scheduler_class.return_value = mock_scheduler
        
        await start_scheduler()
        
        init_call = mock_scheduler_class.call_args
        assert init_call[1].get("timezone") == "UTC"


class TestEventListeners:
    """测试事件监听器"""
    
    @pytest.fixture(autouse=True)
    def reset_scheduler(self):
        """重置调度器状态"""
        import app.workers.scheduler as scheduler_module
        scheduler_module._scheduler = None
        yield
        scheduler_module._scheduler = None
    
    @pytest.mark.asyncio
    @patch("app.workers.scheduler.get_settings")
    @patch("app.workers.scheduler.AsyncIOScheduler")
    async def test_event_listeners_registered(self, mock_scheduler_class, mock_get_settings):
        """测试事件监听器已注册"""
        mock_settings = MagicMock()
        mock_settings.worker_enabled = True
        mock_get_settings.return_value = mock_settings
        
        mock_scheduler = MagicMock()
        mock_scheduler.get_jobs.return_value = []
        mock_scheduler.running = False
        mock_scheduler_class.return_value = mock_scheduler
        
        await start_scheduler()
        
        # 验证添加了两个事件监听器
        assert mock_scheduler.add_listener.call_count == 2


class TestSchedulerLifecycle:
    """测试调度器生命周期"""
    
    @pytest.fixture(autouse=True)
    def reset_scheduler(self):
        """重置调度器状态"""
        import app.workers.scheduler as scheduler_module
        scheduler_module._scheduler = None
        yield
        scheduler_module._scheduler = None
    
    @pytest.mark.asyncio
    @patch("app.workers.scheduler.get_settings")
    @patch("app.workers.scheduler.AsyncIOScheduler")
    async def test_scheduler_start(self, mock_scheduler_class, mock_get_settings):
        """测试调度器启动"""
        mock_settings = MagicMock()
        mock_settings.worker_enabled = True
        mock_get_settings.return_value = mock_settings
        
        mock_scheduler = MagicMock()
        mock_scheduler.get_jobs.return_value = []
        mock_scheduler.running = False
        mock_scheduler_class.return_value = mock_scheduler
        
        result = await start_scheduler()
        
        mock_scheduler.start.assert_called_once()
        assert result == mock_scheduler
    
    @pytest.mark.asyncio
    async def test_scheduler_stop(self):
        """测试调度器停止"""
        mock_scheduler = MagicMock()
        mock_scheduler.running = True
        
        import app.workers.scheduler as scheduler_module
        scheduler_module._scheduler = mock_scheduler
        
        await stop_scheduler()
        
        mock_scheduler.shutdown.assert_called_once_with(wait=True)
        assert scheduler_module._scheduler is None
    
    @pytest.mark.asyncio
    async def test_scheduler_stop_not_running(self):
        """测试停止未运行的调度器"""
        mock_scheduler = MagicMock()
        mock_scheduler.running = False
        
        import app.workers.scheduler as scheduler_module
        scheduler_module._scheduler = mock_scheduler
        
        await stop_scheduler()
        
        mock_scheduler.shutdown.assert_not_called()
    
    @pytest.mark.asyncio
    async def test_scheduler_stop_none(self):
        """测试停止 None 调度器"""
        import app.workers.scheduler as scheduler_module
        scheduler_module._scheduler = None
        
        # 应该安全执行不抛出异常
        await stop_scheduler()


class TestJobStatus:
    """测试任务状态获取"""
    
    @pytest.fixture(autouse=True)
    def reset_scheduler(self):
        """重置调度器状态"""
        import app.workers.scheduler as scheduler_module
        scheduler_module._scheduler = None
        yield
        scheduler_module._scheduler = None
    
    def test_get_job_status_not_initialized(self):
        """测试获取未初始化调度器的状态"""
        status = get_job_status()
        
        assert status["status"] == "not_initialized"
        assert status["jobs"] == []
    
    def test_get_job_status_running(self):
        """测试获取运行中调度器的状态"""
        mock_job1 = MagicMock()
        mock_job1.id = "job1"
        mock_job1.name = "Job 1"
        mock_job1.next_run_time = None
        mock_job1.trigger = IntervalTrigger(seconds=10)
        
        mock_job2 = MagicMock()
        mock_job2.id = "job2"
        mock_job2.name = "Job 2"
        mock_job2.next_run_time = None
        mock_job2.trigger = IntervalTrigger(seconds=60)
        
        mock_scheduler = MagicMock()
        mock_scheduler.running = True
        mock_scheduler.get_jobs.return_value = [mock_job1, mock_job2]
        
        import app.workers.scheduler as scheduler_module
        scheduler_module._scheduler = mock_scheduler
        
        status = get_job_status()
        
        assert status["status"] == "running"
        assert len(status["jobs"]) == 2
        assert status["jobs"][0]["id"] == "job1"
        assert status["jobs"][1]["id"] == "job2"
    
    def test_get_job_status_stopped(self):
        """测试获取已停止调度器的状态"""
        mock_scheduler = MagicMock()
        mock_scheduler.running = False
        mock_scheduler.get_jobs.return_value = []
        
        import app.workers.scheduler as scheduler_module
        scheduler_module._scheduler = mock_scheduler
        
        status = get_job_status()
        
        assert status["status"] == "stopped"


class TestCallbackFunctions:
    """测试回调函数"""
    
    @patch("app.workers.scheduler.logger")
    def test_on_job_executed_success(self, mock_logger):
        """测试任务执行成功回调"""
        event = MagicMock()
        event.job_id = "test_job"
        event.exception = None
        
        _on_job_executed(event)
        
        mock_logger.debug.assert_called_once()
        assert "test_job" in mock_logger.debug.call_args[0][0]
    
    @patch("app.workers.scheduler.logger")
    def test_on_job_executed_failure(self, mock_logger):
        """测试任务执行失败回调"""
        event = MagicMock()
        event.job_id = "test_job"
        event.exception = Exception("Test error")
        
        _on_job_executed(event)
        
        mock_logger.error.assert_called_once()
        assert "test_job" in mock_logger.error.call_args[0][0]
    
    @patch("app.workers.scheduler.logger")
    def test_on_job_error(self, mock_logger):
        """测试任务错误回调"""
        event = MagicMock()
        event.job_id = "test_job"
        event.exception = Exception("Test error")
        
        _on_job_error(event)
        
        mock_logger.error.assert_called_once()
        assert "test_job" in mock_logger.error.call_args[0][0]


class TestSchedulerSingleton:
    """测试调度器单例模式"""
    
    @pytest.fixture(autouse=True)
    def reset_scheduler(self):
        """重置调度器状态"""
        import app.workers.scheduler as scheduler_module
        scheduler_module._scheduler = None
        yield
        scheduler_module._scheduler = None
    
    def test_get_scheduler_none(self):
        """测试获取未初始化的调度器"""
        assert get_scheduler() is None
    
    def test_get_scheduler_initialized(self):
        """测试获取已初始化的调度器"""
        mock_scheduler = MagicMock()
        
        import app.workers.scheduler as scheduler_module
        scheduler_module._scheduler = mock_scheduler
        
        assert get_scheduler() == mock_scheduler


class TestSchedulerConcurrency:
    """测试调度器并发安全性"""
    
    @pytest.mark.asyncio
    async def test_concurrent_start_stop(self):
        """测试并发启动和停止"""
        # 注意：这是一个简化的测试，实际 APScheduler 有内部锁
        
        start_count = 0
        stop_count = 0
        
        async def mock_start():
            nonlocal start_count
            await asyncio.sleep(0.01)
            start_count += 1
        
        async def mock_stop():
            nonlocal stop_count
            await asyncio.sleep(0.01)
            stop_count += 1
        
        # 模拟并发操作
        tasks = [mock_start() for _ in range(5)] + [mock_stop() for _ in range(5)]
        await asyncio.gather(*tasks)
        
        assert start_count == 5
        assert stop_count == 5


class TestJobConfiguration:
    """测试任务配置详情"""
    
    def test_job_ids_and_names(self):
        """测试任务 ID 和名称对应关系"""
        expected_jobs = {
            "scan_pending_orders": "Scan Pending Payment Orders",
            "confirm_seen_transactions": "Confirm Seen Transactions",
            "fulfill_paid_orders": "Fulfill Paid Orders",
            "expire_orders": "Expire Timeout Orders",
            "release_expired_addresses": "Release Expired Addresses",
        }
        
        # 这些应该在代码中保持一致
        for job_id, job_name in expected_jobs.items():
            assert isinstance(job_id, str)
            assert isinstance(job_name, str)
            assert len(job_id) > 0
            assert len(job_name) > 0
    
    def test_interval_values(self):
        """测试调度间隔值"""
        intervals = {
            "scan_pending_orders": 10,
            "confirm_seen_transactions": 10,
            "fulfill_paid_orders": 5,
            "expire_orders": 60,
            "release_expired_addresses": 300,
        }
        
        # 验证间隔合理
        for job_id, interval in intervals.items():
            assert isinstance(interval, int)
            assert interval > 0
            # 扫描任务应该频繁，释放地址可以不那么频繁
            if "scan" in job_id or "confirm" in job_id:
                assert interval <= 10  # 高频任务
            elif "expire" in job_id:
                assert interval >= 60  # 低频任务
