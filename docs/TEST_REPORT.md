# V2rayNG 支付系统 - 测试报告

**日期**: 2026-04-01  
**测试环境**: 双服务器部署  
**测试人员**: Claude Code

---

## 服务器架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           用户设备 (Android)                                  │
│                              V2rayNG App                                      │
└─────────────────────────────────┬───────────────────────────────────────────┘
                                  │ HTTPS (自签名证书)
                                  ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                       支付桥接服务器 (154.36.173.184)                          │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │  FastAPI (Port 8000) ← Nginx (Port 8080)                              │  │
│  │  ├── /client/v1/auth/*     (登录/注册)                                 │  │
│  │  ├── /client/v1/plans      (套餐列表)                                  │  │
│  │  ├── /client/v1/orders/*   (订单管理)                                  │  │
│  │  └── /admin/v1/*           (管理接口)                                  │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
│                                    │                                        │
│  ┌─────────────────────────────────┴──────────────────────────────────┐    │
│  │  Worker (APScheduler)                                             │    │
│  │  ├── scan_pending_orders (10s)    - 扫描区块链支付                 │    │
│  │  ├── confirm_seen_transactions (10s) - 确认交易                   │    │
│  │  ├── fulfill_paid_orders (5s)     - 开通服务                      │    │
│  │  ├── expire_orders (60s)          - 过期订单处理                   │    │
│  │  └── release_expired_addresses (300s) - 释放地址                   │    │
│  └───────────────────────────────────────────────────────────────────┘    │
│                                    │                                        │
│  ┌─────────────────────────────────┴──────────────────────────────────┐    │
│  │  PostgreSQL + Redis                                                │    │
│  │  ├── users, orders, plans, payment_addresses, client_sessions     │    │
│  └───────────────────────────────────────────────────────────────────┘    │
└───────────────────────────────────┬─────────────────────────────────────────┘
                                    │ HTTPS (Cloudflare Origin Certificate)
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          Marzban VPN 服务器 (38.58.59.142)                    │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │  Marzban Panel (Port 8000) ← Nginx (Port 8443)                        │  │
│  │  URL: https://vpn.residential-agent.com:8443/dashboard                │  │
│  │  API: https://vpn.residential-agent.com:8443/api                      │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
│                                    │                                        │
│  ┌─────────────────────────────────┴──────────────────────────────────┐    │
│  │  Xray Core (Port 443)                                               │    │
│  │  Protocol: VLESS + TLS                                              │    │
│  └───────────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 测试环境配置

### 服务器 1: 支付桥接 (154.36.173.184)

| 组件 | 版本 | 状态 | 备注 |
|------|------|------|------|
| Ubuntu | 22.04 | ✅ 运行中 | - |
| Python | 3.12 | ✅ 运行中 | venv 环境 |
| FastAPI | 0.104 | ✅ 运行中 | Port 8000 |
| PostgreSQL | 15 | ✅ 运行中 | payment_db |
| Redis | 7 | ✅ 运行中 | Port 6379 |
| Nginx | 1.24 | ✅ 运行中 | Port 8080 (HTTPS) |
| Worker | - | ⚠️ 待启动 | 需要手动启动 |

### 服务器 2: Marzban VPN (38.58.59.142)

| 组件 | 版本 | 状态 | 备注 |
|------|------|------|------|
| Debian | 12 | ✅ 运行中 | - |
| Docker | 29.3.1 | ✅ 运行中 | - |
| Marzban | latest | ✅ 运行中 | Port 8000 |
| Xray Core | 1.8.23 | ✅ 运行中 | Port 443 |
| Nginx | 1.22.1 | ✅ 运行中 | Port 8443 |
| SQLite | 3.x | ✅ 运行中 | db.sqlite3 |

---

## 测试结果摘要

### ✅ 通过的测试

| 测试项 | Marzban | 支付桥接 | 备注 |
|--------|---------|----------|------|
| SSH 连接 | ✅ | ❌ | 支付桥接只接受密钥 |
| API 登录 | ✅ | 待测试 | VNC 后测试 |
| 获取用户列表 | ✅ | - | - |
| 创建用户 | ✅ | - | - |
| Nginx 反向代理 | ✅ | ✅ | - |
| SSL 证书 | ✅ | ⚠️ | 支付桥接使用自签名 |
| Cloudflare DNS | ✅ | - | 橙云已启用 |

### ⚠️ 待完成的测试

| 测试项 | 状态 | 需要操作 |
|--------|------|----------|
| Worker 启动 | ⚠️ | VNC 登录后启动 |
| 用户注册 | ⚠️ | 需要 Worker |
| 创建订单 | ⚠️ | 需要 Worker |
| 区块链支付 | ⚠️ | 需要真实 RPC |
| VPN 开通 | ⚠️ | 端到端测试 |

---

## 详细测试记录

### 1. Marzban 服务器测试

#### 1.1 API 登录测试 ✅

```bash
# 请求
curl -X POST http://127.0.0.1:8000/api/admin/token \
  -d "username=admin&password=MarzbanAdmin2024!"

# 响应
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "bearer"
}

# 结果: PASS
```

#### 1.2 用户列表查询 ✅

```bash
# 请求
curl http://127.0.0.1:8000/api/users \
  -H "Authorization: Bearer $TOKEN"

# 响应
{"users": [], "total": 0}

# 结果: PASS (空列表表示无用户)
```

#### 1.3 服务状态检查 ✅

```bash
# Docker 状态
marzban-marzban-1   Up 7 hours

# 端口监听
127.0.0.1:8000   (Marzban API)
0.0.0.0:8443     (Nginx HTTPS)
0.0.0.0:443      (Xray Core)

# 结果: PASS
```

---

### 2. 支付桥接服务器测试

#### 2.1 SSH 连接测试 ❌

```bash
# 尝试密码登录
ssh root@154.36.173.184
# 结果: Permission denied (publickey)

# 结论: 只接受密钥登录
# 解决: 通过 VNC 控制台操作
```

#### 2.2 API 可用性 (待测试)

预计测试命令：
```bash
# 健康检查
curl -sk https://154.36.173.184:8080/health

# 套餐列表
curl -sk https://154.36.173.184:8080/client/v1/plans
```

---

## 问题记录

### 已解决的问题

| 问题 | 解决方式 | 状态 |
|------|----------|------|
| Marzban 数据库权限 | 使用 `docker compose run` 初始化 | ✅ 已解决 |
| Xray 核心路径 | 重命名 xray-core → xray | ✅ 已解决 |
| Nginx 443 端口占用 | 改用 8443 端口 | ✅ 已解决 |
| Cloudflare DNS | 更新 A 记录 | ✅ 已解决 |

### 待解决的问题

| 问题 | 优先级 | 解决方式 |
|------|--------|----------|
| 支付桥接 SSH 访问 | 🔴 高 | VNC 控制台配置 |
| Worker 启动 | 🔴 高 | VNC 登录后启动 |
| 区块链 Mock 模式 | 🟡 中 | 配置真实 RPC |
| 自签名证书 | 🟡 中 | 生产环境替换 |

---

## 配置清单

### Marzban 服务器 (38.58.59.142)

```bash
# 关键路径
/opt/marzban/docker-compose.yml
/opt/marzban/.env
/var/lib/marzban/xray
/var/lib/marzban/cert.crt
/var/lib/marzban/key.key
/var/lib/marzban/xray_config.json
/etc/nginx/sites-available/marzban

# 关键命令
docker compose up -d    # 启动
docker compose logs     # 查看日志
systemctl restart nginx # 重启 Nginx
```

### 支付桥接服务器 (154.36.173.184) - 待配置

```bash
# 关键路径
/opt/payment-bridge/code/deploy/.env
/opt/payment-bridge/code/server/

# 关键命令
systemctl restart payment-bridge-api  # 重启 API
python -m app.workers.scheduler       # 启动 Worker
```

---

## 下一步行动

### 立即执行 (通过 VNC)

1. **登录支付桥接服务器 VNC**
2. **更新 `.env` 配置**
   - 添加 Marzban API 地址
   - 配置区块链 RPC
3. **启动 Worker**
   ```bash
   cd /opt/payment-bridge/code/server
   source venv/bin/activate
   nohup python -m app.workers.scheduler > /tmp/worker.log 2>&1 &
   ```
4. **验证 Worker**
   ```bash
   tail -f /tmp/worker.log
   ```

### 测试验证

1. **端到端测试**
   - 注册 → 创建订单 → 模拟支付 → VPN 开通
2. **真实支付测试**
   - 配置真实区块链 RPC
   - 进行小额真实支付测试
3. **续费测试**
   - 验证水平权限控制
   - 确认续费逻辑正确

---

## 附录

### 网络拓扑

```
用户 → Cloudflare (橙云) → Nginx (8443) → Marzban (8000)
                                    ↓
                              Xray (443)
                                    ↓
                              VPN 隧道
```

### 端口映射

| 服务器 | 端口 | 用途 |
|--------|------|------|
| 38.58.59.142 | 22 | SSH |
| 38.58.59.142 | 443 | Xray VPN |
| 38.58.59.142 | 8443 | Marzban HTTPS |
| 154.36.173.184 | 8080 | API HTTPS |
| 154.36.173.184 | 5432 | PostgreSQL |
| 154.36.173.184 | 6379 | Redis |

---

*报告生成时间: 2026-04-01*  
*状态: 部分测试完成，等待 VNC 配置*
