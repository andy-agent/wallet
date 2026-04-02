# Backend Migrations

当前迁移基线直接来源于 `final_engineering_delivery_package` 的冻结 SQL。

## 目录

- `baseline/0001_init.up.sql`
  首版 schema 基线
- `baseline/0001_init.down.sql`
  首版回滚脚本，仅用于本地或 staging 校验
- `seeds/0001_bootstrap_seed.sql`
  首启最小 seed 模板

## 执行顺序

1. 执行 `baseline/0001_init.up.sql`
2. 按环境替换占位值后执行 `seeds/0001_bootstrap_seed.sql`
3. 启动应用并执行健康检查

## 版本策略

- 当前采用文件序号作为迁移版本源
- `0001` 代表冻结包的首版 schema
- 后续迁移必须递增编号，并遵守 expand / backfill / contract 策略

## 回滚策略

- 本地 / staging:
  可使用 `0001_init.down.sql` 回滚首版 schema
- 生产:
  不直接对资金域做物理回滚
  通过补偿脚本与新迁移处理
