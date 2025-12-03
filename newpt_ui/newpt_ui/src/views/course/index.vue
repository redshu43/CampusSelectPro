<template>
  <div>
    <el-card shadow="never" class="operate-container">
      <el-button type="primary" icon="Plus" @click="handleAdd">发布新课程</el-button>
    </el-card>

    <el-card shadow="never" style="margin-top: 20px;">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="课程名称" min-width="150" />
        <el-table-column prop="score" label="学分" width="80" align="center" />
        <el-table-column label="容量" width="120" align="center">
          <template #default="scope">
            <span>{{ scope.row.selectedNum }} / {{ scope.row.capacity }}</span>
          </template>
        </el-table-column>

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

        <el-table-column label="操作" width="180" align="center">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(scope.row.id)">删除</el-button>
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

    <el-dialog
        v-model="dialogVisible"
        :title="isEditMode ? '编辑课程' : '发布新课程'"
        width="600px"
        @close="resetForm"
    >
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">

        <el-form-item label="课程名称" prop="name">
          <el-input v-model="formData.name" placeholder="例如：高级Java程序设计" />
        </el-form-item>

        <el-row>
          <el-col :span="12">
            <el-form-item label="学分" prop="score">
              <el-input-number v-model="formData.score" :min="1" :max="10" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程容量" prop="capacity">
              <el-input-number v-model="formData.capacity" :min="10" :max="500" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="上课周次" prop="weekDay">
              <el-select v-model="formData.weekDay" placeholder="请选择">
                <el-option v-for="day in weekOptions" :key="day" :label="day" :value="day" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="上课节次" prop="sectionTime">
              <el-input v-model="formData.sectionTime" placeholder="例如：3-4节" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="开课周期" prop="dateRange">
          <el-date-picker
              v-model="formData.dateRange"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 100%"
          />
        </el-form-item>

      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">
          {{ isEditMode ? '保存修改' : '发布课程' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

// --- 数据定义 ---
const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const dialogVisible = ref(false)
const formRef = ref(null)

// 标记当前是“新增”还是“编辑”
const isEditMode = ref(false)
const currentId = ref(null)

// 周次选项
const weekOptions = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']

// 表单数据
const formData = reactive({
  name: '',
  score: 2,
  capacity: 50,
  weekDay: '',
  sectionTime: '',
  dateRange: []
})

// 校验规则
const rules = {
  name: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  weekDay: [{ required: true, message: '请选择上课周次', trigger: 'change' }],
  sectionTime: [{ required: true, message: '请输入上课节次', trigger: 'blur' }],
  dateRange: [{ required: true, message: '请选择开课周期', trigger: 'change' }]
}

// --- 方法 ---

// 1. 获取列表 (Read)
const fetchList = async () => {
  loading.value = true
  try {
    const res = await request.get('/login/admin/courses', {
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

// 2. 点击新增 (Create 准备)
const handleAdd = () => {
  isEditMode.value = false
  currentId.value = null
  resetForm()
  dialogVisible.value = true
}

// 3. 点击编辑 (Update 准备)
const handleEdit = (row) => {
  isEditMode.value = true
  currentId.value = row.id

  // 回填数据
  formData.name = row.name
  formData.score = row.score
  formData.capacity = row.capacity
  formData.weekDay = row.weekDay
  formData.sectionTime = row.sectionTime

  // 把两个时间拼成数组，让日期选择器显示出来
  if (row.startTime && row.endTime) {
    formData.dateRange = [row.startTime, row.endTime]
  } else {
    formData.dateRange = []
  }

  dialogVisible.value = true
}

// 4. 点击删除 (Delete)
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这门课程吗？', '提示', { type: 'warning' })
      .then(async () => {
        try {
          await request.delete(`/login/admin/courses/${id}`)
          ElMessage.success('删除成功')
          fetchList() // 刷新列表
        } catch (error) {
          console.error(error)
        }
      })
      .catch(() => {})
}

// 5. 提交表单 (Create & Update)
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 构造提交数据
        const postData = {
          ...formData,
          // 手动把 'T' 替换成 ' ' (空格)，防止后端报错
          // (加一个 || '' 是为了防止日期为空时报错)
          startTime: (formData.dateRange[0] || '').replace('T', ' '),
          endTime: (formData.dateRange[1] || '').replace('T', ' ')
        }

        if (isEditMode.value) {
          // --- 编辑模式 (PUT) ---
          await request.put(`/login/admin/courses/${currentId.value}`, postData)
          ElMessage.success('修改成功')
        } else {
          // --- 新增模式 (POST) ---
          await request.post('/login/admin/courses', postData)
          ElMessage.success('发布成功')
        }

        dialogVisible.value = false
        fetchList()
      } catch (error) {
        console.error(error)
      }
    }
  })
}

// 工具：格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  return timeStr.replace('T', ' ')
}

// 重置表单
const resetForm = () => {
  formData.name = ''
  formData.score = 2
  formData.capacity = 50
  formData.weekDay = ''
  formData.sectionTime = ''
  formData.dateRange = []
  if (formRef.value) formRef.value.clearValidate()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchList()
}

onMounted(() => {
  fetchList()
})
</script>