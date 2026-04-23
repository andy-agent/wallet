# VPN 三档套餐真实控制面改造 Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 把基础 / 高级 / 商业三档 VPN 套餐做成真实可执行的商品与控制面体系，完成中文三层购买、真实 QoS、商业固定 IP、单用户预建立多线路能力，以及后台配置与回归测试。

**Architecture:** 保持 `code/backend` 作为商品、订单、订阅、权益真相源；引入 `line / qos / static-ip` 控制层和控制器执行状态；`Marzban` 继续负责订阅输出；Android 购买页从“直接选 `planCode`”升级为“套餐属性 + 服务时长 + 地域”三层中文选择；Admin Web 负责商品和控制面配置。

**Tech Stack:** NestJS backend, PostgreSQL migrations, Android Kotlin + Compose, React + Vite admin-web, Marzban control-plane integration, node-side Xray/tc execution.

---

## 开始前先做这些事

所有实现任务都基于以下设计文档：

- `/Users/cnyirui/git/projects/liaojiang/docs/plans/2026-04-23-vpn-tiered-qos-static-ip-design.md`
- `/Users/cnyirui/git/projects/liaojiang/docs/plans/2026-04-15-marzban-control-plane-decoupling.md`
- `/Users/cnyirui/git/projects/liaojiang/docs/plans/2026-04-22-subscription-entitlement-state-machine.md`

在开始任何代码修改前，先执行：

```bash
cd /Users/cnyirui/git/projects/liaojiang
bd show liaojiang-b9mt
git status
```

执行约束：

- 用户前端文案必须使用中文，不允许把 `lineCode` 直接暴露到 UI
- 商业套餐续费必须保留原固定 IP，不允许自动替换
- `/vpn/selection` 不能通过“切换一次建一次 Marzban 用户”实现
- 高级套餐“最低 5Mbps”必须依赖真实容量控制，不能只靠商品描述

## 并行拆分

推荐 5 路并行，写入 ownership，避免冲突：

1. **Backend Product & Entitlement Worker**
   - 负责 `code/backend` 的 schema、catalog、orders、vpn、provisioning
2. **Control-Plane Worker**
   - 负责 `code/backend` 中的控制器协议、执行状态、Marzban 接缝
3. **Android Purchase Worker**
   - 负责 `code/Android/V2rayNG` 的购买链路、订阅展示、切换规则
4. **Admin Web Worker**
   - 负责 `code/admin-web` 的套餐 / 地域 / 线路 / IP 池配置页面
5. **Verification Worker**
   - 负责回归用例、e2e、构建、真机检查脚本

主线程负责：

- 锁定接口契约
- 合并子代理结果
- 处理冲突
- 最终回归和验收

## Task 1: 增量迁移与基础枚举扩展

**Files:**
- Create: `code/backend/migrations/changes/0002_tiered_vpn_control_plane.up.sql`
- Create: `code/backend/migrations/changes/0002_tiered_vpn_control_plane.down.sql`
- Modify: `code/backend/migrations/README.md`
- Test: `code/backend/test/support/client-catalog.fixture.ts`

**Step 1: 写失败的 schema fixture 扩展测试**

在 `code/backend/test/support/client-catalog.fixture.ts` 旁新增最小 fixture 校验，确认以下对象可被创建：

- `vpn_lines`
- `vpn_line_nodes`
- `qos_profiles`
- `static_ip_pools`
- `static_ip_allocations`

**Step 2: 运行现有 Postgres e2e，确认新增表缺失导致失败**

