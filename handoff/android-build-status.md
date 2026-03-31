# V2rayNG Android 构建状态 - 官方代码库

## 构建状态: ✅ 成功

**日期**: 2026-04-01

## 变更摘要

### 1. 切换到官方代码库 ✅
- **旧仓库**: `github.com/andy-agent/wallet` (第三方修改版)
- **新仓库**: `github.com/2dust/v2rayNG` (官方原版)

### 2. 支付模块集成 ✅
集成到官方 V2rayNG 代码的功能：

| 模块 | 状态 | 说明 |
|------|------|------|
| Room 数据库 | ✅ | UserEntity, OrderEntity, PaymentHistoryEntity |
| PlansActivity | ✅ | 套餐选择页面 |
| PaymentActivity | ✅ | 支付页面 (SOL/USDT/SPL) |
| UserProfileActivity | ✅ | 用户资料和订单历史 |
| LoginActivity | ✅ | 用户登录/注册 |
| Retrofit API | ✅ | 支付接口调用 |

### 3. 支付入口优化 ✅

**侧边栏导航** (menu_drawer.xml):
- 第一项: "Purchase Plan" (套餐购买) - 使用 `ic_add_24dp` 图标

**顶部工具栏菜单** (menu_main.xml):
- 新增: "Purchase Plan" (套餐购买)

### 4. 推广页面处理 ✅
- **AppConfig.kt**: 注释掉 `APP_PROMOTION_URL`，保留占位供后期替换
- **menu_drawer.xml**: 注释掉推广菜单项，保留位置
- **MainActivity.kt**: 注释掉推广菜单处理逻辑

## 修复的兼容性问题

### 颜色主题适配
- 问题: `PlansAdapter.kt` 使用了旧主题的 `colorAccent`
- 解决: 改为使用官方 Material Design 3 主题的 `md_theme_secondary` (橙色 #f97910)

## 构建产物

```
app/build/outputs/apk/fdroid/debug/
├── v2rayNG_2.0.17-fdroid_arm64-v8a.apk  ✅ 已安装
├── v2rayNG_2.0.17-fdroid_armeabi-v7a.apk
├── v2rayNG_2.0.17-fdroid_universal.apk
├── v2rayNG_2.0.17-fdroid_x86.apk
└── v2rayNG_2.0.17-fdroid_x86_64.apk
```

## 安装状态

- **设备**: ba2b016 (arm64-v8a)
- **包名**: com.v2ray.ang.fdroid
- **状态**: ✅ 安装成功并启动

## 启动命令

```bash
adb -s ba2b016 shell am start -n com.v2ray.ang.fdroid/com.v2ray.ang.ui.MainActivity
```

## 依赖版本 (保持与可工作的配置一致)

```toml
kotlin = "1.9.25"
room = "2.6.1"
kotlinx-metadata-jvm = "0.9.0"
retrofit = "2.11.0"
```

## 后端服务配置

- **服务器**: 154.36.173.184:8080
- **支付接口**: /client/v1/orders, /client/v1/plans
- **支持代币**: SOL, USDT-TRC20, SPL (8zFP8GeszFz7FvuHesguekTxDjm4KLsJEYBZTKyMLEoE)
- **定价**: 所有支付方式固定 3 USDT
