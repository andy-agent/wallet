你在独立 git worktree 中执行 `liaojiang-f93.5`。

任务目标：
- 将 `code/admin-web` 部署到真实环境
- 完成后台最小真实 smoke

部署策略固定为：
- 部署目标：服务器三 `154.37.208.72`
- 站点入口：`https://api.residential-agent.com/admin/`
- 不新建 admin 子域
- 保持 backend API 继续使用同源 `/api`

你只能修改：
- `code/admin-web/**`
- `code/deploy/**`（若确实需要补部署说明）
- 服务器三上的真实部署文件（通过 SSH）

不要修改：
- `code/backend/**`
- Android
- `code/sol-agent/**`
- `code/backend-chain-usdt/**`
- beads 数据

已知最新前提：
- backend admin 最小域已在主线：auth/dashboard/accounts/orders/plans/withdrawals/audit/system-configs/legal/versions/regions/nodes
- admin-web 已在主线对齐到现有 backend admin 契约
- 服务器三 backend 已由 `cryptovpn-backend.service` 正式管理
- 现网 API 健康正常

部署要求：
1. 本地构建 `code/admin-web`
2. 把静态产物部署到服务器三
3. 配置 nginx 让 `/admin/` 指向 admin-web 静态文件
4. 不破坏现有 `/api/*` 代理
5. 完成后验证：
   - `https://api.residential-agent.com/admin/` 可访问
   - `POST /api/admin/v1/auth/login` 返回 token
   - 至少确认 dashboard / orders / withdrawals 的真实 API 返回 200
   - 若能进一步完成浏览器级 smoke 更好，但不是唯一验收条件

验收标准：
- `pnpm --dir code/admin-web build`
- 真实环境 `/admin/` 可访问
- 不破坏 `https://api.residential-agent.com/api/healthz`
- 输出实际部署位置、nginx 变更点、已验证 URL

执行要求：
- 最小改动
- 完成后本地提交一次 commit（如果修改了仓库文件），不要 push

最终回复格式：
1. changed_files
2. deployment_steps
3. verification_commands
4. verification_results
5. residual_risks
6. commit_sha_or_no_commit
