# 当前状态

**最后更新**: 2026-03-31

## 系统状态概览

| 组件 | 状态 | 备注 |
|------|------|------|
| 服务端 API | 🟡 部分可用 | 需要修复字段验证 |
| PostgreSQL | 🟢 运行中 | 套餐数据已初始化 |
| Redis | 🟢 运行中 | 缓存服务正常 |
| Android 构建 | 🟢 正常 | Kotlin 1.9.25, Room 兼容 |
| SSL/HTTPS | 🟢 已配置 | 自签名证书 |

## 已完成功能

- [x] 服务端基础架构 (FastAPI + PostgreSQL + Redis)
- [x] 用户认证系统 (注册/登录/JWT)
- [x] 套餐管理 API
- [x] Android Room 数据库集成
- [x] 订单数据模型 (客户端和服务端)
- [x] SSL 自签名证书配置

## 待解决问题

### 🔴 P0 - 阻断性问题

1. **服务端订单创建需要登录**
   - 当前: `create_order` 依赖 `get_current_user`
   - 期望: 支持匿名创建订单，首次购买自动创建账号
   - 位置: `code/server/app/api/client/orders.py:127`

2. **CreateOrderRequest 字段不匹配**
   - 服务端缺少: `client_device_id`, `client_version`, `client_token`
   - 位置: `code/server/app/api/client/orders.py:49-71`

3. **Optional 字段不接受 null**
   - 错误: `Input should be a valid string`
   - 需要: Pydantic 配置允许 null 值

### 🟡 P1 - 功能缺失

4. **LoginActivity 是占位符**
   - 文件: `LoginActivity.kt` 只有 29 行，无实际逻辑
   - 需要: 实现完整的登录/注册 UI

5. **用户无法主动购买**
   - 缺少: 购买按钮、套餐选择页面
   - 需要: 集成到 V2rayNG 主界面

6. **地址池为空**
   - 需要: 初始化 Solana 和 Tron 收款地址

### 🟢 P2 - 体验优化

7. 支付二维码显示
8. 订单状态实时轮询
9. 错误提示优化

## 下一步行动

1. 修复服务端 CreateOrderRequest 模型
2. 决定: 支持匿名购买 vs 强制登录
3. 实现客户端登录功能
4. 添加购买入口到主界面

## 参考文档

- [完整技术规格](TECHNICAL_SPECIFICATION.md)
