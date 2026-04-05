你在独立 git worktree 中为 `liaojiang-a8m` 做预验收分析。

任务目标：
- 不改代码，只基于当前仓库判断该 bug 的最小修复面与可执行验收命令
- 为主编排器提供后续 acceptance checklist

你只能读取：
- `code/Android/V2rayNG/**`
- `AGENTS.md`
- `code/Android/AGENTS.md`
- beads 任务信息如果命令可用

不要修改任何文件，不要提交 commit。

分析重点：
- `PaymentRepository.kt` 当前 token 过期判断是否确实只支持不带毫秒格式
- 当前 Android 测试基建下，哪种最小单元测试策略最稳妥
- 哪个 Gradle 任务最适合作为本任务验收命令
- 是否还存在与 `liaojiang-a8m` 紧邻、但不应在本任务顺手修的风险点

输出要求：
- 只给结论，不要做实现
- 如果你认为需要提炼纯函数/helper 才好测，直接说明原因

最终回复格式：
1. root_cause_confirmation
2. recommended_fix_shape
3. recommended_test_strategy
4. recommended_verification_commands
5. adjacent_risks
