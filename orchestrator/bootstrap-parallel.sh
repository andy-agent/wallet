#!/usr/bin/env bash
set -euo pipefail
source "$(dirname "$0")/lib.sh"

require_cmd git

TASK_FILE_INPUT="${1:?usage: bootstrap-parallel.sh <task-yaml>}"
TASK_FILE="$(task_file_abs "$TASK_FILE_INPUT")"

ROOT="$(git_root)"
PROOT="$(project_root)"
TASK_ID="$(task_id_from_yaml "$TASK_FILE")"
KIMI_BRANCH="$(branch_from_yaml "$TASK_FILE")"
KIMI_WT_REL="$(worktree_from_yaml "$TASK_FILE")"
BASE_BRANCH="$(base_branch_from_yaml "$TASK_FILE")"
CODEX_BRANCH="$(codex_branch_from_yaml "$TASK_FILE")"
CODEX_WT_REL="$(codex_worktree_from_yaml "$TASK_FILE")"

if [[ -z "$TASK_ID" || -z "$KIMI_BRANCH" || -z "$KIMI_WT_REL" ]]; then
  echo "[ERR] task yaml missing required fields: task_id/branch/worktree" >&2
  exit 1
fi

KIMI_WT_ABS="$(resolve_from_git_root "$KIMI_WT_REL")"
CODEX_WT_ABS="$(resolve_from_git_root "$CODEX_WT_REL")"
ART_DIR="$(artifacts_abs_from_task "$TASK_ID")"

mkdir -p "$ART_DIR"

fetch_origin_if_exists "$ROOT"

ensure_branch_from_base "$CODEX_BRANCH" "$BASE_BRANCH"
ensure_branch_from_base "$KIMI_BRANCH" "$BASE_BRANCH"

ensure_worktree "$CODEX_WT_ABS" "$CODEX_BRANCH"
ensure_worktree "$KIMI_WT_ABS" "$KIMI_BRANCH"

python3 - "$TASK_FILE" "$ART_DIR/bootstrap.json" "$ROOT" "$PROOT" "$CODEX_BRANCH" "$CODEX_WT_ABS" "$KIMI_BRANCH" "$KIMI_WT_ABS" <<'PY'
import json,sys,datetime
(task_file,out,git_root,project_root,codex_branch,codex_wt,kimi_branch,kimi_wt)=sys.argv[1:]
obj={
  "timestamp": datetime.datetime.now(datetime.timezone.utc).isoformat(),
  "task_file": task_file,
  "git_root": git_root,
  "project_root": project_root,
  "codex_branch": codex_branch,
  "codex_worktree": codex_wt,
  "kimi_branch": kimi_branch,
  "kimi_worktree": kimi_wt,
}
with open(out,"w",encoding="utf-8") as fh:
  json.dump(obj,fh,ensure_ascii=False,indent=2)
PY

echo "[OK] bootstrap done"
echo "  task        : $TASK_ID"
echo "  codex branch: $CODEX_BRANCH"
echo "  codex wt    : $CODEX_WT_ABS"
echo "  kimi branch : $KIMI_BRANCH"
echo "  kimi wt     : $KIMI_WT_ABS"
echo "  artifacts   : $ART_DIR"