Run:

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/backend
npm run test:e2e -- admin-postgres.e2e-spec.ts
```

Expected:
- FAIL，原因是 fixture / query 无法找到新增控制面对象

**Step 3: 写增量迁移**

在 `0002_tiered_vpn_control_plane.up.sql` 中完成：

- `plans` 新增字段：
  - `product_tier`
  - `display_name_zh`
  - `marketing_title_zh`
  - `marketing_description_zh`
  - `requires_region_selection`
  - `switch_region_allowed`
  - `speed_profile_code`
  - `static_ip_required`
- `orders` 新增字段：
  - `selected_region_code`
  - `product_tier`
  - `term_months`
- 新增表：
  - `vpn_lines`
  - `vpn_line_nodes`
  - `qos_profiles`
  - `static_ip_pools`
  - `static_ip_allocations`

**Step 4: 写回滚脚本**

在 `0002_tiered_vpn_control_plane.down.sql` 中按 reverse order 删除新增表和列。

**Step 5: 更新迁移文档**

在 `code/backend/migrations/README.md` 中补充 `changes/0002_*` 的执行顺序和 expand / backfill / contract 说明。

**Step 6: 扩展 pg-mem fixture**

修改 `code/backend/test/support/client-catalog.fixture.ts`，补齐：

- 香港 / 新加坡 / 美国三地
- 基础 / 高级 / 商业线路
- QoS profile 假数据
- 商业固定 IP 池假数据

**Step 7: 重跑 admin / vpn 相关 e2e**

Run:

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/backend
npm run test:e2e -- admin-postgres.e2e-spec.ts vpn-postgres.e2e-spec.ts
```

Expected:
- 通过或仅剩业务逻辑层失败，不再因 schema 缺失失败

**Step 8: Commit**

```bash
cd /Users/cnyirui/git/projects/liaojiang
git add code/backend/migrations code/backend/test/support/client-catalog.fixture.ts
git commit -m "feat(backend): add tiered vpn control-plane schema"
```

## Task 2: Catalog 与套餐读取改造为三档中文商品

**Files:**
- Modify: `code/backend/src/modules/database/client-catalog.types.ts`
- Modify: `code/backend/src/modules/database/bootstrap-client-catalog.ts`
- Modify: `code/backend/src/modules/database/postgres-data-access.service.ts`
- Modify: `code/backend/src/modules/database/client-catalog.service.ts`
- Modify: `code/backend/src/modules/plans/plans.service.ts`
- Modify: `code/backend/src/modules/admin/plans/dto/upsert-admin-plan.request.ts`
- Modify: `code/backend/src/modules/admin/plans/admin-plans.service.ts`
- Test: `code/backend/test/admin-postgres.e2e-spec.ts`
- Test: `code/backend/test/vpn-postgres.e2e-spec.ts`

**Step 1: 扩展 catalog 类型**

在 `client-catalog.types.ts` 为 `Plan` 增加：

- `productTier`
- `displayNameZh`
- `marketingTitleZh`
- `marketingDescriptionZh`
- `requiresRegionSelection`
- `switchRegionAllowed`
- `speedProfileCode`
- `staticIpRequired`

**Step 2: 更新 bootstrap catalog**

在 `bootstrap-client-catalog.ts` 中加入 12 个 SKU 占位：

- 基础套餐 1/3/6/12 月
- 高级套餐 1/3/6/12 月
- 商业套餐 1/3/6/12 月

要求：

- 用户可见名称全部中文
- 不在返回给客户端的字段里暴露技术线路码

**Step 3: 更新 Postgres data access 映射**

修改 `postgres-data-access.service.ts`：

- 读写新增 `plans` 字段
- `allowedRegionIds` 继续保留，用于 UI 初始购买地域候选
- 保持兼容现有 flat pagination

**Step 4: 更新后台套餐 DTO**

修改 `upsert-admin-plan.request.ts`，补充新增套餐字段的校验规则。

**Step 5: 更新后台套餐 service**

修改 `admin-plans.service.ts`，把新增字段透传到 `PlanMutationInput`。

**Step 6: 更新客户端套餐返回**

修改 `plans.service.ts`，确保 `GET /client/v1/plans` 返回：

- 中文展示名
- 三档商品属性
- 时长
- 地域选择规则
- 速率说明字段
- 固定 IP 说明字段

**Step 7: 更新 e2e 断言**

