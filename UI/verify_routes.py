#!/usr/bin/env python3

from html.parser import HTMLParser
from pathlib import Path
import sys


ROOT = Path(__file__).resolve().parent
PAGES = ROOT / "pages"


class LinkParser(HTMLParser):
    def __init__(self) -> None:
        super().__init__()
        self.links: list[str] = []
        self.buttons: int = 0

    def handle_starttag(self, tag, attrs):
        attrs = dict(attrs)
        if tag == "a":
            self.links.append(attrs.get("href", ""))
        elif tag == "button":
            self.buttons += 1


def main() -> int:
    missing: list[tuple[str, str]] = []
    total_pages = 0
    total_links = 0
    total_buttons = 0

    for page in sorted(PAGES.rglob("*.html")):
        total_pages += 1
        parser = LinkParser()
        parser.feed(page.read_text())
        total_links += len(parser.links)
        total_buttons += parser.buttons

        for href in parser.links:
            if not href or href.startswith("#"):
                missing.append((str(page.relative_to(ROOT)), href or "(empty)"))
                continue

            if href.startswith(("http://", "https://", "mailto:", "tel:", "javascript:")):
                continue

            target = (page.parent / href).resolve()
            if not target.exists():
                missing.append((str(page.relative_to(ROOT)), href))

    print(f"pages={total_pages}")
    print(f"links={total_links}")
    print(f"buttons={total_buttons}")
    print(f"missing={len(missing)}")
    for page, href in missing:
        print(f"MISSING {page} -> {href}")

    return 1 if missing else 0


if __name__ == "__main__":
    sys.exit(main())
