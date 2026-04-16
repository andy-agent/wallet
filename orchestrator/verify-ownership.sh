#!/usr/bin/env bash
set -euo pipefail
source "$(dirname "$0")/lib.sh"

TASK_FILE_INPUT="${1:?usage: verify-ownership.sh <task-yaml>}"
TASK_FILE="$(task_file_abs "$TASK_FILE_INPUT")"

require_cmd git
require_cmd python3

TASK_ID="$(task_id_from_yaml "$TASK_FILE")"
KIMI_WT_ABS="$(resolve_from_git_root "$(worktree_from_yaml "$TASK_FILE")")"
BASE_BRANCH="$(base_branch_from_yaml "$TASK_FILE")"
OWNERSHIP_FILE_REL="$(ownership_file_from_yaml "$TASK_FILE")"
OWNERSHIP_FILE="$(resolve_from_project_root "$OWNERSHIP_FILE_REL")"
PROJECT_SUBDIR="$(project_subdir_from_git_root)"

if [[ ! -f "$OWNERSHIP_FILE" ]]; then
  echo "[ERR] ownership file not found: $OWNERSHIP_FILE" >&2
  exit 1
fi

fetch_origin_if_exists "$KIMI_WT_ABS"

TMP_CHANGED="$(mktemp)"
trap 'rm -f "$TMP_CHANGED"' EXIT

BASE_REF=""
if git -C "$KIMI_WT_ABS" show-ref --verify --quiet "refs/remotes/origin/$BASE_BRANCH"; then
  BASE_REF="origin/$BASE_BRANCH"
elif git -C "$KIMI_WT_ABS" show-ref --verify --quiet "refs/heads/$BASE_BRANCH"; then
  BASE_REF="$BASE_BRANCH"
fi

if [[ -n "$BASE_REF" ]]; then
  git -C "$KIMI_WT_ABS" diff --name-only "$BASE_REF"...HEAD > "$TMP_CHANGED"
else
  echo "[WARN] base branch not found for diff: $BASE_BRANCH, fallback HEAD~1..HEAD"
  git -C "$KIMI_WT_ABS" diff --name-only HEAD~1..HEAD > "$TMP_CHANGED"
fi

python3 - "$TASK_FILE" "$OWNERSHIP_FILE" "$TMP_CHANGED" "$PROJECT_SUBDIR" <<'PY'
import fnmatch,sys,yaml
from pathlib import Path

task_file, ownership_file, changed_file, project_subdir = sys.argv[1:]

with open(task_file, encoding='utf-8') as fh:
    task = yaml.safe_load(fh) or {}

task_allow = [str(x).strip() for x in (task.get('allow') or []) if str(x).strip()]
task_deny  = [str(x).strip() for x in (task.get('deny') or []) if str(x).strip()]

conf_allow=[]
conf_deny=[]
for raw in Path(ownership_file).read_text(encoding='utf-8').splitlines():
    line=raw.strip()
    if not line or line.startswith('#'):
        continue
    if line.startswith('+'):
        conf_allow.append(line[1:].strip())
    elif line.startswith('-'):
        conf_deny.append(line[1:].strip())

allow_patterns = list(dict.fromkeys(task_allow + conf_allow))
deny_patterns  = list(dict.fromkeys(task_deny + conf_deny))

def variants(path: str):
    out=[path]
    if project_subdir and path.startswith(project_subdir + '/'):
        out.append(path[len(project_subdir)+1:])
    return out

def match_any(patterns, path):
    for p in patterns:
        if not p:
            continue
        for v in variants(path):
            if fnmatch.fnmatch(v, p) or fnmatch.fnmatch(path, p):
                return True, p
    return False, ""

changed=[ln.strip() for ln in Path(changed_file).read_text(encoding='utf-8').splitlines() if ln.strip()]
violations=[]

for f in changed:
    denied, dpat = match_any(deny_patterns, f)
    if denied:
        violations.append((f, f"DENY({dpat})"))
        continue

    if allow_patterns:
        allowed, apat = match_any(allow_patterns, f)
        if not allowed:
            violations.append((f, "NOT_IN_ALLOW"))

if violations:
    print("[FAIL] ownership verification failed")
    for f, reason in violations:
        print(f"[ERR] {f} -> {reason}")
    sys.exit(1)

print("[OK] ownership verification passed")
print(f"[INFO] checked files: {len(changed)}")
PY

echo "[OK] task ${TASK_ID} ownership check done"
