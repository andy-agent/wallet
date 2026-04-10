(function () {
  function normalizePath(href) {
    const url = new URL(href, window.location.href);
    const pathname = url.pathname || "";
    const idx = pathname.indexOf("/pages/");
    return idx >= 0 ? pathname.slice(idx) : pathname;
  }

  function postCurrent(path) {
    if (!window.parent || window.parent === window) {
      return;
    }
    window.parent.postMessage({ type: "crypto-vpn:navigate", pagePath: path }, "*");
  }

  document.addEventListener("click", (event) => {
    const link = event.target.closest("a[href]");
    if (!link) {
      return;
    }
    if (link.hasAttribute("download")) {
      return;
    }
    const href = link.getAttribute("href");
    if (!href || href.startsWith("#") || href.startsWith("javascript:") || href.startsWith("mailto:") || href.startsWith("tel:") || href.startsWith("http://") || href.startsWith("https://")) {
      return;
    }
    const pagePath = normalizePath(href);
    event.preventDefault();
    postCurrent(pagePath);
  });

  window.addEventListener("DOMContentLoaded", () => {
    postCurrent(normalizePath(window.location.href));
  });
})();
