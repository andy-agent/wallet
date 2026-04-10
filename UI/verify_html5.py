#!/usr/bin/env python3

import json
import pathlib
import sys


ROOT = pathlib.Path(__file__).resolve().parent
SCOPE = ROOT / "full-delivery-html5-scope.json"

PHASE_FOLDERS = {
    "p0": "p0",
    "p1": "p1",
    "p2_core": "p2-core",
    "p2_extended": "p2-extended",
}


def main() -> int:
    scope = json.loads(SCOPE.read_text())
    missing = []
    existing = []

    for phase, routes in scope["routed_pages_with_png"].items():
        folder = PHASE_FOLDERS[phase]
        for route in routes:
            rel_path = pathlib.Path("pages") / folder / f"{route}.html"
            abs_path = ROOT / rel_path
            if abs_path.exists():
                existing.append(str(rel_path))
            else:
                missing.append(str(rel_path))

    print(f"expected_pages={len(existing) + len(missing)}")
    print(f"existing_pages={len(existing)}")
    print(f"missing_pages={len(missing)}")

    if existing:
        print("existing:")
        for path in existing:
            print(f"  {path}")

    if missing:
        print("missing:")
        for path in missing:
            print(f"  {path}")

    return 1 if missing else 0


if __name__ == "__main__":
    sys.exit(main())