在 `admin-postgres.e2e-spec.ts` 和 `vpn-postgres.e2e-spec.ts` 中改为断言：

- 中文套餐名
- 12 SKU 至少有代表项
- 商业套餐的 `requiresRegionSelection = true`
- 基础 / 高级的 `switchRegionAllowed = true`

**Step 8: Run tests**

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/backend
npm run test:e2e -- admin-postgres.e2e-spec.ts vpn-postgres.e2e-spec.ts
```

Expected:
- PASS

**Step 9: Commit**

```bash
cd /Users/cnyirui/git/projects/liaojiang
git add code/backend/src/modules/database code/backend/src/modules/plans code/backend/src/modules/admin/plans code/backend/test
git commit -m "feat(backend): expose tiered vpn plan catalog"
```

## Task 3: 订单接口支持地域与期限真相

**Files:**
- Modify: `code/backend/src/modules/orders/dto/create-order.request.ts`
- Modify: `code/backend/src/modules/orders/orders.types.ts`
- Modify: `code/backend/src/modules/orders/orders.service.ts`
- Modify: `code/backend/src/modules/database/runtime-state.types.ts`
- Modify: `code/backend/src/modules/database/runtime-state.repository.ts`
- Modify: `code/backend/src/modules/database/postgres-runtime-state.repository.ts`
- Test: `code/backend/test/orders-postgres.e2e-spec.ts`

**Step 1: 写失败的订单 e2e**

在 `orders-postgres.e2e-spec.ts` 增加新用例：

- 基础套餐下单必须接受 `selectedRegionCode`
- 商业套餐未传地域时返回失败
- 创建订单后返回 `productTier / termMonths / selectedRegionCode`

**Step 2: 运行 e2e，确认 DTO 缺字段失败**

Run:

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/backend
npm run test:e2e -- orders-postgres.e2e-spec.ts
```

Expected:
- FAIL，原因是 DTO 不接受新增字段或返回缺失

**Step 3: 扩展 DTO**

在 `create-order.request.ts` 增加：

- `selectedRegionCode`
- `termMonths` 或从 plan 派生后回填校验逻辑

如果 `termMonths` 完全由 SKU 决定，则 DTO 只保留 `selectedRegionCode`，并在 service 中从 `plan` 读取 `billingCycleMonths`。

**Step 4: 扩展 order record**

在 `orders.types.ts` 和 runtime-state 类型中新增：

- `selectedRegionCode`
- `productTier`
- `termMonths`

**Step 5: 更新 createOrder 逻辑**

在 `orders.service.ts` 中新增规则：

- 商业套餐必须选地域
- 基础 / 高级套餐也要求选初始地域
- `selectedRegionCode` 必须属于当前 plan 可售地域
- 价格仍然从 plan 读取，不从客户端传入

**Step 6: 持久化新增字段**

修改 runtime-state repository 与 postgres-runtime-state repository 的 create/save/list 映射。

**Step 7: 重跑订单 e2e**

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/backend
npm run test:e2e -- orders-postgres.e2e-spec.ts
```

Expected:
- PASS

**Step 8: Commit**

```bash
cd /Users/cnyirui/git/projects/liaojiang
git add code/backend/src/modules/orders code/backend/src/modules/database code/backend/test/orders-postgres.e2e-spec.ts
git commit -m "feat(backend): persist vpn order region and term"
```

## Task 4: 订阅权益与线路激活模型重构

**Files:**
- Create: `code/backend/src/modules/vpn/subscription-entitlement.types.ts`
- Create: `code/backend/src/modules/vpn/subscription-entitlement.policy.ts`
- Modify: `code/backend/src/modules/vpn/vpn.types.ts`
- Modify: `code/backend/src/modules/vpn/vpn.service.ts`
- Modify: `code/backend/src/modules/provisioning/provisioning.service.ts`
- Test: `code/backend/test/vpn-postgres.e2e-spec.ts`

**Step 1: 写失败的 entitlement e2e**

新增断言：

- 基础套餐激活后有三地基础线路 entitlement
- 高级套餐激活后有三地高级线路 entitlement
- 商业套餐激活后只有下单地域的商业线路 entitlement
- 商业套餐 `switchRegionAllowed = false`

**Step 2: 运行 vpn e2e，确认当前 `activateSubscription()` 能力不足**

Run:

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/backend
npm run test:e2e -- vpn-postgres.e2e-spec.ts
```

