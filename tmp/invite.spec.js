const { test } = require('@playwright/test');

test('invite page diagnostics', async ({ page }) => {
  const logs = [];
  page.on('console', msg => logs.push(`console:${msg.type()}:${msg.text()}`));
  page.on('pageerror', err => logs.push(`pageerror:${err.message}`));
  const resp = await page.goto('https://api.residential-agent.com/invite?code=FLOW2026', { waitUntil: 'networkidle' });
  console.log('status=' + (resp && resp.status()));
  console.log('title=' + await page.title());
  console.log('text=' + JSON.stringify((await page.locator('body').innerText()).slice(0, 1000)));
  for (const line of logs) console.log(line);
});
