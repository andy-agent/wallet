# admin-web 与 backend admin 契约差异文档

> 来源优先级：`code/admin-web/src/**` → `code/backend/src/**` → `final_engineering_delivery_package/05_ia_and_page_spec.md` → `final_engineering_delivery_package/07_api_spec.md` → `final_engineering_delivery_package/08_openapi_v1.yaml`

---

## 1. admin-web 当前页面清单

| 页面名称 | 路由 | 组件文件路径 | 实现状态 | 备注 |
|---|---|---|---|---|
| 后台登录 | `/login` | `code/admin-web/src/pages/Login.tsx` | 仅 UI，无真实 API 调用 | 采用本地 mock（`localStorage.setItem('admin_token', 'mock_token_...')`） |
| 数据概览 | `/` | `code/admin-web/src/pages/Dashboard.tsx` | 前端已实现 | 依赖 `GET /api/admin/v1/dashboard/stats` |
| 用户管理 | `/accounts` | `code/admin-web/src/pages/Accounts.tsx` | 仅骨架 | `<Empty description="...待接入真实 Admin Account API" />` |
| 订单管理 | `/orders` | `code/admin-web/src/pages/Orders.tsx` | 前端已实现 | 含列表、筛选、详情弹窗、操作按钮 |
| 套餐管理 | `/plans` | `code/admin-web/src/pages/Plans.tsx` | 前端已实现 | 含列表、创建/编辑弹窗 |
| 区域管理 | `/regions` | `code/admin-web/src/pages/Regions.tsx` | 仅骨架 | `<Empty />` |
| 节点管理 | `/nodes` | `code/admin-web/src/pages/Nodes.tsx` | 仅骨架 | `<Empty />` |
| 提现审核 | `/withdrawals` | `code/admin-web/src/pages/Withdrawals.tsx` | 仅骨架 | `<Empty />` |
| 版本管理 | `/versions` | `code/admin-web/src/pages/Versions.tsx` | 仅骨架 | `<Empty />` |
| 法务文档 | `/legal-docs` | `code/admin-web/src/pages/LegalDocs.tsx` | 仅骨架 | `<Empty />` |
| 系统配置 | `/system-configs` | `code/admin-web/src/pages/SystemConfigs.tsx` | 仅骨架 | `<Empty />` |
| 审计日志 | `/audit-logs` | `code/admin-web/src/pages/AuditLogs.tsx` | 前端已实现 | 含列表、筛选、实体追踪弹窗 |

**路由定义文件**：`code/admin-web/src/App.tsx`（第 35–55 行）  
**菜单定义文件**：`code/admin-web/src/layouts/MainLayout.tsx`（第 28–84 行）

---

## 2. admin-web 当前调用的 API 清单

所有请求均通过 `code/admin-web/src/api/request.ts` 创建 axios 实例，`baseURL` 为 `/api`，因此实际完整路径前缀为 `/api/admin/v1/*`。

| 所属页面 | 方法 | 接口路径（完整） | 定义位置 | 备注 |
|---|---|---|---|---|
| 订单管理 | GET | `/api/admin/v1/orders` | `code/admin-web/src/api/index.ts:12` | 列表查询 |
| 订单管理 | GET | `/api/admin/v1/orders/:id` | `code/admin-web/src/api/index.ts:17` | 详情查询 |
| 订单管理 | POST | `/api/admin/v1/orders/:id/manual-fulfill` | `code/admin-web/src/api/index.ts:22` | 人工确认 |
| 订单管理 | POST | `/api/admin/v1/orders/:id/retry-fulfill` | `code/admin-web/src/api/index.ts:27` | 重试发货 |
| 订单管理 | POST | `/api/admin/v1/orders/:id/ignore` | `code/admin-web/src/api/index.ts:32` | 标记忽略 |
| 套餐管理 | GET | `/api/admin/v1/plans` | `code/admin-web/src/api/index.ts:39` | 列表查询 |
| 套餐管理 | POST | `/api/admin/v1/plans` | `code/admin-web/src/api/index.ts:44` | 创建套餐 |
| 套餐管理 | PUT | `/api/admin/v1/plans/:id` | `code/admin-web/src/api/index.ts:49` | 更新套餐 |
| 审计日志 | GET | `/api/admin/v1/audit-logs` | `code/admin-web/src/api/index.ts:56` | 日志列表 |
| 审计日志 | GET | `/api/admin/v1/audit-logs/:entityType/:entityId` | `code/admin-web/src/api/index.ts:61` | 实体追踪 |
| 数据概览 | GET | `/api/admin/v1/dashboard/stats` | `code/admin-web/src/api/index.ts:68` | 统计数据 |

