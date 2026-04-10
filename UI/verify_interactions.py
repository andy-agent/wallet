#!/usr/bin/env python3

from html.parser import HTMLParser
from pathlib import Path
import re
import sys


ROOT = Path(__file__).resolve().parent
PAGES = ROOT / "pages"
PSEUDO_CLASSES = {"btn", "quick", "nav-item", "pill", "plan", "notice", "list-item", "card", "p01-nav", "p01-quick"}


def infer_fallback(page: str, class_name: str, text: str):
    text = " ".join(text.split())

    if "pill" in class_name or "plan" in class_name:
        return "toggle"

    if page.endswith("p0/email_login.html"):
        if "登录并同步账户" in text:
            return "route"
        if "创建账户" in text:
            return "route"
        if "导入钱包" in text:
            return "route"

    if page.endswith("p0/wallet_onboarding.html"):
        if "继续进入应用" in text or "创建新钱包" in text or "导入助记词" in text or "仅观察模式" in text:
            return "route"

    if page.endswith("p0/wallet_home.html"):
        if "list-item" in class_name:
            return "route"
        if "nav-item" in class_name:
            return "route"

    if page.endswith("p1/plans.html") and "使用钱包支付并开通" in text:
        return "route"

    if page.endswith("p1/region_selection.html") and "list-item" in class_name:
        return "route"

    if page.endswith("p1/wallet_payment_confirm.html") and "确认支付并开通" in text:
        return "route"

    if page.endswith("p2-core/asset_detail.html"):
        if "notice" in class_name or "nav-item" in class_name:
            return "route"

    if page.endswith("p2-core/receive.html"):
        if "复制地址" in text or "分享二维码" in text:
            return "action"
        if "notice" in class_name:
            return "route"

    if page.endswith("p2-core/send.html") and "确认并发送" in text:
        return "route"

    return None


class InteractionParser(HTMLParser):
    def __init__(self, rel_path: str):
        super().__init__()
        self.rel_path = rel_path
        self.items = []
        self.stack = []

    def handle_starttag(self, tag, attrs):
        attrs = dict(attrs)
        classes = set((attrs.get("class") or "").split())
        if tag == "a":
            self.items.append(("a", attrs.get("href"), classes, ""))
            self.stack.append(None)
            return

        is_pseudo = bool(classes & PSEUDO_CLASSES)
        if is_pseudo:
            entry = {
                "tag": tag,
                "classes": classes,
                "attrs": attrs,
                "text": []
            }
            self.stack.append(entry)
        else:
            self.stack.append(None)

    def handle_data(self, data):
        if self.stack and self.stack[-1] is not None:
            self.stack[-1]["text"].append(data)

    def handle_endtag(self, tag):
        if not self.stack:
            return
        entry = self.stack.pop()
        if entry is None:
            return
        text = " ".join(part.strip() for part in entry["text"] if part.strip())
        self.items.append(("pseudo", entry["attrs"], entry["classes"], text))


def main() -> int:
    failures = []
    checked = 0

    for page in sorted(PAGES.rglob("*.html")):
        rel = str(page.relative_to(ROOT))
        parser = InteractionParser(rel)
        parser.feed(page.read_text())
        for kind, attrs, classes, text in parser.items:
            if kind == "a":
                continue

            classes_str = " ".join(sorted(classes))
            checked += 1

            if attrs.get("data-href") or attrs.get("data-action") or attrs.get("data-toggle-group"):
                continue

            inferred = infer_fallback(rel, classes_str, text)
            if inferred:
                continue

            failures.append((rel, classes_str, text[:80]))

    print(f"pseudo_controls_checked={checked}")
    print(f"pseudo_controls_unwired={len(failures)}")
    for rel, classes, text in failures[:200]:
        print(f"UNWIRED {rel} :: {classes} :: {text}")

    return 1 if failures else 0


if __name__ == "__main__":
    sys.exit(main())
