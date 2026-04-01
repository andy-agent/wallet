import axios, { AxiosError, type AxiosInstance, type AxiosResponse } from 'axios';
import { message } from 'antd';

// API 基础配置
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

// 创建 axios 实例
const request: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 从 localStorage 获取 token
    const token = localStorage.getItem('admin_token');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse) => {
    const { data } = response;
    
    // 如果响应格式是 { code, message, data }
    if (data && typeof data.code === 'number') {
      if (data.code !== 0 && data.code !== 200) {
        message.error(data.message || '请求失败');
        return Promise.reject(new Error(data.message));
      }
      return data.data;
    }
    
    return data;
  },
  (error: AxiosError) => {
    const { response } = error;
    
    if (response) {
      const { status, data } = response as AxiosResponse;
      
      switch (status) {
        case 401:
          message.error('未登录或登录已过期');
          localStorage.removeItem('admin_token');
          window.location.href = '/login';
          break;
        case 403:
          message.error('没有权限执行此操作');
          break;
        case 404:
          message.error('请求的资源不存在');
          break;
        case 500:
          message.error('服务器错误，请稍后重试');
          break;
        default:
          message.error((data as any)?.message || `请求失败 (${status})`);
      }
    } else {
      message.error('网络错误，请检查网络连接');
    }
    
    return Promise.reject(error);
  }
);

export default request;
