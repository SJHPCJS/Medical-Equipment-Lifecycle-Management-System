<template>
  <div class="card" style="padding:16px;">
    <div class="title-lg">Users & Access - User Management</div>
    <div class="subtitle" style="margin-top:8px;">Frontend-only mock. Replace with API later.</div>

    <!-- Filters -->
    <div class="filters" style="margin-top:16px; display:grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap:12px;">
      <input class="input" v-model="filters.username" placeholder="Search by username" />
      <div>
        <label>User Role</label>
        <MultiSelect v-model="filters.roleIds" :options="roles.map(r => ({ value: r.id, label: r.name }))" placeholder="All roles" />
      </div>
      <div>
        <label>Department</label>
        <MultiSelect v-model="filters.departmentIds" :options="departments.map(d => ({ value: d.id, label: d.name }))" placeholder="All departments" />
      </div>
      <div style="display:flex; gap:8px;">
        <button class="btn" @click="resetFilters">Reset</button>
        <button class="btn btn-primary" @click="openCreate">Add User</button>
      </div>
    </div>

    <!-- Table -->
    <div class="table-wrapper" style="margin-top:16px; overflow:auto;">
      <table class="table">
        <thead>
          <tr>
            <th>User ID</th>
            <th>Username</th>
            <th>User Role</th>
            <th>Department</th>
            <th style="width:140px;">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="u in pagedUsers" :key="u.id">
            <td>{{ u.id }}</td>
            <td>{{ u.username }}</td>
            <td>{{ roleName(u.roleId) }}</td>
            <td>{{ departmentName(u.departmentId) }}</td>
            <td style="white-space:nowrap;">
              <button class="btn btn-blue" @click="openEdit(u)">Edit</button>
              <button class="btn btn-red" style="margin-left:8px;" @click="remove(u)">Delete</button>
            </td>
          </tr>
          <tr v-if="pagedUsers.length === 0">
            <td colspan="5" style="text-align:center; color:var(--color-muted);">No data</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div class="pagination" style="display:flex; align-items:center; gap:8px; justify-content:flex-end; margin-top:12px;">
      <button class="btn" :disabled="page===1" @click="page=1">First</button>
      <button class="btn" :disabled="page===1" @click="page--">Prev</button>
      <span style="color:var(--color-muted);">Page {{ page }} / {{ totalPages }}</span>
      <button class="btn" :disabled="page===totalPages" @click="page++">Next</button>
      <button class="btn" :disabled="page===totalPages" @click="page=totalPages">Last</button>
      <select class="input" style="width:auto;" v-model.number="pageSize">
        <option :value="5">5</option>
        <option :value="10">10</option>
        <option :value="20">20</option>
      </select>
    </div>

    <!-- Modal -->
    <div v-if="modal.open" class="modal-backdrop">
      <div class="modal card">
        <div class="title-lg">{{ modal.mode === 'create' ? 'Add User' : 'Edit User' }}</div>
        <div class="form-grid">
          <div>
            <label>User ID</label>
            <input class="input" :value="modal.form.id" disabled />
          </div>
          <div>
            <label>Username</label>
            <input class="input" v-model="modal.form.username" placeholder="Enter username" />
          </div>
          <div>
            <label>User Role</label>
            <select class="input" v-model="modal.form.roleId">
              <option v-for="r in roles" :key="r.id" :value="r.id">{{ r.name }}</option>
            </select>
          </div>
          <div>
            <label>Department</label>
            <select class="input" v-model="modal.form.departmentId">
              <option v-for="d in departments" :key="d.id" :value="d.id">{{ d.name }}</option>
            </select>
          </div>
          <div v-if="modal.mode==='create' || modal.mode==='edit'">
            <label>Password</label>
            <div style="display:flex; gap:8px; align-items:center;">
              <input class="input" v-model="modal.form.password" :type="showPassword ? 'text' : 'password'" placeholder="Enter password" />
              <button class="btn btn-blue" style="width:auto;" @click="showPassword=!showPassword">{{ showPassword ? 'Hide' : 'Show' }}</button>
            </div>
          </div>
        </div>
        <div style="display:flex; gap:8px; justify-content:flex-end; margin-top:16px;">
          <button class="btn" @click="closeModal">Cancel</button>
          <button class="btn btn-primary" @click="save">Save</button>
        </div>
      </div>
    </div>
  </div>
  
