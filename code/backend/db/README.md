# Database Baseline

本目录用于记录后端数据库落地约束。

## 当前状态

- schema 基线来自 [0001_init.up.sql](/Users/cnyirui/git/projects/liaojiang/code/backend/migrations/baseline/0001_init.up.sql)
- seed 基线来自 [0001_bootstrap_seed.sql](/Users/cnyirui/git/projects/liaojiang/code/backend/migrations/seeds/0001_bootstrap_seed.sql)

## 首次部署顺序

1. 执行 schema 基线
2. 执行 bootstrap seed
3. 启动后端
4. 跑健康检查与最小冒烟

## 后续约束

- 以 `final_engineering_delivery_package` 为事实源
- 迁移编号只能递增
- 资金域问题优先补偿，不直接依赖 downgrade
