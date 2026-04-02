# 现状审计基线文档

- 状态: 当前唯一审计基线
- 生成日期: 2026-04-02
- 适用阶段: 新 PRD 校验、`bd` 任务树重建、后续验收对照
- 审计方式: repository-first，基于源码、迁移、部署文件、本地构建/测试结果整理

## 1. 使用规则

1. 从本文件生成后，后续 PRD 校验和 `bd` 任务树重建，统一以本文件为唯一基线。
2. 旧状态文档仅可作为旁证，不再作为任务拆解输入。
3. 当旧文档与本文件冲突时，按以下优先级判断:
   `源码与本地验证结果 > 本文件 > 历史总结文档`
4. 本文件刻意区分三件事:
   - 代码存在
   - 代码已连通
   - 代码已本地验证通过
5. 后续新 PRD 到达后，必须逐条映射到本文件中的能力矩阵，再决定 `bd` 任务，而不是直接从旧 handoff 或旧总结文档派活。

## 2. 非基线资料说明

以下文件保留，但不再视为真相源:

- `docs/current-status.md`
- `docs/IMPLEMENTATION_SUMMARY.md`
- `docs/LOGIC_GAPS_ANALYSIS.md`
- `docs/CODE_AUDIT.md`
- `code/PROJECT_SUMMARY.md`
- `handoff/*`

原因:

- 它们对同一能力给出了互相冲突的结论。
- 部分文档滞后于当前源码。
- 部分文档描述的是上一轮设计，不再对应当前接口和部署状态。

## 3. 原始需求一期基线

以下内容取自 `需求分析/项目需求审查与开发实施文档.md`，作为后续 PRD 校验的历史需求基线。

### 3.1 一期必须实现

- 客户端套餐列表
- 创建订单
- SOL 支付
- USDT-TRC20 支付
- 订单轮询
- 支付成功后新购/续费
- 拉取订阅
- 本地保存订阅信息
- 管理后台基础订单查看
- 异常订单人工处理

### 3.2 一期明确排除

- USDT-ERC20
- WebSocket/SSE 实时推送
- 自动资金归集
- 节点测试
- Webhook 通知
- Telegram 通知
- 后台页面作为首版核心交付

### 3.3 一期关键业务规则

- 客户端只能访问 `/client/v1/*`
- 管理接口与客户端接口必须隔离
- 客户端不持有私钥，不参与验链定夺
- 订单状态机必须完整且幂等
- 所有开通/续费动作必须服务端主导
- MVP 优先轮询，不优先 WebSocket
- 一期优先 `SOL + USDT-TRC20 + 新购/续费`

## 4. 仓库现状总览

### 4.1 真实主模块

- `code/server`: FastAPI 服务端、调度器、链上扫描、履约、管理接口
- `code/Android/V2rayNG`: Android 客户端改造版本
- `code/admin-web`: React/Vite 管理前端
- `code/deploy`: Docker 与部署文档

### 4.2 当前仓库的整体判断

- 这不是一个“从零开始”的项目，而是多轮实现叠加后的仓库。
- 当前源码范围已经超过原始一期边界。
- 当前仓库存在明显的接口漂移、文档漂移、测试漂移和部署漂移。
- 当前最需要的不是继续补写总结文档，而是先用统一基线收敛“真实已做了什么”和“真实可用到什么程度”。

## 5. 能力矩阵

判定口径:

- `已实现且可见`: 源码明确存在主要实现
- `已实现但契约漂移`: 两端代码都在，但接口或数据结构不一致
- `已实现但未验证`: 代码存在，但本地未形成可通过的构建/测试证据
- `超一期范围已实现`: 已进入代码，但不属于原始一期边界
- `未稳定`: 有实现，但当前验证结果显示不可直接作为完成项

