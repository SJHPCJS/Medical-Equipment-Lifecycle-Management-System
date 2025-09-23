<template>
  <div class="card" style="padding:16px;">
    <div class="title-lg">My Department Devices</div>
    <div class="subtitle" style="margin-top:8px;">Readonly device list and details for this department.</div>

    <div class="filters" style="margin-top:16px; display:grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap:12px;">
      <input class="input" v-model="keyword" placeholder="Search by id/type/vendor" />
      <select class="input" v-model="status">
        <option value="">All status</option>
        <option>In Use</option>
        <option>Under Repair</option>
      </select>
    </div>

    <div class="cards" style="margin-top:16px; display:grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap:12px;">
      <div v-for="d in filtered" :key="d.id" class="device-card card" style="padding:0; overflow:hidden; cursor:pointer;" @click="openDetail(d)">
        <div class="image-wrap">
          <img :src="getDeviceImageUrl(d.id)" @error="onDeviceImgError" alt="device" />
          <div class="status-badge" :data-status="d.status">{{ d.status }}</div>
        </div>
        <div style="padding:12px; display:grid; gap:6px;">
          <div class="title-md">{{ d.type }}</div>
          <div class="subtitle">ID: {{ d.id }}</div>
          <div class="muted">{{ shortDesc(d.description) }}</div>
          <div class="muted">Vendor: {{ d.vendor }}</div>
          <div style="display:flex; gap:8px; margin-top:8px;">
            <button class="btn" @click.stop="openUsageLogs(d)">Usage Logs</button>
            <button class="btn" @click.stop="addUsage(d)">Add Usage</button>
            <button class="btn" @click.stop="openRepair(d)">Repair</button>
          </div>
        </div>
      </div>
      <div v-if="filtered.length===0" class="subtitle" style="padding:16px;">No data</div>
    </div>

    <!-- Usage Logs Modal -->
    <div v-if="usage.open" class="modal-backdrop">
      <div class="modal card">
        <div class="title-lg">Usage Logs - {{ usage.device.id }}</div>
        <div class="subtitle">Records for this device</div>
        <div style="margin-top:12px; display:grid; gap:8px;">
          <div v-for="u in usage.list" :key="u.id" class="card" style="padding:12px;">
            <div><b>Start:</b> {{ fmt(u.start) }} | <b>End:</b> {{ fmt(u.end) }}</div>
            <div><b>Purpose:</b> {{ u.purpose }}</div>
            <div><b>Status Notes:</b> {{ u.notes || '-' }}</div>
            <div><b>Exception:</b> {{ u.exception || '-' }}</div>
          </div>
          <div v-if="usage.list.length===0" class="subtitle">No logs</div>
        </div>
        <div style="display:flex; gap:8px; justify-content:flex-end; margin-top:16px;">
          <button class="btn" @click="closeUsage">Close</button>
        </div>
      </div>
    </div>

    <!-- Add Usage Modal -->
    <div v-if="add.open" class="modal-backdrop">
      <div class="modal card">
        <div class="title-lg">Add Usage - {{ add.device.id }}</div>
        <div class="form-grid">
          <div>
            <label>Start Time</label>
            <DateTimePicker v-model="add.form.start" :minute-step="5" :start-year="2024" :end-year="2030" />
          </div>
          <div>
            <label>End Time</label>
            <DateTimePicker v-model="add.form.end" :minute-step="5" :start-year="2024" :end-year="2030" />
          </div>
          <div>
            <label>Purpose</label>
            <input class="input" v-model="add.form.purpose" placeholder="Enter purpose" />
          </div>
          <div>
            <label>Status Notes</label>
            <input class="input" v-model="add.form.notes" placeholder="Enter notes" />
          </div>
          <div>
            <label>Exception</label>
            <input class="input" v-model="add.form.exception" placeholder="Enter exception if any" />
          </div>
        </div>
        <div style="display:flex; gap:8px; justify-content:flex-end; margin-top:16px;">
          <button class="btn" @click="closeAdd">Cancel</button>
          <button class="btn btn-primary" @click="saveUsage">Save</button>
        </div>
      </div>
    </div>

    <!-- Repair Modal -->
    <div v-if="repair.open" class="modal-backdrop">
      <div class="modal card">
        <div class="title-lg">Repair - {{ repair.device.id }}</div>
        <div class="form-grid">
          <div>
            <label>Warranty Type</label>
            <select class="input" v-model="repair.form.warranty">
              <option value="In Warranty">In Warranty</option>
              <option value="Out of Warranty">Out of Warranty</option>
            </select>
          </div>
          <div style="grid-column: 1 / -1;">
            <label>Description</label>
            <textarea class="input" v-model="repair.form.description" placeholder="Describe the issue"></textarea>
          </div>
        </div>
        <div style="display:flex; gap:8px; justify-content:flex-end; margin-top:16px;">
          <button class="btn" @click="closeRepair">Cancel</button>
          <button class="btn btn-primary" @click="submitRepair">Submit</button>
        </div>
      </div>
    </div>

    <!-- Detail Drawer -->
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
import DateTimePicker from '@/components/DateTimePicker.vue'
import { clone, deptDevices as seedDevices, usageLogs as seedUsage } from '@/mocks/department.js'
import { getDeviceImageUrl, onDeviceImgError } from '@/utils/images.js'