</template>

<script setup>
import { reactive, ref, computed, watch } from 'vue'
import MultiSelect from '@/components/MultiSelect.vue'
import { clone, users as seedUsers, roles, departments, getNextUserId } from '@/mocks/admin.js'

const state = reactive({
  users: clone(seedUsers),
})

const filters = reactive({ username: '', roleIds: [], departmentIds: [] })
const page = ref(1)
const pageSize = ref(10)

const filteredUsers = computed(() => {
  return state.users.filter(u => {
    const matchName = !filters.username || u.username.toLowerCase().includes(filters.username.toLowerCase())
    const matchRole = filters.roleIds.length===0 || filters.roleIds.includes(u.roleId)
    const matchDept = filters.departmentIds.length===0 || filters.departmentIds.includes(u.departmentId)
    return matchName && matchRole && matchDept
  })
})

const totalPages = computed(() => Math.max(1, Math.ceil(filteredUsers.value.length / pageSize.value)))

watch([filteredUsers, pageSize], () => {
  if (page.value > totalPages.value) page.value = totalPages.value
})

const pagedUsers = computed(() => {
  const start = (page.value - 1) * pageSize.value
  return filteredUsers.value.slice(start, start + pageSize.value)
})

function resetFilters() {
  filters.username = ''
  filters.roleIds = []
  filters.departmentIds = []
}

function roleName(roleId) {
  const r = roles.find(r => r.id === roleId)
  return r ? r.name : '-'
}

function departmentName(deptId) {
  const d = departments.find(d => d.id === deptId)
  return d ? d.name : '-'
}

const modal = reactive({
  open: false,
  mode: 'create', // 'create' | 'edit'
  form: { id: '', username: '', roleId: roles[0]?.id || '', departmentId: departments[0]?.id || '', password: '' },
})
const showPassword = ref(false)

function openCreate() {
  modal.open = true
  modal.mode = 'create'
  modal.form = { id: getNextUserId(state.users), username: '', roleId: roles[0]?.id || '', departmentId: departments[0]?.id || '', password: '' }
  showPassword.value = false
}

function openEdit(user) {
  modal.open = true
  modal.mode = 'edit'
  modal.form = { id: user.id, username: user.username, roleId: user.roleId, departmentId: user.departmentId, password: user.password || '' }
  showPassword.value = false
}

function closeModal() {
  modal.open = false
}

function save() {
  const payload = clone(modal.form)
  if (modal.mode === 'create') {
    state.users.push({ id: payload.id, username: payload.username, roleId: payload.roleId, departmentId: payload.departmentId, password: payload.password })
  } else {
    const idx = state.users.findIndex(u => u.id === payload.id)
    if (idx !== -1) {
      state.users[idx] = { id: payload.id, username: payload.username, roleId: payload.roleId, departmentId: payload.departmentId, password: payload.password }
    }
  }
  closeModal()
}

function remove(user) {
  if (!confirm(`Delete user "${user.username}"?`)) return
  state.users = state.users.filter(u => u.id !== user.id)
}
</script>

<style scoped>
.table {
  width: 100%;
  border-collapse: collapse;
}
.table th, .table td {
  padding: 10px 12px;
  border-bottom: 1px solid #e5e7eb;
  text-align: left;
  white-space: normal;
  word-break: break-word;
}
.table th { background: #f9fafb; font-weight: 700; }

.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
}
.modal {
  width: min(720px, 100%);
  padding: 16px;
}
.form-grid {
  margin-top: 16px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 12px;
}
</style>

