import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import 'dayjs/locale/zh-cn';

import MainLayout from './layouts/MainLayout';
import Accounts from './pages/Accounts';
import AuditLogs from './pages/AuditLogs';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import LegalDocs from './pages/LegalDocs';
import Nodes from './pages/Nodes';
import Orders from './pages/Orders';
import Plans from './pages/Plans';
import Regions from './pages/Regions';
import SystemConfigs from './pages/SystemConfigs';
import Versions from './pages/Versions';
import Withdrawals from './pages/Withdrawals';

// 设置 dayjs 语言
dayjs.locale('zh-cn');

// 简单的登录检查组件
const PrivateRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const token = localStorage.getItem('admin_token');
  return token ? <>{children}</> : <Navigate to="/login" replace />;
};

const App: React.FC = () => {
  return (
    <ConfigProvider locale={zhCN}>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route
            path="/"
            element={
              <PrivateRoute>
                <MainLayout />
              </PrivateRoute>
            }
          >
            <Route index element={<Dashboard />} />
            <Route path="accounts" element={<Accounts />} />
            <Route path="orders" element={<Orders />} />
            <Route path="plans" element={<Plans />} />
            <Route path="regions" element={<Regions />} />
            <Route path="nodes" element={<Nodes />} />
            <Route path="withdrawals" element={<Withdrawals />} />
            <Route path="versions" element={<Versions />} />
            <Route path="legal-docs" element={<LegalDocs />} />
            <Route path="system-configs" element={<SystemConfigs />} />
            <Route path="audit-logs" element={<AuditLogs />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </ConfigProvider>
  );
};

export default App;
