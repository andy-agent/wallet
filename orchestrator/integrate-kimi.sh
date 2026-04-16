#!/usr/bin/env bash
set -euo pipefail
source "$(dirname "$0")/lib.sh"

TASK_FILE_INPUT="${1:?usage: integrate-kimi.sh <task-yaml>}"
TASK_FILE="$(task_file_abs "$TASK_FILE_INPUT")"

require_cmd git
require_cmd python3

ROOT="$(git_root)"
PROOT="$(project_root)"
PROJ_SUBDIR="$(project_subdir_from_git_root)"
TASK_ID="$(task_id_from_yaml "$TASK_FILE")"
KIMI_BRANCH="$(branch_from_yaml "$TASK_FILE")"
CODEX_BRANCH="$(codex_branch_from_yaml "$TASK_FILE")"
CODEX_WT_ABS="$(resolve_from_git_root "$(codex_worktree_from_yaml "$TASK_FILE")")"

if [[ ! -d "$CODEX_WT_ABS" ]]; then
  echo "[ERR] codex worktree not found: $CODEX_WT_ABS" >&2
  exit 1
fi

TMP_BRANCH="codex/tmp-int-${TASK_ID}-$(date +%s)"
TMP_WT="$(mktemp -d "${ROOT}/../wt-int-${TASK_ID}-XXXX")"
trap 'git -C "$ROOT" worktree remove --force "$TMP_WT" >/dev/null 2>&1 || true; git -C "$ROOT" branch -D "$TMP_BRANCH" >/dev/null 2>&1 || true' EXIT

fetch_origin_if_exists "$ROOT"

git -C "$ROOT" worktree add -b "$TMP_BRANCH" "$TMP_WT" "$CODEX_BRANCH"

git -C "$TMP_WT" merge --no-ff "$KIMI_BRANCH" -m "merge: ${TASK_ID} from kimi"

INTEGRATION_CMDS=()
while IFS= read -r cmd; do
  [[ -n "$cmd" ]] || continue
  INTEGRATION_CMDS+=("$cmd")
done < <(yaml_get_list integration_commands "$TASK_FILE")

if [[ ${#INTEGRATION_CMDS[@]} -eq 0 ]]; then
  INTEGRATION_CMDS=(
    "npm install"
    "npm run build"
    "npm run ci:real-run"
    "npm run auth:regression:matrix"
    "npm run local:real-run"
  )
fi

RUN_DIR="$TMP_WT"
if [[ -n "$PROJ_SUBDIR" && -d "$TMP_WT/$PROJ_SUBDIR" ]]; then
  RUN_DIR="$TMP_WT/$PROJ_SUBDIR"
elif [[ -n "$PROJ_SUBDIR" ]]; then
  echo "[WARN] project subdir not found in temp worktree: $TMP_WT/$PROJ_SUBDIR"
  echo "[WARN] fallback to repo root: $TMP_WT"
fi

echo "[INFO] integration run dir: $RUN_DIR"

for cmd in "${INTEGRATION_CMDS[@]}"; do
  echo "[RUN] $cmd"
  (cd "$RUN_DIR" && bash -lc "$cmd")
done

# Fast-forward codex branch only after successful verification.
git -C "$CODEX_WT_ABS" fetch "$TMP_WT" "$TMP_BRANCH"
git -C "$CODEX_WT_ABS" checkout "$CODEX_BRANCH"
git -C "$CODEX_WT_ABS" merge --ff-only "$TMP_BRANCH"

echo "[OK] integration passed"
echo "  updated branch: $CODEX_BRANCH"
echo "  merged branch : $KIMI_BRANCH"
