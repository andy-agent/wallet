# v2rayNG Payment Bridge 部署指南

## 概述

本文档描述了 v2rayNG Payment Bridge 项目的 Docker 化部署流程。

## 服务器信息

- **IP**: 154.36.173.184
- **OS**: Ubuntu 24.04 LTS
- **SSH 密钥**: `/users/cnyirui/server/154.36.173.184/keys/154.36.173.184.pem`

## 部署约束

1. **不使用 443 端口** - xray 服务已占用，使用 8080/8443 替代
2. **不干扰 xray 服务** - 独立运行，不修改现有配置
3. **区块链钱包服务不对外暴露** - 仅容器内部访问
4. **数据库使用容器内网络** - 不暴露 PostgreSQL 和 Redis 端口
5. **Docker 容器化部署** - 所有服务运行在 Docker 容器中

## 架构图

```
┌─────────────────────────────────────────────────────────────┐
│                     测试服务器 (154.36.173.184)               │
│                                                              │
│  ┌─────────────┐      ┌─────────────┐      ┌─────────────┐  │
│  │   Nginx     │      │    API      │      │   Worker    │  │
│  │   :8443     │◄────►│   :8000     │      │  (后台任务)  │  │
│  └─────────────┘      └──────┬──────┘      └──────┬──────┘  │
│                              │                     │        │
│  ┌─────────────┐      ┌──────┴──────┐      ┌──────┴──────┐  │
│  │   xray      │      │  PostgreSQL │      │    Redis    │  │
│  │   :443      │      │  (内部网络)  │      │  (内部网络)  │  │
│  │  (现有服务)  │      └─────────────┘      └─────────────┘  │
│  └─────────────┘                                            │
│                                                              │
│  ┌─────────────┐                                            │
│  │   Wallet    │  ← 仅内部网络访问，不暴露宿主机端口          │
│  │   :9000     │                                            │
│  └─────────────┘                                            │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

## 快速部署

### 1. 一键部署脚本

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/deploy
./deploy-test.sh
```

### 2. 手动部署步骤

#### 2.1 安装 Docker 和 Docker Compose

```bash
# 安装 Docker
if ! command -v docker &> /dev/null; then
    apt-get update
    apt-get install -y apt-transport-https ca-certificates curl gnupg lsb-release
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
    echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null
    apt-get update
    apt-get install -y docker-ce docker-ce-cli containerd.io
    systemctl enable docker
    systemctl start docker
fi

# 安装 Docker Compose
if ! command -v docker-compose &> /dev/null; then
    curl -L "https://github.com/docker/compose/releases/download/v2.23.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
fi
```

#### 2.2 上传代码

```bash
# 在本地执行
rsync -avz -e "ssh -i /users/cnyirui/server/154.36.173.184/keys/154.36.173.184.pem" \
    --exclude='.git' \
    --exclude='__pycache__' \
    --exclude='*.pyc' \
    --exclude='.env' \
    --exclude='*.log' \
    /Users/cnyirui/git/projects/liaojiang/code/ \
    root@154.36.173.184:/opt/payment-bridge/
```

#### 2.3 配置环境变量

```bash
# 在服务器上执行
ssh -i /users/cnyirui/server/154.36.173.184/keys/154.36.173.184.pem root@154.36.173.184
cd /opt/payment-bridge/deploy

# 复制环境变量模板
cp .env.example .env

# 编辑 .env 文件，填入正确的配置
vim .env
```

#### 2.4 启动服务

```bash
cd /opt/payment-bridge/deploy

# 停止旧服务
docker-compose -f docker-compose.test.yml down

# 构建并启动
docker-compose -f docker-compose.test.yml up -d --build

# 等待数据库就绪
sleep 10

# 执行数据库迁移
docker-compose -f docker-compose.test.yml exec -T api alembic upgrade head

# 检查服务状态
docker-compose -f docker-compose.test.yml ps
```

## 服务说明

### 1. PostgreSQL (payment_postgres)

- **镜像**: postgres:15-alpine
- **端口**: 不暴露到宿主机（仅容器内访问）
- **数据卷**: postgres_data
- **健康检查**: pg_isready

### 2. Redis (payment_redis)

- **镜像**: redis:7-alpine
- **端口**: 不暴露到宿主机（仅容器内访问）
- **数据卷**: redis_data
- **持久化**: AOF 模式

### 3. Wallet Service (payment_wallet)

- **构建**: code/server/Dockerfile.wallet
- **端口**: 9000（仅容器内访问，不映射到宿主机）
- **功能**: 区块链地址生成、余额查询、支付检测
- **健康检查**: /health

### 4. API Service (payment_api)

- **构建**: code/server/Dockerfile
- **端口**: 8080（映射到容器内 8000）
- **功能**: REST API 服务
- **健康检查**: /healthz
- **依赖**: postgres, redis, wallet

### 5. Worker Service (payment_worker)

- **构建**: code/server/Dockerfile
- **功能**: 后台任务处理（订单扫描、支付检测）
- **依赖**: postgres, redis, wallet

### 6. Nginx (payment_nginx) [可选]

- **镜像**: nginx:alpine
- **端口**: 8443（HTTPS 替代端口）
- **功能**: 反向代理、SSL 终止

## 环境变量说明

