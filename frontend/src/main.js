import '@/utils/api'; // 引入 API 配置（拦截器会自动生效）
import { createApp } from 'vue'
import App from './App.vue'
import './assets/common.css'
import './assets/main.css'
import router from './router'

const app = createApp(App)
app.use(router)
app.mount('#app')