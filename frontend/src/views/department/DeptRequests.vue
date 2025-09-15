<template>
  <div class="card" style="padding:16px;">
    <div class="title-lg">Equipment Requests</div>
    <div class="subtitle" style="margin-top:8px;">Request new devices or accessories for the department.</div>

    <!-- Filters & Create -->
    <div class="filters" style="margin-top:16px; display:grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap:12px;">
      <input class="input" v-model="filters.keyword" placeholder="Search by id/content" />
      <select class="input" v-model="filters.status">
        <option value="">All status</option>
        <option>Pending</option>
        <option>Approved</option>
        <option>Rejected</option>
      </select>
      <div style="display:flex; gap:8px;">
        <button class="btn" @click="resetFilters">Reset</button>
        <button class="btn btn-primary" @click="openCreate">New Request</button>
      </div>
    </div>

    <!-- Table -->
    <div class="table-wrapper" style="margin-top:16px; overflow:auto;">
      <table class="table" style="table-layout:fixed; width:100%;">
        <thead>
          <tr>
            <th>ID</th>
            <th>Kind</th>
            <th>Content</th>
            <th>Status</th>
            <th style="width:140px;">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="r in filtered" :key="r.id">
            <td>{{ r.id }}</td>
            <td>{{ r.kind }}</td>
            <td>{{ r.content }}</td>
            <td>{{ r.status }}</td>
            <td style="white-space:nowrap;">
              <button class="btn btn-red" @click="cancel(r)" v-if="r.status==='Pending'">Cancel</button>
              <button class="btn btn-green" v-else @click="view(r)">View</button>
            </td>
          </tr>
          <tr v-if="filtered.length===0">
            <td colspan="5" style="text-align:center; color:var(--color-muted);">No requests</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Create Modal -->
    <div v-if="modal.open" class="modal-backdrop">
      <div class="modal card">
        <div class="title-lg">New Equipment Request</div>
        <div class="form-grid">
          <div>
            <label>Kind</label>
            <select class="input" v-model="modal.form.kind">
              <option>New Device</option>
              <option>Accessory</option>
              <option>Consumables</option>
              <option>Upgrade</option>
            </select>
          </div>
          <div style="grid-column: 1 / -1;">
            <label>Content</label>
            <textarea class="input" v-model="modal.form.content" placeholder="Describe your need, quantity, preferred vendor, budget, etc."></textarea>
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
import { clone, deptRequests as seed } from '@/mocks/department.js'

const state = reactive({ list: clone(seed) })
const filters = reactive({ keyword: '', status: '' })

const filtered = computed(() => {
  const kw = filters.keyword.toLowerCase()
  return state.list.filter(r => {
    const matchKw = !kw || `${r.id} ${r.content}`.toLowerCase().includes(kw)
    const matchStatus = !filters.status || r.status === filters.status
    return matchKw && matchStatus
  })
})

function resetFilters() { filters.keyword=''; filters.status='' }

const modal = reactive({ open: false, form: { kind: 'New Device', content: '' } })
function openCreate() { modal.open = true; modal.form = { kind: 'New Device', content: '' } }
function closeCreate() { modal.open = false }
function save() {
  const id = `REQ-${String(Math.floor(Math.random()*100000)).padStart(4,'0')}`
  state.list.unshift({ id, status: 'Pending', ...modal.form })
  closeCreate()
}

function cancel(r) { r.status = 'Rejected' }
function view(r) { alert(`View ${r.id} (demo only)`) }
</script>

<style scoped>
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { padding: 10px 12px; border-bottom: 1px solid #e5e7eb; text-align: left; white-space: normal; word-break: break-word; }
.table th { background: #f9fafb; font-weight: 700; }
.modal-backdrop { position: fixed; inset: 0; background: rgba(0,0,0,0.35); display:flex; align-items:center; justify-content:center; padding:16px; }
.modal { width: min(720px, 100%); padding: 16px; }
.form-grid { margin-top: 16px; display:grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 12px; }
textarea.input { height: 120px; resize: vertical; }
</style>
