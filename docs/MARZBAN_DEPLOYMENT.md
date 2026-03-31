# Marzban 服务器部署指南

## 服务器信息

| 项目 | 值 |
|------|-----|
| **IP 地址** | 38.58.59.142 |
| **域名** | vpn.residential-agent.com |
| **SSH 密码** | ypbaHXDG9534 |
| **管理面板** | https://vpn.residential-agent.com:8443/dashboard |
| **API 地址** | https://vpn.residential-agent.com:8443/api |
| **管理员账号** | admin |
| **管理员密码** | MarzbanAdmin2024! |

---

## 已完成的配置

### 1. Docker & Marzban 安装
```bash
cd /opt/marzban
docker compose ps
# 状态: running
```

### 2. SSL 证书
- Cloudflare Origin Certificate 已安装
- 证书路径: `/var/lib/marzban/cert.crt`
- 密钥路径: `/var/lib/marzban/key.key`

### 3. Nginx 反向代理
- 监听 8443 端口
- 转发到 Marzban 8000 端口
- 配置 SSL

### 4. Cloudflare DNS
- A 记录: vpn.residential-agent.com → 38.58.59.142
- 代理状态: 已启用（橙云）

---

## 支付桥接服务器配置更新

在 **154.36.173.184** 服务器上执行以下操作：

### 步骤 1: 编辑 .env 文件
```bash
nano /opt/payment-bridge/code/deploy/.env
```

### 步骤 2: 更新 Marzban 配置
将以下内容添加到 .env 文件：

```env
# Marzban 配置 (新服务器)
MARZban_API_URL=https://vpn.residential-agent.com:8443/api
MARZban_USERNAME=admin
MARZban_PASSWORD=MarzbanAdmin2024!
MARZban_MOCK_MODE=false

# 区块链配置 (需要补充)
SOLANA_MOCK_MODE=false
SOLANA_RPC_URL=https://api.mainnet-beta.solana.com

TRON_MOCK_MODE=false
TRON_FULL_NODE=https://api.trongrid.io
TRON_SOLIDITY_NODE=https://api.trongrid.io
TRON_EVENT_SERVER=https://api.trongrid.io
TRON_API_KEY=your-tron-api-key  # 从 https://www.trongrid.io/ 获取
```

### 步骤 3: 重启服务
```bash
cd /opt/payment-bridge/code/server
source venv/bin/activate

# 重启 API 服务
systemctl restart payment-bridge-api

# 启动 Worker
python -m app.workers.scheduler &
```

---

## API 测试

### 测试 Marzban 连接
```bash
# 获取 Token
curl -X POST https://vpn.residential-agent.com:8443/api/admin/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=admin&password=MarzbanAdmin2024!"
```

### 测试完整流程
```bash
# 1. 注册
curl -sk https://154.36.173.184:8080/client/v1/auth/register \
  -X POST \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"Test1234"}'

# 2. 获取套餐
curl -sk https://154.36.173.184:8080/client/v1/plans

# 3. 创建订单（需要登录后的 JWT）
```

---

## 故障排查

### Marzban 服务问题
```bash
# 检查状态
ssh root@38.58.59.142
cd /opt/marzban
docker compose logs --tail 50

# 重启
docker compose restart
```

### Xray 核心问题
```bash
# 检查 Xray 是否运行
ps aux | grep xray

# 重新下载
cd /var/lib/marzban
wget https://github.com/XTLS/Xray-core/releases/download/v1.8.23/Xray-linux-64.zip
unzip -o Xray-linux-64.zip
mv xray-core xray
chmod +x xray
docker compose restart
```

### Nginx 问题
```bash
# 检查配置
nginx -t

# 查看日志
tail -f /var/log/nginx/error.log

# 重启
systemctl restart nginx
```

---

## 安全建议

1. **立即修改默认密码**
   - 登录 https://vpn.residential-agent.com:8443/dashboard
   - 修改 admin 密码

2. **限制 SSH 访问**
   - 只允许特定 IP 访问 38.58.59.142:22

3. **定期备份**
   ```bash
   # 备份数据库
   cp /var/lib/marzban/db.sqlite3 /backup/marzban-$(date +%Y%m%d).db
   ```

---

## 相关文档

- [代码审计文档](./CODE_AUDIT.md)
- [实现摘要](./IMPLEMENTATION_SUMMARY.md)
