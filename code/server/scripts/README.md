# 运营工具 CLI 管理脚本

本目录包含用于运营管理的命令行工具脚本。

## 脚本列表

### 1. init_plans.py - 初始化套餐

从 JSON/CSV 文件导入套餐到数据库。

```bash
# 从 JSON 导入套餐
python scripts/init_plans.py --file examples/plans.json

# 从 CSV 导入套餐并更新已存在的套餐
python scripts/init_plans.py --file examples/plans.csv --update-existing

# 试运行模式
python scripts/init_plans.py --file examples/plans.json --dry-run
```

**参数:**
- `--file, -f`: 套餐数据文件路径 (JSON 或 CSV 格式)
- `--update-existing, -u`: 如果套餐已存在，更新现有套餐信息
- `--dry-run, -d`: 试运行模式，不实际写入数据库

### 2. import_addresses.py - 导入收款地址

从 JSON/CSV 导入地址和加密私钥到地址池。

```bash
# 导入 Solana 地址
python scripts/import_addresses.py --file examples/solana_addresses.json --chain solana

# 导入 Tron 地址 (CSV 格式)
python scripts/import_addresses.py --file examples/tron_addresses.csv --chain tron

# 试运行模式
python scripts/import_addresses.py --file examples/addresses.json --chain solana --dry-run
```

**参数:**
- `--file, -f`: 地址数据文件路径 (JSON 或 CSV 格式)
- `--chain, -c`: 区块链类型 (`solana` 或 `tron`)
- `--dry-run, -d`: 试运行模式，不实际写入数据库
- `--skip-validation`: 跳过地址格式验证

**地址格式验证:**
- Solana: Base58 编码, 32-44 字符
- Tron: Base58 编码, 以 'T' 开头, 34 字符

### 3. check_order.py - 查询订单状态

查询订单完整状态信息，用于调试和问题排查。

```bash
# 查询订单状态
python scripts/check_order.py --order-id 01HQ1234567890ABCDEF123456

# 使用订单号查询
python scripts/check_order.py --order-id ORD1234567890ABCD

# JSON 格式输出
python scripts/check_order.py --order-id <order_id> --json
```

**参数:**
- `--order-id, -o`: 订单ID 或订单号 (order_no)
- `--json, -j`: 以 JSON 格式输出

**显示信息:**
- 订单基本信息
- 支付信息
- 交易信息
- 套餐信息
- Fulfillment 结果
- 地址池信息
- 客户端会话

### 4. manual_fulfill.py - 手动触发履行

手动为订单触发 fulfill 流程，用于异常订单处理。

```bash
# 手动履行订单
python scripts/manual_fulfill.py --order-id 01HQ1234567890ABCDEF123456

# 强制履行 (跳过状态检查)
python scripts/manual_fulfill.py --order-id <order_id> --force

# 续费订单需要提供 token
python scripts/manual_fulfill.py --order-id <order_id> --token <access_token>

# 试运行模式
python scripts/manual_fulfill.py --order-id <order_id> --dry-run
```

**参数:**
- `--order-id, -o`: 订单ID
- `--force, -f`: 强制履行 (跳过状态检查，谨慎使用)
- `--token, -t`: 续费订单的 access token (renew 类型订单需要)
- `--dry-run, -d`: 试运行模式，检查但不执行

## 环境变量

所有脚本都需要以下环境变量:

```bash
# 数据库 (必需)
DATABASE_URL=postgresql://user:pass@localhost:5432/dbname

# Marzban API (manual_fulfill.py 需要)
MARZBAN_BASE_URL=http://localhost:8000
MARZBAN_ADMIN_USERNAME=admin
MARZBAN_ADMIN_PASSWORD=admin

# JWT (manual_fulfill.py 需要)
JWT_SECRET=your-jwt-secret-32chars-long!!
```

可以从 `.env` 文件加载环境变量。

## 示例数据文件

`examples/` 目录包含示例数据文件:

- `plans.json` / `plans.csv`: 套餐数据示例
- `solana_addresses.json`: Solana 地址示例
- `tron_addresses.csv`: Tron 地址示例

## 注意事项

1. 所有脚本都使用 `argparse` 处理命令行参数，使用 `--help` 查看详细说明
2. 脚本设计为可独立运行，不依赖 FastAPI 服务器
3. 数据库操作使用 SQLAlchemy，支持 PostgreSQL
4. 地址导入支持幂等性，重复的地址会被跳过
5. 订单履行支持幂等性，已履行的订单会返回现有结果
