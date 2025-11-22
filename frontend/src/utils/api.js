import axios from 'axios'

// API 基础配置
const API_BASE_URL = 'http://localhost:8080'

// 创建 axios 实例
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000
})

// 请求拦截器
apiClient.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器
apiClient.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userRole')
      localStorage.removeItem('username')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// 用户相关 API
export const userApi = {
  login: (credentials) => apiClient.post('/api/users/login', credentials),
  register: (data) => apiClient.post('/api/users/register', data)
}

// 商品相关 API
export const productApi = {
  getAll: () => apiClient.get('/api/products'),
  search: (query) => apiClient.get(`/api/products/search?query=${query}`)
}

// 购物车相关 API
export const cartApi = {
  get: () => apiClient.get('/api/cart'),
  add: (productId, quantity) => apiClient.post('/api/cart/add', { productId, quantity }),
  updateItem: (itemId, quantity) => apiClient.put(`/api/cart/items/${itemId}`, { quantity }),
  removeItem: (itemId) => apiClient.delete(`/api/cart/items/${itemId}`)
}

// 订单相关 API
export const orderApi = {
  getAll: () => apiClient.get('/api/orders'),
  getById: (id) => apiClient.get(`/api/orders/${id}`),
  create: () => apiClient.post('/api/orders/create'),
  pay: (orderId) => apiClient.post('/api/orders/pay', { orderId }),
  cancel: (orderId) => apiClient.put(`/api/orders/${orderId}/cancel`),
  complete: (orderId) => apiClient.put(`/api/orders/${orderId}/complete`)
}

// 管理员相关 API
export const adminApi = {
  // 商品管理
  getProducts: () => apiClient.get('/api/admin/products'),
  createProduct: (data) => apiClient.post('/api/admin/products', data),
  updateProduct: (id, data) => apiClient.put(`/api/admin/products/${id}`, data),
  deleteProduct: (id) => apiClient.delete(`/api/admin/products/${id}`),
  
  // 订单管理
  getOrders: () => apiClient.get('/api/admin/orders'),
  shipOrder: (id) => apiClient.post(`/api/admin/orders/${id}/ship`),
  deleteOrder: (id) => apiClient.delete(`/api/admin/orders/${id}`),
  
  // 用户管理
  getUsers: () => apiClient.get('/api/admin/users'),
  deleteUser: (id) => apiClient.delete(`/api/admin/users/${id}`)
}

export default apiClient
