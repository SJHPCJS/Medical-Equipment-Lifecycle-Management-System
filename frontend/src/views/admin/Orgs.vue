<template>
  <div class="card" style="padding:16px;">
    <div class="title-lg">Organization & Departments</div>
    <div class="subtitle" style="margin-top:8px;">Data is loaded from backend APIs.</div>

    <div class="ui-toolbar" style="margin-top:16px; justify-content:space-between; width:100%">
      <div style="display:flex; gap:8px; flex-wrap:wrap;">
        <input class="input" v-model="keyword" placeholder="Search by name" />
        <button class="btn" @click="keyword=''">Reset</button>
      </div>
      <button class="btn btn-primary" @click="openCreate">Add Department</button>
    </div>

    <div class="table-wrapper" style="margin-top:16px; overflow:auto;">
      <table class="ui-table" style="width:100%; max-width:100%; table-layout:auto;">
        <thead>
          <tr>
            <th style="width:140px;">Dept ID</th>
            <th style="min-width:220px;">Name</th>
            <th style="width:120px;">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading"><td colspan="3"><TableSkeleton :rows="5" /></td></tr>
          <tr v-else-if="filtered.length===0">
            <td colspan="3"><EmptyState title="No departments" hint="Create a department to get started." /></td>
          </tr>
          <tr v-else v-for="d in filtered" :key="d.id">
            <td>{{ d.id }}</td>
            <td>{{ d.name }}</td>
            <td style="white-space:nowrap;">
              <button class="btn btn-blue" @click="openEdit(d)">Edit</button>
              <button class="btn btn-red" style="margin-left:8px;" @click="remove(d)">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal -->
    <div v-if="modal.open" class="ui-modal-backdrop">
      <div class="ui-modal card">
        <div class="title-lg">{{ modal.mode==='create' ? 'Add Department' : 'Edit Department' }}</div>
        <div class="ui-form-grid">
          <div>
            <label>Dept ID</label>
            <input class="input" :value="modal.form.id || '(auto)'" disabled />
          </div>
          <div>
            <label>Name</label>
            <input class="input" v-model="modal.form.name" placeholder="Enter name" />
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
import { reactive, ref, computed, onMounted } from 'vue'
import EmptyState from '@/components/admin/EmptyState.vue'
import TableSkeleton from '@/components/admin/TableSkeleton.vue'
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
    box.innerHTML = `<div style="font-weight:700;">Confirm</div><div style="margin-top:8px;">${message}</div><div style="margin-top:12px; display:flex; justify-content:flex-end; gap:8px;"><button id="cancel" class="btn">Cancel</button><button id="ok" class="btn btn-primary">OK</button></div>`
    overlay.appendChild(box)
    document.body.appendChild(overlay)
    overlay.querySelector('#cancel').addEventListener('click', () => { document.body.removeChild(overlay); resolve(false) })
    overlay.querySelector('#ok').addEventListener('click', () => { document.body.removeChild(overlay); resolve(true) })
  })
}

const state = reactive({ departments: [] })
const loading = ref(true)
const keyword = ref('')

const filtered = computed(() => {
  if (!keyword.value) return state.departments
  return state.departments.filter(d => d.name.toLowerCase().includes(keyword.value.toLowerCase()))
})

const modal = reactive({ open: false, mode: 'create', form: { id: '', name: '' } })

function nextDeptId() { return '' }

function openCreate() {
  modal.open = true
  modal.mode = 'create'
  modal.form = { id: '', name: '' }
}

function openEdit(d) {
  modal.open = true
  modal.mode = 'edit'
  modal.form = { id: d.id, name: d.name }
}

function closeModal() { modal.open = false }

async function refresh() {
  loading.value = true
  const resp = await fetch('/req/admin/departments')
  const json = await resp.json()
  if (json.code === '000') state.departments = json.data || []
  loading.value = false
}

async function save() {
  const payload = { ...modal.form }
  if (!payload.name || !payload.name.trim()) { return showDialog('Department name is required') }
  if (modal.mode === 'create') {
    const resp = await fetch('/req/admin/newDepartment', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ department_name: payload.name }) })
    const json = await resp.json().catch(() => ({ code: 'ERR' }))
    if (json.code !== '000') { return showDialog(json.message || 'Failed to add department') }
  } else {
    const resp = await fetch('/req/admin/changeDepartmentName', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ department_id: payload.id, department_name: payload.name }) })
    const json = await resp.json().catch(() => ({ code: 'ERR' }))
    if (json.code !== '000') { return showDialog(json.message || 'Failed to update department') }
  }
  await refresh()
  closeModal()
}

async function remove(dep) {
  if (!(await showConfirm(`Delete department "${dep.name}"?`))) return
  const resp = await fetch(`/req/admin/delDepartment?departmentId=${encodeURIComponent(dep.id)}`, { method: 'DELETE' })
  const json = await resp.json().catch(() => ({ code: 'ERR' }))
  if (json.code !== '000') return showDialog(json.message || 'Failed to delete department')
  await refresh()
}

onMounted(refresh)
</script>

<style scoped>
</style>

