# 三档 VPN 套餐真实控制面改造设计

> 创建日期: 2026-04-23
> 优先级: P1
> 当前负责人: Codex / Liaojiang Dev
> 对齐 issue: `liaojiang-b9mt`

## 需求摘要

本次改造不是单纯补套餐文案或价格，而是把 VPN 商品体系升级为一套真实可执行的控制面：

1. App 购买页支持三档套餐：
   - 基础套餐
   - 高级套餐
   - 商业套餐
2. 每档套餐都支持四种服务时长：
   - 1 个月
   - 3 个月
   - 6 个月
   - 12 个月
3. 面向用户的购买信息全部中文化，不能暴露 `HK_BASIC` 这类内部英文线路名。
4. 基础套餐、高级套餐允许在香港 / 新加坡 / 美国之间切换地域。
5. 商业套餐必须在购买时选择地域，购买后不允许切换地域。
6. 真实控制要求不是展示型规则，而是必须落实到控制面：
   - 基础套餐：最高 2Mbps
   - 高级套餐：最低 5Mbps，最高 10Mbps
   - 商业套餐：固定独享 IP
7. 商业套餐续费时必须保留原固定 IP，不允许静默变更。

用户已经确认采用控制面解耦方案 B：

- 业务后端继续做商品、订单、订阅真相源
- 新增 `line / qos / static-ip` 控制层
- `Marzban` 继续负责订阅输出
- 节点侧 `Xray / tc / controller` 负责执行真实 QoS 与固定 IP

## 用户价值

- 用户能用中文直接理解自己买的是什么，不需要理解技术线路码。
- 用户在购买时一次完成“套餐属性、时长、地域”选择，减少下单后再补选的心智负担。
- 基础 / 高级套餐购买后可以自由切换地域，但切换不需要重复建用户或重复下发整套账户。
- 商业套餐购买后拿到真实固定 IP，续费后 IP 不变，适合长期业务场景。
- 运营可在后台管理商品、线路、QoS 与固定 IP 库存，不再依赖手工配置。

## 范围界定

### 本期必须做

1. Android App 购买页改为三层中文选择模型
2. 后端套餐、订单、订阅、线路、QoS、固定 IP 数据模型扩展
3. 订单下单时显式携带地域选择
4. `Marzban` 用户模型升级为“单用户预建立多线路能力”，不是切换时重建
5. 节点侧引入真实 QoS 与固定 IP 执行能力
6. 商业套餐固定 IP 预占、支付成功分配、续费保留、到期释放
7. 后台补齐套餐、线路、QoS、固定 IP 池的配置入口
8. 回归测试覆盖基础 / 高级 / 商业三档真实链路

### 本期不做

- 完全替换 `Marzban`
- 重写整套订阅协议格式
- 多商业 IP 轮换策略
- 商业套餐中途切换地域
- 更复杂的按量计费或带宽包叠加

## 当前现状

### 已有能力

- 客户端套餐读取：
  - `GET /api/client/v1/plans`
- 客户端下单：
  - `POST /api/client/v1/orders`
- 客户端地域与节点：
  - `GET /api/client/v1/vpn/regions`
  - `GET /api/client/v1/vpn/nodes`
  - `POST /api/client/v1/vpn/selection`
- 当前套餐和区域能力已经具备最基础关系：
  - `plans`
  - `vpn_regions`
  - `plan_region_permissions`
- 当前 `Marzban` 集成已经具备：
  - 创建或更新用户
  - 同步到期时间
  - 返回订阅链接

### 现有问题

1. 购买入口仍然是“先选具体 `planCode`”，不是“选套餐属性 + 时长 + 地域”
2. 下单请求没有地域字段，无法把购买时地域选择纳入订单真相
3. 当前套餐权限只到“区域”，没有正式的“线路 / QoS / 固定 IP”层
4. 当前 `Marzban` 用户模型过于简化，只适合续费与订阅输出，不适合多线路预建立
5. 当前 `vpn/selection` 更像本地节点选择，不足以支撑真正的线路激活切换
6. 当前系统没有固定 IP 池和分配状态机
7. 当前系统没有真实 QoS 执行层

