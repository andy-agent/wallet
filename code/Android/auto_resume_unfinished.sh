#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
export CODEX_REPO_ROOT_OVERRIDE="$SCRIPT_DIR"
export CODEX_BEADS_AUTO_CREATE="${CODEX_BEADS_AUTO_CREATE:-0}"
export CODEX_BEADS_WRITE_HUMAN_DOCS="${CODEX_BEADS_WRITE_HUMAN_DOCS:-0}"
exec "$SCRIPT_DIR/scripts/auto_resume_unfinished.sh" "$@"
