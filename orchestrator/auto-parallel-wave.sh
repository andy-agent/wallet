#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
source "$SCRIPT_DIR/lib.sh"

usage() {
  cat <<USAGE
usage: $(basename "$0") <bootstrap|dispatch|collect|verify|integrate> [options]

options:
  --driver <cli|stub>      dispatch driver (default: cli)
  --timeout-sec <N>        dispatch timeout seconds (default: 600)
  --max-parallel <N>       max concurrent tasks for dispatch/collect/verify
  --stub-change <PATH>     optional file path for stub driver
  --no-repair              skip repair-auto-wave.py before bootstrap
USAGE
}

if [[ $# -lt 1 ]]; then
  usage >&2
  exit 1
fi

PHASE="$1"
shift || true

DRIVER="cli"
TIMEOUT_SEC="600"
MAX_PARALLEL="${AUTO_WAVE_MAX_PARALLEL:-0}"
STUB_CHANGE=""
DO_REPAIR="1"
ALLOW_STUB_FALLBACK="${AUTO_WAVE_ALLOW_STUB_FALLBACK:-1}"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --driver)
      DRIVER="${2:?missing value for --driver}"
      shift 2
      ;;
    --timeout-sec)
      TIMEOUT_SEC="${2:?missing value for --timeout-sec}"
      shift 2
      ;;
    --max-parallel)
      MAX_PARALLEL="${2:?missing value for --max-parallel}"
      shift 2
      ;;
    --stub-change)
      STUB_CHANGE="${2:?missing value for --stub-change}"
      shift 2
      ;;
    --no-repair)
      DO_REPAIR="0"
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "[ERR] unknown arg: $1" >&2
      usage >&2
      exit 1
      ;;
  esac
done

case "$DRIVER" in
  cli|stub) ;;
  *)
    echo "[ERR] invalid --driver: $DRIVER (expected: cli|stub)" >&2
    exit 1
    ;;
esac

if ! [[ "$TIMEOUT_SEC" =~ ^[0-9]+$ ]] || [[ "$TIMEOUT_SEC" -le 0 ]]; then
  echo "[ERR] invalid --timeout-sec: $TIMEOUT_SEC (expected positive integer)" >&2
  exit 1
fi

if ! [[ "$MAX_PARALLEL" =~ ^[0-9]+$ ]] || [[ "$MAX_PARALLEL" -lt 0 ]]; then
  echo "[ERR] invalid --max-parallel: $MAX_PARALLEL (expected non-negative integer)" >&2
  exit 1
fi

ROOT="$(git_root)"
MANIFEST="$ROOT/.parallel/auto/manifest.json"

if [[ ! -f "$MANIFEST" ]]; then
  echo "[ERR] manifest not found: $MANIFEST" >&2
  exit 1
fi

require_cmd jq
require_cmd python3
require_cmd git

TASKS=()
while IFS= read -r task; do
  [[ -n "$task" ]] || continue
  TASKS+=("$task")
done < <(jq -r '.all_tasks[]' "$MANIFEST")

if [[ "${#TASKS[@]}" -eq 0 ]]; then
  echo "[ERR] no tasks found in manifest: $MANIFEST" >&2
  exit 1
fi

