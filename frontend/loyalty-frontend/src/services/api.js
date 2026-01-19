import axios from 'axios'

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Customer API
export const customerAPI = {
  getAll: (page = 0, size = 20) => api.get(`/v1/customers?page=${page}&size=${size}`),
  getById: (id) => api.get(`/v1/customers/${id}`),
  enroll: (data) => api.post('/v1/customers/enroll', data),
  getPoints: (id) => api.get(`/v1/customers/${id}/points`),
  search: (query) => api.get(`/v1/customers/search?query=${query}`)
}

// Transaction API
export const transactionAPI = {
  getAll: (customerId, page = 0) => api.get(`/v1/transactions/customer/${customerId}?page=${page}&size=20`),
  create: (customerId, data) => api.post(`/v1/transactions?customerId=${customerId}`, data),
  getRecent: (limit = 10) => api.get(`/v1/transactions/recent?limit=${limit}`)
}

// Reward API
export const rewardAPI = {
  getAll: () => api.get('/v1/rewards/active'),
  getAvailable: () => api.get('/v1/rewards/available'),
  redeem: (customerId, rewardId) => api.post(`/v1/rewards/redeem?customerId=${customerId}&rewardId=${rewardId}`),
  getRecentRedemptions: (limit = 10) => api.get(`/v1/rewards/redemptions/recent?limit=${limit}`)
}

// Promotion API
export const promotionAPI = {
  getAll: () => api.get('/v1/promotions/active'),
  create: (data) => api.post('/v1/promotions', data),
  getForCustomer: (customerId) => api.get(`/v1/promotions/customer/${customerId}`)
}

// Analytics API
export const analyticsAPI = {
  getSummary: () => api.get('/v1/analytics/summary'),
  getCustomerActivity: () => api.get('/v1/analytics/customers'),
  getRedemptionTrends: () => api.get('/v1/analytics/redemptions'),
  getSalesAnalytics: () => api.get('/v1/analytics/sales')
}

export default api

