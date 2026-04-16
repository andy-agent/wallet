# Auto Parallel Manifest

- Repo: /Users/cnyirui/git/projects/liaojiang
- Project subdir: .
- Kimi lanes target: 7 (min=5, max=8)
- Kimi lanes generated: 7

## Kimi Tasks
- .parallel/auto/tasks/AUTO-kimi-auto-code.yaml
- .parallel/auto/tasks/AUTO-kimi-auto-vpnui.yaml
- .parallel/auto/tasks/AUTO-kimi-auto-ui.yaml
- .parallel/auto/tasks/AUTO-kimi-auto-payment-backup.yaml
- .parallel/auto/tasks/AUTO-kimi-auto-final-engineering-delivery-package.yaml
- .parallel/auto/tasks/AUTO-kimi-auto-lane.yaml
- .parallel/auto/tasks/AUTO-kimi-auto-gstack.yaml

## Claude Task
- .parallel/auto/tasks/AUTO-claude-minimax-quality.yaml

## Recommended execution
orchestrator/auto-parallel-wave.sh bootstrap
orchestrator/auto-parallel-wave.sh dispatch
orchestrator/auto-parallel-wave.sh collect
orchestrator/auto-parallel-wave.sh verify
orchestrator/auto-parallel-wave.sh integrate
