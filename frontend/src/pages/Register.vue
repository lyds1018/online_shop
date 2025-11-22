<template>
  <div class="register-container">
    <h2>注册</h2>
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label>用户名：</label>
        <input type="text" v-model="username" required />
      </div>

      <div class="form-group">
        <label>密码：</label>
        <input type="password" v-model="password" required />
      </div>

      <div class="form-group">
        <label>确认密码：</label>
        <input type="password" v-model="confirmPassword" required />
      </div>

      <button type="submit">注册</button>

      <p class="login-link">
        已有账号？<router-link to="/login">立即登录</router-link>
      </p>
    </form>
  </div>
</template>

<script>
import { userApi } from '@/utils/api';
import { showError, showSuccess } from '@/utils/helpers';

export default {
  name: 'Register',
  data() {
    return {
      username: '',
      password: '',
      confirmPassword: ''
    }
  },
  methods: {
    async handleSubmit() {
      if (this.password !== this.confirmPassword) {
        alert('两次输入的密码不一致')
        return
      }

      try {
        const res = await userApi.register({
          username: this.username,
          password: this.password
        })

        if (res.data.success) {
          showSuccess('注册成功！')
          this.$router.push('/login')
        } else {
          alert(res.data.message || '注册失败')
        }
      } catch (err) {
        showError(err, '注册失败')
      }
    }
  }
}
</script>

<style scoped>
.register-container {
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

.register-container:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 45px rgba(0, 0, 0, 0.15);
}

/* 装饰元素 */
.register-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 6px;
  background: linear-gradient(90deg, #3498db, #64b5f6);
}

.register-container h2 {
  text-align: center;
  color: #2c3e50;
  margin-bottom: 30px;
  font-size: 2em;
  font-weight: 700;
  position: relative;
}

.register-container h2::after {
  content: '';
  position: absolute;
  bottom: -12px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, #3498db, #64b5f6);
  border-radius: 3px;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #34495e;
  font-weight: 600;
  font-size: 0.95em;
}

input {
  width: 100%;
  padding: 14px 20px;
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
  transform: translateY(-1px);
}

input::placeholder {
  color: #95a5a6;
  font-size: 14px;
}

button {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #3498db, #64b5f6);
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

button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(52, 152, 219, 0.4);
  background: linear-gradient(135deg, #2980b9, #42a5f5);
}

.login-link {
  text-align: center;
  margin-top: 20px;
  font-size: 0.95em;
}

.login-link a {
  color: #3498db;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

.login-link a:hover {
  color: #2980b9;
  text-decoration: underline;
}
</style>