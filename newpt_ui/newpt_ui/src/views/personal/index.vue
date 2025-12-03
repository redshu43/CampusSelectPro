<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>我的个人信息</span>
        <el-tag type="success">学生</el-tag>
      </div>
    </template>

    <el-descriptions border :column="2" v-loading="loading">
      <el-descriptions-item label="姓名">{{ info.name }}</el-descriptions-item>
      <el-descriptions-item label="账号">{{ info.account }}</el-descriptions-item>
      <el-descriptions-item label="性别">
        <el-tag v-if="info.gender === 1" type="primary">男</el-tag>
        <el-tag v-else-if="info.gender === 0" type="danger">女</el-tag>
        <el-tag v-else type="info">未知</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="年龄">{{ info.age }}</el-descriptions-item>
      <el-descriptions-item label="专业">{{ info.majorId }}</el-descriptions-item>
      <el-descriptions-item label="班级">{{ info.classIdt }}</el-descriptions-item>
    </el-descriptions>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'

const info = ref({})
const loading = ref(false)

const fetchMyInfo = async () => {
  loading.value = true
  try {
    // 调用我们之前写好的“学生专属接口”
    // 后端会自动从 Token 里知道我是谁，所以不需要传 ID
    const res = await request.get('/student/me')
    info.value = res.data
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchMyInfo()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}
</style>