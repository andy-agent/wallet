# Admin Web 部署说明

## 部署目标
- 服务器：`154.37.208.72`
- 入口：`https://api.residential-agent.com/admin/`
- 静态文件路径：`/opt/cryptovpn/admin-web/`

## 部署步骤

### 1. 本地构建
```bash
cd code/admin-web
pnpm install
pnpm build
```

### 2. 部署静态文件
```bash
scp -i /users/cnyirui/server/154.37.208.72/keys/154.37.208.72.pem \
  -r code/admin-web/dist/* \
  root@154.37.208.72:/opt/cryptovpn/admin-web/
```

### 3. Nginx 配置
在 `/etc/nginx/sites-available/vpn-residential-agent.conf` 中添加：
```nginx
# Admin Web 静态文件
location ^~ /admin/ {
    alias /opt/cryptovpn/admin-web/;
    index index.html;
    try_files $uri $uri/ /admin/index.html;
    
    # 缓存静态资源
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

### 4. 重载 nginx
```bash
nginx -t && systemctl reload nginx
```

## 验证命令
```bash
# 验证 admin 页面可访问
curl https://api.residential-agent.com/admin/

# 验证 healthz 未被破坏
curl https://api.residential-agent.com/api/healthz

# 验证静态资源
curl https://api.residential-agent.com/admin/assets/index-Dblt_WXg.css
```

## 已知限制
- 当前 backend 的 admin 模块在服务器上为未完成的空壳状态
- Admin API (`/api/admin/*`) 需要 backend 代码同步后才能使用
- Dashboard/Orders/Withdrawals 等页面需要 admin API 支持
