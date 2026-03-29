#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
export CODEX_REPO_ROOT_OVERRIDE="$PROJECT_ROOT"
export CODEX_BEADS_AUTO_CREATE="${CODEX_BEADS_AUTO_CREATE:-0}"
export CODEX_BEADS_WRITE_HUMAN_DOCS="${CODEX_BEADS_WRITE_HUMAN_DOCS:-0}"

exec "$HOME/.codex/scripts/auto_resume_unfinished.sh" "$@"