**当前未调用的 Spec 页面（无对应前端组件/无 API 调用）**：
- **佣金账本页**：`final_engineering_delivery_package/05_ia_and_page_spec.md` 中 Admin 页面树明确要求，但 `code/admin-web/src/App.tsx` 中无路由、无组件文件。

---

## 3. backend 当前实际存在的 admin API 清单

**结果：空（无任何 `/api/admin/v1/*` 端点实现）**

**证据**：
1. 全局 grep 无命中：
   ```bash
   $ grep -rn "@Controller.*admin" code/backend/src/
   No admin controllers found
   $ grep -rn "'admin/v1" code/backend/src/
   No admin/v1 references found
   ```
2. `code/backend/src/modules/admin/admin.module.ts` 为空白模块：
   ```typescript
   @Module({})
   export class AdminModule {}
   ```
3. 现有所有 Controller 均挂载在 `client/v1` 或 `healthz` 路径下：

| Controller 文件 | 路径前缀 | 说明 |
|---|---|---|
| `code/backend/src/modules/auth/auth.controller.ts` | `client/v1/auth` | 客户端认证 |
| `code/backend/src/modules/accounts/accounts.controller.ts` | `client/v1` | 客户端 Me/Session |
| `code/backend/src/modules/orders/orders.controller.ts` | `client/v1/orders` | 客户端订单 |
| `code/backend/src/modules/plans/plans.controller.ts` | `client/v1/plans` | 客户端套餐（只读） |
| `code/backend/src/modules/vpn/vpn.controller.ts` | `client/v1/vpn` | 客户端 VPN |
| `code/backend/src/modules/vpn/subscription.controller.ts` | `client/v1/subscriptions` | 客户端订阅 |
| `code/backend/src/modules/withdrawals/withdrawals.controller.ts` | `client/v1/withdrawals` | 客户端提现 |
| `code/backend/src/modules/wallet/wallet.controller.ts` | `client/v1/wallet` | 客户端钱包 |
| `code/backend/src/modules/referral/referral.controller.ts` | `client/v1` | 客户端邀请/佣金 |
| `code/backend/src/modules/health/health.controller.ts` | `healthz` | 健康检查 |
| `code/backend/src/app.controller.ts` | `/` | 根路由 |

---

## 4. 与 `final_engineering_delivery_package` 中 Admin 页面/接口规格的差异

### 4.1 页面差异（基于 `05_ia_and_page_spec.md`）

| Spec 要求页面 | admin-web 当前状态 | 差异说明 |
|---|---|---|
| 后台登录页 | 有 UI，无 API | 仅 mock，未对接 `POST /api/admin/v1/auth/login` |
| 仪表盘 | 有 UI，无 API | 已实现前端，但后端无对应接口 |
| 用户列表 / 详情 | 仅骨架 | 页面为 `<Empty />`，无列表/详情/操作 UI |
| 套餐管理 | 有 UI，部分操作缺失 | 前端有创建/编辑，但缺少“发布”、“禁用”按钮 |
| 区域管理 | 仅骨架 | 页面为 `<Empty />` |
| 节点管理 | 仅骨架 | 页面为 `<Empty />` |
| 订单中心 | 有 UI，操作命名不符 | 前端操作按钮为“确认”、“重试”、“忽略”，与 Spec 的 `mark-exception`、`retry-provision` 不一致 |
| 佣金账本页 | **完全缺失** | `App.tsx` 无路由、无组件文件 |
| 提现审核页 | 仅骨架 | 页面为 `<Empty />` |
| 版本管理 | 仅骨架 | 页面为 `<Empty />` |
| 法务文档管理 | 仅骨架 | 页面为 `<Empty />` |
| 系统配置页 | 仅骨架 | 页面为 `<Empty />` |
| 审计日志页 | 有 UI，多了未定义接口 | 前端多了实体追踪接口 `GET /api/admin/v1/audit-logs/:entityType/:entityId`，Spec 未定义 |

