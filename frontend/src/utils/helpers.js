import { ORDER_STATUS_MAP, STORAGE_KEYS } from './constants'

/**
 * 格式化订单状态文本
 * @param {string} status - 订单状态
 * @returns {string} 格式化后的状态文本
 */
export const getStatusText = (status) => {
  return ORDER_STATUS_MAP[status] || status
}

/**
 * 格式化日期
 * @param {string|Date} date - 日期对象或字符串
 * @returns {string} 格式化后的日期字符串
 */
export const formatDate = (date) => {
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

/**
 * 格式化价格
 * @param {number} price - 价格
 * @returns {string} 格式化后的价格字符串
 */
export const formatPrice = (price) => {
  return `¥${Number(price).toFixed(2)}`
}

/**
 * 获取图片 URL
 * @param {string} imgFileName - 图片文件名
 * @returns {string} 图片 URL
 */
export const getImageUrl = (imgFileName) => {
  if (!imgFileName) return null
  try {
    return require(`@/img/${imgFileName}`)
  } catch (error) {
    console.warn('图片加载失败:', imgFileName)
    return `/src/img/${imgFileName}`
  }
}

/**
 * 防抖函数
 * @param {Function} fn - 要执行的函数
 * @param {number} delay - 延迟时间（毫秒）
 * @returns {Function} 防抖后的函数
 */
export const debounce = (fn, delay = 300) => {
  let timer = null
  return function (...args) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

/**
 * 从本地存储获取用户信息
 * @returns {Object} 用户信息对象
 */
export const getUserInfo = () => {
  return {
    token: localStorage.getItem(STORAGE_KEYS.TOKEN),
    userRole: localStorage.getItem(STORAGE_KEYS.USER_ROLE),
    username: localStorage.getItem(STORAGE_KEYS.USERNAME)
  }
}

/**
 * 保存用户信息到本地存储
 * @param {string} token - 用户令牌
 * @param {string} role - 用户角色
 * @param {string} username - 用户名
 */
export const saveUserInfo = (token, role, username) => {
  localStorage.setItem(STORAGE_KEYS.TOKEN, token)
  localStorage.setItem(STORAGE_KEYS.USER_ROLE, role)
  localStorage.setItem(STORAGE_KEYS.USERNAME, username)
}

/**
 * 清除用户信息
 */
export const clearUserInfo = () => {
  localStorage.removeItem(STORAGE_KEYS.TOKEN)
  localStorage.removeItem(STORAGE_KEYS.USER_ROLE)
  localStorage.removeItem(STORAGE_KEYS.USERNAME)
}

/**
 * 显示错误消息
 * @param {Error} error - 错误对象
 * @param {string} defaultMessage - 默认错误消息
 */
export const showError = (error, defaultMessage = '操作失败') => {
  const message = error.response?.data?.message || error.message || defaultMessage
  alert(message)
}

/**
 * 显示成功消息
 * @param {string} message - 成功消息
 */
export const showSuccess = (message) => {
  alert(message)
}