| 领域 | 能力 | 原始一期定位 | 代码现状 | 本地验证 | 当前判定 |
|------|------|--------------|----------|----------|----------|
| 服务端 | `/client/v1/plans` 套餐列表 | 必须实现 | 已实现 | 有测试但不全绿 | 已实现且可见 |
| 服务端 | `/client/v1/orders` 创建订单 | 必须实现 | 已实现 | 相关测试不全绿 | 已实现但契约漂移 |
| 服务端 | `/client/v1/orders/{id}` 查询订单 | 必须实现 | 已实现 | 相关测试不全绿 | 已实现但契约漂移 |
| 服务端 | `/client/v1/subscription` 拉取订阅 | 必须实现 | 已实现 | 未形成联调通过证据 | 已实现但契约漂移 |
| 服务端 | 用户注册/登录/刷新令牌 | 原文建议支持 | 已实现 | 源码存在 | 已实现且可见 |
| 服务端 | SOL 扫描与确认 | 必须实现 | 已实现 | 有测试，但整套不全绿 | 已实现但未验证 |
| 服务端 | USDT-TRC20 扫描与确认 | 必须实现 | 已实现 | 有测试，但整套不全绿 | 已实现但未验证 |
| 服务端 | Marzban 新购/续费履约 | 必须实现 | 已实现 | 存在双实现与告警 | 已实现但未稳定 |
| 服务端 | 管理端订单查询/处理 API | 一期建议具备基础能力 | 已实现 | 部分测试失败 | 已实现但未稳定 |
| Android | 登录/注册页 | 非首要，但当前方案已引入 | 已实现 | 本机无法复编 | 已实现但未验证 |
| Android | 套餐页 | 必须实现 | 已实现 | 本机无法复编 | 已实现但未验证 |
| Android | 支付页与轮询 | 必须实现 | 已实现 | 本机无法复编 | 已实现但契约漂移 |
| Android | 订阅导入 | 必须实现 | 已实现 | 本机无法复编 | 已实现但契约漂移 |
| admin-web | React 管理后台 | 原始一期非核心 | 已实现 | 构建失败 | 超一期范围已实现 |
| 服务端 | WebSocket 推送 | 原始一期排除 | 已实现 | 未验证 | 超一期范围已实现 |
| 服务端 | SPL Token 支付 | 原始一期排除 | 已实现 | 未验证 | 超一期范围已实现 |
| 服务端 | USDT-ERC20 支付 | 原始一期排除 | 已实现 | 未验证 | 超一期范围已实现 |
| 服务端 | 资金归集 sweeper | 原始一期排除 | 已实现 | 未验证 | 超一期范围已实现 |
| 部署 | Docker Compose/环境配置 | 必须具备基础部署能力 | 已实现 | 文档与实际漂移 | 已实现但未稳定 |
| 文档 | 项目状态总结 | 应服务开发 | 多份冲突 | 不可直接用 | 未稳定 |

## 6. 关键证据与结论

### 6.1 服务端真实范围已超过一期

证据:

- `code/server/app/core/config.py` 已纳入 `SPL_TOKEN`、`ETH_RPC_URL`、`USDT_CONTRACT_ADDRESS`、`sweeper_*`
- `code/server/app/api/client/orders.py` 接受 `SPL_TOKEN`、`USDT_ERC20`
- `code/server/app/main.py` 注册了 `ws_router`
- `code/server/app/workers/sweeper.py` 存在真实归集模块

结论:

- 当前源码已不再是“严格的一期 MVP 范围”。
- 新 PRD 若继续坚持一期边界，后续任务树必须包含“保留但不作为当前目标”或“裁剪/冻结超范围模块”的任务。

### 6.2 Android 与服务端订单接口存在硬性契约漂移

证据:

- 服务端 `create_order` 要求登录用户依赖和 `X-Client-Version` 头，见 `code/server/app/api/client/orders.py`
- Android `PaymentRepository.createOrder()` 仅提交请求体，见 `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/repository/PaymentRepository.kt`
- Android `Order` 模型要求 `status_text / plan / payment / fulfillment` 嵌套结构，见 `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/model/Order.kt`
- 服务端当前返回的是扁平结构，见 `code/server/app/api/client/orders.py`

结论:

- 当前“登录存在”和“购买链路可用”不是一回事。
- 订单创建、订单查询、支付完成后的返回结构，当前不能直接视为联调通过。

### 6.3 Android 与服务端订阅接口存在结构漂移

证据:

- Android 订阅响应模型期望 `user / subscription / servers`
- 服务端 `/client/v1/subscription` 当前返回扁平字段 `subscription_url / expires_at / traffic_* / nodes`

结论:

- 订阅拉取和订阅导入逻辑当前不能按“已闭环”计算。

### 6.4 admin-web 与当前后端不是同一套接口契约

证据:

- admin-web 调用 `/admin/v1/orders/{id}/manual-fulfill`、`/ignore`、`/dashboard/stats`
- 后端当前暴露 `/manual-confirm`、`/mark-ignore`
- admin-web `types/index.ts` 中的 `Order`、`Plan`、分页结构、状态枚举，均与后端当前返回模型不一致

结论:

- admin-web 应视为另一轮实现残留，不应直接纳入“当前已联通后台”的结论。

### 6.5 服务端内部存在双履约实现，属于结构性风险

证据:

- `code/server/app/workers/fulfillment.py`
- `code/server/app/services/fulfillment.py`