### 4.2 接口差异（基于 `07_api_spec.md` + `08_openapi_v1.yaml`）

**A. 命名/路径漂移（P2）**

| 当前 admin-web 调用 | Spec / OpenAPI 定义 | 差异类型 |
|---|---|---|
| `GET /api/admin/v1/dashboard/stats` | `GET /api/admin/v1/dashboard/summary` | 路径后缀漂移：`stats` → `summary` |
| `POST /api/admin/v1/orders/:id/manual-fulfill` | 无此端点；Spec 为 `POST /api/admin/v1/orders/{orderNo}/retry-provision` | 功能与命名均不匹配 |
| `POST /api/admin/v1/orders/:id/retry-fulfill` | 无此端点；Spec 为 `POST /api/admin/v1/orders/{orderNo}/retry-provision` | 命名漂移且功能重叠 |
| `POST /api/admin/v1/orders/:id/ignore` | 无此端点；Spec 为 `POST /api/admin/v1/orders/{orderNo}/mark-exception` | 功能语义不匹配 |
| `GET /api/admin/v1/audit-logs/:entityType/:entityId` | **未在 Spec 中定义** | 前端自定义了 Spec 没有的实体追踪接口 |
| 参数 `:id`（Orders/Plans） | Spec 使用 `{orderNo}`、`{planId}` | 路径参数命名漂移 |

**B. 缺失的 Admin API（全部未在 backend 实现）**

根据 `07_api_spec.md` 与 `08_openapi_v1.yaml`，Backend 应提供但当前缺失的 Admin API 共 **35 个路径**（部分路径含多个 Method）。按模块归类如下：

- **AdminAuth**：`POST /api/admin/v1/auth/login`、`POST /api/admin/v1/auth/logout`
- **AdminDashboard**：`GET /api/admin/v1/dashboard/summary`
- **AdminAccounts**：`GET /api/admin/v1/accounts`、`GET /api/admin/v1/accounts/{accountId}`、`POST /api/admin/v1/accounts/{accountId}/freeze`、`POST /api/admin/v1/accounts/{accountId}/unfreeze`、`POST /api/admin/v1/accounts/{accountId}/evict-sessions`
- **AdminPlans**：`GET /api/admin/v1/plans`、`POST /api/admin/v1/plans`、`PUT /api/admin/v1/plans/{planId}`、`POST /api/admin/v1/plans/{planId}/publish`、`POST /api/admin/v1/plans/{planId}/disable`
- **AdminVPN**：`GET /api/admin/v1/vpn/regions`、`POST /api/admin/v1/vpn/regions`、`PUT /api/admin/v1/vpn/regions/{regionId}`、`GET /api/admin/v1/vpn/nodes`、`POST /api/admin/v1/vpn/nodes`、`PUT /api/admin/v1/vpn/nodes/{nodeId}`、`POST /api/admin/v1/vpn/nodes/{nodeId}/disable`
- **AdminOrders**：`GET /api/admin/v1/orders`、`GET /api/admin/v1/orders/{orderNo}`、`POST /api/admin/v1/orders/{orderNo}/mark-exception`、`POST /api/admin/v1/orders/{orderNo}/retry-provision`
- **AdminCommissions**：`GET /api/admin/v1/commissions/ledger`
- **AdminWithdrawals**：`GET /api/admin/v1/withdrawals`、`POST /api/admin/v1/withdrawals/{requestNo}/approve`、`POST /api/admin/v1/withdrawals/{requestNo}/reject`、`POST /api/admin/v1/withdrawals/{requestNo}/record-payout`、`POST /api/admin/v1/withdrawals/{requestNo}/retry-broadcast`
- **AdminVersions**：`GET /api/admin/v1/app-versions`、`POST /api/admin/v1/app-versions`、`POST /api/admin/v1/app-versions/{versionId}/publish`
- **AdminLegal**：`GET /api/admin/v1/legal-documents`、`PUT /api/admin/v1/legal-documents/{docType}`、`POST /api/admin/v1/legal-documents/{docType}/publish`
- **AdminConfig**：`GET /api/admin/v1/system-configs`、`PUT /api/admin/v1/system-configs/{configKey}`
- **AdminAudit**：`GET /api/admin/v1/audit-logs`

---

## 5. 缺失接口分级

### P0 — 当前页面完全不可联调（Backend 0 实现，前端无法获取数据或执行操作）

