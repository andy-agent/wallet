(function () {
  const PSEUDO_SELECTOR = ".btn, .quick, .nav-item, .pill, .plan, .notice, .list-item, .card.padded";

  function isEmbedded() {
    return !!window.parent && window.parent !== window;
  }

  function projectRoot() {
    const href = window.location.href;
    if (href.includes("/pages/")) {
      return href.split("/pages/")[0] + "/";
    }
    if (href.includes("/index.html")) {
      return href.split("/index.html")[0] + "/";
    }
    return href;
  }

  function normalizePath(href) {
    const url = new URL(href, window.location.href);
    const pathname = url.pathname || "";
    const idx = pathname.indexOf("/pages/");
    return idx >= 0 ? pathname.slice(idx) : pathname;
  }

  function postCurrent(path) {
    if (!isEmbedded()) {
      return;
    }
    window.parent.postMessage({ type: "crypto-vpn:navigate", pagePath: path }, "*");
  }

  function activateToggle(element) {
    const group = element.getAttribute("data-toggle-group");
    if (!group) {
      return;
    }
    document.querySelectorAll(`[data-toggle-group="${group}"]`).forEach((item) => {
      item.classList.toggle("active", item === element);
      item.setAttribute("aria-pressed", item === element ? "true" : "false");
    });
  }

  async function runAction(element) {
    const action = element.getAttribute("data-action");
    if (!action) {
      return false;
    }
    if (action === "copy") {
      const text = element.getAttribute("data-copy") || "";
      if (text) {
        try {
          await navigator.clipboard.writeText(text);
        } catch (error) {
          window.prompt("复制以下内容", text);
        }
      }
      return true;
    }
    return false;
  }

  function resolveHref(path) {
    return projectRoot() + path.replace(/^\//, "");
  }

  function textOf(element) {
    return (element.textContent || "").replace(/\s+/g, " ").trim();
  }

  function currentPagePath() {
    return normalizePath(window.location.href);
  }

  function inferFallback(target) {
    const text = textOf(target);
    const page = currentPagePath();

    if (target.matches(".pill, .plan")) {
      return { type: "toggle" };
    }

    if (page.endsWith("/pages/p0/email_login.html")) {
      if (text.includes("登录并同步账户")) return { type: "route", path: "/pages/p0/vpn_home.html" };
      if (text.includes("创建账户")) return { type: "route", path: "/pages/p0/email_register.html" };
      if (text.includes("导入钱包")) return { type: "route", path: "/pages/p2-extended/import_wallet_method.html" };
    }

    if (page.endsWith("/pages/p0/wallet_onboarding.html")) {
      if (text.includes("继续进入应用")) return { type: "route", path: "/pages/p0/vpn_home.html" };
      if (text.includes("创建新钱包")) return { type: "route", path: "/pages/p2-extended/backup_mnemonic.html" };
      if (text.includes("导入助记词 / 私钥")) return { type: "route", path: "/pages/p2-extended/import_wallet_method.html" };
      if (text.includes("仅观察模式")) return { type: "route", path: "/pages/p0/wallet_home.html" };
      if (target.matches(".pill")) return { type: "toggle" };
    }

    if (page.endsWith("/pages/p0/wallet_home.html")) {
      if (target.matches(".pill")) return { type: "toggle" };
      if (target.matches(".list-item")) return { type: "route", path: "/pages/p2-core/asset_detail.html" };
      if (target.matches(".nav-item")) {
        if (text.includes("总览")) return { type: "route", path: "/pages/p0/vpn_home.html" };
        if (text.includes("VPN")) return { type: "route", path: "/pages/p1/plans.html" };
        if (text.includes("钱包")) return { type: "route", path: "/pages/p0/wallet_home.html" };
        if (text.includes("增长")) return { type: "route", path: "/pages/p2-core/invite_center.html" };
        if (text.includes("我的")) return { type: "route", path: "/pages/p2-core/profile.html" };
      }
    }

    if (page.endsWith("/pages/p1/plans.html")) {
      if (target.matches(".plan")) return { type: "toggle" };
      if (text.includes("使用钱包支付并开通")) return { type: "route", path: "/pages/p1/order_checkout.html" };
    }

    if (page.endsWith("/pages/p1/region_selection.html")) {
      if (target.matches(".pill")) return { type: "toggle" };
      if (target.matches(".list-item")) return { type: "route", path: "/pages/p0/vpn_home.html" };
    }

    if (page.endsWith("/pages/p1/wallet_payment_confirm.html") && text.includes("确认支付并开通")) {
      return { type: "route", path: "/pages/p1/order_result.html" };
    }

    if (page.endsWith("/pages/p2-core/asset_detail.html")) {
      if (target.matches(".pill")) return { type: "toggle" };
      if (target.matches(".notice")) {
        if (text.includes("支付年度 Pro 套餐")) return { type: "route", path: "/pages/p1/order_detail.html" };
        if (text.includes("佣金返还")) return { type: "route", path: "/pages/p2-core/commission_ledger.html" };
        if (text.includes("发送到冷钱包")) return { type: "route", path: "/pages/p2-core/send.html" };
      }
      if (target.matches(".nav-item")) {
        if (text.includes("总览")) return { type: "route", path: "/pages/p0/vpn_home.html" };
        if (text.includes("VPN")) return { type: "route", path: "/pages/p1/plans.html" };
        if (text.includes("钱包")) return { type: "route", path: "/pages/p2-core/asset_detail.html" };
        if (text.includes("增长")) return { type: "route", path: "/pages/p2-core/invite_center.html" };
        if (text.includes("我的")) return { type: "route", path: "/pages/p2-core/profile.html" };
      }
    }

    if (page.endsWith("/pages/p2-core/receive.html")) {
      if (target.matches(".pill")) return { type: "toggle" };
      if (text.includes("复制地址") || text.includes("分享二维码")) {
        return { type: "action", action: "copy", value: "TQ2xP9v7m5aE2sH1cV4Z9Q6wB8Lk3N5xY7" };
      }
      if (target.matches(".notice")) return { type: "route", path: "/pages/p2-core/legal_documents.html" };
    }

    if (page.endsWith("/pages/p2-core/send.html")) {
      if (target.matches(".pill")) return { type: "toggle" };
      if (text.includes("确认并发送")) return { type: "route", path: "/pages/p2-core/send_result.html" };
    }

    return null;
  }

  document.addEventListener("click", (event) => {
    const target = event.target.closest(`a[href], [data-href], [data-action], [data-toggle-group], ${PSEUDO_SELECTOR}`);
    if (!target) {
      return;
    }
    if (target.hasAttribute("download")) {
      return;
    }
    const fallback = inferFallback(target);

    if (target.hasAttribute("data-toggle-group") || fallback?.type === "toggle") {
      event.preventDefault();
      activateToggle(target);
      return;
    }

    if (target.hasAttribute("data-action") || fallback?.type === "action") {
      event.preventDefault();
      if (fallback?.type === "action") {
        target.setAttribute("data-action", fallback.action);
        target.setAttribute("data-copy", fallback.value);
      }
      runAction(target);
      return;
    }

    const href = target.getAttribute("href") || target.getAttribute("data-href") || (fallback?.type === "route" ? resolveHref(fallback.path) : null);
    if (!href || href.startsWith("#") || href.startsWith("javascript:") || href.startsWith("mailto:") || href.startsWith("tel:") || href.startsWith("http://") || href.startsWith("https://")) {
      return;
    }

    if (isEmbedded()) {
      event.preventDefault();
      postCurrent(normalizePath(href));
      return;
    }

    if (!target.getAttribute("href")) {
      event.preventDefault();
      window.location.href = href;
    }
  });

  window.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(PSEUDO_SELECTOR).forEach((element) => {
      if (!element.hasAttribute("tabindex")) {
        element.setAttribute("tabindex", "0");
      }
      if (!element.hasAttribute("role")) {
        element.setAttribute("role", "button");
      }
    });
    postCurrent(normalizePath(window.location.href));
  });

  document.addEventListener("keydown", (event) => {
    if (event.key !== "Enter" && event.key !== " ") {
      return;
    }
    const target = event.target.closest(`a[href], [data-href], [data-action], [data-toggle-group], ${PSEUDO_SELECTOR}`);
    if (!target) {
      return;
    }
    event.preventDefault();
    target.click();
  });
})();
