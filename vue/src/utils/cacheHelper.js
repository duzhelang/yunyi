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
  }
}