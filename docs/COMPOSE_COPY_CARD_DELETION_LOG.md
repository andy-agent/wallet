# Compose Copy Card Deletion Log

用于记录逐次删除的页面文案/卡片，便于后续回溯、恢复或复盘。

## Entries

### 2026-04-16 00:36:06 +0800

- File: [ReceivePage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/ReceivePage.kt)
- Page: `Receive`
- Removed:
  - Hero `当前网络` card stats item: `地址尾号`
  - Hero `当前网络` card stats item: `校验状态`
  - `收款码` card trailing status text, including cases such as `已配置收款地址`
- Change detail:
  - `P2CoreHeroValueCard(... stats = emptyList())`
  - `P2CoreQrAddressCard(... status = null)`
- Reason:
  - User requested batch deletion of explanatory copy cards and status text on the receive page.

### 2026-04-16 00:36:06 +0800

- File: [InviteSharePage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/InviteSharePage.kt)
- Page: `InviteShare`
- Removed:
  - Hero stats item: `分享渠道 - 系统分享`
  - Hero stats item: `状态 - 可转发`
  - QR card trailing status text: `Share Ready`
- Change detail:
  - `P2CoreHeroValueCard(... stats = emptyList())`
  - `P2CoreQrAddressCard(... status = null)`
- Reason:
  - User requested removing share-channel and status cards from the share page.

- File: [InviteCenterPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/InviteCenterPage.kt)
- Page: `InviteCenter`
- Changed:
  - Copy invite-code actions now show visible success/empty-state toast feedback
- Reason:
  - User requested copy interactions to align with receive-page feedback.

- File: [InviteSharePage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/InviteSharePage.kt)
- Page: `InviteShare`
- Changed:
  - `复制链接` and `复制邀请码` actions now show visible success/empty-state toast feedback
- Reason:
  - User requested copy interactions to align with receive-page feedback.

### 2026-04-16 00:36:06 +0800

- File: [InviteCenterPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/InviteCenterPage.kt)
- Page: `InviteCenter`
- Changed:
  - `分享推广链接` 从页面跳转动作改为系统分享动作
  - `我的邀请码` 卡片不再显示长账号标识/状态尾标
- Reason:
  - User decided to keep only InviteCenter page and use it as the single share action entry.

- File: [P2CoreNavGraph.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/navigation/P2CoreNavGraph.kt)
- Page: `InviteShare route`
- Changed:
  - `invite_share` 路由改为重定向回 `invite_center`
- Reason:
  - User decided to keep only InviteCenter as the in-app entry page.
