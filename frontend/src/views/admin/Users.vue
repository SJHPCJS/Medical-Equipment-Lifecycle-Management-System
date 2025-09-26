<template>
  <div class="card" style="padding:16px;">
    <div class="subtitle" style="margin-top:0;">Data is loaded from backend APIs.</div>

    <!-- Filters -->
    <div class="ui-toolbar" style="margin-top:16px;">
      <input class="input" v-model="filters.username" placeholder="Search by name" />
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
      <table class="ui-table" style="width:100%; max-width: 100%; table-layout:auto;">
        <thead>
          <tr>
            <th style="width:140px;">User ID</th>
            <th>Name</th>
            <th>User Role</th>
            <th>Department</th>
            <th style="width:140px;">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading"><td colspan="5"><TableSkeleton :rows="6" /></td></tr>
          <tr v-else-if="pagedUsers.length === 0">
            <td colspan="5"><EmptyState title="No users" hint="Try adjusting filters or add a new user." /></td>
          </tr>
          <tr v-else v-for="u in pagedUsers" :key="u.id">
            <td>{{ u.id }}</td>
            <td>{{ u.username }}</td>
            <td>{{ roleName(u.roleId) }}</td>
            <td>{{ departmentName(u.departmentId) }}</td>
            <td style="white-space:nowrap;">
              <button class="btn btn-blue" @click="openEdit(u)">Edit</button>
              <button class="btn btn-red" style="margin-left:8px;" @click="remove(u)">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div class="ui-pagination" style="margin-top:12px;">
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
    <div v-if="modal.open" class="ui-modal-backdrop">
      <div class="ui-modal card">
        <div class="title-lg">{{ modal.mode === 'create' ? 'Add User' : 'Edit User' }}</div>
        <div class="ui-form-grid">
          <div v-if="modal.mode==='edit'">
            <label>User ID</label>
            <input class="input" :value="modal.form.id" disabled />
          </div>
          <div>
            <label>Name</label>
            <input class="input" v-model="modal.form.username" placeholder="Enter name" />
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
import { reactive, ref, computed, watch, onMounted } from 'vue'
import EmptyState from '@/components/admin/EmptyState.vue'
import TableSkeleton from '@/components/admin/TableSkeleton.vue'
import MultiSelect from '@/components/MultiSelect.vue'
function showDialog(message) {
  let overlay = document.createElement('div')
  overlay.style.position = 'fixed'
  overlay.style.inset = '0'
  overlay.style.background = 'rgba(0,0,0,0.35)'
  overlay.style.display = 'flex'
  overlay.style.alignItems = 'center'
  overlay.style.justifyContent = 'center'
  overlay.style.zIndex = '9999'
  const box = document.createElement('div')
  box.className = 'card'
  box.style.padding = '16px'
  box.style.maxWidth = '420px'
  box.style.minWidth = '280px'
  box.innerHTML = `<div style="font-weight:700;">Notice</div><div style="margin-top:8px;">${message}</div><div style="margin-top:12px; display:flex; justify-content:flex-end;"><button id="ok" class="btn btn-primary">OK</button></div>`
  overlay.appendChild(box)
  document.body.appendChild(overlay)
  overlay.querySelector('#ok').addEventListener('click', () => { document.body.removeChild(overlay) })
}

function showConfirm(message) {
  return new Promise(resolve => {
    let overlay = document.createElement('div')
    overlay.style.position = 'fixed'
    overlay.style.inset = '0'
    overlay.style.background = 'rgba(0,0,0,0.35)'
    overlay.style.display = 'flex'
    overlay.style.alignItems = 'center'
    overlay.style.justifyContent = 'center'
    overlay.style.zIndex = '9999'
    const box = document.createElement('div')
    box.className = 'card'
    box.style.padding = '16px'
    box.style.maxWidth = '420px'
    box.style.minWidth = '280px'
    box.innerHTML = `<div style=\"font-weight:700;\">Confirm</div><div style=\"margin-top:8px;\">${message}</div><div style=\"margin-top:12px; display:flex; justify-content:flex-end; gap:8px;\"><button id=\"cancel\" class=\"btn\">Cancel</button><button id=\"ok\" class=\"btn btn-primary\">OK</button></div>`
    overlay.appendChild(box)
    document.body.appendChild(overlay)
    overlay.querySelector('#cancel').addEventListener('click', () => { document.body.removeChild(overlay); resolve(false) })
    overlay.querySelector('#ok').addEventListener('click', () => { document.body.removeChild(overlay); resolve(true) })
  })
}

