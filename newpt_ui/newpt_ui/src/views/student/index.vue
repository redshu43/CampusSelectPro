<template>
  <div>
    <el-card class="operate-container" shadow="never">
      <el-button type="primary" icon="Plus" @click="handleAdd">新增学生</el-button>
    </el-card>

    <el-card shadow="never" style="margin-top: 20px;">
      <el-table :data="tableData" border stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="account" label="账号" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column label="性别" width="80">
          <template #default="scope">
            <span>{{ scope.row.gender === 1 ? '男' : '女' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="80" />
        <el-table-column prop="majorId" label="专业" />
        <el-table-column prop="classIdt" label="班级" />
        <el-table-column label="操作" width="180" align="center">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button
                size="small"
                type="danger"
                link
                @click="handleDelete(scope.row.account)"
            >
              删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top: 20px; display: flex; justify-content: flex-end;">
        <el-pagination
            background
            layout="total, prev, pager, next, jumper"
            :total="total"
            :page-size="pageSize"
            :current-page="currentPage"
            @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog
        v-model="dialogVisible"
        :title="dialogTitle"
        width="500px"
        @close="resetForm"
    >
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="账号" prop="account">
          <el-input v-model="formData.account"
                    :disabled="isEditMode"
                    placeholder="请输入学号" />
<!--          编辑模式下禁用，防止修改学号-->
        </el-form-item>


        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入真实姓名" />
        </el-form-item>

        <el-form-item label="密码" prop="password" v-if="!isEditMode">
          <el-input v-model="formData.password" placeholder="默认密码" />
<!--          仅新增模式显示-->
        </el-form-item>

        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="formData.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="formData.age" :min="15" :max="60" />
        </el-form-item>

        <el-form-item label="专业ID" prop="majorId">
          <el-input v-model="formData.majorId" placeholder="例如: 1 E" />
        </el-form-item>
        <el-form-item label="班级ID" prop="classIdt">
          <el-input v-model="formData.classIdt" placeholder="例如: 1" />
        </el-form-item>
      </el-form>

      <template #footer>
    <span class="dialog-footer">
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request' // 引入我们封装好的带有Token的请求工具
import { ElMessage, ElMessageBox } from 'element-plus'
import { reactive, computed } from 'vue'

// --- 弹窗相关变量 ---
const dialogVisible = ref(false)
const formRef = ref(null)
const isEditMode = ref(false) // 标记当前是“新增”还是“编辑”

// 弹窗标题动态变化
const dialogTitle = computed(() => isEditMode.value ? '编辑学生' : '新增学生') // 会有一个缓存

// 表单数据 (对应后端的 DTO)
const formData = reactive({ // reactive vue3响应式创建对象
  account: '',
  name: '',
  Password: '', // 仅新增用
  gender: 1,
  age: 20,
  majorId: '',
  classIdt: '' // 注意类型，如果后端要是数字，这里输入框可能要处理一下
})

// 表单校验规则
const rules = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  Password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// --- 按钮点击事件 ---

// 1. 点击“新增学生”按钮
// (记得去 template 顶部把那个“新增学生”按钮加上 @click="handleAdd")
const handleAdd = () => {
  isEditMode.value = false
  dialogVisible.value = true
  // 重置表单数据 (清空)
  resetForm()
}

// 2. 点击表格里的“编辑”按钮
// (记得去 template 表格列把“编辑”按钮加上 @click="handleEdit(scope.row)")
const handleEdit = (row) => {
  isEditMode.value = true
  dialogVisible.value = true

  // 回填数据 (把行数据填入表单)
  // 注意：这里要小心，后端返回的字段名可能和 DTO 不完全一致
  // 比如后端返回 majorName，但我们提交要 majorId。
  // 如果你列表里没返回 majorId，编辑时这个字段就会空着，这是正常的，暂时先手动填。

  formData.account = row.account
  formData.name = row.name
  formData.gender = row.gender //=== '男' ? 1 : 0 // 如果后端返回的是中文，这里要转回数字
  formData.age = row.age
  formData.password = row.password
  formData.classIdt = row.classIdt
  formData.majorId = row.majorId
  // formData.classIdt = ... (如果在列表中没返回ID，这里暂时无法回填，需要手动输)
}

// 3. 点击“确定”提交
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEditMode.value) {
          // --- 编辑模式 (调用 PUT) ---
          await request.put(`/login/admin/students/by-account/${formData.account}`, {
            name: formData.name,
            gender: formData.gender,
            age: formData.age,
            majorId: formData.majorId,
            classIdt: formData.classIdt
            // 注意：不传 password
          })
          ElMessage.success('修改成功')
        } else {
          // --- 新增模式 (调用 POST) ---
          await request.post('/login/admin/student', formData)
          ElMessage.success('新增成功')
        }

        dialogVisible.value = false
        fetchStudentList() // 刷新列表

      } catch (error) {
        console.error(error)
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  // 清空 reactive 对象
  formData.account = ''
  formData.name = ''
  formData.password = ''
  formData.gender = 1
  formData.age = 20
  formData.majorId = ''
  formData.classIdt = ''

  // 清除校验红字
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}











// 数据定义
const tableData = ref([]) // 表格数据
const loading = ref(false) // 加载状态
const total = ref(0)      // 总条数
const currentPage = ref(1) // 当前页
const pageSize = ref(10)   // 每页多少条

// 删除逻辑
const handleDelete = (account) => {
  // 弹出确认框
  ElMessageBox.confirm(
      `确定要删除账号为 "${account}" 的学生吗？`,
      '警告',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(async () => {
        // 用户点击了“确定”
        try {
          // 调用后端 API
          // URL: DELETE /login/admin/students/by-account/{account}
          await request.delete(`/login/admin/students/by-account/${account}`)

          ElMessage.success('删除成功')

          // (关键) 重新加载列表，让数据消失
          fetchStudentList()

        } catch (error) {
          console.error(error)
        }
      })
      .catch(() => {
        // 用户点击了“取消”，什么都不做
      })
}


// 方法：获取学生列表
const fetchStudentList = async () => {
  loading.value = true
  try {
    // 调用后端 API (R - Read)
    // URL: /login/admin/students?page=1&size=10
    const res = await request.get('/login/admin/students', {
      params: {
        page: currentPage.value,
        size: pageSize.value
      }
    })

    // 后端返回的是 PageVO: { list: [], total: 100, ... }
    tableData.value = res.data.list
    total.value = res.data.total

  } catch (error) {
    console.error("获取数据失败", error)
  } finally {
    loading.value = false
  }
}

// 方法：翻页
const handlePageChange = (newPage) => {
  currentPage.value = newPage
  fetchStudentList() // 重新请求数据
}

// 页面加载时自动触发
onMounted(() => {
  fetchStudentList()
})
</script>