Expected:
- FAIL，原因是当前订阅只有 `planCode / selectedLineCode / selectedNodeId`

**Step 3: 定义 entitlement 输入输出**

在 `subscription-entitlement.types.ts` 定义：

- 当前 plan 快照
- 当前 subscription 快照
- 订单输入
- 目标 entitlement

最少字段：

- `allowedLineCodes`
- `switchRegionAllowed`
- `lockedRegionCode`
- `speedProfileCode`
- `staticIpRequired`
- `staticIpAllocationId`

**Step 4: 写 entitlement policy**

在 `subscription-entitlement.policy.ts` 实现规则：

- 基础套餐 -> 基础线路集合
- 高级套餐 -> 高级线路集合
- 商业套餐 -> 单地域商业线路

**Step 5: 扩展订阅持久化模型**

在 `vpn.types.ts` 和 repository 映射中补齐：

- `allowedLineCodes`
- `switchRegionAllowed`
- `lockedRegionCode`
- `speedProfileCode`
- `staticIpAllocationId`

**Step 6: 改造 `activateSubscription()`**

把 `vpn.service.ts` 中的 `activateSubscription()` 改为：

- 先算 entitlement
- 再生成订阅状态
- 不在这里直接做节点执行

**Step 7: 更新 provisioning**

在 `provisioning.service.ts` 中把 entitlement 输出交给后续控制器同步逻辑，而不是只把 `expireAt` 交给 `Marzban`。

**Step 8: 重跑 vpn e2e**

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/backend
npm run test:e2e -- vpn-postgres.e2e-spec.ts
```

Expected:
- PASS

**Step 9: Commit**

```bash
cd /Users/cnyirui/git/projects/liaojiang
git add code/backend/src/modules/vpn code/backend/src/modules/provisioning code/backend/test/vpn-postgres.e2e-spec.ts
git commit -m "feat(backend): add tiered vpn entitlement policy"
```

## Task 5: Marzban 单用户预建立多线路能力与控制器协议

**Files:**
- Create: `code/backend/src/modules/vpn-control-plane/vpn-control-plane.types.ts`
- Create: `code/backend/src/modules/vpn-control-plane/vpn-control-plane.service.ts`
- Create: `code/backend/src/modules/vpn-control-plane/vpn-control-plane.module.ts`
- Modify: `code/backend/src/modules/marzban/marzban.types.ts`
- Modify: `code/backend/src/modules/marzban/marzban.service.ts`
- Modify: `code/backend/src/modules/vpn/vpn.service.ts`
- Modify: `code/backend/src/app.module.ts`
- Test: `code/backend/test/vpn-postgres.e2e-spec.ts`

**Step 1: 写失败的行为测试**

在 `vpn-postgres.e2e-spec.ts` 新增行为断言：

- 基础 / 高级切换地域时复用同一个 `marzbanUsername`
- 商业套餐切换地域被拒绝
- `/vpn/selection` 不触发重建用户

**Step 2: 定义控制器协议**

在 `vpn-control-plane.types.ts` 定义：

- `desiredLines`
- `activeLine`
- `speedProfileCode`
- `staticIpAllocationId`
- `reason`
- `syncStatus`

**Step 3: 写 service 协议层**

在 `vpn-control-plane.service.ts` 提供：

- `upsertDesiredStateForProvisioning(...)`
- `switchActiveLine(...)`
- `markAppliedState(...)`

先做最小内聚，不直接写 node-side 执行器。

**Step 4: 扩展 Marzban types**

在 `marzban.types.ts` 为用户期望状态补充：

- 允许线路集合
- 当前激活线路
- 订阅展示元数据

**Step 5: 改造 `marzban.service.ts`**

要求：

- 仍保持单账号单用户
- 不因切换地域重建用户
- 发货时一次性处理 allowed lines
- 切换时只更新 active line 相关状态

**Step 6: 改造 `vpn.service.ts` 的 selection 路径**

`/vpn/selection` 改成：

- 校验 entitlement
- 更新当前 active line
- 调控制器协议 service
- 不重建 `Marzban user`

**Step 7: 把模块接入主应用**

在 `app.module.ts` 引入 `VpnControlPlaneModule`。

**Step 8: Run tests**

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/backend
npm run test:e2e -- vpn-postgres.e2e-spec.ts
npm run typecheck
```

