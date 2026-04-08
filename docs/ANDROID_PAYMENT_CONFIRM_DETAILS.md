# Android 实机支付确认信息采集（设备 `ba2b016`）

## 执行约束
- 执行时间：2026-04-08（Asia/Shanghai）
- 未输入任何账号密码/钱包密码
- 未发起任何链上交易
- 仅采集非敏感、页面可见信息

## 当前可确认信息（真实页面）
1. 钱包确认页可达性：
- 已触发到“请输入钱包密码”页面，说明支付确认后续需要钱包密码二次确认。
- 该页面未展示订单号、链路、收款地址等明文信息。

2. 支付相关可见记录（Pay 页最近交易）：
- 记录1：`退款 / Mastercard (2477) / +0.7 USD / 2026-04-07 15:32`
- 记录2：`银行卡消费 / Mastercard (2477) / -0.7 USD / 2026-04-07 15:31`

## 本次目标字段提取结果（用于 live chain test）
- 订单号：未在可见页面提取到
- 资产/网络：未在可见页面提取到（未出现如 USDT-TRC20/ERC20 等链路标识）
- 应付金额：未在链上支付确认页提取到；仅见卡交易金额 `0.7 USD` 记录
- 收款地址：未在可见页面提取到
- 钱包确认页细节（可替代）：`请输入钱包密码`（进入二次确认拦截页）

## 截图与页面抓取文件
- `/tmp/liaojiang-screens/liaojiang_payment_current.png`
- `/tmp/liaojiang-screens/liaojiang_payment_current.xml`
- `/tmp/liaojiang-screens/liaojiang_payment_after_ack.png`
- `/tmp/liaojiang-screens/liaojiang_payment_after_ack.xml`
- `/tmp/liaojiang-screens/liaojiang_pay_tab.png`
- `/tmp/liaojiang-screens/liaojiang_pay_tab.xml`
- `/tmp/liaojiang-screens/liaojiang_tx_detail.png`
- `/tmp/liaojiang-screens/liaojiang_tx_detail.xml`

## 结论
在不触碰凭据、不发送交易的前提下，当前设备可复现到钱包密码确认门槛，但未能到达可读取“订单号/链路/收款地址”的最终链上支付确认明细页。
