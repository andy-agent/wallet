# 支付管理后台

基于 React + TypeScript + Vite + Ant Design 的支付网关管理后台。

## 功能模块

- **数据概览** - 订单统计、支付状态分布、交易趋势图表
- **订单管理** - 订单列表、详情查看、人工确认、重试发货、标记忽略
- **套餐管理** - 套餐列表、创建/编辑套餐
- **审计日志** - 操作日志查询、实体追踪

## 技术栈

- React 19
- TypeScript
- Vite 6
- Ant Design 5
- React Router 7
- Axios
- Day.js
- @ant-design/charts

## 开发

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览生产构建
npm run preview
```

## 环境变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| VITE_API_BASE_URL | API 基础 URL | /api |

## 目录结构

```
src/
├── api/          # API 请求封装
├── components/   # 公共组件
├── hooks/        # 自定义 Hooks
├── layouts/      # 布局组件
├── pages/        # 页面组件
├── types/        # TypeScript 类型定义
├── utils/        # 工具函数
├── App.tsx       # 应用入口
└── main.tsx      # 渲染入口
```

## 后端 API

详见后端服务文档，主要接口包括：

- `GET /admin/v1/orders` - 订单列表
- `GET /admin/v1/orders/{id}` - 订单详情
- `POST /admin/v1/orders/{id}/manual-fulfill` - 人工确认
- `POST /admin/v1/orders/{id}/retry-fulfill` - 重试发货
- `POST /admin/v1/orders/{id}/ignore` - 标记忽略
- `GET /admin/v1/plans` - 套餐列表
- `POST /admin/v1/plans` - 创建套餐
- `PUT /admin/v1/plans/{id}` - 更新套餐
- `GET /admin/v1/audit-logs` - 审计日志
- `GET /admin/v1/audit-logs/{entity_type}/{entity_id}` - 实体追踪
