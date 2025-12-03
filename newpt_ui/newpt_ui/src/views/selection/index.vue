<template>
  <div>
    <el-card shadow="never">
      <div slot="header" class="clearfix">
        <span style="font-weight: bold; font-size: 18px;">选课大厅</span>
        <el-tag type="warning" style="margin-left: 10px">抢课进行中</el-tag>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading" style="margin-top: 20px">
        <el-table-column prop="name" label="课程名称" min-width="150" />
        <el-table-column prop="score" label="学分" width="80" align="center" />

        <el-table-column label="上课时间" min-width="180">
          <template #default="scope">
            <el-tag size="small" type="info">{{ scope.row.weekDay }}</el-tag>
            <span style="margin-left: 8px">{{ scope.row.sectionTime }}</span>
          </template>
        </el-table-column>

        <el-table-column label="剩余名额" width="150" align="center">
          <template #default="scope">
            <el-progress
                :percentage="calculatePercentage(scope.row)"
                :status="getFormatStatus(scope.row)"
            >
              {{ scope.row.capacity - scope.row.selectedNum }} / {{ scope.row.capacity }}
            </el-progress>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120" align="center">
          <template #default="scope">
            <el-button
                type="primary"
                size="small"
                :disabled="scope.row.selectedNum >= scope.row.capacity"
                @click="handleSelect(scope.row)"
            >
              {{ scope.row.selectedNum >= scope.row.capacity ? '已满' : '抢课' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top: 20px; display: flex; justify-content: flex-end;">
        <el-pagination
            background
            layout="total, prev, pager, next"
            :total="total"
            :page-size="pageSize"
            :current-page="currentPage"
            @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 1. 获取课程列表 (调用新写的接口)
const fetchList = async () => {
  loading.value = true
  try {
    const res = await request.get('/student/course/list', {
      params: { page: currentPage.value, size: pageSize.value }
    })
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 2. 抢课逻辑
const handleSelect = (row) => {
  ElMessageBox.confirm(`确定要选择 "${row.name}" 吗？`, '提示', {
    confirmButtonText: '立即抢课',
    cancelButtonText: '再想想',
    type: 'info'
  }).then(async () => {
    try {
      // 调用后端抢课接口
      await request.post('/student/course/select', {
        courseId: row.id
      })


      // 2. (新) 开启轮询，提示排队中
      // 使用 Element Plus 的全屏 Loading 服务
      const loadingInstance = ElLoading.service({
        lock: true,
        text: '正在排队抢课中，请稍候...',
        background: 'rgba(0, 0, 0, 0.7)',
      })

      // 3. 定义轮询函数
      let timer = null;
      let count = 0; // 计数器，防止无限等待

      const checkResult = async () => {
        try {
          // 调用查询接口
          const res = await request.get('/student/course/result', { params: { courseId: row.id } })
          const status = res.data // 0, 1, -1

          if (status === 1) {
            // 成功
            clearInterval(timer)
            loadingInstance.close()
            ElMessage.success(`恭喜！"${row.name}" 抢课成功！`)
            fetchList() // 刷新列表
          } else if (status === -1) {
            // 失败
            clearInterval(timer)
            loadingInstance.close()
            ElMessage.error(`很遗憾，"${row.name}" 抢课失败（库存不足或系统繁忙）`)
            fetchList()
          } else {
            // status === 0 (排队中)，继续等...
            count++;
            if (count > 20) { // 等了 20次 (约20秒) 还没结果，算超时
              clearInterval(timer)
              loadingInstance.close()
              ElMessage.warning('系统繁忙，请稍后在"我的课程"中查看结果')
            }
          }
        } catch (e) {
          clearInterval(timer)
          loadingInstance.close()
        }
      }

      // 4. 每隔 0.5 秒查一次
      timer = setInterval(checkResult, 500)

    } catch (error) {
      console.error(error)
    }
  })
}

// 辅助：计算进度条百分比
const calculatePercentage = (row) => {
  if (row.capacity === 0) return 100
  return Math.floor((row.selectedNum / row.capacity) * 100)
}

// 辅助：进度条颜色
const getFormatStatus = (row) => {
  const p = calculatePercentage(row)
  if (p >= 100) return 'exception' // 红色
  if (p >= 80) return 'warning'    // 黄色
  return 'success'                 // 绿色
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchList()
}

onMounted(() => {
  fetchList()
})
</script>