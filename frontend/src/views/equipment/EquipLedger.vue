<template>
  <div class="card" style="padding:16px;">
    <div class="title-lg">Equipment Ledger</div>
    <div class="subtitle" style="margin-top:8px;">Manage equipment inventory using frontend-only mock data.</div>

    <div class="filters" style="margin-top:16px; display:grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap:12px;">
      <input class="input" v-model="filters.keyword" placeholder="Search by id/type/vendor" />
      <div>
        <label>Type</label>
        <MultiSelect v-model="filters.types" :options="types.map(t => ({ value: t, label: t }))" placeholder="All types" />
      </div>
      <div>
        <label>Status</label>
        <MultiSelect v-model="filters.statuses" :options="statuses.map(s => ({ value: s, label: s }))" placeholder="All status" />
      </div>
      <div>
        <label>Department</label>
        <MultiSelect v-model="filters.departmentIds" :options="departments.map(d => ({ value: d.id, label: d.name }))" placeholder="All departments" />
      </div>
      <div>
        <label>Vendor</label>
        <MultiSelect v-model="filters.vendors" :options="vendorsList.map(v => ({ value: v, label: v }))" placeholder="All vendors" />
      </div>
      <div style="display:flex; gap:8px; align-items:end;">
        <button class="btn" @click="resetFilters">Reset</button>
        <button class="btn btn-primary" @click="exportCsv">Export CSV</button>
      </div>
    </div>

    <div class="table-wrapper" style="margin-top:16px; overflow:auto;">
      <table class="table" style="table-layout:fixed; width:100%;">
        <thead>
          <tr>
            <th>Device ID</th>
            <th>Type</th>
            <th>Status</th>
            <th>Department</th>
            <th>Vendor</th>
            <th style="width:280px;">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="d in filtered" :key="d.id" @click="openDetail(d)" style="cursor:pointer;">
            <td>{{ d.id }}</td>
            <td>{{ d.type }}</td>
            <td>
              <template v-if="d.status==='Unassigned'">
                <select class="input" style="width:auto;" v-model="d.departmentId" @click.stop>
                  <option value="">-- Assign Dept --</option>
                  <option v-for="dep in departments" :key="dep.id" :value="dep.id">{{ dep.name }}</option>
                </select>
              </template>
              <template v-else>{{ d.status }}</template>
            </td>
            <td>{{ departmentName(d.departmentId) }}</td>
            <td>{{ d.vendor }}</td>
            <td style="white-space:nowrap;">
              <button class="btn" @click.stop="upload('Manual', d)">Upload Manual</button>
              <button class="btn" style="margin-left:8px;" @click.stop="upload('Warranty', d)">Upload Warranty</button>
              <button class="btn btn-blue" style="margin-left:8px;" @click.stop="edit(d)">Edit</button>
              <button class="btn btn-red" style="margin-left:8px;" @click.stop="remove(d)">Delete</button>
            </td>
          </tr>
          <tr v-if="filtered.length===0">
            <td colspan="6" style="text-align:center; color:var(--color-muted);">No data</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="detail.open" class="drawer-backdrop" @click="closeDetail">
      <div class="drawer card" @click.stop>
        <div class="title-lg">Device Detail</div>
        <div class="subtitle">ID: {{ detail.device.id }}</div>
        <div style="margin-top:12px;">
          <div><b>Type:</b> {{ detail.device.type }}</div>
          <div><b>Status:</b> {{ detail.device.status }}</div>
          <div><b>Department:</b> {{ departmentName(detail.device.departmentId) }}</div>
          <div><b>Vendor:</b> {{ detail.device.vendor }}</div>
        </div>
        <div style="display:flex; gap:8px; justify-content:flex-end; margin-top:16px;">
          <button class="btn" @click="closeDetail">Close</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, computed } from 'vue'
import MultiSelect from '@/components/MultiSelect.vue'
import { devices as seedDevices, deviceStatuses as statuses, clone } from '@/mocks/equipment.js'
import { departments } from '@/mocks/admin.js'

const state = reactive({ devices: clone(seedDevices) })

const types = computed(() => Array.from(new Set(state.devices.map(d => d.type))))
const vendorsList = computed(() => Array.from(new Set(state.devices.map(d => d.vendor))))

const filters = reactive({ keyword: '', types: [], statuses: [], departmentIds: [], vendors: [] })

const filtered = computed(() => {
  const kw = filters.keyword.toLowerCase()
  return state.devices.filter(d => {
    const matchKw = !kw || `${d.id} ${d.type} ${d.vendor}`.toLowerCase().includes(kw)
    const matchType = filters.types.length===0 || filters.types.includes(d.type)
    const matchStatus = filters.statuses.length===0 || filters.statuses.includes(d.status)
    const matchDept = filters.departmentIds.length===0 || filters.departmentIds.includes(d.departmentId)
    const matchVendor = filters.vendors.length===0 || filters.vendors.includes(d.vendor)
    return matchKw && matchType && matchStatus && matchDept && matchVendor
  })
})

function resetFilters() { filters.keyword=''; filters.types=[]; filters.statuses=[]; filters.departmentIds=[]; filters.vendors=[] }

function departmentName(id) { const dep = departments.find(d => d.id === id); return dep ? dep.name : '-' }

function exportCsv() {
  const rows = [['Device ID','Type','Status','Department','Vendor'], ...filtered.value.map(d => [d.id, d.type, d.status, departmentName(d.departmentId), d.vendor])]
  const csv = rows.map(r => r.map(x => `"${String(x).replaceAll('"','""')}"`).join(',')).join('\n')
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'equipment-ledger.csv'
  a.click()
  URL.revokeObjectURL(url)
}

const detail = reactive({ open: false, device: {} })
function openDetail(d) { detail.open = true; detail.device = d }
function closeDetail() { detail.open = false }

function upload(kind, d) { alert(`Demo only: upload ${kind} for ${d.id}`) }
function edit(d) { alert(`Demo only: edit ${d.id}`) }
function remove(d) { if (!confirm(`Delete device ${d.id}?`)) return; state.devices = state.devices.filter(x => x.id !== d.id) }
</script>

<style scoped>
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { padding: 10px 12px; border-bottom: 1px solid #e5e7eb; text-align: left; white-space: normal; word-break: break-word; }
.table th { background: #f9fafb; font-weight: 700; }

.drawer-backdrop { position: fixed; inset:0; background: rgba(0,0,0,0.35); display:flex; }
.drawer { margin-left:auto; width:min(480px, 100%); padding:16px; }
</style>
