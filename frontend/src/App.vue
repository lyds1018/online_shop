<template>
  <div id="app">
    <!-- 顶部导航栏，仅在登录状态且当前路由允许显示时显示 -->
    <nav class="navbar" v-if="isLoggedIn && $route.meta.showNavbar !== false">
      <div class="nav-brand">在线商城</div>
      <div class="nav-links">
        <!-- 对于root用户，只显示后台管理 -->
        <a href="#" @click.prevent="goAdmin" v-if="username == 'root'">后台管理</a>
        
        <!-- 对于非root用户，显示商品、购物车、我的订单 -->
        <router-link to="/products" v-if="username !== 'root'">商品</router-link>
        <router-link to="/cart" v-if="username !== 'root'">购物车</router-link>
        <router-link to="/orders" v-if="username !== 'root'">我的订单</router-link>
        <a href="#" @click.prevent="logout">退出登录</a>
      </div>
    </nav>

    <!-- 主体内容 -->
    <main class="app-container">
      <router-view :token="token" :userRole="userRole" @login-success="setLogin" />
    </main>
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      token: null,
      userRole: null,
      username: null // 添加用户名数据属性
    }
  },
  computed: {
    isLoggedIn() {
      return !!this.token
    },
    isAdmin() {
      return this.userRole === 'ADMIN'
    }
  },
  created() {
    // 页面加载时从 localStorage 初始化登录状态
    this.token = localStorage.getItem('token')
    this.userRole = localStorage.getItem('userRole')
    this.username = localStorage.getItem('username') // 从localStorage获取用户名

    // 如果 token 不存在且不是登录页/注册页，自动跳回登录页
    if (!this.token && !['/login', '/register'].includes(this.$route.path)) {
      this.$router.replace('/login')
    }
  },
  methods: {
    goAdmin() {
      if (this.isAdmin) {
        this.$router.push('/admin')
      } else {
        alert('权限不足，需要管理员权限')
      }
    },
    logout() {
      this.token = null
      this.userRole = null
      this.username = null
      localStorage.removeItem('token')
      localStorage.removeItem('userRole')
      localStorage.removeItem('username')
      this.$router.push('/login')
    },
    setLogin(token, role, username) { // 添加username参数
      this.token = token
      this.userRole = role
      this.username = username
      localStorage.setItem('token', token)
      localStorage.setItem('userRole', role)
      localStorage.setItem('username', username) // 存储用户名
      this.$router.push('/products') // 登录成功后跳转到商品页
    }
  }
}
</script>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  line-height: 1.6;
  color: #333;
  background-color: #f8f9fa;
}

/* 导航栏样式优化 */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 32px;
  background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
  color: #fff;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.nav-brand {
  font-size: 1.6em;
  font-weight: 700;
  letter-spacing: 1px;
}

.nav-links {
  display: flex;
  gap: 24px;
}

.nav-links a,
.nav-links router-link {
  color: #ecf0f1;
  text-decoration: none;
  font-size: 1.1em;
  font-weight: 500;
  position: relative;
  transition: color 0.3s ease;
  padding: 8px 0;
}

.nav-links a:hover,
.nav-links router-link:hover {
  color: #3498db;
}

/* 添加悬停下划线动画 */
.nav-links a::after,
.nav-links router-link::after {
  content: '';
  position: absolute;
  width: 0;
  height: 2px;
  bottom: 0;
  left: 0;
  background-color: #3498db;
  transition: width 0.3s ease;
}

.nav-links a:hover::after,
.nav-links router-link:hover::after {
  width: 100%;
}

/* 主容器样式 */
.app-container {
  padding: 32px;
  min-height: calc(100vh - 88px);
  max-width: 1400px;
  margin: 0 auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .navbar {
    padding: 12px 20px;
    flex-direction: column;
    gap: 16px;
  }
  
  .nav-links {
    justify-content: center;
    flex-wrap: wrap;
    gap: 16px;
  }
  
  .app-container {
    padding: 20px 16px;
    min-height: calc(100vh - 120px);
  }
}
</style>
