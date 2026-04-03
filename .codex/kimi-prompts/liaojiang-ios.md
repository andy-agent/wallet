你在独立 git worktree 中执行 beads 任务 `liaojiang-ios`。

任务目标：
- 产出一份 `admin-web 与 backend admin 契约差异文档`
- 文档用于后续 admin 实施与真实环境部署，不修改业务代码

你只能修改：
- `docs/**`

不要修改：
- `code/backend/**`
- `code/admin-web/**`
- Android
- beads 数据
- 构建产物

输出文件要求：
- 新建 `docs/ADMIN_WEB_CONTRACT_GAP_REPORT.md`

文档必须包含：
1. admin-web 当前页面清单
2. admin-web 当前调用的 API 清单
3. backend 当前实际存在的 admin API 清单
4. 与 `final_engineering_delivery_package` 中 Admin 页面/接口规格的差异
5. 缺失接口分级：
   - P0 当前页面完全不可联调
   - P1 可展示但不可操作
   - P2 字段/命名漂移
6. 推荐后续实施顺序

来源优先级：
- `code/admin-web/src/**`
- `code/backend/src/**`
- `final_engineering_delivery_package/05_ia_and_page_spec.md`
- `final_engineering_delivery_package/07_api_spec.md`
- `final_engineering_delivery_package/08_openapi_v1.yaml`

验收标准：
- 文档能直接作为后续 beads 任务拆解输入
- 不能泛讲，要写出真实文件路径、接口路径、差异点

执行要求：
- 最小改动
- 完成后本地提交一次 commit，不要 push

最终回复格式：
1. changed_files
2. summary
3. commit_sha
