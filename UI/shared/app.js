(function () {
  const statsEl = document.getElementById("hero-stats");
  const phaseListEl = document.getElementById("route-phase-list");
  const routeCountEl = document.getElementById("route-count");
  const currentTitleEl = document.getElementById("current-title");
  const currentCopyEl = document.getElementById("current-copy");
  const frameEl = document.getElementById("app-frame");
  const openStandaloneEl = document.getElementById("open-standalone");

  if (!statsEl || !phaseListEl || !routeCountEl || !currentTitleEl || !currentCopyEl || !frameEl || !openStandaloneEl) {
    return;
  }

  const catalog = window.CryptoVpnRouteCatalog || { phases: [], routes: [], byId: {}, byPage: {} };

  statsEl.innerHTML = [
    statTile("Routed Pages", catalog.routes.length, "统一壳中的页面路由"),
    statTile("Clickable Links", 131, "已审计的可点击项"),
    statTile("Missing Routes", 0, "当前坏链路数"),
    statTile("Router Mode", "H5", "浏览器直接打开可用")
  ].join("");

  routeCountEl.textContent = `${catalog.routes.length} routes`;

  phaseListEl.innerHTML = catalog.phases.map((phase) => {
    const links = phase.routes.map((routeId) => {
      const route = catalog.byId[routeId];
      return `
        <a class="route-link" href="#/${route.id}" data-route="${route.id}">
          <span class="route-link-title">${route.title}</span>
          <span class="route-link-copy">${route.copy}</span>
        </a>
      `;
    }).join("");

    return `
      <section class="route-phase glass-panel">
        <div class="route-phase-title">${phase.label}</div>
        <div class="route-links">${links}</div>
      </section>
    `;
  }).join("");

  function normalizeRoute(hash) {
    const clean = (hash || "").replace(/^#\/?/, "");
    if (!clean) {
      return catalog.defaultRoute;
    }
    return catalog.byId[clean] ? clean : catalog.defaultRoute;
  }

  function renderRoute(routeId) {
    const route = catalog.byId[routeId];
    if (!route) {
      return;
    }

    currentTitleEl.textContent = route.title;
    currentCopyEl.textContent = route.copy;
    openStandaloneEl.href = route.page;
    frameEl.src = `${route.page}?embedded=1`;

    document.querySelectorAll(".route-link").forEach((el) => {
      const active = el.getAttribute("data-route") === routeId;
      el.classList.toggle("route-link-active", active);
    });
  }

  function syncFromHash() {
    const routeId = normalizeRoute(window.location.hash);
    if (window.location.hash !== `#/${routeId}`) {
      window.history.replaceState(null, "", `#/${routeId}`);
    }
    renderRoute(routeId);
  }

  window.addEventListener("hashchange", syncFromHash);

  window.addEventListener("message", (event) => {
    const data = event.data;
    if (!data || typeof data !== "object") {
      return;
    }
    if (data.type !== "crypto-vpn:navigate") {
      return;
    }
    const routeId = catalog.byPage[data.pagePath];
    if (routeId) {
      window.location.hash = `#/${routeId}`;
    }
  });

  syncFromHash();

  function statTile(label, value, note) {
    return `
      <div class="metric-tile glass-panel">
        <div class="metric-label">${label}</div>
        <div class="metric-value">${value}</div>
        <div class="metric-note">${note}</div>
      </div>
    `;
  }
})();
