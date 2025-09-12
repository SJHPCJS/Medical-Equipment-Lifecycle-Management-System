<template>
  <div class="card" style="padding:16px;">
    <div class="title-lg">Organization & Departments</div>
    <div class="subtitle" style="margin-top:8px;">Manage departments using frontend-only mock data.</div>

    <div style="margin-top:16px; display:flex; gap:8px; justify-content:space-between; flex-wrap:wrap;">
      <div style="display:flex; gap:8px;">
        <input class="input" v-model="keyword" placeholder="Search by name" />
        <button class="btn" @click="keyword=''">Reset</button>
      </div>
      <button class="btn btn-primary" @click="openCreate">Add Department</button>
    </div>

    <div class="table-wrapper" style="margin-top:16px; overflow:auto;">
      <table class="table">
        <thead>
          <tr>
            <th>Dept ID</th>
            <th>Name</th>
            <th style="width:120px;">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="d in filtered" :key="d.id">
            <td>{{ d.id }}</td>
            <td>{{ d.name }}</td>
            <td>
              <button class="btn" @click="openEdit(d)">Edit</button>
              <button class="btn" style="margin-left:8px;" @click="remove(d)">Delete</button>
            </td>
          </tr>
          <tr v-if="filtered.length===0">
            <td colspan="3" style="text-align:center; color:var(--color-muted);">No data</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal -->
    <div v-if="modal.open" class="modal-backdrop">
      <div class="modal card">
        <div class="title-lg">{{ modal.mode==='create' ? 'Add Department' : 'Edit Department' }}</div>
        <div class="form-grid">
          <div>
            <label>Dept ID</label>
            <input class="input" :value="modal.form.id" disabled />
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
import { reactive, ref, computed } from 'vue'
import { clone, departments as seedDepartments } from '@/mocks/admin.js'

const state = reactive({ departments: clone(seedDepartments) })
const keyword = ref('')

const filtered = computed(() => {
  if (!keyword.value) return state.departments
  return state.departments.filter(d => d.name.toLowerCase().includes(keyword.value.toLowerCase()))
})

const modal = reactive({ open: false, mode: 'create', form: { id: '', name: '' } })

function nextDeptId() {
  const num = state.departments
    .map(d => Number((d.id || '').split('-')[1] || 0))
    .reduce((m, v) => Math.max(m, v), 0) + 1
  return `DPT-${String(num).padStart(3, '0')}`
}

function openCreate() {
  modal.open = true
  modal.mode = 'create'
  modal.form = { id: nextDeptId(), name: '' }
}

function openEdit(d) {
  modal.open = true
  modal.mode = 'edit'
  modal.form = { id: d.id, name: d.name }
}

function closeModal() { modal.open = false }

function save() {
  const payload = clone(modal.form)
  if (modal.mode === 'create') {
    state.departments.push(payload)
  } else {
    const idx = state.departments.findIndex(d => d.id === payload.id)
    if (idx !== -1) state.departments[idx] = payload
  }
  closeModal()
}

function remove(dep) {
  if (!confirm(`Delete department "${dep.name}"?`)) return
  state.departments = state.departments.filter(d => d.id !== dep.id)
}
</script>

<style scoped>
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { padding: 10px 12px; border-bottom: 1px solid #e5e7eb; text-align: left; white-space: nowrap; }
.table th { background: #f9fafb; font-weight: 700; }

.modal-backdrop { position: fixed; inset: 0; background: rgba(0,0,0,0.35); display:flex; align-items:center; justify-content:center; padding:16px; }
.modal { width: min(560px, 100%); padding: 16px; }
.form-grid { margin-top: 16px; display:grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 12px; }
</style>

