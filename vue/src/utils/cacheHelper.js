export const CacheHelper = {
  get(key) {
    try {
      return localStorage.getItem(key)
    } catch {
      return null
    }
  },

  set(key, value) {
    try {
      localStorage.setItem(key, value)
    } catch (error) {
      console.error('CacheHelper set error:', error)
    }
  },

  remove(key) {
    try {
      localStorage.removeItem(key)
    } catch (error) {
      console.error('CacheHelper remove error:', error)
    }
  },

  clearAll() {
    try {
      localStorage.clear()
    } catch (error) {
      console.error('CacheHelper clearAll error:', error)
    }
  },

  clearAllLoginState() {
    try {
      localStorage.removeItem('user')
      localStorage.removeItem('menus')
      localStorage.removeItem('currentPathName')
    } catch (error) {
      console.error('CacheHelper clearAllLoginState error:', error)
    }
  },

  /**
   * 获取JSON对象
   * @param {string} key - 缓存键名
   * @returns {Object|null} 解析后的对象，失败返回null
   */
  getJson(key) {
    try {
      const value = localStorage.getItem(key)
      return value ? JSON.parse(value) : null
    } catch (error) {
      console.error('CacheHelper getJson error:', error)
      return null
    }
  },

  /**
   * 存储JSON对象
   * @param {string} key - 缓存键名
   * @param {Object} value - 要存储的对象
   */
  setJson(key, value) {
    try {
      localStorage.setItem(key, JSON.stringify(value))
    } catch (error) {
      console.error('CacheHelper setJson error:', error)
    }
  },

  /**
   * 带过期时间的存储
   * @param {string} key - 缓存键名
   * @param {*} value - 要存储的值
   * @param {number} ttlMs - 过期时间（毫秒）
   */
  setWithExpiry(key, value, ttlMs) {
    try {
      const item = {
        value: value,
        expiry: Date.now() + ttlMs
      }
      localStorage.setItem(key, JSON.stringify(item))
    } catch (error) {
      console.error('CacheHelper setWithExpiry error:', error)
    }
  },

  /**
   * 获取带过期时间的值
   * @param {string} key - 缓存键名
   * @returns {*} 存储的值，已过期或不存在返回null
   */
  getWithExpiry(key) {
    try {
      const itemStr = localStorage.getItem(key)
      if (!itemStr) {
        return null
      }
      const item = JSON.parse(itemStr)
      if (Date.now() > item.expiry) {
        // 已过期，删除并返回null
        localStorage.removeItem(key)
        return null
      }
      return item.value
    } catch (error) {
      console.error('CacheHelper getWithExpiry error:', error)
      return null
    }
  }
}