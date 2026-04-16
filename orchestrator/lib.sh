#!/usr/bin/env bash
set -euo pipefail

script_dir() {
  cd -- "$(dirname -- "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd
}

project_root() {
  cd -- "$(script_dir)/.." >/dev/null 2>&1 && pwd
}

git_root() {
  git -C "$(project_root)" rev-parse --show-toplevel
}

project_subdir_from_git_root() {
  local proot groot
  proot="$(project_root)"
  groot="$(git_root)"
  python3 - "$groot" "$proot" <<'PY'
import os,sys
root=os.path.abspath(sys.argv[1])
proj=os.path.abspath(sys.argv[2])
rel=os.path.relpath(proj,root)
print("" if rel=="." else rel.replace("\\","/"))
PY
}

resolve_from_project_root() {
  local maybe_path="$1"
  local proot
  proot="$(project_root)"
  if [[ "$maybe_path" = /* ]]; then
    printf '%s\n' "$maybe_path"
  else
    printf '%s\n' "$proot/$maybe_path"
  fi
}

resolve_from_git_root() {
  local maybe_path="$1"
  local groot
  groot="$(git_root)"
  if [[ "$maybe_path" = /* ]]; then
    printf '%s\n' "$maybe_path"
  else
    printf '%s\n' "$groot/$maybe_path"
  fi
}

require_cmd() {
  command -v "$1" >/dev/null 2>&1 || {
    echo "[ERR] missing command: $1" >&2
    exit 1
  }
}

fetch_origin_if_exists() {
  local repo_path="${1:-$(git_root)}"
  if git -C "$repo_path" remote | grep -Fx "origin" >/dev/null 2>&1; then
    git -C "$repo_path" fetch origin --prune || true
  else
    echo "[INFO] origin remote not configured, skip fetch: $repo_path"
  fi
}

yaml_get_scalar() {
  local key="$1"
  local file="$2"
  python3 - "$key" "$file" <<'PY'
import sys,yaml
k=sys.argv[1]
f=sys.argv[2]
with open(f, encoding='utf-8') as fh:
    data=yaml.safe_load(fh) or {}
val=data.get(k, "")
if val is None:
    val=""
if isinstance(val, (list, dict)):
    print("")
else:
    print(str(val))
PY
}

yaml_get_list() {
  local key="$1"
  local file="$2"
  python3 - "$key" "$file" <<'PY'
import sys,yaml
k=sys.argv[1]
f=sys.argv[2]
with open(f, encoding='utf-8') as fh:
    data=yaml.safe_load(fh) or {}
vals=data.get(k, [])
if vals is None:
    vals=[]
if not isinstance(vals, list):
    vals=[vals]
for v in vals:
    print(str(v))
PY
}

task_file_abs() {
  resolve_from_project_root "$1"
}

task_id_from_yaml() {
  yaml_get_scalar "task_id" "$1"
}

branch_from_yaml() {
  yaml_get_scalar "branch" "$1"
}

codex_branch_from_yaml() {
  local v
  v="$(yaml_get_scalar "codex_branch" "$1")"
  if [[ -n "$v" ]]; then
    printf '%s\n' "$v"
  else
    printf 'codex/%s\n' "$(task_id_from_yaml "$1")"
  fi
}

worktree_from_yaml() {
  yaml_get_scalar "worktree" "$1"
}

codex_worktree_from_yaml() {
  local v
  v="$(yaml_get_scalar "codex_worktree" "$1")"
  if [[ -n "$v" ]]; then
    printf '%s\n' "$v"
  else
    printf '../wt-codex-%s\n' "$(task_id_from_yaml "$1")"
  fi
}

base_branch_from_yaml() {
  local v
  v="$(yaml_get_scalar "base_branch" "$1")"
  if [[ -n "$v" ]]; then
    printf '%s\n' "$v"
  else
    local b
    b="$(git -C "$(git_root)" branch --show-current 2>/dev/null || true)"
    if [[ -n "$b" ]]; then
      printf '%s\n' "$b"
    else
      printf 'master\n'
    fi
  fi
}

ownership_file_from_yaml() {
  local v
  v="$(yaml_get_scalar "ownership_file" "$1")"
  if [[ -n "$v" ]]; then
    printf '%s\n' "$v"
  else
    printf '.parallel/ownership.conf\n'
  fi
}

artifacts_rel_from_task() {
  local task_id="$1"
  printf '.parallel/artifacts/%s\n' "$task_id"
}

artifacts_abs_from_task() {
  local task_id="$1"
  resolve_from_project_root "$(artifacts_rel_from_task "$task_id")"
}

ensure_branch_from_base() {
  local branch="$1"
  local base="$2"
  local root
  root="$(git_root)"

  if git -C "$root" show-ref --verify --quiet "refs/heads/$branch"; then
    return 0
  fi

  if git -C "$root" show-ref --verify --quiet "refs/remotes/origin/$base"; then
    git -C "$root" branch "$branch" "origin/$base"
    return 0
  fi

  if git -C "$root" show-ref --verify --quiet "refs/heads/$base"; then
    git -C "$root" branch "$branch" "$base"
    return 0
  fi

  echo "[ERR] base branch not found locally/remotely: $base" >&2
  exit 1
}

ensure_worktree() {
  local wt_abs="$1"
  local branch="$2"
  local root
  root="$(git_root)"

  if git -C "$root" worktree list --porcelain | awk '/^worktree /{print $2}' | grep -Fx "$wt_abs" >/dev/null 2>&1; then
    return 0
  fi

  mkdir -p "$(dirname "$wt_abs")"
  git -C "$root" worktree add "$wt_abs" "$branch"
}
