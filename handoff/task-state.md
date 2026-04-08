# Task State

- Updated At: 2026-04-08 20:31 +08:00
- Repository: /Users/cnyirui/git/projects/liaojiang
- Next Task: `liaojiang-rcb.20.2`（执行 funding 后的 refresh-status 与开通验收）
- Counts: `open=2` `in_progress=2` `closed=218` `ready=0`
- Dirty Files:
  - .codex/bd-orchestrator-state.json
  - .codex/recovery-context.md
  - .codex/recovery-state.json

## Resume Commands
```bash
bd ready --json
bd list --status=in_progress --json
bd stats
git status --short
```

## Current Verification

- Android 主线已全部关闭
  - `liaojiang-4j0.22.13.8` 已验收关闭：
    - 主线提交：`3a7aa8ba` `Add market read API module`
    - `cd code/backend && npm run test:e2e -- market.e2e-spec.ts` 通过
  - `liaojiang-4j0.22.13.9` 已验收关闭：
    - 主线提交：`cedfb122` `docs: record market api deployment status`
    - 现网验证通过：
      - `GET https://api.residential-agent.com/api/client/v1/market/overview`
      - `GET https://api.residential-agent.com/api/client/v1/market/instruments/crypto:bitcoin`
      - `GET https://api.residential-agent.com/api/client/v1/market/instruments/crypto:bitcoin/candles?timeframe=1d&limit=5`
  - `liaojiang-4j0.22.13.10` 已验收关闭：
    - 主线提交：`0aff28f2` `Wire Android market pages to live market API`
    - `cd code/Android/V2rayNG && env JAVA_HOME='/Applications/Android Studio.app/Contents/jbr/Contents/Home' ./gradlew :app:compilePlaystoreDebugKotlin` 通过
    - 运行路径中的 `marketSample*` 依赖已移除
- `liaojiang-rcb.20.1` 已验收关闭
  - 主线提交：`35ff28a9` `docs: capture live payment guide for rcb20`
  - `docs/RCB20_LIVE_PAYMENT_GUIDE.md` 已记录 fresh order 的非敏感付款指引：
    - `orderNo = ORD-1775650877315-64DA3EFB`
    - rail = `SOLANA`
    - asset = `USDT`
    - amount = `9.990000`
    - collection address 已记录
    - 后续需要的 `networkCode` / `txHash` 输入已记录

## Remaining Work

- `liaojiang-rcb.20.2`
  - 状态：`BLOCKED`
  - blocker：
    - 需要对 `docs/RCB20_LIVE_PAYMENT_GUIDE.md` 中的 fresh order 完成一次真实、人类控制的付款
    - 需要 resulting `txHash`
  - 付款完成后，主控应立即执行：
    - `submit-client-tx`
    - `refresh-status`
    - subscription readback
    - VPN issuance / status readback
- `liaojiang-rcb.20`
  - 等 `20.2` 完成后才能形成 PASS/NEEDS_WORK 结论
- `liaojiang-rcb`
  - 等 `20.2` / `20` 完成后才能最终关闭