Expected:
- PASS

**Step 9: Commit**

```bash
cd /Users/cnyirui/git/projects/liaojiang
git add code/backend/src/modules/vpn-control-plane code/backend/src/modules/marzban code/backend/src/modules/vpn code/backend/src/app.module.ts code/backend/test/vpn-postgres.e2e-spec.ts
git commit -m "feat(backend): reuse marzban user across vpn region switches"
```

## Task 6: 商业固定 IP 池与续费保留

**Files:**
- Create: `code/backend/src/modules/vpn/static-ip.types.ts`
- Create: `code/backend/src/modules/vpn/static-ip.service.ts`
- Modify: `code/backend/src/modules/provisioning/provisioning.service.ts`
- Modify: `code/backend/src/modules/orders/orders.service.ts`
- Modify: `code/backend/src/modules/vpn/vpn.service.ts`
- Test: `code/backend/test/vpn-postgres.e2e-spec.ts`
- Test: `code/backend/test/orders-postgres.e2e-spec.ts`

**Step 1: 写失败的商业套餐用例**

新增测试：

- 商业套餐创建订单时预占固定 IP
- 支付成功后转正式分配
- 续费后保留原分配
- 如果原 IP 无法保留，续费进入异常状态

**Step 2: 实现静态 IP 类型与服务**

在 `static-ip.service.ts` 中实现：

- `reserveForOrder(...)`
- `activateForSubscription(...)`
- `retainForRenewal(...)`
- `releaseExpiredAllocation(...)`

**Step 3: 订单阶段接入预占**

在 `orders.service.ts` 中：

- 商业订单创建时预占固定 IP
- 订单过期时释放预占

**Step 4: 发货阶段接入正式分配**

在 `provisioning.service.ts` 中：

- 商业订单支付成功后，将预占转正式分配

**Step 5: 续费规则**

在 `vpn.service.ts` / `provisioning.service.ts` 中：

- 商业续费优先保留原 `staticIpAllocationId`
- 若无法保留，则抛出受控异常并进入异常处理状态

