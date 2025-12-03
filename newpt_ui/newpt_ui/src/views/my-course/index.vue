<template>
  <div>
    <el-card shadow="never">
      <div slot="header" class="clearfix">
        <span style="font-weight: bold; font-size: 18px;">我的课程表</span>
        <el-tag type="success" style="margin-left: 10px">已选课程</el-tag>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading" style="margin-top: 20px">
        <el-table-column prop="id" label="课程ID" width="80" />
        <el-table-column prop="name" label="课程名称" min-width="150" />
        <el-table-column prop="score" label="学分" width="80" align="center" />

        <el-table-column label="上课时间" min-width="180">
          <template #default="scope">
            <el-tag size="small" type="info">{{ scope.row.weekDay }}</el-tag>
            <span style="margin-left: 8px">{{ scope.row.sectionTime }}</span>
          </template>
        </el-table-column>

        <el-table-column label="开课周期" min-width="200">
          <template #default="scope">
            <div style="font-size: 12px; color: #666">
              <div>始：{{ formatTime(scope.row.startTime) }}</div>
              <div>终：{{ formatTime(scope.row.endTime) }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120" align="center">
          <template #default="scope">
            <el-button
                type="danger"
                size="small"
                icon="Delete"
                @click="handleDrop(scope.row)"
            >
              退课
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const loading = ref(false)

// 1. 获取我的课程列表
const fetchMyCourses = async () => {
  loading.value = true
  try {
    // 调用后端 API (R)
    const res = await request.get('/student/course/my-courses')
    // 注意：这里后端返回的是 List，不是 PageVO，直接赋值即可
    tableData.value = res.data
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 2. 退课逻辑
const handleDrop = (row) => {
  ElMessageBox.confirm(
      `确定要退出 "${row.name}" 吗？退课后名额将释放。`,
      '退课警告',
      {
        confirmButtonText: '狠心退课',
        cancelButtonText: '我再想想',
        type: 'warning'
      }
  ).then(async () => {
    try {
      // 调用后端 API (D)
      await request.post('/student/course/drop', {
        courseId: row.id
      })

      ElMessage.success('退课成功')
      fetchMyCourses() // 刷新列表

    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  return timeStr.replace('T', ' ')
}

onMounted(() => {
  fetchMyCourses()
})
</script>