# Orchestrator (Codex Controller + Kimi Worker)

## Scope
- Codex: orchestration, ownership verification, integration and regression gate.
- Kimi: code changes inside Kimi-owned paths only.
- VS Code: observer only (diff/log/session continuation).

## Scripts
- `bootstrap-parallel.sh <task-yaml>`: create branches/worktrees/artifact folder.
- `dispatch-kimi.sh [--driver cli|stub] <task-yaml>`: dispatch task to Kimi CLI (`cli`) or run pipeline stub.
- `collect-kimi.sh [--push] <task-yaml>`: collect artifacts and branch status from Kimi worktree.
- `verify-ownership.sh <task-yaml>`: enforce allow/deny rules.
- `integrate-kimi.sh <task-yaml>`: merge in temp branch + verify + ff-only merge into codex branch.

## Recommended flow
```bash
./orchestrator/bootstrap-parallel.sh tasks/FEAT-001-kimi.yaml
./orchestrator/dispatch-kimi.sh --driver cli tasks/FEAT-001-kimi.yaml
./orchestrator/collect-kimi.sh --push tasks/FEAT-001-kimi.yaml
./orchestrator/verify-ownership.sh tasks/FEAT-001-kimi.yaml
./orchestrator/integrate-kimi.sh tasks/FEAT-001-kimi.yaml
```

## Smoke flow (no real Kimi CLI execution)
```bash
./orchestrator/bootstrap-parallel.sh tasks/SMOKE-kimi.yaml
./orchestrator/dispatch-kimi.sh --driver stub --stub-change handoff/SMOKE-kimi.md tasks/SMOKE-kimi.yaml
./orchestrator/collect-kimi.sh tasks/SMOKE-kimi.yaml
./orchestrator/verify-ownership.sh tasks/SMOKE-kimi.yaml
./orchestrator/integrate-kimi.sh tasks/SMOKE-kimi.yaml
```