## 设计原则

### 1. 用户界面中文化，控制面技术化

面向用户显示：

- 套餐属性：基础套餐 / 高级套餐 / 商业套餐
- 服务时长：1 个月 / 3 个月 / 6 个月 / 12 个月
- 地域：香港 / 新加坡 / 美国

面向控制面的内部对象：

- `product tier`
- `region`
- `line`
- `qos profile`
- `static ip allocation`

结论：

- 前端不显示 `HK_BASIC` 这种内部名
- 后端必须保留可编排的内部线路标识

### 2. 单账号对应单 Marzban 用户

用户切换地域不能靠“切一次建一次用户”。

必须采用：

- 一个账号对应一个 `Marzban user`
- 发货时按当前订阅权益预建立允许访问的线路能力
- 切换地域时只切换“当前激活线路”
- 不重建 `Marzban user`

### 3. QoS 和固定 IP 必须真实执行

本期不接受“只改文案、只改展示”。

因此：

- 基础套餐必须真实限速到 2Mbps
- 高级套餐必须有真实的 10Mbps 封顶和 5Mbps 保障机制
- 商业套餐必须真实绑定固定独享 IP

### 4. 商业套餐固定 IP 生命周期稳定

商业套餐续费时必须：

- 保留原固定 IP
- 不允许静默更换
- 若原 IP 无法保留，必须进入异常处理，不得默认替换

## 商品模型

### 用户可见商品模型

用户购买页只需要三维选择：

1. 套餐属性
2. 服务时长
3. 地域

价格按月价线性累计，不加折扣：

#### 基础套餐

- 1 个月：3U
- 3 个月：9U
- 6 个月：18U
- 12 个月：36U

#### 高级套餐

- 1 个月：8U
- 3 个月：24U
- 6 个月：48U
- 12 个月：96U

#### 商业套餐

- 1 个月：20U
- 3 个月：60U
- 6 个月：120U
- 12 个月：240U

### 内部 SKU 模型

为了兼容当前订单链路，仍然保留 `planCode` 作为实际下单 SKU。

推荐 12 个 SKU：

- 基础套餐 × 1 / 3 / 6 / 12 月
- 高级套餐 × 1 / 3 / 6 / 12 月
- 商业套餐 × 1 / 3 / 6 / 12 月

地域不进入 SKU 维度，而由订单字段单独承载。

这样可以避免 SKU 膨胀为 36 个，同时不破坏现有订单结构。

## 控制面模型

### 1. Region

地域是面向用户的售卖与切换维度：

- 香港
- 新加坡
- 美国

### 2. Line

线路是面向控制面的真实接入能力，示例：

- 香港基础线路
- 香港高级线路
- 香港商业固定 IP 线路
- 新加坡基础线路
- 美国商业固定 IP 线路

用户界面只显示：

- 香港
- 新加坡
- 美国

但系统内部需要把用户选择翻译为对应 `line`。

### 3. QoS Profile

新增正式 QoS profile：

- 基础档：2Mbps 封顶
- 高级档：10Mbps 封顶 + 5Mbps 保障
- 商业档：固定 IP 独享档

### 4. Static IP

商业套餐不直接“写死一个 IP 到套餐上”，而是通过池化与分配表管理：

- IP 池定义
- 预占
- 正式分配
- 续费延续
- 到期释放

## 后端数据模型改造

### 继续保留

- `plans`
- `vpn_regions`
- `vpn_nodes`
- `orders`
- `vpn_subscriptions`

### `plans` 新增字段

- `product_tier`
- `display_name_zh`
- `marketing_title_zh`
- `marketing_description_zh`
- `requires_region_selection`
- `switch_region_allowed`
- `speed_profile_code`
- `static_ip_required`

### 新增表

#### `vpn_lines`

用途：
- 定义每个地域下的真实可编排线路

