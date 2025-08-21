import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';

const api: AxiosInstance = axios.create({
  baseURL: 'http://192.168.8.194:8080/pahana-edu',  
  headers: {
    'Content-Type': 'application/json',

  },
});

// Request interceptor to attach token
import type { InternalAxiosRequestConfig } from 'axios';

api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('jwtToken');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => Promise.reject(error)
);

// Response interceptor to handle errors globally
api.interceptors.response.use(
  (response: AxiosResponse) => response,
  error => {
    if (error.response?.status === 401) {
      // handle 401 (unauthorized) - e.g., redirect to login
      console.warn('Unauthorized! Redirecting to login...');
      // window.location.href = '/login'; // optional redirect
    }
    return Promise.reject(error);
  }
);

export default api;