const state = reactive({ devices: clone(seedDevices), usageMap: groupUsage(seedUsage) })
const keyword = ref('')
const status = ref('')

function groupUsage(list) {
  const map = {}
  for (const u of list) {
    map[u.deviceId] ||= []
    map[u.deviceId].push(u)
  }
  return map
}

const filtered = computed(() => {
  const kw = keyword.value.toLowerCase()
  return state.devices.filter(d => {
    const matchKw = !kw || `${d.id} ${d.type} ${d.vendor}`.toLowerCase().includes(kw)
    const matchStatus = !status.value || d.status === status.value
    return matchKw && matchStatus
  })
})

const usage = reactive({ open: false, device: {}, list: [] })
function openUsageLogs(d) { usage.open = true; usage.device = d; usage.list = state.usageMap[d.id] || [] }
function closeUsage() { usage.open = false }

const add = reactive({ open: false, device: {}, form: { start: '', end: '', purpose: '', notes: '', exception: '' } })
function addUsage(d) { add.open = true; add.device = d; add.form = { start: '', end: '', purpose: '', notes: '', exception: '' } }
function closeAdd() { add.open = false }
function toIsoFromLocalForm(v) {
  if (!v) return ''
  // Accept formats like 'YYYY-MM-DD HH:mm' or 'YYYY-MM-DDTHH:mm'
  const normalized = v.replace(' ', 'T')
  const withSeconds = /:\d{2}$/.test(normalized) ? normalized : `${normalized}:00`
  // Force Z to avoid locale-dependent rendering
  return `${withSeconds}Z`
}

function saveUsage() {
  const id = `UL-${String(Math.floor(Math.random()*100000)).padStart(4,'0')}`
  const record = { id, deviceId: add.device.id, start: toIsoFromLocalForm(add.form.start), end: toIsoFromLocalForm(add.form.end), purpose: add.form.purpose, notes: add.form.notes, exception: add.form.exception }
  state.usageMap[add.device.id] ||= []
  state.usageMap[add.device.id].push(record)
  closeAdd()
}

const repair = reactive({ open: false, device: {}, form: { warranty: 'In Warranty', description: '' } })
function openRepair(d) { repair.open = true; repair.device = d; repair.form = { warranty: 'In Warranty', description: '' } }
function closeRepair() { repair.open = false }
function submitRepair() { alert(`Repair submitted for ${repair.device.id} (demo only)`); closeRepair() }

function fmt(ts) { try { return new Date(ts).toLocaleString('en-US', { hour12: false }) } catch { return ts } }

function shortDesc(text) { const t = text || ''; return t.length > 100 ? `${t.slice(0, 100)}…` : t }

const detail = reactive({ open: false, device: {} })
function openDetail(d) { detail.open = true; detail.device = d }
function closeDetail() { detail.open = false }
</script>

<style scoped>
.device-card .image-wrap { position: relative; width: 100%; aspect-ratio: 16/9; background: #f3f4f6; }
.device-card .image-wrap img { width: 100%; height: 100%; object-fit: cover; display:block; }
.device-card .status-badge { position: absolute; left: 12px; top: 12px; background: rgba(0,0,0,0.6); color: #fff; padding: 2px 8px; border-radius: 999px; font-size: 12px; }

.drawer-backdrop { position: fixed; inset:0; background: rgba(0,0,0,0.35); display:flex; }
.drawer { margin-left:auto; width:min(920px, 100%); padding:16px; }
.detail-grid { display:grid; grid-template-columns: 1fr 1fr; gap:16px; margin-top:12px; }
.detail-image img { width:100%; height: 320px; object-fit: cover; border-radius: 8px; background: #f3f4f6; }
.detail-info { display:flex; flex-direction: column; }

@media (max-width: 800px) {
  .detail-grid { grid-template-columns: 1fr; }
}

.modal-backdrop { position: fixed; inset: 0; background: rgba(0,0,0,0.35); display:flex; align-items:center; justify-content:center; padding:16px; }
.modal { width: min(720px, 100%); padding: 16px; }
.form-grid { margin-top: 16px; display:grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 12px; }
textarea.input { height: 120px; resize: vertical; }
</style>
