import axios from 'axios';

const request = axios.create({
  baseURL: '/backend',
  timeout: 60000 // 将超时时间从 10 秒延长到 60 秒，以给 AI 响应留出充足时间
});

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default request;
