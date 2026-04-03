你在独立 git worktree 中执行 `liaojiang-f93.3`。

任务目标：
- 在 `code/backend` 中实现 backend admin 财务与治理域的最小接口
- 本轮覆盖：
  - withdrawals
  - audit
  - system-configs
  - legal
  - versions
  - regions
  - nodes

你只能修改：
- `code/backend/**`

不要修改：
- `code/admin-web/**`
- Android
- `code/sol-agent/**`
- `code/backend-chain-usdt/**`
- beads 数据
- 构建产物

事实源优先级：
1. `docs/ADMIN_WEB_CONTRACT_GAP_REPORT.md`
2. `final_engineering_delivery_package/05_ia_and_page_spec.md`
3. `final_engineering_delivery_package/07_api_spec.md`
4. `final_engineering_delivery_package/08_openapi_v1.yaml`
5. 当前 backend 结构

范围要求：
- 只做最小可联调后端接口，不做复杂写工作流的全部细节
- 优先满足后台页面需要的读接口，再补必要的最小动作接口
- 保持 admin 模块结构清晰，便于后续 `f93.4` 对接 admin-web

建议最小接口面：
- `GET /api/admin/v1/withdrawals`
- `GET /api/admin/v1/audit-logs`
- `GET /api/admin/v1/system-configs`
- `GET /api/admin/v1/legal-documents`
- `GET /api/admin/v1/app-versions`
- `GET /api/admin/v1/vpn/regions`
- `GET /api/admin/v1/vpn/nodes`

如有余力再补最小写接口，但不要影响范围控制。

验收标准：
- `pnpm --dir code/backend typecheck`
- `pnpm --dir code/backend build`
- 如补测试则一起通过
- 上述最小 admin 接口真实存在，且受 admin 鉴权保护

执行要求：
- 最小修改
- 不改已完成的 `f93.1` / `f93.2` 范围
- 完成后本地提交一次 commit，不要 push

最终回复格式：
1. changed_files
2. verification_commands
3. verification_results
4. residual_risks
5. commit_sha