**Step 6: Run tests**

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/backend
npm run test:e2e -- orders-postgres.e2e-spec.ts vpn-postgres.e2e-spec.ts
```

Expected:
- PASS

**Step 7: Commit**

```bash
cd /Users/cnyirui/git/projects/liaojiang
git add code/backend/src/modules/vpn code/backend/src/modules/orders code/backend/src/modules/provisioning code/backend/test
git commit -m "feat(backend): preserve static ip on business vpn renewal"
```

## Task 7: Android App 三层中文购买与订阅展示

**Files:**
- Modify: `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/model/Plan.kt`
- Modify: `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/model/Order.kt`
- Modify: `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/api/PaymentApi.kt`
- Modify: `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/repository/PaymentRepository.kt`
- Modify: `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p1/model/PlansContract.kt`
- Modify: `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p1/model/OrderCheckoutContract.kt`
- Modify: `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p1/PlansPage.kt`
- Modify: `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p1/OrderCheckoutPage.kt`
- Modify: `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p2extended/SubscriptionDetailPage.kt`
- Modify: `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepository.kt`
- Test: `code/Android/V2rayNG/app/src/test/java/com/v2ray/ang/payment/data/model/TieredPlanSelectionTest.kt`
- Test: `code/Android/V2rayNG/app/src/test/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepositoryTieredPlanTest.kt`

**Step 1: 写失败的选择模型测试**

覆盖：

- 三层选择生成正确中文显示
- 商业套餐必须选地域
- 基础 / 高级允许切换地域的文案展示正确

**Step 2: 扩展 `Plan.kt`**

补充字段：

- `productTier`
- `displayNameZh`
- `marketingTitleZh`
- `marketingDescriptionZh`
- `requiresRegionSelection`
- `switchRegionAllowed`
- `speedProfileCode`
- `staticIpRequired`

并新增中文 helper：

- `tierDisplayText()`
- `speedRuleDisplayText()`
- `regionRuleDisplayText()`

**Step 3: 扩展 `CreateOrderRequest`**

在 `Order.kt` 中给 `CreateOrderRequest` 增加：

- `selectedRegionCode`

**Step 4: 修改 `PaymentRepository.createOrder()`**

要求：

- 透传 `selectedRegionCode`
- 不再把购买地域当成“支付后补选”

**Step 5: 重构 `PlansContract.kt`**

新增 UI 状态：

- `selectedTier`
- `selectedTermMonths`
- `selectedRegionCode`
- `tierOptions`
- `termOptions`
- `regionOptions`
- `marketingHighlights`

**Step 6: 改造 `PlansPage.kt`**

把当前“单 plan 卡片列表”改为：

- 中文套餐属性选择
- 服务时长选择
- 地域选择
- 下方同步更新介绍和价格

**Step 7: 改造 `OrderCheckoutPage.kt`**

直接展示：

- 套餐属性
- 服务时长
- 已选地域
- 价格

不再显示“支付后补选区域”。

**Step 8: 更新订阅详情页**

在 `SubscriptionDetailPage.kt` 与 repository 展示层中增加：

- 当前套餐中文名
- 地域切换规则
- 商业固定 IP 信息
- 高级速率信息

**Step 9: Run Android tests**

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG
./gradlew :app:testFdroidDebugUnitTest --tests "com.v2ray.ang.payment.data.model.TieredPlanSelectionTest"
./gradlew :app:testFdroidDebugUnitTest --tests "com.v2ray.ang.composeui.common.repository.RealCryptoVpnRepositoryTieredPlanTest"
./gradlew :app:compileFdroidDebugKotlin
```

Expected:
- PASS

**Step 10: Commit**

```bash
cd /Users/cnyirui/git/projects/liaojiang
git add code/Android/V2rayNG/app/src/main/java code/Android/V2rayNG/app/src/test/java
git commit -m "feat(android): add chinese tiered vpn purchase flow"
```

## Task 8: Admin Web 商品与控制面配置入口

**Files:**
- Modify: `code/admin-web/src/types/index.ts`
- Modify: `code/admin-web/src/api/index.ts`
- Modify: `code/admin-web/src/pages/Plans.tsx`
- Modify: `code/admin-web/src/pages/Regions.tsx`
- Modify: `code/admin-web/src/pages/Nodes.tsx`
- Modify: `code/admin-web/src/pages/SystemConfigs.tsx`
- Create: `code/admin-web/src/pages/QosProfiles.tsx`
- Create: `code/admin-web/src/pages/StaticIpPools.tsx`
- Modify: `code/admin-web/src/App.tsx`
- Modify: `code/admin-web/src/layouts/MainLayout.tsx`

**Step 1: 扩展前端类型**

在 `types/index.ts` 中补充：

- `productTier`
- `displayNameZh`
- `switchRegionAllowed`
- `speedProfileCode`
- `staticIpRequired`
- `VpnLine`
- `QosProfile`
- `StaticIpPool`

**Step 2: 扩展 API 封装**

在 `api/index.ts` 中补充：

- 线路列表接口
- QoS profile 列表接口
- 固定 IP 池列表接口

若后端接口尚未实现，则先在计划执行阶段一起补。

