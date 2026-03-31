#!/usr/bin/env python3
"""
Marzban 真实连接测试脚本

测试内容:
1. 管理员登录获取 token
2. 创建用户 API
3. 查询用户 API
4. 更新用户 API（续费）
5. 获取订阅链接
6. 删除用户

使用方法:
    cd /Users/cnyirui/git/projects/liaojiang/code/server
    python scripts/test_marzban_real.py

环境变量:
    MARZBAN_BASE_URL - Marzban 面板地址
    MARZBAN_ADMIN_USERNAME - 管理员用户名
    MARZBAN_ADMIN_PASSWORD - 管理员密码
"""

import asyncio
import os
import sys
from datetime import datetime, timedelta, timezone

# 添加项目路径
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from app.integrations.marzban import MarzbanClient, MarzbanAPIError


class MarzbanRealTest:
    """Marzban 真实连接测试类"""
    
    def __init__(self):
        self.base_url = os.getenv("MARZBAN_BASE_URL", "")
        self.username = os.getenv("MARZBAN_ADMIN_USERNAME", "")
        self.password = os.getenv("MARZBAN_ADMIN_PASSWORD", "")
        self.client: MarzbanClient = None
        self.test_username = f"test_user_{int(datetime.now().timestamp())}"
        self.results = []
        
    def log(self, message: str, level: str = "INFO"):
        """记录日志"""
        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        print(f"[{timestamp}] [{level}] {message}")
        self.results.append({"time": timestamp, "level": level, "message": message})
        
    def check_config(self) -> bool:
        """检查配置是否完整"""
        self.log("=" * 60)
        self.log("检查 Marzban 配置")
        self.log("=" * 60)
        
        missing = []
        if not self.base_url:
            missing.append("MARZBAN_BASE_URL")
        if not self.username:
            missing.append("MARZBAN_ADMIN_USERNAME")
        if not self.password:
            missing.append("MARZBAN_ADMIN_PASSWORD")
            
        if missing:
            self.log(f"缺少配置项: {', '.join(missing)}", "ERROR")
            self.log("请设置以下环境变量或修改 .env 文件:")
            self.log(f"  export MARZBAN_BASE_URL=https://your-marzban.com")
            self.log(f"  export MARZBAN_ADMIN_USERNAME=admin")
            self.log(f"  export MARZBAN_ADMIN_PASSWORD=your-password")
            return False
            
        self.log(f"MARZBAN_BASE_URL: {self.base_url}")
        self.log(f"MARZBAN_ADMIN_USERNAME: {self.username}")
        self.log("配置检查通过 ✓")
        return True
        
    async def test_authenticate(self) -> bool:
        """测试管理员登录"""
        self.log("")
        self.log("-" * 60)
        self.log("测试 1: 管理员登录获取 Token")
        self.log("-" * 60)
        
        try:
            self.client = MarzbanClient(
                base_url=self.base_url,
                username=self.username,
                password=self.password
            )
            token = await self.client.authenticate()
            
            if token:
                # 隐藏部分 token 用于显示
                display_token = token[:20] + "..." + token[-10:] if len(token) > 30 else token
                self.log(f"登录成功 ✓")
                self.log(f"Token: {display_token}")
                return True
            else:
                self.log("登录失败: 未获取到 token", "ERROR")
                return False
                
        except MarzbanAPIError as e:
            self.log(f"登录失败: {e.message}", "ERROR")
            if e.status_code:
                self.log(f"HTTP 状态码: {e.status_code}", "ERROR")
            return False
        except Exception as e:
            self.log(f"登录异常: {str(e)}", "ERROR")
            return False
            
    async def test_create_user(self) -> bool:
        """测试创建用户"""
        self.log("")
        self.log("-" * 60)
        self.log("测试 2: 创建用户")
        self.log("-" * 60)
        
        try:
            # 计算过期时间 (30天后)
            expire = int((datetime.now(timezone.utc) + timedelta(days=30)).timestamp())
            # 10GB 流量限制
            data_limit = 10 * 1024 * 1024 * 1024
            
            self.log(f"用户名: {self.test_username}")
            self.log(f"过期时间: {datetime.fromtimestamp(expire).isoformat()}")
            self.log(f"流量限制: {data_limit / 1024 / 1024 / 1024:.2f} GB")
            
            user = await self.client.create_user(
                username=self.test_username,
                expire=expire,
                data_limit=data_limit
            )
            
            self.log(f"用户创建成功 ✓")
            self.log(f"  - 用户名: {user.username}")
            self.log(f"  - 状态: {user.status}")
            self.log(f"  - 订阅链接: {user.subscription_url}")
            return True
            
        except MarzbanAPIError as e:
            self.log(f"创建用户失败: {e.message}", "ERROR")
            if e.status_code == 409:
                self.log("用户已存在，将尝试使用其他用户名", "WARNING")
                self.test_username = f"test_user_{int(datetime.now().timestamp())}_2"
                return await self.test_create_user()
            return False
        except Exception as e:
            self.log(f"创建用户异常: {str(e)}", "ERROR")
            return False
            
    async def test_get_user(self) -> bool:
        """测试查询用户"""
        self.log("")
        self.log("-" * 60)
        self.log("测试 3: 查询用户")
        self.log("-" * 60)
        
        try:
            user = await self.client.get_user(self.test_username)
            
            if user:
                self.log(f"查询用户成功 ✓")
                self.log(f"  - 用户名: {user.username}")
                self.log(f"  - 状态: {user.status}")
                self.log(f"  - 过期时间: {datetime.fromtimestamp(user.expire).isoformat() if user.expire else '永不过期'}")
                self.log(f"  - 流量限制: {user.data_limit / 1024 / 1024 / 1024:.2f} GB" if user.data_limit else "  - 流量限制: 无限制")
                self.log(f"  - 已用流量: {user.used_traffic / 1024 / 1024:.2f} MB")
                self.log(f"  - 订阅链接: {user.subscription_url}")
                return True
            else:
                self.log(f"用户不存在: {self.test_username}", "ERROR")
                return False
                
        except Exception as e:
            self.log(f"查询用户异常: {str(e)}", "ERROR")
            return False
            
    async def test_modify_user(self) -> bool:
        """测试更新用户（续费场景）"""
        self.log("")
        self.log("-" * 60)
        self.log("测试 4: 更新用户（续费）")
        self.log("-" * 60)
        
        try:
            # 获取当前用户信息
            current_user = await self.client.get_user(self.test_username)
            if not current_user:
                self.log("用户不存在，无法续费", "ERROR")
                return False
                
            # 计算新的过期时间（延长 30 天）
            current_expire = current_user.expire or int(datetime.now(timezone.utc).timestamp())
            new_expire = current_expire + (30 * 24 * 60 * 60)  # 增加 30 天
            
            # 增加流量（增加 10GB）
            current_limit = current_user.data_limit or 0
            new_data_limit = current_limit + (10 * 1024 * 1024 * 1024)
            
            self.log(f"续费前:")
            self.log(f"  - 过期时间: {datetime.fromtimestamp(current_expire).isoformat()}")
            self.log(f"  - 流量限制: {current_limit / 1024 / 1024 / 1024:.2f} GB")
            self.log(f"续费后:")
            self.log(f"  - 过期时间: {datetime.fromtimestamp(new_expire).isoformat()}")
            self.log(f"  - 流量限制: {new_data_limit / 1024 / 1024 / 1024:.2f} GB")
            
            updated_user = await self.client.modify_user(
                username=self.test_username,
                expire=new_expire,
                data_limit=new_data_limit,
                status="active"
            )
            
            self.log(f"用户续费成功 ✓")
            self.log(f"  - 新过期时间: {datetime.fromtimestamp(updated_user.expire).isoformat()}")
            self.log(f"  - 新流量限制: {updated_user.data_limit / 1024 / 1024 / 1024:.2f} GB")
            return True
            
        except MarzbanAPIError as e:
            self.log(f"续费失败: {e.message}", "ERROR")
            return False
        except Exception as e:
            self.log(f"续费异常: {str(e)}", "ERROR")
            return False
            
    async def test_get_subscription(self) -> bool:
        """测试获取订阅内容"""
        self.log("")
        self.log("-" * 60)
        self.log("测试 5: 获取订阅链接内容")
        self.log("-" * 60)
        
        try:
            # 获取用户信息
            user = await self.client.get_user(self.test_username)
            if not user:
                self.log("用户不存在", "ERROR")
                return False
                
            subscription_url = user.subscription_url
            self.log(f"订阅链接: {subscription_url}")
            
            # 从 URL 中提取 token
            if "/sub/" in subscription_url:
                token = subscription_url.split("/sub/")[-1]
            else:
                self.log("无法从订阅链接提取 token", "WARNING")
                return True  # 不算失败
                
            # 获取订阅内容
            try:
                content = await self.client.get_subscription_content(self.test_username, token)
                content_preview = content[:200] + "..." if len(content) > 200 else content
                self.log(f"订阅内容获取成功 ✓")
                self.log(f"  - 内容长度: {len(content)} 字符")
                self.log(f"  - 内容预览: {content_preview}")
                return True
            except MarzbanAPIError as e:
                self.log(f"获取订阅内容失败: {e.message}", "WARNING")
                self.log("这可能是因为订阅链接需要特定 User-Agent 或其他配置", "INFO")
                return True  # 连接成功即可
                
        except Exception as e:
            self.log(f"获取订阅异常: {str(e)}", "ERROR")
            return False
            
    async def test_delete_user(self) -> bool:
        """测试删除用户"""
        self.log("")
        self.log("-" * 60)
        self.log("测试 6: 删除测试用户")
        self.log("-" * 60)
        
        try:
            result = await self.client.delete_user(self.test_username)
            
            if result:
                self.log(f"用户删除成功 ✓")
                self.log(f"  - 已删除用户: {self.test_username}")
                return True
            else:
                self.log(f"用户不存在或删除失败", "WARNING")
                return True  # 用户不存在也算清理成功
                
        except MarzbanAPIError as e:
            self.log(f"删除用户失败: {e.message}", "ERROR")
            return False
        except Exception as e:
            self.log(f"删除用户异常: {str(e)}", "ERROR")
            return False
            
    async def cleanup(self):
        """清理资源"""
        if self.client:
            await self.client.close()
            
    async def run_all_tests(self):
        """运行所有测试"""
        self.log("")
        self.log("=" * 60)
        self.log("Marzban 真实连接测试开始")
        self.log("=" * 60)
        
        # 检查配置
        if not self.check_config():
            return False
            
        try:
            # 测试 1: 登录
            if not await self.test_authenticate():
                self.log("\n认证失败，停止后续测试", "ERROR")
                return False
                
            # 测试 2: 创建用户
            if not await self.test_create_user():
                self.log("\n创建用户失败，停止后续测试", "ERROR")
                return False
                
            # 测试 3: 查询用户
            await self.test_get_user()
            
            # 测试 4: 续费
            await self.test_modify_user()
            
            # 测试 5: 获取订阅
            await self.test_get_subscription()
            
            # 测试 6: 删除用户
            await self.test_delete_user()
            
            return True
            
        except Exception as e:
            self.log(f"\n测试过程中发生异常: {str(e)}", "ERROR")
            return False
        finally:
            await self.cleanup()
            
    def print_summary(self):
        """打印测试摘要"""
        self.log("")
        self.log("=" * 60)
        self.log("测试摘要")
        self.log("=" * 60)
        
        errors = [r for r in self.results if r["level"] == "ERROR"]
        warnings = [r for r in self.results if r["level"] == "WARNING"]
        
        if errors:
            self.log(f"错误数: {len(errors)}", "ERROR")
        if warnings:
            self.log(f"警告数: {len(warnings)}", "WARNING")
            
        if not errors:
            self.log("所有测试完成 ✓")
        else:
            self.log("测试完成，但存在错误", "WARNING")


async def main():
    """主函数"""
    # 加载 .env 文件
    env_path = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), ".env")
    if os.path.exists(env_path):
        print(f"加载环境变量: {env_path}")
        with open(env_path) as f:
            for line in f:
                line = line.strip()
                if line and not line.startswith("#") and "=" in line:
                    key, value = line.split("=", 1)
                    os.environ.setdefault(key, value)
    
    test = MarzbanRealTest()
    success = await test.run_all_tests()
    test.print_summary()
    
    return 0 if success else 1


if __name__ == "__main__":
    exit_code = asyncio.run(main())
    sys.exit(exit_code)
