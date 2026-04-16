#!/usr/bin/env bash
set -euo pipefail
source "$(dirname "$0")/lib.sh"

usage() {
  cat <<USAGE
usage: collect-kimi.sh [--push] <task-yaml>
USAGE
}

DO_PUSH=0
if [[ "${1:-}" == "--push" ]]; then
  DO_PUSH=1
  shift
fi

TASK_FILE_INPUT="${1:?missing task-yaml}"
TASK_FILE="$(task_file_abs "$TASK_FILE_INPUT")"

require_cmd git
require_cmd python3

ROOT="$(git_root)"
TASK_ID="$(task_id_from_yaml "$TASK_FILE")"
KIMI_BRANCH="$(branch_from_yaml "$TASK_FILE")"
KIMI_WT_ABS="$(resolve_from_git_root "$(worktree_from_yaml "$TASK_FILE")")"
BASE_BRANCH="$(base_branch_from_yaml "$TASK_FILE")"
ART_DIR_ABS="$(artifacts_abs_from_task "$TASK_ID")"
ART_DIR_REL="$(artifacts_rel_from_task "$TASK_ID")"
KIMI_ART_DIR="$KIMI_WT_ABS/$ART_DIR_REL"

mkdir -p "$ART_DIR_ABS"

echo "[INFO] collecting from $KIMI_WT_ABS"

fetch_origin_if_exists "$KIMI_WT_ABS"

git -C "$KIMI_WT_ABS" status --short || true
git -C "$KIMI_WT_ABS" log --oneline -n 3 || true

BASE_REF=""
if git -C "$KIMI_WT_ABS" show-ref --verify --quiet "refs/remotes/origin/$BASE_BRANCH"; then
  BASE_REF="origin/$BASE_BRANCH"
elif git -C "$KIMI_WT_ABS" show-ref --verify --quiet "refs/heads/$BASE_BRANCH"; then
  BASE_REF="$BASE_BRANCH"
fi

if [[ ! -f "$KIMI_ART_DIR/changed-files.txt" ]]; then
  if [[ -n "$BASE_REF" ]]; then
    git -C "$KIMI_WT_ABS" diff --name-only "$BASE_REF"...HEAD > "$KIMI_ART_DIR/changed-files.txt" || true
  else
    git -C "$KIMI_WT_ABS" diff --name-only HEAD~1..HEAD > "$KIMI_ART_DIR/changed-files.txt" || true
  fi
fi

for f in changed-files.txt commands-run.txt test-report.txt handoff.md kimi-last-session.md; do
  if [[ -f "$KIMI_ART_DIR/$f" ]]; then
    cp "$KIMI_ART_DIR/$f" "$ART_DIR_ABS/$f"
    echo "[OK] collected: $f"
  else
    echo "[WARN] missing: $f"
  fi
done

python3 - "$ART_DIR_ABS/collect-summary.json" "$KIMI_WT_ABS" "$KIMI_BRANCH" <<'PY'
import json,subprocess,sys,datetime
out,wt,branch=sys.argv[1:]
def cmd(args):
    return subprocess.check_output(args, text=True).strip()
summary={
  "timestamp": datetime.datetime.now(datetime.timezone.utc).isoformat(),
  "worktree": wt,
  "branch": branch,
  "head": cmd(["git","-C",wt,"rev-parse","HEAD"]),
  "status_short": cmd(["git","-C",wt,"status","--short"]) if True else ""
}
with open(out,"w",encoding="utf-8") as fh:
    json.dump(summary,fh,ensure_ascii=False,indent=2)
PY

if [[ "$DO_PUSH" -eq 1 ]]; then
  git -C "$KIMI_WT_ABS" push -u origin "$KIMI_BRANCH"
  echo "[OK] pushed branch: $KIMI_BRANCH"
else
  echo "[INFO] skip push (use --push to enable)"
fi

echo "[OK] collect done"
