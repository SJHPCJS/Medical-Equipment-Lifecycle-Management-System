<template>
  <div class="card" style="padding:16px;">
    <div class="title-lg">Repair Requests</div>
    <div class="subtitle" style="margin-top:8px;">Create and track repair/maintenance/parts replacement tickets.</div>

    <!-- Filters -->
    <div class="filters" style="margin-top:16px; display:grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap:12px;">
      <input class="input" v-model="filters.keyword" placeholder="Search by id/device/desc" />
      <select class="input" v-model="filters.type">
        <option value="">All types</option>
        <option>Repair</option>
        <option>Maintenance</option>
        <option>Part Replacement</option>
      </select>
      <select class="input" v-model="filters.status">
        <option value="">All status</option>
        <option>Pending Review</option>
        <option>In Repair</option>
        <option>In Acceptance</option>
        <option>Completed</option>
        <option>Rejected</option>
      </select>
      <div style="display:flex; gap:8px;">
        <button class="btn" @click="resetFilters">Reset</button>
        <button class="btn btn-primary" @click="openCreate">New Ticket</button>
      </div>
    </div>

    <!-- Table -->
    <div class="table-wrapper" style="margin-top:16px; overflow:auto;">
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Device</th>
            <th>Type</th>
            <th>Description</th>
            <th>Status</th>
            <th>Created</th>
            <th style="width:160px;">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="t in filtered" :key="t.id">
            <td>{{ t.id }}</td>
            <td>{{ t.deviceId }}</td>
            <td>{{ t.type }}</td>
            <td>{{ t.description }}</td>
            <td>{{ t.status }}</td>
            <td>{{ fmt(t.createdAt) }}</td>
            <td>
              <button class="btn" @click="accept(t)" v-if="t.status==='In Acceptance'">Confirm</button>
              <button class="btn" @click="view(t)" v-else>View</button>
            </td>
          </tr>
          <tr v-if="filtered.length===0">
            <td colspan="7" style="text-align:center; color:var(--color-muted);">No tickets</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Create Modal -->
    <div v-if="modal.open" class="modal-backdrop">
      <div class="modal card">
        <div class="title-lg">New Repair Ticket</div>
        <div class="form-grid">
          <div>
            <label>Device ID</label>
            <input class="input" v-model="modal.form.deviceId" placeholder="e.g. EQ-0001" />
          </div>
          <div>
            <label>Type</label>
            <select class="input" v-model="modal.form.type">
              <option>Repair</option>
              <option>Maintenance</option>
              <option>Part Replacement</option>
            </select>
          </div>
          <div style="grid-column: 1 / -1;">
            <label>Description</label>
            <textarea class="input" v-model="modal.form.description" placeholder="Describe the issue"></textarea>
          </div>
        </div>
        <div style="display:flex; gap:8px; justify-content:flex-end; margin-top:16px;">
          <button class="btn" @click="closeCreate">Cancel</button>
          <button class="btn btn-primary" @click="save">Submit</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, computed } from 'vue'
import { clone, deptRepairTickets as seed } from '@/mocks/department.js'

const state = reactive({ list: clone(seed) })
const filters = reactive({ keyword: '', type: '', status: '' })

const filtered = computed(() => {
  const kw = filters.keyword.toLowerCase()
  return state.list.filter(t => {
    const matchKw = !kw || `${t.id} ${t.deviceId} ${t.description}`.toLowerCase().includes(kw)
    const matchType = !filters.type || t.type === filters.type
    const matchStatus = !filters.status || t.status === filters.status
    return matchKw && matchType && matchStatus
  })
})

function resetFilters() { filters.keyword=''; filters.type=''; filters.status='' }

const modal = reactive({ open: false, form: { deviceId: '', type: 'Repair', description: '' } })
function openCreate() { modal.open = true; modal.form = { deviceId: '', type: 'Repair', description: '' } }
function closeCreate() { modal.open = false }
function save() {
  const id = `DR-${String(Math.floor(Math.random()*100000)).padStart(4,'0')}`
  const createdAt = new Date().toISOString()
  state.list.unshift({ id, createdAt, status: 'Pending Review', ...modal.form })
  closeCreate()
}

function accept(t) { t.status = 'Completed' }
function view(t) { alert(`View ${t.id} (demo only)`) }

function fmt(ts) { try { return new Date(ts).toLocaleString() } catch { return ts } }
</script>

<style scoped>
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { padding: 10px 12px; border-bottom: 1px solid #e5e7eb; text-align: left; white-space: nowrap; }
.table th { background: #f9fafb; font-weight: 700; }
.modal-backdrop { position: fixed; inset: 0; background: rgba(0,0,0,0.35); display:flex; align-items:center; justify-content:center; padding:16px; }
.modal { width: min(720px, 100%); padding: 16px; }
.form-grid { margin-top: 16px; display:grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 12px; }
textarea.input { height: 120px; resize: vertical; }
</style>
