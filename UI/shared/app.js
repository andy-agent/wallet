(async function () {
  const statsEl = document.getElementById("hero-stats");
  const phaseGridEl = document.getElementById("phase-grid");
  const blockedListEl = document.getElementById("blocked-list");
  const blockedCountEl = document.getElementById("blocked-count");

  if (!statsEl || !phaseGridEl || !blockedListEl || !blockedCountEl) {
    return;
  }

  const response = await fetch("./full-delivery-html5-scope.json");
  const scope = await response.json();

  const phaseMeta = {
    p0: { label: "P0", copy: "启动、登录、首页、更新与钱包入口。", folder: "p0" },
    p1: { label: "P1", copy: "套餐、节点、订单与支付确认链路。", folder: "p1" },
    p2_core: { label: "P2 Core", copy: "资产、发送收款、邀请、我的、法务。", folder: "p2-core" },
    p2_extended: { label: "P2 Extended", copy: "钱包安全、多链、Swap、Bridge、DApp、签名交互。", folder: "p2-extended" }
  };

  const counts = scope.counts;
  statsEl.innerHTML = [
    statTile("Routed Pages", counts.routed_pages_total, "full delivery route truth"),
    statTile("Confirmed PNG", counts.routed_pages_with_confirmed_png, "first-round HTML5 scope"),
    statTile("Blocked Routes", counts.routed_pages_missing_confirmed_png, "waiting for latest PNG"),
    statTile("Global Dialog", counts.global_dialog_total, "session eviction only")
  ].join("");

  for (const [phaseKey, routes] of Object.entries(scope.routed_pages_with_png)) {
    const meta = phaseMeta[phaseKey];
    const cards = await Promise.all(Object.entries(routes).map(async ([route, asset]) => {
      const href = `./pages/${meta.folder}/${route}.html`;
      const ready = await pageExists(href);
      const linkClass = ready ? "page-link" : "page-link page-link-disabled";
      const label = ready ? "打开页面" : "待重建";
      return `
        <article class="page-card">
          <div class="page-phase">${meta.label}</div>
          <div class="page-route">${route}</div>
          <div class="page-source">Source PNG: ${asset}</div>
          <div class="page-actions">
            <a class="${linkClass}" href="${href}">${label}</a>
          </div>
        </article>
      `;
    })).then((items) => items.join(""));

    phaseGridEl.insertAdjacentHTML("beforeend", `
      <section class="phase-card glass-panel">
        <div class="phase-card-header">
          <div>
            <div class="eyebrow">${meta.label}</div>
            <h2>${meta.label} Routed Pages</h2>
            <div class="phase-copy">${meta.copy}</div>
          </div>
          <span class="status-chip">${Object.keys(routes).length} pages</span>
        </div>
        <div class="phase-pages">${cards}</div>
      </section>
    `);
  }

  blockedCountEl.textContent = `${scope.missing_confirmed_png_routes.length} routes`;
  blockedListEl.innerHTML = scope.missing_confirmed_png_routes
    .map((route) => `<div class="blocked-item">${route}</div>`)
    .join("");

  function statTile(label, value, note) {
    return `
      <div class="metric-tile glass-panel">
        <div class="metric-label">${label}</div>
        <div class="metric-value">${value}</div>
        <div class="metric-note">${note}</div>
      </div>
    `;
  }

  async function pageExists(path) {
    try {
      const resp = await fetch(path, { method: "HEAD" });
      return resp.ok;
    } catch (error) {
      return false;
    }
  }
})();
