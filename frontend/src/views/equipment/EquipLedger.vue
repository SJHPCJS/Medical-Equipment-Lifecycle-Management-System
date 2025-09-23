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

    <div class="cards" style="margin-top:16px; display:grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap:12px;">
      <div v-for="d in filtered" :key="d.id" class="device-card card" @click="openDetail(d)" style="cursor:pointer; padding:0; overflow:hidden;">
        <div class="image-wrap">
          <img :src="getDeviceImageUrl(d.id)" @error="onDeviceImgError" alt="device" />
          <div class="status-badge" :data-status="d.status">{{ d.status }}</div>
        </div>
        <div style="padding:12px; display:grid; gap:6px;">
          <div class="title-md">{{ d.type }}</div>
          <div class="subtitle">ID: {{ d.id }}</div>
          <div class="muted">{{ shortDesc(d.description) }}</div>
          <div class="muted">Vendor: {{ d.vendor }}</div>
          <div class="muted">Dept: {{ departmentName(d.departmentId) || '-' }}</div>
          <div style="display:flex; gap:8px; margin-top:8px;">
            <button class="btn" @click.stop="upload('Manual', d)">Upload Manual</button>
            <button class="btn" @click.stop="upload('Warranty', d)">Warranty</button>
          </div>
        </div>
      </div>
      <div v-if="filtered.length===0" class="subtitle" style="padding:16px;">No data</div>
    </div>

    <div v-if="detail.open" class="drawer-backdrop" @click="closeDetail">
      <div class="drawer card" @click.stop>
        <div class="title-lg">Device Detail</div>
        <div class="subtitle">ID: {{ detail.device.id }}</div>
        <div class="detail-grid">
          <div class="detail-image">
            <img :src="getDeviceImageUrl(detail.device.id)" @error="onDeviceImgError" alt="device" />
          </div>
          <div class="detail-info">
            <div style="display:grid; gap:8px;">
              <div><b>Type:</b> {{ detail.device.type }}</div>
              <div><b>Status:</b> {{ detail.device.status }}</div>
              <div><b>Department:</b> {{ departmentName(detail.device.departmentId) }}</div>
              <div><b>Vendor:</b> {{ detail.device.vendor }}</div>
              <div><b>Description:</b> {{ detail.device.description || 'No description' }}</div>
              <div><b>Diseases:</b> {{ (detail.device.diseases||[]).join(', ') || '-' }}</div>
            </div>
            <div style="display:flex; gap:8px; margin-top:16px;">
              <button class="btn" @click="closeDetail">Close</button>
            </div>
          </div>
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
import { getDeviceImageUrl, onDeviceImgError } from '@/utils/images.js'

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

function shortDesc(text) { const t = text || ''; return t.length > 100 ? `${t.slice(0, 100)}…` : t }
</script>

<style scoped>
.drawer-backdrop { position: fixed; inset:0; background: rgba(0,0,0,0.35); display:flex; }
.drawer { margin-left:auto; width:min(920px, 100%); padding:16px; }
.detail-grid { display:grid; grid-template-columns: 1fr 1fr; gap:16px; margin-top:12px; }
.detail-image img { width:100%; height: 320px; object-fit: cover; border-radius: 8px; background: #f3f4f6; }
.detail-info { display:flex; flex-direction: column; }
.device-card .image-wrap { position: relative; width: 100%; aspect-ratio: 16/9; background: #f3f4f6; }
.device-card .image-wrap img { width: 100%; height: 100%; object-fit: cover; display:block; }
.device-card .status-badge { position: absolute; left: 12px; top: 12px; background: rgba(0,0,0,0.6); color: #fff; padding: 2px 8px; border-radius: 999px; font-size: 12px; }

@media (max-width: 800px) {
  .detail-grid { grid-template-columns: 1fr; }
}
</style>
