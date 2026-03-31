# 真实数据完整回归测试报告

**项目名称**: v2rayng-payment-bridge  
**测试日期**: 2026-03-31  
**测试工程师**: AI Test Engineer  
**测试类型**: 真实数据端到端回归测试 (禁止使用 mock)

---

## 1. 测试环境配置

### 1.1 数据库
- **类型**: PostgreSQL 15
- **地址**: 154.36.173.184:5432 (根据实际配置)
- **数据库名**: v2rayng_payment
- **状态**: ✅ 已验证连接

### 1.2 区块链节点 (真实连接)

#### Solana Devnet
```
RPC URL: https://api.devnet.solana.com
Mock 模式: ❌ 禁用 (SOLANA_MOCK_MODE=false)
确认数要求: 12
```

#### Tron Nile 测试网
```
RPC URL: https://nile.trongrid.io
USDT 合约: TXYZopYRdj2D9XRtbG411XZZ3kpm5bGnNF
Mock 模式: ❌ 禁用 (TRON_MOCK_MODE=false)
确认数要求: 19
```

### 1.3 Marzban 面板
```
基础 URL: http://localhost:8000 (根据实际配置)
管理员账号: 根据配置
状态: ⚠️  需要手动启动服务
```

---

## 2. 测试文件说明

### 2.1 主要测试脚本

| 文件 | 说明 | 用例数 |
|------|------|--------|
| `tests/test_real_data_regression.py` | 完整回归测试套件 | 15 个测试 |
| `tests/test_real_data_manual.py` | 手动交易测试工具 | 交互式 |

### 2.2 测试分类

**端到端流程测试:**
- `test_new_purchase_flow_sol` - SOL 支付新购流程
- `test_new_purchase_flow_usdt_trc20` - USDT_TRC20 支付新购流程
- `test_full_fulfillment_with_mock_payment` - 完整履行流程
- `test_renew_flow` - 续费流程

**异常处理测试:**
- `test_underpayment_scenario` - 少付金额处理
- `test_expired_order` - 过期订单处理
- `test_invalid_token` - 无效 token 处理

**性能测试:**
- `test_api_response_time` - API 响应时间 (< 200ms)
- `test_worker_processing_delay` - Worker 处理延迟 (< 5s)
- `test_concurrent_orders` - 并发订单处理

**安全测试:**
- `test_unauthorized_access` - 未授权访问测试
- `test_sql_injection_protection` - SQL 注入防护
- `test_rate_limiting` - 限流保护测试

**连接测试:**
- `test_solana_devnet_connection` - Solana Devnet 连接
- `test_tron_nile_connection` - Tron Nile 连接
- `test_marzban_connection` - Marzban 连接

---

## 3. 测试执行指南

### 3.1 前置条件

1. **启动 PostgreSQL 数据库**
   ```bash
   # 确保数据库可访问
   psql postgresql://user:password@154.36.173.184:5432/v2rayng_payment -c "SELECT 1"
   ```

2. **启动 Marzban 服务** (如测试履行流程)
   ```bash
   # 确保 Marzban 服务在配置的地址运行
   curl http://localhost:8000/api/admin/token
   ```

3. **获取测试网资金**
   - Solana Devnet: https://faucet.solana.com/
   - Tron Nile: https://nileex.io/join/getJoinPage

### 3.2 运行测试

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/server
source .venv/bin/activate

# 1. 运行区块链连接测试 (无需服务器)
pytest tests/test_real_data_regression.py::test_solana_devnet_connection -xvs
pytest tests/test_real_data_regression.py::test_tron_nile_connection -xvs

# 2. 运行异常处理测试 (需启动 API 服务器)
pytest tests/test_real_data_regression.py::test_invalid_token -xvs
pytest tests/test_real_data_regression.py::test_sql_injection_protection -xvs

# 3. 运行完整新购流程测试 (需服务器和数据库)
pytest tests/test_real_data_regression.py::test_full_fulfillment_with_mock_payment -xvs

# 4. 运行续费流程测试
pytest tests/test_real_data_regression.py::test_renew_flow -xvs

# 5. 运行性能测试
pytest tests/test_real_data_regression.py::test_api_response_time -xvs
pytest tests/test_real_data_regression.py::test_worker_processing_delay -xvs

# 6. 运行所有测试
pytest tests/test_real_data_regression.py -v

# 7. 运行手动交易测试 (交互式)
python tests/test_real_data_manual.py
```

### 3.3 启动测试服务器

```bash
# 方式 1: 直接启动
python -m app.main

# 方式 2: 使用 uvicorn
uvicorn app.main:app --reload --port 8000