> 由于 backend 当前没有任何 `/api/admin/v1/*` 接口，**所有 Admin 页面均属于 P0**。以下按页面列出具体缺失的 API。

| 页面 | 缺失的 API（Spec 定义） | 前端现状 |
|---|---|---|
| **登录页** | `POST /api/admin/v1/auth/login` | 完全 mock，无法真实登录 |
| **数据概览** | `GET /api/admin/v1/dashboard/summary` | 前端图表/统计卡片无法渲染数据 |
| **用户管理** | `GET /api/admin/v1/accounts`<br>`GET /api/admin/v1/accounts/{accountId}`<br>`POST .../freeze`<br>`POST .../unfreeze`<br>`POST .../evict-sessions` | 仅骨架 `<Empty />`，列表/详情/操作全无 |
| **订单管理** | `GET /api/admin/v1/orders`<br>`GET /api/admin/v1/orders/{orderNo}`<br>`POST .../mark-exception`<br>`POST .../retry-provision` | 前端列表/详情/操作按钮完整，但所有接口 404 |
| **套餐管理** | `GET /api/admin/v1/plans`<br>`POST /api/admin/v1/plans`<br>`PUT /api/admin/v1/plans/{planId}`<br>`POST .../publish`<br>`POST .../disable` | 前端列表/创建/编辑完整，缺少发布/禁用按钮，且全部接口 404 |
| **区域管理** | `GET /api/admin/v1/vpn/regions`<br>`POST /api/admin/v1/vpn/regions`<br>`PUT /api/admin/v1/vpn/regions/{regionId}` | 仅骨架 `<Empty />` |
| **节点管理** | `GET /api/admin/v1/vpn/nodes`<br>`POST /api/admin/v1/vpn/nodes`<br>`PUT /api/admin/v1/vpn/nodes/{nodeId}`<br>`POST .../disable` | 仅骨架 `<Empty />` |
| **佣金账本** | `GET /api/admin/v1/commissions/ledger` | **前端页面缺失** + 后端接口缺失 |
| **提现审核** | `GET /api/admin/v1/withdrawals`<br>`POST .../approve`<br>`POST .../reject`<br>`POST .../record-payout`<br>`POST .../retry-broadcast` | 仅骨架 `<Empty />` |
| **版本管理** | `GET /api/admin/v1/app-versions`<br>`POST /api/admin/v1/app-versions`<br>`POST .../publish` | 仅骨架 `<Empty />` |
| **法务文档** | `GET /api/admin/v1/legal-documents`<br>`PUT /api/admin/v1/legal-documents/{docType}`<br>`POST .../publish` | 仅骨架 `<Empty />` |
| **系统配置** | `GET /api/admin/v1/system-configs`<br>`PUT /api/admin/v1/system-configs/{configKey}` | 仅骨架 `<Empty />` |
| **审计日志** | `GET /api/admin/v1/audit-logs` | 前端列表完整，但接口 404；实体追踪接口亦无后端支持 |

### P1 — 可展示但不可操作（Read API 已存在，Write API 缺失）

| 页面 | 说明 |
|---|---|
| **无** | 由于 backend 尚未实现任何 Admin Read API，当前不存在“可展示但不可操作”的页面。本项为空。 |

### P2 — 字段/命名漂移（前端与 Spec 不一致，可能导致联调后需要二次修改）

| 漂移项 | 当前前端实现 | Spec / OpenAPI 要求 | 影响 |
|---|---|---|---|
| **Dashboard 路径** | `GET /api/admin/v1/dashboard/stats`<br>`code/admin-web/src/api/index.ts:68` | `GET /api/admin/v1/dashboard/summary` | 即使后端实现了 `summary`，前端仍需改路径 |
| **订单操作命名** | `manual-fulfill`、`retry-fulfill`、`ignore`<br>`code/admin-web/src/api/index.ts:22,27,32` | `mark-exception`、`retry-provision` | 语义与功能均不匹配，需重构前端 action 层 |
| **订单参数名** | `:id`（`Orders.tsx` 中 `record.id`） | `{orderNo}` | 路径参数不一致，后端需兼容或前端需改传参 |
| **Plan 参数名** | `:id`（`Plans.tsx` 中 `editingPlan.id`） | `{planId}` | 同上 |
| **Plan 缺失操作** | 无 `publish`、`disable` 按钮与 API 调用 | Spec 要求具备发布/禁用 | 功能缺口，需补充前端 UI 与 API 封装 |
| **AuditLogs 多余接口** | `GET /api/admin/v1/audit-logs/:entityType/:entityId`<br>`code/admin-web/src/api/index.ts:61` | Spec 未定义该接口 | 若后端不实现，前端实体追踪功能将 404 |
| **响应结构预期** | `request.ts` 期望 `{ code: 0 \| 200, message, data }` | Spec 要求 `{ requestId, code: "OK", message, data }` | `code` 类型为字符串 vs 数字；`requestId` 未被前端使用但需确保拦截器兼容 |

