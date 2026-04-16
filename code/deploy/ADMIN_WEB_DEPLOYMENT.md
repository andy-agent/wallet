# Admin Web 部署说明

## 部署目标
- 服务器：`154.37.208.72`
- 管理入口：`https://api.residential-agent.com/admin/`
- 邀请落地页：`https://vpn.residential-agent.com/invite?code=ABC123`
- 静态文件路径：`/opt/cryptovpn/admin-web/`
- Android APK 下载地址：部署时通过 `VITE_PUBLIC_ANDROID_APK_URL` 注入

## 部署步骤

### 1. 本地构建
```bash
cd code/admin-web
pnpm install
VITE_PUBLIC_ANDROID_APK_URL=https://vpn.residential-agent.com/downloads/cryptovpn-android.apk pnpm build
```

说明：
- `/invite` 页面会直接读取 `VITE_PUBLIC_ANDROID_APK_URL`
- 页面内“打开 App”按钮使用 Android deep link：`v2rayng://invite?code=...`
- 如果未注入 `VITE_PUBLIC_ANDROID_APK_URL`，`/invite` 页面仍可访问，但下载按钮会禁用并提示未配置下载地址

### 2. 部署静态文件
```bash
scp -i /users/cnyirui/server/154.37.208.72/keys/154.37.208.72.pem \
  -r code/admin-web/dist/* \
  root@154.37.208.72:/opt/cryptovpn/admin-web/
```

### 3. Nginx 配置
在 `/etc/nginx/sites-available/vpn-residential-agent.conf` 中添加：
```nginx
# Admin Web 构建产物
location ^~ /assets/ {
    alias /opt/cryptovpn/admin-web/assets/;
    expires 1y;
    add_header Cache-Control "public, immutable";
}

# 公开邀请落地页
location = /invite {
    root /opt/cryptovpn/admin-web;
    try_files /index.html =404;
}

# 如果未来邀请页增加子路由，继续回退到同一个 SPA 入口
location ^~ /invite/ {
    root /opt/cryptovpn/admin-web;
    try_files $uri $uri/ /index.html;
}

# APK 直链示例
location = /downloads/cryptovpn-android.apk {
    alias /opt/cryptovpn/downloads/cryptovpn-android.apk;
    add_header Content-Type application/vnd.android.package-archive;
    add_header Content-Disposition "attachment; filename=cryptovpn-android.apk";
}

# Admin Web 静态文件与后台路由
location = /admin {
    return 301 /admin/;
}

location ^~ /admin/ {
    alias /opt/cryptovpn/admin-web/;
    index index.html;
    try_files $uri $uri/ /index.html;
}
```

关键要求：
- `vpn.residential-agent.com/invite?code=...` 必须落到 `admin-web` 的 `index.html`，不能返回空白页或后端 JSON。
- 因为 `admin-web` 使用 `BrowserRouter`，`/invite` 和 `/admin/*` 都必须配置 history fallback，最终回退到同一个 `index.html`。
- `/admin` 和现有后台页面行为要保持不变；不要把 `/invite` 指向后台鉴权逻辑。
- `VITE_PUBLIC_ANDROID_APK_URL` 指向的 APK 地址必须与 nginx 中的下载 location 一致，确保用户点击后直接下载 APK。

### 4. 重载 nginx
```bash
nginx -t && systemctl reload nginx
```

## 验证命令
```bash
# 验证 admin 页面可访问
curl https://api.residential-agent.com/admin/

# 验证邀请页可访问
curl "https://vpn.residential-agent.com/invite?code=ABC123"

# 验证 healthz 未被破坏
curl https://api.residential-agent.com/api/healthz

# 验证静态资源
curl https://api.residential-agent.com/admin/assets/index-Dblt_WXg.css

# 验证 APK 下载地址
curl -I https://vpn.residential-agent.com/downloads/cryptovpn-android.apk
```

## 已知限制
- 当前 backend 的 admin 模块在服务器上为未完成的空壳状态
- Admin API (`/api/admin/*`) 需要 backend 代码同步后才能使用
- Dashboard/Orders/Withdrawals 等页面需要 admin API 支持
- 直装 APK 场景下，用户安装完成后仍需回到邀请页点击一次“打开 App”，才能通过 `v2rayng://invite?code=...` 把邀请码带进 App
