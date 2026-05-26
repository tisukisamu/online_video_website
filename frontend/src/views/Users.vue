<template>
  <div class="min-h-screen bg-black text-white p-4 pb-20 pt-[80px]">
    <div class="max-w-6xl mx-auto">
      <div class="flex items-center justify-between mb-6">
        <div>
          <h1 class="text-2xl font-bold text-white mb-1">用户管理</h1>
          <p class="text-gray-400 text-sm">管理系统内的所有用户账号</p>
        </div>
        <div class="flex gap-3">
          <button 
            class="px-4 py-2 rounded bg-[#333] text-white hover:bg-[#444] transition-colors flex items-center gap-2 border-none cursor-pointer"
            @click="fetchUsers"
          >
            <reload-outlined :class="{ 'animate-spin': loading }" />
            刷新
          </button>
          <button 
            class="px-4 py-2 rounded bg-[#fe2c55] text-white hover:bg-[#e62548] transition-colors flex items-center gap-2 shadow-lg shadow-red-900/20 border-none cursor-pointer"
            @click="showAddModal"
          >
            <plus-outlined />
            新增用户
          </button>
        </div>
      </div>
      
      <div class="bg-[#161823] rounded-lg border border-white/5 overflow-hidden">
        <a-table
          :columns="columns"
          :data-source="users"
          :loading="loading"
          row-key="id"
          :pagination="{ pageSize: 10, theme: 'dark' }"
          class="dark-table"
        >
          <template #bodyCell="{ column, record }">
            <!-- 头像/用户名 -->
            <template v-if="column.key === 'username'">
              <div class="flex items-center gap-3">
                <a-avatar :size="32" :src="resolveAvatarUrl(record.avatar) || DEFAULT_AVATAR" class="bg-gray-700" />
                <div class="flex flex-col">
                  <span class="text-white font-medium">{{ record.name }}</span>
                  <span class="text-gray-500 text-xs">@{{ record.username }}</span>
                </div>
              </div>
            </template>

            <!-- 角色 -->
            <template v-if="column.key === 'role'">
              <span 
                class="px-2 py-0.5 rounded text-xs font-medium border"
                :class="record.role === 'ADMIN' ? 'bg-red-500/10 text-red-400 border-red-500/20' : 'bg-blue-500/10 text-blue-400 border-blue-500/20'"
              >
                {{ record.role === 'ADMIN' ? '管理员' : '普通用户' }}
              </span>
            </template>
            
            <!-- 状态 -->
            <template v-if="column.key === 'status'">
              <div class="flex items-center gap-1.5">
                <div class="w-1.5 h-1.5 rounded-full" :class="record.status === 'ACTIVE' ? 'bg-green-500' : 'bg-red-500'"></div>
                <span class="text-sm" :class="record.status === 'ACTIVE' ? 'text-gray-300' : 'text-gray-500'">
                  {{ record.status === 'ACTIVE' ? '正常' : '已禁用' }}
                </span>
              </div>
            </template>
            
            <template v-if="column.key === 'action'">
              <div class="flex items-center gap-3">
                <button class="text-blue-400 hover:text-blue-300 text-sm transition-colors bg-transparent border-none cursor-pointer" @click="editUser(record)">编辑</button>
                <a-popconfirm
                  title="确定要删除该用户吗？"
                  ok-text="删除"
                  cancel-text="取消"
                  @confirm="deleteUser(record.id)"
                >
                  <button class="text-gray-500 hover:text-red-400 text-sm transition-colors bg-transparent border-none cursor-pointer">删除</button>
                </a-popconfirm>
              </div>
            </template>
          </template>
        </a-table>
      </div>
    </div>

    <!-- 新增/编辑用户弹窗 -->
    <a-modal
      v-model:open="modalVisible"
      :title="modalTitle"
      @ok="handleSubmit"
      wrap-class-name="dark-modal"
    >
      <a-form :model="formData" layout="vertical" class="mt-4">
        <a-form-item label="用户名" required>
          <a-input v-model:value="formData.username" placeholder="请输入用户名" :disabled="isEdit" />
        </a-form-item>
        <a-form-item label="姓名" required>
          <a-input v-model:value="formData.name" placeholder="请输入姓名" />
        </a-form-item>
        <a-form-item label="邮箱">
          <a-input v-model:value="formData.email" placeholder="请输入邮箱" />
        </a-form-item>
        <a-form-item label="年龄">
          <a-input-number v-model:value="formData.age" :min="1" :max="150" class="w-full" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import { getUsers, createUser, updateUser, deleteUserById } from '../api/user'
