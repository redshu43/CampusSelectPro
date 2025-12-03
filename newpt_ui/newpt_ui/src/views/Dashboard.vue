<template>
  <div class="dashboard-container">
<!--侧边栏-->
    <el-container>
      <el-aside width="200px" class="aside">
        <div class="logo">教务系统</div>
        <el-menu
            active-text-color="#409EFF"
            background-color="#304156"
            text-color="#fff"
            default-active="1"
            class="el-menu-vertical"
            router
        >
          <el-menu-item index="/dashboard/home">
            <el-icon><HomeFilled /></el-icon>
            <span>系统首页</span>
          </el-menu-item>

          <el-menu-item index="/dashboard/student" v-if="userRole === 'ROLE_ADMIN'">
            <el-icon><User /></el-icon>
            <span>学生管理</span>
          </el-menu-item>

          <el-menu-item index="/dashboard/course" v-if="userRole === 'ROLE_ADMIN'">
            <el-icon><Reading /></el-icon>
            <span>课程管理</span>
          </el-menu-item>

          <template v-if="userRole === 'ROLE_STUDENT'">
            <el-menu-item index="/dashboard/personal">
              <el-icon><UserFilled /></el-icon>
              <span>个人中心</span>
            </el-menu-item>

            <el-menu-item index="/dashboard/selection">
              <el-icon><VideoPlay /></el-icon> <span>选课大厅</span>
            </el-menu-item>

            <el-menu-item index="/dashboard/my-course">
              <el-icon><Collection /></el-icon>
              <span>我的选课</span>
            </el-menu-item>

          </template>
          
        </el-menu>
      </el-aside>

      <el-container>
          <el-header class="header">
            <div class="header-left">
              <span>欢迎回来！</span>
            </div>

            <div class="header-right">
              <el-dropdown @command="handleCommand">
      <span class="el-dropdown-link" style="cursor: pointer; display: flex; align-items: center;">
        <el-avatar :size="32" icon="UserFilled" style="margin-right: 8px;" />
        <span>{{userName}}</span> <el-icon class="el-icon--right"><arrow-down /></el-icon>
      </span>

                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="personal">个人中心</el-dropdown-item>
                    <el-dropdown-item command="password">修改密码</el-dropdown-item>
                    <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </el-header>
<!--主要内容现实区域-->
        <el-main>
<!--          根据路由动态显示不同的组件-->
          <router-view />
        </el-main>
      </el-container>
    </el-container>


<!--    密码修改弹窗-->
    <el-dialog v-model="pwdDialogVisible" title="修改密码" width="400px">
      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="80px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { HomeFilled, User, UserFilled, ArrowDown, Reading, VideoPlay, Collection } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ref, onMounted, reactive } from 'vue'
import request from '../utils/request'

const router = useRouter()

// --- 1. 定义响应式变量 ---
const userRole = ref('')  // 角色
const userName = ref('用户') // 名字 (这是你之前缺少的)

// --- 2. 核心：Token 解析函数 (修复中文乱码版本) ---
const parseToken = (token) => {
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    // 使用 URI 编码处理中文，防止乱码
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    return JSON.parse(jsonPayload)
  } catch (e) {
    console.error("Token 解析失败:", e)
    return null
  }
}

// --- 3. 页面加载逻辑 ---
onMounted(() => {
  const token = localStorage.getItem('token')
  if (token) {
    const decoded = parseToken(token)
    // 打印解码后的原始数据，方便调试
    console.log("Token 解码结果:", decoded)

    if (decoded) {
      // 获取角色
      userRole.value = decoded.role || ''

      // 获取名字 (优先用 name，没有则用 sub/account)
      userName.value = decoded.name || decoded.sub || '用户'

      // 打印最终拿到的角色，确认是否正确
      console.log("当前判定角色:", userRole.value)
    }
  } else {
    // 如果没有 Token，跳回登录页
    // router.push('/login')
  }
})

// --- 4. 下拉菜单逻辑 ---
const handleCommand = (command) => {
  if (command === 'logout') {
    handleLogout()
  } else if (command === 'password') {
    openPwdDialog()
  } else if (command === 'personal') {
    router.push('/dashboard/personal')
  }
}

const handleLogout = () => {
  localStorage.removeItem('token')
  ElMessage.info('已退出登录')
  router.push('/login')
}

// --- 5. 修改密码逻辑 ---
const pwdDialogVisible = ref(false)
const pwdFormRef = ref(null)
const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const openPwdDialog = () => {
  pwdForm.oldPassword = ''
  pwdForm.newPassword = ''
  pwdForm.confirmPassword = ''
  pwdDialogVisible.value = true
}

const submitPassword = async () => {
  if (!pwdFormRef.value) return
  await pwdFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 后端路径是 /changePassword
        await request.post('/auth/changePassword', pwdForm)

        ElMessage.success('密码修改成功，请重新登录')
        pwdDialogVisible.value = false
        handleLogout() // 强制退出

      } catch (error) {
        console.error(error)
      }
    }
  })
}
</script>
<style scoped>
.dashboard-container {
  height: 100vh;
  display: flex;
}
.aside {
  background-color: #304156; /* 深色背景，没有这个你看不到侧边栏 */
  color: white;
  height: 100vh; /* 强制撑满高度 */
  display: flex;
  flex-direction: column;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  background-color: #2b3649;
}
.header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}
.el-menu-vertical {
  border-right: none;
  flex: 1;
}
</style>