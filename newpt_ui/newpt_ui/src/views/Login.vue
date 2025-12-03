<template>
  <div class="login-container">

    <div class="login-card">
      <div class="login-header">
        <h2>教务管理系统</h2>
        <p>欢迎回来，请登录您的账号</p>
      </div>

      <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          size="large"
          class="login-form"
      >
        <el-form-item prop="account">
          <el-input
              v-model="loginForm.account"
              placeholder="请输入账号 (学号/工号/管理员)"
              :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              show-password
              :prefix-icon="Lock"
              @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-button
            type="primary"
            :loading="isLoading"
            class="login-button"
            @click="handleLogin"
        >
          {{ isLoading ? '登录中...' : '立即登录' }}
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { User, Lock } from '@element-plus/icons-vue' // 引入图标
import { ElMessage } from 'element-plus'
import request from '../utils/request.js'
import { useRouter } from 'vue-router'

// --- 1. 数据定义 ---
const router = useRouter()
const loginFormRef = ref(null) // 表单引用
const isLoading = ref(false)   // 按钮加载状态

// 表单数据对象
const loginForm = reactive({
  account: '',
  password: ''
})

// 表单校验规则
const loginRules = {
  account: [
    { required: true, message: '请输入登录账号', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 10 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' }
  ]
}

// --- 2. 登录逻辑 ---
const handleLogin = async () => {
  // 1. 先校验表单
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid, fields) => {
    if (valid) {
      // 校验通过，开始请求
      isLoading.value = true

      try {
        // 2. 发送请求给后端 (这里直接用 axios，后面我们会封装它)
        // 注意：这里 URL 还是硬编码的 localhost，后面我们会配置代理
        const res = await request.post('/auth/login', {
          account: loginForm.account,
          password: loginForm.password
        })
        // 3. 因为拦截器里判断了 state===200，能走到这里说明一定成功了
        //    而且拦截器帮我们返回了 res (即 JsonResult)，不需要再 res.data 了
        ElMessage.success('登录成功！')
        localStorage.setItem('token', res.data) // 存 Token (给拦截器用)
        router.push('/dashboard')

      } catch (error) {
        // 5. 网络错误 (如 404, 500)
        console.error(error)
        ElMessage.error('服务器连接失败，请检查网络或后端服务')
      } finally {
        isLoading.value = false // 无论成功失败，都要停止加载动画
      }
    } else {
      console.log('表单校验失败', fields)
    }
  })
}
</script>

<style scoped>
/* 1. 全屏容器 */
.login-container {
  height: 100vh; /* 占满视口高度 */
  width: 100%;   /* 占满视口宽度 */
  display: flex; /* Flex 布局 */
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
  /* 改为纯净的白色背景 */
  background-color: #ffffff;
}

/* 2. 登录卡片 */
.login-card {
  width: 400px;
  padding: 40px 35px; /* 稍微调整内边距 */
  background: white;
  border-radius: 8px; /* 圆角稍微小一点，更商务 */
  /* 重点：在白底上，我们需要一个极淡的边框和柔和的阴影来突出卡片
  */
  border: 1px solid #ebeef5; /* 极淡的灰色边框 */
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05); /* 非常柔和的阴影 */
  text-align: center;
}

/* 3. 标题区域 */
.login-header h2 {
  margin: 0;
  font-size: 26px; /* 字体稍大 */
  color: #303133;
  font-weight: 500;
}

.login-header p {
  margin: 15px 0 35px;
  color: #909399;
  font-size: 14px;
}

/* 4. 按钮样式 */
.login-button {
  width: 100%;
  padding: 22px 0;
  font-size: 16px;
  letter-spacing: 2px;
  border-radius: 6px;
  margin-top: 10px;
}

/* 微调输入框样式，让它更搭白色背景 */
:deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #dcdfe6 inset; /* 默认边框色 */
}
:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset; /* 悬停边框色 */
}
:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff inset !important; /* 聚焦时使用主题蓝 */
}
</style>