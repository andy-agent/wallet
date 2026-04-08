# Android 真机下单流程非敏感预检（liaojiang-4j0.22.6.2.1）

更新时间：2026-04-08（Asia/Shanghai）
设备：`ba2b016`（`com.v2ray.ang.fdroid`，versionName `2.0.17`）
约束：仅使用非敏感证据；不读取、不打印、不落盘任何凭证值。

## 已通过检查（Passed Checks）

1. 公共后端健康检查可达  
   - `GET https://api.residential-agent.com/health`  
   - 结果：`HTTP/2 200`，响应体为网关标识文本（`cryptovpn backend gateway`）。

2. 公共套餐端点可达（公开访问层）  
   - `GET https://api.residential-agent.com/client/v1/plans`  
   - 结果：`HTTP/2 200`，当前返回同网关标识文本（`cryptovpn backend gateway`），至少证明域名、TLS、反向代理与路径可达。

3. 真机应用可启动并进入业务主壳，不是“立即崩溃”  
   - 启动后 `dumpsys activity` 显示前台为 `com.v2ray.ang.fdroid/com.v2ray.ang.ui.ComposeLauncherAlias`（`ComposeContainerActivity`）。
   - `logcat` 未见 `Process: com.v2ray.ang.fdroid` 相关 `FATAL EXCEPTION`。

4. 订单相关页面可在当前构建中进入且未立即崩溃  
   - 通过 UI 操作进入“订单列表”，`uiautomator dump` 可见：`订单列表`、`待支付`、`已完成`、`暂无 VPN 订单` 等文本。
   - 同步检查 `logcat`：未见该包名进程崩溃。

5. 登录态“保留/可直接进入业务壳”的安全信号存在  
   - 应用私有目录仅查看文件名：`shared_prefs/payment_prefs.xml`、`files/mmkv/{MAIN,SETTING,SUB}`。  
   - 启动后直接进入业务壳与业务页（非显式登录页），可作为“存在本地会话/状态缓存”的非敏感侧证。

## 不确定项（Uncertain Checks）

1. `https://api.residential-agent.com/client/v1/plans` 当前返回网关标识文本，而非明确套餐 JSON。  
   - 可达性已验证通过；但“真实套餐数据契约是否已对公网生效”仍不确定。

2. “套餐页（plans）到下单页（order/payment）”完整前向跳转在本轮采集中稳定性不足。  
   - 证据里可见套餐入口与订单页可达，但部分操作会落到“正在检查更新...”启动检查页，导致 plans 页面稳定复现不足。

3. “是否为真实已登录用户会话”无法仅靠非敏感信号做强结论。  
   - 当前仅能确认“存在本地状态文件 + 页面行为非强制登录拦截”；无法在不触碰敏感值的前提下证明 token 有效性与账号身份。

## 阻塞项（Blockers）

1. 无“必须新增产品输入”的硬阻塞。  
2. 但建议在执行真实支付前补一个极小探针（不含凭证打印）：  
   - 在 app 内触发一次 plans 拉取并仅记录 `HTTP code` 与响应 `Content-Type`；  
   - 若返回非 JSON 或非 2xx，先修复网关路由再进入真实支付尝试。

## 结论（Go / No-Go for liaojiang-4j0.22.6.2.2）

结论：**有条件 Go（Proceed with caution）**。  

依据：  
- 公网后端入口与关键路径可达；  
- 真机应用可启动、订单相关页面可进入且未观察到立即崩溃；  
- 未发现需要新增产品决策才能继续的阻断。  

风险提示：  
- `plans` 公网返回体与预期业务 JSON 不一致（当前为网关文本），以及 plans 跳转稳定性仍有不确定性。  
- 因此建议在 `liaojiang-4j0.22.6.2.2` 开始时先做一次“只采集状态码/类型”的快速探针，再执行真实登录/下单/支付尝试。
