import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
// 引入Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 引入路由
import router from './router'

const app = createApp(App)

// 使用 Element Plus
app.use(ElementPlus)
app.use(router)

app.mount('#app')