字段建议：
- `line_id`
- `line_code`
- `display_name_zh`
- `region_id`
- `product_tier`
- `speed_profile_code`
- `line_type` (`SHARED_BASIC`, `SHARED_PREMIUM`, `DEDICATED_STATIC_IP`)
- `switchable`
- `status`

#### `vpn_line_nodes`

用途：
- 把节点映射到线路

字段建议：
- `line_id`
- `node_id`
- `priority`
- `weight`
- `status`

#### `qos_profiles`

用途：
- 存储真实 QoS 参数

字段建议：
- `qos_profile_code`
- `display_name_zh`
- `guaranteed_mbps`
- `max_mbps`
- `burst_mbps`
- `enforcement_mode`
- `status`

#### `static_ip_pools`

用途：
- 商业固定 IP 资源池

字段建议：
- `pool_id`
- `region_id`
- `line_id`
- `ip_address`
- `provider`
- `status`
- `remark`

#### `static_ip_allocations`

用途：
- 商业固定 IP 分配记录

字段建议：
- `allocation_id`
- `account_id`
- `subscription_id`
- `pool_id`
- `region_id`
- `line_id`
- `ip_address`
- `status`
- `reserved_at`
- `activated_at`
- `released_at`
- `expires_at`

### 订单扩展字段

订单必须新增：

- `selected_region_code`
- `product_tier`
- `term_months`

必要原因：

- 购买地域必须成为订单真相
- 不能只从 `planCode` 反推用户购买意图

### 订阅权益扩展字段

订阅或运行时权益至少要扩展：

- `allowed_line_codes`
- `switch_region_allowed`
- `locked_region_code`
- `speed_profile_code`
- `static_ip_allocation_id`

## 订单与订阅流程

### 购买流程

1. 用户在 App 选择：
   - 套餐属性
   - 服务时长
   - 地域
2. App 把三层选择映射为：
   - `planCode`
   - `selectedRegionCode`
   - `productTier`
   - `termMonths`
3. 后端创建订单
4. 支付成功后，后端根据商品属性生成 entitlement
5. entitlement 驱动 `Marzban + 节点控制器` 同步真实控制状态

### 订阅激活规则

#### 基础套餐

- entitlement 包含三地基础线路
- 允许切换地域
- 节点侧执行 2Mbps 封顶

#### 高级套餐

- entitlement 包含三地高级线路
- 允许切换地域
- 节点侧执行 10Mbps 封顶
- 节点控制器执行容量准入，保障 5Mbps

#### 商业套餐

- entitlement 只包含购买地域的商业线路
- 不允许切换地域
- 预占并分配固定 IP
- 续费保留原固定 IP

## Marzban 与控制器协同

### 当前问题

当前 `Marzban` 集成只能：

- 创建用户
- 更新到期时间
- 返回订阅链接

不足以支撑：

- 多线路预建立
- 真实 QoS
- 固定 IP 生命周期

### 目标方案

采用单用户预建立模式：

1. 一个账号只有一个 `Marzban user`
2. 基础 / 高级套餐购买成功后：
   - 预建立所有允许切换的线路能力
3. 商业套餐购买成功后：
   - 只建立所选地域的商业线路
4. `/vpn/selection` 只切换当前激活线路
5. 不因切换地域而重建 `Marzban user`

### 控制器职责

新增 line / qos / static-ip 控制器，职责包括：

- 接收后端下发的 entitlement
- 同步到 `Marzban` 可见线路
- 为节点侧下发：
  - 当前激活线路
  - QoS profile
  - 固定 IP 分配
- 写回执行状态

### `/vpn/selection` 新职责

`/vpn/selection` 不再承担“触发用户重建”职责，只负责：

- 校验当前订阅是否允许目标地域 / 线路
- 更新用户当前激活线路
- 触发控制器切换当前线路
- 回写运行态状态

## Android App 改造

### 购买页

现有购买页是“单选计划卡片”，需要改为单卡片三层选择：