**Step 3: 改造套餐页**

`Plans.tsx` 必须支持配置：

- 套餐属性
- 服务时长
- 中文标题 / 描述
- 是否需要地域选择
- 是否允许切换地域
- 速率档位
- 是否商业固定 IP

**Step 4: 改造地域 / 节点页**

`Regions.tsx` 与 `Nodes.tsx` 增加：

- 线路归属
- 产品档位
- QoS 档位
- 商业 / 共享线路标签

**Step 5: 新增 QoS 页面**

`QosProfiles.tsx` 展示：

- 中文名称
- 保底速率
- 封顶速率
- 状态

**Step 6: 新增固定 IP 池页面**

`StaticIpPools.tsx` 展示：

- 地域
- IP
- 线路
- 当前状态
- 已分配账号 / 订阅

**Step 7: 更新路由和菜单**

在 `App.tsx` 和 `MainLayout.tsx` 中注册新页面。

**Step 8: Run admin-web checks**

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/admin-web
npm run lint
npm run build
```

Expected:
- PASS

**Step 9: Commit**

```bash
cd /Users/cnyirui/git/projects/liaojiang
git add code/admin-web
git commit -m "feat(admin-web): add vpn qos and static ip management"
```

## Task 9: 回归与验收

**Files:**
- Create: `docs/regression-report.md`
- Modify: `handoff/task-state.md`
- Modify: `docs/current-status.md`
- Test: `code/backend/test/orders-postgres.e2e-spec.ts`
- Test: `code/backend/test/vpn-postgres.e2e-spec.ts`
- Test: `code/backend/test/admin-postgres.e2e-spec.ts`

**Step 1: 后端自动化回归**

Run:

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/backend
npm run typecheck
npm run build
npm run test:e2e -- orders-postgres.e2e-spec.ts vpn-postgres.e2e-spec.ts admin-postgres.e2e-spec.ts
```

Expected:
- PASS

**Step 2: Android 自动化回归**

Run:

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG
./gradlew :app:testFdroidDebugUnitTest
./gradlew :app:compileFdroidDebugKotlin
```

Expected:
- PASS

**Step 3: Admin Web 自动化回归**

Run:

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/admin-web
npm run lint
npm run build
```

Expected:
- PASS

**Step 4: 手工验收清单**

覆盖：

1. 基础套餐购买后可在香港 / 新加坡 / 美国切换
2. 高级套餐购买后可在三地切换，切换不重建 `Marzban user`
3. 商业套餐购买后固定地域，不可切换
4. 商业套餐续费后固定 IP 不变
5. App 前端所有购买文案为中文
6. 后台能看到线路、QoS、固定 IP 池状态

**Step 5: 写回归报告**

在 `docs/regression-report.md` 记录：

- 自动化结果
- 手工结果
- 未覆盖风险

**Step 6: 更新 handoff**

在 `handoff/task-state.md` 和 `docs/current-status.md` 写入：

- 已完成范围
- 未完成范围
- 线上部署前置项

**Step 7: Commit**

```bash
cd /Users/cnyirui/git/projects/liaojiang
git add docs/regression-report.md handoff/task-state.md docs/current-status.md
git commit -m "docs: record tiered vpn control-plane regression results"
```

## 执行建议

按下面顺序启动子代理最稳妥：

1. Backend Product & Entitlement Worker
2. Control-Plane Worker
3. Android Purchase Worker
4. Admin Web Worker
5. Verification Worker

依赖关系：

- Android 和 Admin Web 依赖 backend contract 定稿
- Verification 依赖其他 4 路基本完成

## 交付门槛

达到以下条件才算完成：

- App 购买页完成三层中文选择
- 后端订单记录真实保存地域与档位信息
- 基础 / 高级切换地域不重建 `Marzban user`
- 商业套餐固定 IP 真实分配且续费保留不变
- Admin Web 能配置套餐、线路、QoS、固定 IP 池
- 自动化和手工回归通过