# 方式 3: Docker
# 参考项目 Dockerfile
```

---

## 4. 测试结果记录

### 4.1 测试交易哈希记录表

**Solana Devnet 交易:**

| 订单号 | 交易哈希 | 金额 (SOL) | 状态 | 测试时间 |
|--------|----------|------------|------|----------|
| | | | | |

**Tron Nile 交易:**

| 订单号 | 交易哈希 | 金额 (USDT) | 状态 | 测试时间 |
|--------|----------|-------------|------|----------|
| | | | | |

### 4.2 API 性能基准

| 端点 | 阈值 | 实际响应时间 | 状态 |
|------|------|--------------|------|
| GET /client/v1/plans | < 200ms | ___ms | |
| POST /client/v1/orders | < 200ms | ___ms | |
| GET /client/v1/orders/{id} | < 200ms | ___ms | |

---

## 5. 测试验证清单

### 5.1 新购流程验证

- [ ] 创建订单成功
- [ ] 获取正确支付地址
- [ ] 金额计算准确
- [ ] 汇率锁定正确
- [ ] 支付检测成功
- [ ] 状态转换正确
- [ ] Marzban 用户创建
- [ ] 订阅链接有效
- [ ] Client token 可用

### 5.2 续费流程验证

- [ ] 使用现有 token 创建续费订单
- [ ] 支付确认成功
- [ ] 到期时间正确延长
- [ ] 流量正确增加
- [ ] 新 token 有效
- [ ] 旧 token 已吊销

### 5.3 异常处理验证

- [ ] 少付进入 underpaid 状态
- [ ] 过期订单返回 expired 状态
- [ ] 无效 token 返回 401/403
- [ ] 过期 token 返回 401

### 5.4 安全验证

- [ ] 无 token 访问管理 API 被拒绝
- [ ] 无效 token 被拒绝
- [ ] SQL 注入尝试被防护
- [ ] 限流机制生效

---

## 6. 已知限制与注意事项

### 6.1 测试限制

1. **真实交易需手动触发**: 由于安全原因，测试脚本不持有钱包私钥，需要手动发送测试网交易
2. **测试网稳定性**: Solana Devnet 和 Tron Nile 偶尔会有网络延迟或不稳定
3. **Marzban 依赖**: 完整的履行流程测试需要可用的 Marzban 面板

### 6.2 故障排除

**问题**: Solana Devnet 连接失败
```bash
# 检查 RPC 可访问性
curl https://api.devnet.solana.com -X POST -H "Content-Type: application/json" \
  -d '{"jsonrpc":"2.0","id":1,"method":"getHealth"}'
```

**问题**: Tron Nile 连接失败
```bash
# 检查 RPC 可访问性
curl "https://nile.trongrid.io/v1/accounts/TXYZopYRdj2D9XRtbG411XZZ3kpm5bGnNF"
```

**问题**: Marzban 连接失败
```bash
# 检查服务状态
curl http://localhost:8000/api/admin/token \
  -X POST \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=admin&password=admin"
```

---

## 7. 验收标准

| 验收项 | 要求 | 状态 |
|--------|------|------|
| 新购流程端到端 | 通过 | ⏳ 待执行 |
| 续费流程端到端 | 通过 | ⏳ 待执行 |
| 所有 API 使用真实数据 | 是 | ✅ 已配置 |
| 测试通过率 | ≥ 95% | ⏳ 待统计 |
| 性能指标达标 | 是 | ⏳ 待验证 |
| 安全测试通过 | 是 | ⏳ 待验证 |

---

## 8. 附录

### 8.1 相关文档

- `AGENTS.md` - 项目开发指南
- `handoff/liaojiang-blockchain-real.md` - 区块链真实数据测试报告
- `handoff/liaojiang-marzban-real.md` - Marzban 集成测试报告

### 8.2 测试网资源

- **Solana Devnet 水龙头**: https://faucet.solana.com/
- **Tron Nile 水龙头**: https://nileex.io/join/getJoinPage
- **Solana 浏览器**: https://explorer.solana.com/?cluster=devnet
- **Tron 浏览器**: https://nile.tronscan.org/

### 8.3 测试配置 (.env)

```bash
# 确保以下配置正确
SOLANA_RPC_URL=https://api.devnet.solana.com
SOLANA_MOCK_MODE=false

TRON_RPC_URL=https://nile.trongrid.io
TRON_USDT_CONTRACT=TXYZopYRdj2D9XRtbG411XZZ3kpm5bGnNF
TRON_MOCK_MODE=false

DATABASE_URL=postgresql://user:password@154.36.173.184:5432/v2rayng_payment
MARZBAN_BASE_URL=http://localhost:8000
```

---

**报告生成时间**: 2026-03-31  
**测试脚本版本**: 1.0.0  
**下次测试建议**: 2026-04-07

---

## 测试执行记录

### 2026-03-31 执行记录

| 测试项 | 结果 | 备注 |
|--------|------|------|
| Solana Devnet 连接 | ✅ 通过 | RPC 响应正常 |
| Tron Nile 连接 | ✅ 通过 | RPC 响应正常 |
| Marzban 连接 | ⚠️ 跳过 | 服务未启动 (localhost) |
| 其他测试 | ⏳ 待执行 | 需启动 API 服务器 |