---

## 6. 推荐后续实施顺序

基于“尽快让已有前端页面可联调”原则，建议按以下顺序分阶段补齐 backend Admin API 与前端缺失页面。

### Phase 1 — 认证 + 已有完整前端页面的 Read API（最快产生可联调效果）
1. **Admin 登录接口**：`POST /api/admin/v1/auth/login`
   - 解除 mock，让 admin-web 能真实登录。
2. **Dashboard 读接口**：`GET /api/admin/v1/dashboard/summary`
   - 首页数据概览立即可用。
3. **Orders 列表/详情**：`GET /api/admin/v1/orders`、`GET /api/admin/v1/orders/{orderNo}`
   - 订单中心页面可展示数据。
4. **Plans 列表**：`GET /api/admin/v1/plans`
   - 套餐管理页面可展示数据。
5. **AuditLogs 列表**：`GET /api/admin/v1/audit-logs`
   - 审计日志页面可展示数据。

### Phase 2 — 已有完整前端页面的 Write API（补齐操作能力）
6. **Orders 操作**：`POST /api/admin/v1/orders/{orderNo}/mark-exception`、`POST .../retry-provision`
   - 同时需**修改前端** action 命名（从 `manual-fulfill`/`retry-fulfill`/`ignore` 对齐到 Spec）。
7. **Plans 写操作**：`POST /api/admin/v1/plans`、`PUT /api/admin/v1/plans/{planId}`、`POST .../publish`、`POST .../disable`
   - 同时需**补充前端**“发布/禁用”按钮。

### Phase 3 — 骨架页面的前端 + 后端一体化建设
8. **Accounts**：补齐后端 5 个 API + 前端列表/详情/操作 UI。
9. **Withdrawals**：补齐后端 5 个 API + 前端审核 UI。
10. **Regions & Nodes**：补齐后端 VPN Admin API + 前端骨架替换为真实页面。

### Phase 4 — 配置类与缺失页面
11. **Commissions（佣金账本）**：新建前端页面 `Commissions.tsx` 并注册路由，补齐后端 `GET /api/admin/v1/commissions/ledger`。
12. **Versions / LegalDocs / SystemConfigs**：补齐后端 API + 前端配置管理 UI。

### Phase 5 — 契约清理
13. **修正 P2 漂移项**：统一 Dashboard 路径、订单/Plan 参数命名、AuditLogs 实体追踪接口取舍（决定是否实现或删除前端代码）。
14. **响应结构对齐**：确保 backend 统一返回 `{ requestId, code, message, data }`，前端拦截器兼容 `code === "OK"` 或数字 `0/200`。

---

## 附录：关键文件路径索引

| 用途 | 路径 |
|---|---|
| admin-web 路由 | `code/admin-web/src/App.tsx` |
| admin-web 菜单 | `code/admin-web/src/layouts/MainLayout.tsx` |
| admin-web API 封装 | `code/admin-web/src/api/index.ts` |
| admin-web 请求拦截 | `code/admin-web/src/api/request.ts` |
| admin-web 类型定义 | `code/admin-web/src/types/index.ts` |
| backend Admin 模块（空） | `code/backend/src/modules/admin/admin.module.ts` |
| backend 现有 Controller 目录 | `code/backend/src/modules/*/ *.controller.ts` |
| Spec — Admin 页面 | `final_engineering_delivery_package/05_ia_and_page_spec.md` |
| Spec — API 清单 | `final_engineering_delivery_package/07_api_spec.md` |
| Spec — OpenAPI 定义 | `final_engineering_delivery_package/08_openapi_v1.yaml` |
