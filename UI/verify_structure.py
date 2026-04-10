#!/usr/bin/env python3

from pathlib import Path
import sys


ROOT = Path(__file__).resolve().parent
PAGES = ROOT / "pages"


def main() -> int:
    old = []
    summary = []

    for page in sorted(PAGES.rglob("*.html")):
        text = page.read_text()
        has_p01 = 'class="p01-screen"' in text
        has_p2core = 'class="screen"' in text and 'shared/p2-core.css' in text
        has_p2ext = 'class="p2e-phone"' in text
        has_bottom = (
            '<nav class="p01-bottom-nav">' in text or
            '<nav class="bottom-nav">' in text or
            '<div class="bottom-nav">' in text
        )
        summary.append((str(page.relative_to(ROOT)), has_p01, has_p2core, has_p2ext, has_bottom))
        if not (has_p01 or has_p2core or has_p2ext):
            old.append(str(page.relative_to(ROOT)))

    print(f"pages={len(summary)}")
    print(f"old_structure_pages={len(old)}")
    for rel, has_p01, has_p2core, has_p2ext, has_bottom in summary:
        print(f"{rel} p01={int(has_p01)} p2core={int(has_p2core)} p2ext={int(has_p2ext)} bottom={int(has_bottom)}")

    if old:
      print("old_pages:")
      for rel in old:
          print(rel)

    return 1 if old else 0


if __name__ == "__main__":
    sys.exit(main())
