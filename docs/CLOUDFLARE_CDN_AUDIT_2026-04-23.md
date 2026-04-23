# Cloudflare CDN Audit

更新时间：2026-04-23

## 结论

线上入口 `api.residential-agent.com` 已经经过 Cloudflare 代理，但“经过 Cloudflare”不等于“已经被 Cloudflare 缓存”。

本次核对结果：

- `downloads/cryptovpn-android.apk`：已经命中 Cloudflare 缓存。
- `api/client/v1/app-versions/latest`：经过 Cloudflare，但当前是 `DYNAMIC`，未形成边缘缓存。
- `invite?code=...`：经过 Cloudflare，但当前是 `DYNAMIC`。
- `api/client/v1/wallet/chains`：经过 Cloudflare，但当前是 `DYNAMIC`，且因为鉴权请求，本就不适合 CDN 公共缓存。

## 线上头部快照

### 1. APK 下载

请求：

```bash
curl --http1.1 -I 'https://api.residential-agent.com/downloads/cryptovpn-android.apk?v=2.0.17.09'
```

关键响应头：

```text
server: cloudflare
cache-control: max-age=14400
cf-cache-status: HIT
age: 1385
```

### 2. 最新版本接口

请求：

```bash
curl --http1.1 -I 'https://api.residential-agent.com/api/client/v1/app-versions/latest?platform=android&channel=official'
```

关键响应头：

```text
server: cloudflare
cf-cache-status: DYNAMIC
```

### 3. 邀请落地页

请求：

```bash
curl --http1.1 -I 'https://api.residential-agent.com/invite?code=FLOW2026'
```

关键响应头：

```text
server: cloudflare
cf-cache-status: DYNAMIC
```

### 4. 钱包链接口

请求：

```bash
curl --http1.1 -I 'https://api.residential-agent.com/api/client/v1/wallet/chains'
```

关键响应头：

```text
server: cloudflare
cf-cache-status: DYNAMIC
http/2 401
```

## 本次代码侧落地

本轮已在源站公开只读接口补充 `Cache-Control` 头，便于 Cloudflare 按规则接管边缘缓存：

- `GET /api/client/v1/app-versions/latest`
- `GET /api/client/v1/plans`
- `GET /api/client/v1/market/*`

当前代码只解决“源站声明缓存策略”。

若 Cloudflare 仪表盘未配置缓存规则，API JSON 仍可能继续表现为 `DYNAMIC`。

## 建议的 Cloudflare Cache Rules

### 应开启边缘缓存

1. `/api/client/v1/app-versions/latest*`
2. `/api/client/v1/plans*`
3. `/api/client/v1/market/*`

### 不应开启公共 CDN 缓存

1. `/api/client/v1/auth/*`
2. `/api/client/v1/wallet/*`
3. `/api/client/v1/orders/*`
4. 任何携带用户鉴权语义、余额、订单、钱包地址、私有资产状态的接口

## 推荐规则策略

### 版本接口

- 浏览器缓存：`60s`
- 边缘缓存：`600s`
- stale-while-revalidate：`3600s`

### 套餐接口

- 浏览器缓存：`60s`
- 边缘缓存：`300s`
- stale-while-revalidate：`900s`

### 市场接口

- 浏览器缓存：`15s`
- 边缘缓存：`60s`
- stale-while-revalidate：`120s`

## 仍待运维动作

1. 在 Cloudflare 面板或 API 中为以上公共只读路径配置 Cache Rules。
2. 上线后再次核对 `cf-cache-status` 是否从 `DYNAMIC` 变成 `HIT` 或 `MISS` 后可回暖。
3. 对钱包、订单、鉴权接口继续维持 local-first + source-of-truth 模式，不走公共 CDN 缓存。