| 变量名 | 说明 | 示例 |
|--------|------|------|
| `APP_ENV` | 应用环境 | production |
| `DEBUG` | 调试模式 | false |
| `DATABASE_URL` | 数据库连接 URL | postgresql+asyncpg://... |
| `REDIS_URL` | Redis 连接 URL | redis://redis:6379/0 |
| `JWT_SECRET` | JWT 密钥 | openssl rand -hex 32 |
| `ENCRYPTION_MASTER_KEY` | 加密主密钥 | openssl rand -base64 32 |
| `MARZBAN_BASE_URL` | Marzban 服务地址 | https://marzban.example.com |
| `MARZBAN_ADMIN_USERNAME` | Marzban 管理员用户名 | admin |
| `MARZBAN_ADMIN_PASSWORD` | Marzban 管理员密码 | your-password |
| `SOLANA_RPC_URL` | Solana RPC 节点 | https://api.devnet.solana.com |
| `SOLANA_MOCK_MODE` | Solana 模拟模式 | false |
| `TRON_RPC_URL` | Tron RPC 节点 | https://nile.trongrid.io |
| `TRON_USDT_CONTRACT` | USDT 合约地址 | TXYZopYRdj2D9XRtbG411XZZ3kpm5bGnNF |
| `TRON_MOCK_MODE` | Tron 模拟模式 | false |
| `ADMIN_TOKEN` | 管理 API Token | openssl rand -hex 16 |

## 常用命令

### 查看日志

```bash
# 查看所有服务日志
docker-compose -f docker-compose.test.yml logs -f

# 查看特定服务日志
docker-compose -f docker-compose.test.yml logs -f api
docker-compose -f docker-compose.test.yml logs -f worker
docker-compose -f docker-compose.test.yml logs -f wallet
```

### 重启服务

```bash
# 重启所有服务
docker-compose -f docker-compose.test.yml restart

# 重启特定服务
docker-compose -f docker-compose.test.yml restart api
docker-compose -f docker-compose.test.yml restart worker
```

### 进入容器

```bash
# 进入 API 容器
docker-compose -f docker-compose.test.yml exec api bash

# 进入数据库容器
docker-compose -f docker-compose.test.yml exec postgres psql -U payment -d payment_db
```

### 数据库迁移

```bash
# 执行迁移
docker-compose -f docker-compose.test.yml exec -T api alembic upgrade head

# 回滚迁移
docker-compose -f docker-compose.test.yml exec -T api alembic downgrade -1
```

### 备份数据

```bash
# 备份 PostgreSQL
docker-compose -f docker-compose.test.yml exec postgres pg_dump -U payment payment_db > backup.sql

# 备份 Redis
docker-compose -f docker-compose.test.yml exec redis redis-cli BGSAVE
```

## 健康检查

### API 健康检查

```bash
curl http://154.36.173.184:8080/healthz
```

预期响应:
```json
{
  "status": "healthy",
  "version": "1.0.0"
}
```

### 套餐列表检查

```bash
curl http://154.36.173.184:8080/client/v1/plans
```

## 端到端测试

### 1. 创建订单

```bash
curl -X POST http://154.36.173.184:8080/client/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "plan_id": 1,
    "chain": "solana",
    "asset_code": "SOL"
  }'
```

### 2. Mock 支付（测试模式）

在 `SOLANA_MOCK_MODE=true` 模式下，可以通过管理 API 模拟支付：

```bash
curl -X POST http://154.36.173.184:8080/admin/v1/orders/{order_id}/mock-pay \
  -H "Authorization: Bearer ${ADMIN_TOKEN}"
```

### 3. 查询订单状态

```bash
curl http://154.36.173.184:8080/client/v1/orders/{order_id}
```

### 4. 拉取订阅

```bash
curl http://154.36.173.184:8080/client/v1/subscription/{subscription_code}
```

## 故障排查

### 服务无法启动

1. 检查端口占用
   ```bash
   netstat -tlnp | grep 8080
   ```

2. 检查 Docker 日志
   ```bash
   docker-compose -f docker-compose.test.yml logs
   ```

3. 检查环境变量配置
   ```bash
   cat /opt/payment-bridge/deploy/.env
   ```

### 数据库连接失败

1. 检查数据库服务状态
   ```bash
   docker-compose -f docker-compose.test.yml ps postgres
   ```

2. 检查数据库连接 URL
   ```bash
   docker-compose -f docker-compose.test.yml exec api env | grep DATABASE
   ```

### 支付检测失败

1. 检查 Wallet 服务状态
   ```bash
   docker-compose -f docker-compose.test.yml logs wallet
   ```

2. 检查 RPC 节点可用性
   ```bash
   curl ${SOLANA_RPC_URL}/health
   ```

## 安全注意事项

1. **修改默认密码**: 生产环境必须修改所有默认密码
2. **密钥管理**: JWT_SECRET 和 ENCRYPTION_MASTER_KEY 必须安全存储
3. **防火墙配置**: 只开放 8080 和 8443 端口
4. **定期备份**: 配置数据库自动备份
5. **日志审计**: 定期检查异常日志

## 回滚方案

如果部署失败，执行以下命令回滚：

```bash
cd /opt/payment-bridge/deploy

# 停止服务
docker-compose -f docker-compose.test.yml down

# 恢复到上一版本（如果有备份）
docker-compose -f docker-compose.test.yml pull
docker-compose -f docker-compose.test.yml up -d
```

## 联系信息

- 技术负责人: [待填写]
- 服务器提供商: [待填写]

---

**最后更新**: 2026-03-31
**版本**: 1.0.0