1. 第一层：套餐属性
2. 第二层：服务时长
3. 第三层：地域

同步展示内容：

- 中文套餐介绍
- 当前价格
- 速率规则
- 是否可切换地域
- 是否固定独享 IP

### 文案要求

用户前台全部使用中文：

- 不显示 `lineCode`
- 不显示 `planCode`
- 不显示 `HK_BASIC` 一类内部名

展示示例：

- 香港
- 新加坡
- 美国
- 基础套餐
- 高级套餐
- 商业套餐
- 可切换地域
- 固定独享 IP

### 收银台

收银台不再负责“补选地域”。

收银台必须直接展示：

- 套餐属性
- 服务时长
- 已选地域
- 应付金额

### 购买后地域切换

#### 基础 / 高级套餐

- 允许切换香港 / 新加坡 / 美国
- 切换仅变更当前激活线路

#### 商业套餐

- 显示固定地域
- 不显示地域切换入口
- 展示固定 IP 信息

## Admin Web 改造

后台管理端需要从“套餐 CRUD”升级为“商品与控制面配置入口”。

### 套餐管理

支持配置：

- 套餐属性
- 服务时长
- 中文文案
- 月价 / 总价
- 是否允许切换地域
- 是否需要固定 IP
- 对应 QoS 档位

### 线路管理

支持配置：

- 地域
- 线路中文名
- 对应产品档位
- 对应 QoS profile
- 是否允许切换

### 节点管理

支持配置：

- 节点绑定到哪条线路
- 节点健康状态
- 容量
- 权重

### 固定 IP 池管理

支持配置：

- 每个地域可售 IP 数量
- 空闲 / 已预占 / 已分配 / 故障状态

## 真实控制策略

### 基础套餐

- 用户分配到基础线路
- `tc` 做 2Mbps 封顶
- 不承诺最低速率

### 高级套餐

- 用户分配到高级线路
- `tc` 做 10Mbps 封顶
- 节点容量准入控制保证最低 5Mbps

注意：

- “最低 5Mbps” 不能只靠商品文案宣称
- 若无容量控制，只能叫“目标速率”，不能叫“保底”

### 商业套餐

- 用户绑定商业线路
- 分配固定独享 IP
- 不允许跨地域切换
- 续费保留原固定 IP

### 商业续费异常规则

若原固定 IP 无法继续保留：

- 不允许自动替换为新 IP
- 订单进入异常处理态
- 需人工介入或回滚续费结果

## 回归测试要求

### 基础套餐

- 香港 / 新加坡 / 美国任一地域下单成功
- 购买后可切换三地
- 实测速率不超过 2Mbps
- 切换地域不重建 `Marzban user`

### 高级套餐

- 购买后可切换三地
- 实测速率封顶 10Mbps
- 节点容量允许时满足最低 5Mbps
- 切换地域不重建 `Marzban user`

### 商业套餐

- 下单必须选地域
- 支付后分配固定 IP
- 购买后不可切换地域
- 续费后固定 IP 保持不变

### 后台

- 套餐配置可生效
- 线路配置可生效
- 固定 IP 池库存与分配状态可追踪

### App

- 购买页全中文
- 不暴露技术线路码
- 购买后显示真实地域与固定 IP / 速率信息

## 推荐实施顺序

1. 后端数据模型与订单接口扩展
2. entitlement 与 `Marzban` / controller 协议定稿
3. 节点侧 `QoS / static-ip` 控制器落地
4. Android 购买页三层选择改造
5. Admin Web 配置页扩展
6. 真机与线上回归测试

## 多子代理拆分建议

后续实现阶段建议至少拆成 5 路：

1. 后端 schema / order / entitlement / API
2. `Marzban` 与 line / qos / static-ip 控制器
3. Android App 购买页、收银台、订阅页
4. Admin Web 套餐 / 地域 / 节点 / IP 池配置
5. 回归测试与验收

主线程负责：

- 契约锁定
- 冲突处理
- 合并与最终验收
