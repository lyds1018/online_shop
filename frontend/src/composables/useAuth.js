import { USER_ROLES } from '@/utils/constants'
import { clearUserInfo, getUserInfo, saveUserInfo } from '@/utils/helpers'
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'

export function useAuth() {
  const router = useRouter()
  
  // 从本地存储初始化状态
  const userInfo = getUserInfo()
  const token = ref(userInfo.token)
  const userRole = ref(userInfo.userRole)
  const username = ref(userInfo.username)
  
  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userRole.value === USER_ROLES.ADMIN)
  const isRoot = computed(() => username.value === 'root')
  
  /**
   * 登录
   */
  const login = (newToken, role, name) => {
    token.value = newToken
    userRole.value = role
    username.value = name
    saveUserInfo(newToken, role, name)
  }
  
  /**
   * 登出
   */
  const logout = () => {
    token.value = null
    userRole.value = null
    username.value = null
    clearUserInfo()
    router.push('/login')
  }
  
  return {
    token,
    userRole,
    username,
    isLoggedIn,
    isAdmin,
    isRoot,
    login,
    logout
  }
}