import { DEFAULT_AVATAR, resolveAvatarUrl } from '@/utils/constants'
import { 
  ReloadOutlined, 
  PlusOutlined, 
  UserOutlined,
  MailOutlined
} from '@ant-design/icons-vue'

const columns = [
  { title: '用户', dataIndex: 'username', key: 'username' },
  { title: '邮箱', dataIndex: 'email', key: 'email' },
  { title: '年龄', dataIndex: 'age', key: 'age', width: 80 },
  { title: '角色', key: 'role', width: 100 },
  { title: '状态', key: 'status', width: 100 },
  { title: '操作', key: 'action', width: 150 }
]

const users = ref([])
const loading = ref(false)
const modalVisible = ref(false)
const modalTitle = ref('新增用户')
const isEdit = ref(false)
const formData = reactive({
  id: null,
  username: '',
  name: '',
  email: '',
  age: 18
})

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await getUsers()
    if (res.code === 200) {
      users.value = res.data
    }
  } catch (error) {
    message.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const showAddModal = () => {
  modalTitle.value = '新增用户'
  isEdit.value = false
  Object.assign(formData, {
    id: null,
    username: '',
    name: '',
    email: '',
    age: 18
  })
  modalVisible.value = true
}

const editUser = (record) => {
  modalTitle.value = '编辑用户'
  isEdit.value = true
  Object.assign(formData, record)
  modalVisible.value = true
}

const handleSubmit = async () => {
  if (!formData.username || !formData.name) {
    return message.warning('请填写必填项')
  }
  
  try {
    if (isEdit.value) {
      await updateUser(formData.id, formData)
      message.success('更新成功')
    } else {
      await createUser(formData)
      message.success('创建成功')
    }
    modalVisible.value = false
    fetchUsers()
  } catch (error) {
    message.error(isEdit.value ? '更新失败' : '创建失败')
  }
}

const deleteUser = async (id) => {
  try {
    await deleteUserById(id)
    message.success('删除成功')
    fetchUsers()
  } catch (error) {
    message.error('删除失败')
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style>
/* Dark Table Overrides */
.dark-table .ant-table {
  background: transparent !important;
  color: #fff !important;
}
.dark-table .ant-table-thead > tr > th {
  background: #1f212e !important;
  color: #888 !important;
  border-bottom: 1px solid #2a2c35 !important;
}
.dark-table .ant-table-tbody > tr > td {
  background: transparent !important;
  color: #fff !important;
  border-bottom: 1px solid #222 !important;
}
.dark-table .ant-table-tbody > tr:hover > td {
  background: #2a2c35 !important;
}
.dark-table .ant-pagination-item-active {
  background: #fe2c55 !important;
  border-color: #fe2c55 !important;
}
.dark-table .ant-pagination-item a {
  color: #fff !important;
}

/* Modal Dark Mode */
.dark-modal .ant-modal-content {
  background-color: #1f212e;
  color: white;
}
.dark-modal .ant-modal-header {
  background-color: #1f212e;
  border-bottom: 1px solid #333;
}
.dark-modal .ant-modal-title {
  color: white;
}
.dark-modal .ant-modal-close {
  color: white;
}
.dark-modal .ant-form-item-label > label {
  color: #aaa;
}
.dark-modal .ant-input, .dark-modal .ant-input-number {
  background-color: #161823;
  border-color: #333;
  color: white;
}
</style>