// Keep roles static on frontend for now
const roles = [
  { id: 'SYS_ADMIN', name: 'System Administrator' },
  { id: 'EQUIP_MANAGER', name: 'Equipment Manager' },
  { id: 'DEPT_USER', name: 'Department User' },
  { id: 'PROC_STAFF', name: 'Procurement Staff' },
]

const departments = ref([])
const loading = ref(true)

const state = reactive({ users: [] })

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
  const d = departments.value.find(d => d.id === deptId)
  return d ? d.name : '-'
}

const modal = reactive({
  open: false,
  mode: 'create', // 'create' | 'edit'
  form: { id: '', username: '', roleId: roles[0]?.id || '', departmentId: '', password: '' },
})
const showPassword = ref(false)

function nextUserId() { return '' }

function openCreate() {
  modal.open = true
  modal.mode = 'create'
  modal.form = { id: '', username: '', roleId: roles[0]?.id || '', departmentId: departments.value[0]?.id || '', password: '' }
  showPassword.value = false
}

function openEdit(user) {
  modal.open = true
  modal.mode = 'edit'
  modal.form = { id: user.id, username: user.username, roleId: user.roleId, departmentId: user.departmentId, password: '' }
  showPassword.value = false
}

function closeModal() {
  modal.open = false
}

function normalizeRole(dbRole) {
  switch (dbRole) {
    case 'Admin': return 'SYS_ADMIN'
    case 'E-Manager': return 'EQUIP_MANAGER'
    case 'D-User': return 'DEPT_USER'
    case 'P-Staff': return 'PROC_STAFF'
    default: return dbRole || ''
  }
}

function denormalizeRole(uiRole) {
  switch (uiRole) {
    case 'SYS_ADMIN': return 'Admin'
    case 'EQUIP_MANAGER': return 'E-Manager'
    case 'DEPT_USER': return 'D-User'
    case 'PROC_STAFF': return 'P-Staff'
    default: return uiRole || ''
  }
}

async function refresh() {
  loading.value = true
  const resp = await fetch('/req/admin/users')
  const json = await resp.json()
  if (json.code === '000') {
    state.users = (json.data || []).map(u => ({
      id: u.id,
      username: u.username,
      roleId: normalizeRole(u.roleId),
      departmentId: u.departmentId,
    }))
  }
  loading.value = false
}

async function save() {
  const p = { ...modal.form }
  if (!p.username || !p.username.trim()) { return showDialog('Name is required') }
  if (modal.mode === 'create' && (!p.password || !p.password.trim())) { return showDialog('Password is required') }
  if (modal.mode === 'create') {
    const resp = await fetch('/req/admin/user', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ name: p.username, password: p.password || '', role: denormalizeRole(p.roleId), department_id: p.departmentId }) })
    const json = await resp.json().catch(() => ({ code: 'ERR' }))
    if (json.code !== '000') { return showDialog(json.message || 'Failed to add user') }
  } else {
    const resp = await fetch('/req/admin/user', { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ account_id: String(p.id), name: p.username, password: p.password || '', role: denormalizeRole(p.roleId), department_id: p.departmentId }) })
    const json = await resp.json().catch(() => ({ code: 'ERR' }))
    if (json.code !== '000') { return showDialog(json.message || 'Failed to update user') }
  }
  await refresh()
  closeModal()
}

async function remove(user) {
  if (!(await showConfirm(`Delete user "${user.username}"?`))) return
  const resp = await fetch(`/req/admin/user?accountId=${encodeURIComponent(user.id)}`, { method: 'DELETE' })
  const json = await resp.json().catch(() => ({ code: 'ERR' }))
  if (json.code !== '000') return showDialog(json.message || 'Failed to delete user')
  await refresh()
}

async function loadDepartments() {
  const resp = await fetch('/req/admin/departments')
  const json = await resp.json()
  if (json.code === '000') departments.value = json.data || []
}

onMounted(async () => { await Promise.all([loadDepartments(), refresh()]) })
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


