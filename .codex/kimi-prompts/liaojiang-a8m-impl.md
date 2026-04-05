你在独立 git worktree 中执行 `liaojiang-a8m`。

任务目标：
- 修复 Android 真实登录态下 token 过期时间解析错误
- 让 `PaymentRepository` 能正确识别带毫秒和不带毫秒的 ISO8601 过期时间
- 补最小单元测试覆盖解析与过期判定

你只能修改：
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/**`
- `code/Android/V2rayNG/app/src/test/**`
- 如测试确实需要，可最小修改 `code/Android/V2rayNG/app/build.gradle.kts`

不要修改：
- beads 数据
- `.codex/**`
- `docs/**`
- `handoff/**`
- `code/backend/**`
- `code/admin-web/**`
- `code/sol-agent/**`
- 构建产物

已知事实：
- 任务描述已在 beads：`liaojiang-a8m`
- 当前根因位于：
  - `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/repository/PaymentRepository.kt`
- 当前 `parseIsoDate()` 只按 `yyyy-MM-dd'T'HH:mm:ss'Z'` 解析
- 真实 API 返回示例会包含毫秒：
  - `2026-04-04T22:12:11.788Z`
- `isTokenExpired()` 解析失败后会返回 true，导致 `Purchase Plan` 仍走 `LoginActivity`

范围要求：
- 做最小、可回归、可测试修复
- 优先保持现有存储键和登录流程不变
- 如果为了测试可达性需要把纯解析逻辑提炼成 package-private/internal helper，可以做，但不要扩散重构
- 不要顺手改 `PaymentActivity` 或其他非本任务必需逻辑，除非你确认这是同一个阻断点且改动极小

验收标准：
- `PaymentRepository` 对带毫秒和不带毫秒的 UTC ISO8601 都能正确解析
- token 未过期时 `isTokenExpired()` 不会误判为 true
- 补至少一组单元测试覆盖：
  - 带毫秒时间串
  - 不带毫秒时间串
  - 过期判定 buffer 语义
- 优先运行并通过你新增/相关的 Android unit tests

建议验证命令：
- `cd /Users/cnyirui/.config/superpowers/worktrees/liaojiang/codex-liaojiang-a8m-kimi`
- `JAVA_HOME=/opt/homebrew/opt/openjdk@17 ./gradlew :app:testFdroidDebugUnitTest`

执行要求：
- 最小修改
- 完成后本地提交一次 commit，不要 push
- 最终回复必须包含实际 commit sha

最终回复格式：
1. changed_files
2. verification_commands
3. verification_results
4. residual_risks
5. commit_sha
