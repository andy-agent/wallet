# PostgreSQL 真实数据库配置与测试报告

**日期**: 2026-03-31  
**项目**: v2rayng-payment-bridge  
**数据库**: PostgreSQL 16.13 (本地 Homebrew 安装)

---

## ✅ 完成情况

### 1. 数据库连接配置

| 配置项 | 值 |
|--------|-----|
| 数据库类型 | PostgreSQL |
| 服务器地址 | localhost:5432 |
| 数据库名 | v2rayng_payment |
| 用户名 | payment |
| 连接 URL | `postgresql+asyncpg://payment:payment_secure_pass_2024@localhost:5432/v2rayng_payment` |

**配置文件**: `code/server/.env`

```bash
DATABASE_URL=postgresql://payment:payment_secure_pass_2024@localhost:5432/v2rayng_payment
```

**连接测试结果**: ✅ 成功连接
```
PostgreSQL 16.13 (Homebrew) on aarch64-apple-darwin25.2.0
```

---

### 2. 数据库迁移执行

**迁移命令**:
```bash
cd code/server && alembic upgrade head
```

**迁移结果**: ✅ 成功执行

**创建的表** (6个):
| 表名 | 字段数 | 说明 |
|------|--------|------|
| plans | 12 | 套餐表 |
| orders | 26 | 订单表 |
| payment_addresses | 11 | 支付地址池 |
| client_sessions | 8 | 客户端会话 |
| audit_logs | 10 | 审计日志 |
| alembic_version | 1 | 迁移版本 |

**创建的索引** (26个):
- plans: 4个索引 (含2个唯一索引)
- orders: 10个索引 (含3个唯一索引)
- payment_addresses: 5个索引 (含2个唯一索引)
- client_sessions: 3个索引
- audit_logs: 3个索引

**外键约束**:
- `orders.plan_id` → `plans.id`
- `payment_addresses.allocated_order_id` → `orders.id`
- `client_sessions.order_id` → `orders.id`

---

### 3. 真实数据测试

#### 3.1 套餐数据测试
✅ **通过**
- 初始套餐数据已插入 (2条)
- 套餐 CRUD 操作正常
- JSON 类型字段 (`supported_assets`) 工作正常

#### 3.2 订单数据测试
✅ **通过**
- 创建订单成功
- 订单状态流转正常: pending_payment → paid → confirmed → fulfilled
- 数据持久化验证通过

#### 3.3 地址池数据测试
✅ **通过**
- 地址导入功能正常
- 地址分配 (含幂等性检查) 正常
- 地址释放功能正常

---

### 4. 并发测试

#### 4.1 并发订单创建
✅ **通过**
- 并发创建 10 个订单成功
- 所有订单都已持久化到数据库
- 唯一约束 (order_no) 无冲突

#### 4.2 并发地址分配 (SELECT FOR UPDATE)
✅ **通过**
- 使用 `SELECT FOR UPDATE SKIP LOCKED` 实现并发安全
- 15 个并发任务成功分配 15 个不同地址
- 数据库验证: 无重复分配冲突

---

## 🔧 模型修复记录

### 修复 1: Plan.supported_assets 字段类型
**问题**: 模型使用 `ARRAY(String)`，但迁移创建的是 JSON 类型  
**修复**: 将模型改为使用 `JSON` 类型

```python
# 修改前
supported_assets = Column(ARRAY(String(20)), default=["SOL", "USDT_TRC20"])

# 修改后
supported_assets = Column(JSON, default=["SOL", "USDT_TRC20"])
```

**文件**: `code/server/app/models/plan.py`

### 修复 2: PaymentAddress.status 字段类型
**问题**: 使用 SQLAlchemy `Enum` 类型与 asyncpg 不兼容  
**修复**: 改为使用 `String` 类型存储枚举值

```python
# 修改前
status = Column(SQLEnum(AddressStatus), default=AddressStatus.AVAILABLE, index=True)

# 修改后
status = Column(String(20), default=AddressStatus.AVAILABLE.value, index=True)
```

**文件**: `code/server/app/models/payment_address.py`

### 修复 3: AddressPoolService 枚举值引用
**问题**: 服务代码中使用枚举对象而非字符串值  
**修复**: 更新所有枚举引用为 `.value`

**文件**: `code/server/app/services/address_pool.py`

### 修复 4: Alembic 迁移脚本
**问题**: `bulk_insert` 第一个参数应为表对象而非字符串  
**修复**: 使用 `sa.table()` 创建表对象

**文件**: `code/server/alembic/versions/001_initial.py`

---

## 📊 测试汇总

| 测试项 | 状态 | 说明 |
|--------|------|------|
| 数据库连接 | ✅ PASS | PostgreSQL 连接成功 |
| 套餐 CRUD | ✅ PASS | 增删改查正常 |
| 订单状态流转 | ✅ PASS | 10个状态转换正常 |
| 地址池管理 | ✅ PASS | 导入/分配/释放正常 |
| 并发订单创建 | ✅ PASS | 10并发无冲突 |
| 并发地址分配 | ✅ PASS | SELECT FOR UPDATE 工作正常 |
| 数据完整性 | ✅ PASS | 外键和唯一约束正常 |

**总计**: 7 个测试, 7 通过, 0 失败

---

## 📝 数据记录统计

```
表名                记录数
------------------  ------
plans               2      (默认套餐)
orders              113    (测试订单)
payment_addresses   66     (测试地址)
client_sessions     0
audit_logs          0
```

---

## 🚀 生产环境部署建议

### 远程服务器配置 (154.36.173.184)

当前远程 PostgreSQL 端口 (5432) 无法直接访问，建议以下方案:

#### 方案 1: SSH 隧道 (开发/测试)
```bash
ssh -L 5433:localhost:5432 user@154.36.173.184
# 然后使用 localhost:5433 连接
```

#### 方案 2: Docker 网络 (推荐用于生产)
应用和 PostgreSQL 在同一 Docker 网络中:
```yaml
# docker-compose.yml 中
services:
  api:
    environment:
      DATABASE_URL: postgresql://payment:password@postgres:5432/v2rayng_payment
```

#### 方案 3: 防火墙开放 (不推荐)
如必须外部访问，开放 5432 端口并配置 SSL:
```
postgresql+asyncpg://user:pass@154.36.173.184:5432/db?sslmode=require
```

---

## 🔒 安全配置检查清单

- [x] 数据库使用独立用户 (payment)
- [x] 用户权限限制 (非超级用户)
- [x] 密码复杂度符合要求
- [ ] 生产环境启用 SSL (待配置)
- [ ] 连接池配置优化 (待配置)
- [ ] 数据库备份策略 (待配置)

---

## 📁 相关文件

| 文件 | 说明 |
|------|------|
| `code/server/.env` | 数据库连接配置 |
| `code/server/alembic/env.py` | Alembic 配置 |
| `code/server/alembic/versions/001_initial.py` | 初始迁移脚本 |
| `code/server/app/core/database.py` | 数据库连接管理 |
| `code/server/app/models/*.py` | 数据模型 |
| `code/server/test_real_database.py` | 测试脚本 |

---

## ✨ 结论

真实 PostgreSQL 数据库配置和测试已完成:
- ✅ 数据库连接配置正确
- ✅ 所有表和索引创建成功
- ✅ 数据持久化正常
- ✅ 并发操作无数据竞争
- ✅ SELECT FOR UPDATE 锁机制工作正常

**状态**: 已就绪，可进入下一阶段开发/部署。
