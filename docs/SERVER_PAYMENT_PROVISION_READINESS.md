# 服务器侧支付->订单识别->确认->VPN开通 Readiness（非 Mock）

更新时间：2026-04-08 18:40 CST

## 1. 结论（当前可用于真实联调）

- API 主入口 `https://api.residential-agent.com` 在线，`cryptovpn-backend.service` 为 `active`。
- 链侧服务在线：
  - `https://sol.residential-agent.com/api/healthz` 返回 `healthy`（mainnet slot 正常推进）。
  - `https://usdt.residential-agent.com/api/healthz` 返回 `healthy + connected`（TRON 主网区块高度正常）。
- 订单主链路仍在真实流量下可用（可持续创建订单与获取 `payment-target`，日志持续出现 `POST /api/client/v1/orders` 与 `GET .../payment-target` 200/201）。
- DB/Redis 通过服务器三本地隧道端口可达：
  - `127.0.0.1:15432`（PostgreSQL）`pg_isready` 为 accepting connections。
  - `127.0.0.1:16379`（Redis）端口可连，返回 `NOAUTH`（说明服务在线且开启鉴权）。

## 2. 本次实测范围与结果

### 2.1 API 服务器（154.37.208.72）

- `systemctl is-active cryptovpn-backend.service` -> `active`。
- `GET http://127.0.0.1:3000/api/healthz` -> `OK`。
- 公网 `GET https://api.residential-agent.com/api/healthz` -> `OK`。
- 日志抽样（2026-04-08）可见真实客户端持续调用：
  - `POST /api/client/v1/orders` -> 201
  - `GET /api/client/v1/orders/{orderNo}/payment-target` -> 200

### 2.2 链侧服务（38.58.59.119）

- `sol-agent`、`usdt-agent` systemd 均为 `active`。
- 本机探测：
  - `sol-agent` 在 `127.0.0.1:4000` 正常返回 `/api/healthz`。
  - `usdt-agent` 实际监听 `*:4001`（非 4010），`/api/healthz` 正常返回 `healthy + connected`。
- 公网探测：
  - `sol.residential-agent.com/api/healthz` 正常。
  - `usdt.residential-agent.com/api/healthz` 正常。

### 2.3 DB/Redis 落点与可达性（38.58.59.142 经反向隧道）

- 在服务器三确认本地监听：
  - `127.0.0.1:15432`
  - `127.0.0.1:16379`
- `pg_isready -h 127.0.0.1 -p 15432` -> accepting connections。
- Redis raw ping 返回 `-NOAUTH`（符合开启鉴权预期，不是宕机）。

## 3. 本次修复动作

- 本轮未执行线上配置变更。
- 原因：当前服务健康、核心链路可用，未发现可安全、必要且收益明确的“即时修复项”。

## 4. 剩余阻塞/风险

1. 服务器二（`38.58.59.142`）文档中记录的 root 密码登录本次未通过（Permission denied），导致无法直接在该机做容器内深度核验（仅能通过服务器三隧道侧验证可达性）。
2. 本轮未复跑“新真实链上交易 txHash -> `submit-client-tx` -> `refresh-status` -> `COMPLETED` -> 开通凭据写回”的全链路闭环；当前依据为：
   - 现网服务健康与链侧连通性；
   - 实时订单创建/支付目标接口可用；
   - 历史真实链路完成记录（此前已有 `COMPLETED` 证据）。
3. 建议后续补一条可复用的“全链路一次性验证脚本”（使用专用测试钱包和小额交易）以降低人工回归成本。

## 5. Readiness 判定

- 非 mock 服务器 readiness：**基本就绪（Ready with minor blockers）**。
- 影响上线/联调的主要 blocker：**服务器二直连凭据与运维入口一致性**（不影响当前 API 机通过隧道访问 DB/Redis，但影响排障效率与可审计性）。