task_file_abs_from_manifest() {
  local task="$1"
  if [[ "$task" = /* ]]; then
    printf '%s\n' "$task"
  else
    printf '%s\n' "$ROOT/$task"
  fi
}

task_worktree_abs() {
  local task="$1"
  local task_abs worktree_rel
  task_abs="$(task_file_abs_from_manifest "$task")"
  worktree_rel="$(worktree_from_yaml "$task_abs")"
  resolve_from_git_root "$worktree_rel"
}

is_worktree_clean() {
  local wt="$1"
  [[ -d "$wt" ]] || return 1
  [[ -z "$(git -C "$wt" status --porcelain)" ]]
}

run_dispatch() {
  local task="$1"
  local mode="$2"
  local script=""

  if [[ -x "$SCRIPT_DIR/dispatch-worker.sh" ]]; then
    script="$SCRIPT_DIR/dispatch-worker.sh"
  elif [[ -x "$SCRIPT_DIR/dispatch-kimi.sh" ]]; then
    script="$SCRIPT_DIR/dispatch-kimi.sh"
  else
    echo "[ERR] dispatch script missing" >&2
    return 1
  fi

  if [[ "$(basename "$script")" == "dispatch-kimi.sh" ]]; then
    if [[ -n "$STUB_CHANGE" ]]; then
      "$script" --driver "$mode" --timeout-sec "$TIMEOUT_SEC" --stub-change "$STUB_CHANGE" "$task"
    else
      "$script" --driver "$mode" --timeout-sec "$TIMEOUT_SEC" "$task"
    fi
  else
    "$script" --driver "$mode" "$task"
  fi
}

run_phase() {
  local task="$1"
  case "$PHASE" in
    bootstrap)
      if [[ -x "$SCRIPT_DIR/bootstrap-task.sh" ]]; then
        "$SCRIPT_DIR/bootstrap-task.sh" "$task"
      elif [[ -x "$SCRIPT_DIR/bootstrap-parallel.sh" ]]; then
        "$SCRIPT_DIR/bootstrap-parallel.sh" "$task"
      else
        echo "[ERR] bootstrap script missing" >&2
        return 1
      fi
      ;;
    dispatch)
      if [[ "$DRIVER" != "cli" ]]; then
        run_dispatch "$task" "$DRIVER"
        return $?
      fi

      local wt before_head after_head
      wt="$(task_worktree_abs "$task")"
      if [[ ! -d "$wt" ]]; then
        echo "[ERR] lane worktree not found before dispatch: $wt (task=$task)" >&2
        return 1
      fi
      before_head="$(git -C "$wt" rev-parse HEAD)"
      if ! is_worktree_clean "$wt"; then
        echo "[ERR] lane worktree is dirty before dispatch; fallback guard cannot proceed: $wt (task=$task)" >&2
        return 1
      fi

      if run_dispatch "$task" "cli"; then
        return 0
      fi

      echo "[WARN] cli dispatch failed for task=$task" >&2
      if [[ "$ALLOW_STUB_FALLBACK" != "1" ]]; then
        echo "[ERR] stub fallback disabled (AUTO_WAVE_ALLOW_STUB_FALLBACK=$ALLOW_STUB_FALLBACK)" >&2
        return 1
      fi

      after_head="$(git -C "$wt" rev-parse HEAD)"
      if [[ "$before_head" != "$after_head" ]]; then
        echo "[ERR] deny stub fallback: lane HEAD changed (${before_head} -> ${after_head}) for task=$task" >&2
        return 1
      fi
      if ! is_worktree_clean "$wt"; then
        echo "[ERR] deny stub fallback: lane worktree became dirty for task=$task ($wt)" >&2
        return 1
      fi

      echo "[INFO] fallback to stub dispatch for task=$task" >&2
      run_dispatch "$task" "stub"
      ;;
    collect)
      if [[ -x "$SCRIPT_DIR/collect-worker.sh" ]]; then
        "$SCRIPT_DIR/collect-worker.sh" "$task"
      elif [[ -x "$SCRIPT_DIR/collect-kimi.sh" ]]; then
        "$SCRIPT_DIR/collect-kimi.sh" "$task"
      else
        echo "[ERR] collect script missing" >&2
        return 1
      fi
      ;;
    verify)
      if [[ -x "$SCRIPT_DIR/verify-ownership.sh" ]]; then
        "$SCRIPT_DIR/verify-ownership.sh" "$task"
      else
        echo "[ERR] verify script missing" >&2
        return 1
      fi
      ;;
    integrate)
      if [[ -x "$SCRIPT_DIR/integrate-task.sh" ]]; then
        "$SCRIPT_DIR/integrate-task.sh" "$task"
      elif [[ -x "$SCRIPT_DIR/integrate-kimi.sh" ]]; then
        "$SCRIPT_DIR/integrate-kimi.sh" "$task"
      else
        echo "[ERR] integrate script missing" >&2
        return 1
      fi
      ;;
    *)
      echo "[ERR] unknown phase: $PHASE" >&2
      return 1
      ;;
  esac
}

resolve_phase_parallel_limit() {
  if [[ "$MAX_PARALLEL" -gt 0 ]]; then
    printf '%s\n' "$MAX_PARALLEL"
    return 0
  fi

  if [[ "$PHASE" == "dispatch" && "$DRIVER" == "cli" ]]; then
    # Default to serialized CLI dispatch to reduce provider throttling risk.
    printf '1\n'
    return 0
  fi

  printf '%s\n' "${#TASKS[@]}"
}

run_parallel_phase() {
  local limit="$1"
  local rc=0

  if [[ "$limit" -le 1 ]]; then
    for t in "${TASKS[@]}"; do
      if ! run_phase "$t"; then
        rc=1
      fi
    done
    return "$rc"
  fi

  local -a pids=()

  for t in "${TASKS[@]}"; do
    run_phase "$t" &
    pids+=("$!")
    if [[ "${#pids[@]}" -ge "$limit" ]]; then
      if ! wait "${pids[0]}"; then
        rc=1
      fi
      if [[ "${#pids[@]}" -gt 1 ]]; then
        pids=("${pids[@]:1}")
      else
        pids=()
      fi
    fi
  done

  for p in "${pids[@]}"; do
    if ! wait "$p"; then
      rc=1
    fi
  done

  return "$rc"
}

if [[ "$PHASE" == "bootstrap" && "$DO_REPAIR" == "1" ]]; then
  REPAIR_SCRIPT="$SCRIPT_DIR/repair-auto-wave.py"
  if [[ ! -f "$REPAIR_SCRIPT" ]]; then
    echo "[ERR] repair script missing: $REPAIR_SCRIPT" >&2
    exit 1
  fi
  echo "[INFO] running repair: $REPAIR_SCRIPT $MANIFEST"
  python3 "$REPAIR_SCRIPT" "$MANIFEST"
fi

if [[ "$PHASE" == "dispatch" || "$PHASE" == "collect" || "$PHASE" == "verify" ]]; then
  phase_limit="$(resolve_phase_parallel_limit)"
  if [[ "$phase_limit" -lt 1 ]]; then
    phase_limit=1
  fi
  echo "[INFO] phase=$PHASE parallel_limit=$phase_limit driver=$DRIVER"
  run_parallel_phase "$phase_limit"
  exit $?
fi

for t in "${TASKS[@]}"; do
  run_phase "$t"
done

if [[ "$PHASE" == "integrate" && -x "$SCRIPT_DIR/beads-auto-close.sh" ]]; then
  echo "[INFO] running beads auto-close"
  "$SCRIPT_DIR/beads-auto-close.sh" "$MANIFEST"
fi
