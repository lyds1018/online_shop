<template>
  <div class="login-container">
    <h2>登录</h2>
    <form @submit.prevent="handleSubmit">
      <div class="form-group" data-icon="👤">
        <label>用户名：</label>
        <input type="text" v-model="username" placeholder="请输入用户名" required />
      </div>
      <div class="form-group" data-icon="🔒">
        <label>密码：</label>
        <input type="password" v-model="password" placeholder="请输入密码" required />
      </div>
      <button type="submit">登录</button>
      <p class="register-link">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </p>
    </form>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Login',
  data() {
    return {
      username: '',
      password: ''
    }
  },
  methods: {
    async handleSubmit() {
      try {
        const response = await axios.post('/api/users/login', {
          username: this.username,
          password: this.password
        })
        if (response.data.success) {
          const payload = response.data.data || {}
          // 调用父组件方法保存 token、role 和 username
          this.$root.setLogin(payload.token, payload.role, this.username)

          if (payload.role === 'ADMIN') {
            this.$router.push('/admin')
          } else {
            this.$router.push('/products')
          }
        }
      } catch (error) {
        alert('登录失败：' + error.message)
      }
    }
  }
}
</script>

<style scoped>
/* 登录页面容器 */
.login-container {
  max-width: 420px;
  margin: 80px auto;
  padding: 40px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
  transition: transform 0.3s ease;
}

.login-container:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 45px rgba(0, 0, 0, 0.15);
}

/* 装饰元素 */
.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 6px;
  background: linear-gradient(90deg, #3498db, #2ecc71);
}

/* 标题样式 */
.login-container h2 {
  text-align: center;
  color: #2c3e50;
  margin-bottom: 30px;
  font-size: 2em;
  font-weight: 700;
  position: relative;
}

.login-container h2::after {
  content: '';
  position: absolute;
  bottom: -12px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, #3498db, #2ecc71);
  border-radius: 3px;
}

/* 表单样式 */
form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 表单组样式 */
.form-group {
  position: relative;
  margin-bottom: 0;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #34495e;
  font-weight: 600;
  font-size: 0.95em;
  transition: color 0.3s ease;
}

/* 输入框样式 */
input {
  width: 100%;
  padding: 14px 45px 14px 20px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.3s ease;
  outline: none;
  background: #f9f9f9;
}

input:focus {
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
  background: white;
}

input::placeholder {
  color: #95a5a6;
  font-size: 14px;
}

/* 输入框图标容器 */
.form-group::after {
  content: attr(data-icon);
  position: absolute;
  right: 20px;
  top: 70%;
  transform: translateY(-50%);
  color: #95a5a6;
  font-size: 18px;
  pointer-events: none;
  transition: color 0.3s ease;
}

.form-group:focus-within::after {
  color: #3498db;
}

/* 按钮样式 */
button {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #3498db, #2ecc71);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 700;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
}

button::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  transform: translate(-50%, -50%);
  transition: width 0.6s, height 0.6s;
}

button:hover::before {
  width: 300px;
  height: 300px;
}

button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(52, 152, 219, 0.4);
  background: linear-gradient(135deg, #2980b9, #27ae60);
}

button:active {
  transform: translateY(0);
}

button:disabled {
  background: #95a5a6;
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}

/* 注册链接样式 */
.register-link {
  text-align: center;
  margin-top: 25px;
  font-size: 0.95em;
  color: #7f8c8d;
}

.register-link a {
  color: #3498db;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
  position: relative;
  padding: 0 4px;
}

.register-link a::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 2px;
  background: #3498db;
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.register-link a:hover {
  color: #2980b9;
}

.register-link a:hover::after {
  transform: scaleX(1);
}

/* 动画效果 */
.form-group {
  animation: fadeInUp 0.5s ease-out;
}

button {
  animation: fadeInUp 0.7s ease-out;
}

.register-link {
  animation: fadeInUp 0.9s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-container {
    margin: 50px 20px;
    padding: 30px 25px;
  }
  
  .login-container h2 {
    font-size: 1.8em;
    margin-bottom: 25px;
  }
  
  input {
    padding: 12px 40px 12px 16px;
    font-size: 15px;
  }
  
  button {
    padding: 14px;
    font-size: 15px;
  }
}

@media (max-width: 480px) {
  .login-container {
    margin: 30px 15px;
    padding: 25px 20px;
    border-radius: 12px;
  }
  
  .login-container h2 {
    font-size: 1.6em;
  }
  
  .form-group {
    margin-bottom: 15px;
  }
  
  input {
    font-size: 14px;
  }
  
  button {
    padding: 12px;
    font-size: 14px;
  }
}
</style>
