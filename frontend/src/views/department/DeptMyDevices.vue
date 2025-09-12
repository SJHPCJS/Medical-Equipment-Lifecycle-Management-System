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

    <div class="table-wrapper" style="margin-top:16px; overflow:auto;">
      <table class="table">
        <thead>
          <tr>
            <th>Device ID</th>
            <th>Type</th>
            <th>Status</th>
            <th>Vendor</th>
            <th style="width:300px;">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="d in filtered" :key="d.id">
            <td>{{ d.id }}</td>
            <td>{{ d.type }}</td>
            <td>{{ d.status }}</td>
            <td>{{ d.vendor }}</td>
            <td>
              <button class="btn" @click="openUsageLogs(d)">Usage Logs</button>
              <button class="btn" style="margin-left:8px;" @click="addUsage(d)">Add Usage</button>
              <button class="btn" style="margin-left:8px;" @click="openRepair(d)">Repair</button>
            </td>
          </tr>
          <tr v-if="filtered.length===0">
            <td colspan="5" style="text-align:center; color:var(--color-muted);">No data</td>
          </tr>
        </tbody>
      </table>
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
  </div>
</template>

<script setup>
import { reactive, ref, computed } from 'vue'
import DateTimePicker from '@/components/DateTimePicker.vue'
import { clone, deptDevices as seedDevices, usageLogs as seedUsage } from '@/mocks/department.js'

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