结论:

- 两套履约逻辑同时存在，职责与返回结果不同。
- 后续 PRD 若把“新购/续费/凭证下发”列为核心能力，必须优先明确唯一履约主链，再拆任务。

### 6.6 文档已失去单一真相源

证据:

- `code/PROJECT_SUMMARY.md` 声称“全部完成”
- `docs/current-status.md` 标注多个 P0/P1 问题
- `docs/IMPLEMENTATION_SUMMARY.md` 又描述另一版状态

结论:

- 在新 PRD 到达前，不应再从这些文件反向生成任务。

### 6.7 部署资产存在版本漂移

证据:

- `code/deploy/docker-compose.yml` 与 `code/deploy/DEPLOYMENT.md` 对服务组成和端口说明不一致
- 部署文档仍描述 wallet service 和另一套端口/容器布局

结论:

- 部署链路目前只能算“存在部署资产”，不能算“部署方案已统一并验证”。

### 6.8 已发现明确代码错误

证据:

- `code/server/app/api/client/plans.py` 的 `get_plan()` 使用未定义的 `BASE_PRICE_USD`

结论:

- 即使是表面最稳定的客户端公开套餐接口，当前也至少存在已知运行时错误点。

## 7. 本地验证结果

### 7.1 服务端测试

执行命令:

```bash
cd code/server
./.venv/bin/pytest -q
PYTHONPATH=. ./.venv/bin/pytest -q
PYTHONPATH=. ./.venv/bin/pytest -q tests --ignore=tests/test_real_data_manual.py --ignore=tests/test_real_data_regression.py
```

结果:

- 默认执行先因测试入口与导入路径问题失败
- 加 `PYTHONPATH=.` 后，完整收集仍会撞到过时脚本和失配测试
- 排除明显坏入口后，结果为:
  - `289 passed`
  - `41 failed`
  - `3 skipped`

失败集中区域:

- `tests/test_client_api.py`
- `tests/test_admin_api.py`
- `tests/test_scheduler.py`

审计结论:

- 服务端不能按“测试全绿”认定为已完成
- 当前更接近“主体代码存在，但接口和测试基线已经漂移”

### 7.2 Android 构建

执行命令:

```bash
cd code/Android/V2rayNG
./gradlew :app:compileFdroidDebugKotlin
```

结果:

- 本机缺少 Java Runtime，无法完成编译验证

审计结论:

- Android 当前只能判定为“源码存在，当前机器未复验”
- 不能把它计为“已构建通过”

### 7.3 admin-web 构建

执行命令:

```bash
cd code/admin-web
npm run build
```

结果:

- TypeScript 构建失败
- 缺少 `vite/client` 和 `@types/node` 类型依赖

审计结论:

- admin-web 在当前机器上不具备直接交付条件
- 即便补齐依赖，仍需先解决与后端的接口漂移

## 8. 后续 PRD 校验规则

收到新 PRD 后，统一按以下四类打标:

1. `已完成`
   代码与 PRD 一致，且有足够本地验证证据
2. `部分完成`
   代码存在，但契约未打通、验证不足或链路不闭环
3. `未完成`
   当前仓库中不存在对应实现，或仅有文档没有可用代码
4. `超范围已实现`
   代码已存在，但新 PRD 若不纳入当前范围，应作为冻结/裁剪/延后项处理

## 9. 后续 `bd` 任务树重建规则

新 PRD 校验完成后，再基于本文件重建 `bd`，建议遵循以下顺序:

1. 先建 `analysis` 类任务
   - PRD 条目逐项映射
   - 契约冲突核对
   - 超范围模块处置决策
2. 再建 `implementation` 类任务
   - 只为 `部分完成` 和 `未完成` 项创建实施任务
3. 单独建 `verification` 类任务
   - 服务端接口测试
   - Android 编译与真机链路
   - admin-web 构建与联调
4. 如新 PRD 明确排除超范围能力
   - 为 `ERC20 / WebSocket / sweeper / admin-web` 建冻结、隔离或裁剪任务
5. 不允许再从历史 handoff 直接抄任务标题

## 10. 当前基线结论

一句话结论:

- 当前仓库已经有大量实现，但它不是“完成态”，而是“多轮实现叠加后出现明显漂移的半稳定态”。

作为后续工作基线，应坚持以下判断:

- 不把“源码存在”误判为“功能完成”
- 不把“旧文档声称完成”误判为“当前真实可用”
- 不把“超范围实现”误判为“当前版本必须交付”

本文件自生成起，作为后续 PRD 校验和 `bd` 重建的唯一基线，直到被新的同格式审计文档替换。
