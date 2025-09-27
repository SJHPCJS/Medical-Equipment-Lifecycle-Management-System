<template>
  <div class="card" style="padding:16px;">
    <div class="title-lg">My Department Devices</div>
    <div class="subtitle" style="margin-top:8px;">Readonly device list and details for this department.</div>

    <div class="filters" style="margin-top:16px; display:grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap:12px;">
      <input class="input" v-model="keyword" placeholder="Search by id/type/vendor" />
      <select class="input" v-model="status">
        <option value="">All status</option>
        <option value="In Use">In Use</option>
        <option value="Under Repair">Under Repair</option>
      </select>
    </div>

    <div class="cards" style="margin-top:16px; display:grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap:12px;">
      <div v-for="d in filteredDevices" :key="d.equipmentId" class="device-card card" style="padding:0; overflow:hidden; cursor:pointer;" @click="openDetail(d)">
        <div class="image-wrap">
          <img :src="getDeviceImageUrl(d.equipmentId)" @error="onDeviceImgError" alt="device" />
          <div class="status-badge" :data-status="d.status">{{ d.status }}</div>
        </div>
        <div style="padding:12px; display:grid; gap:6px;">
          <div class="title-md">{{ d.equipmentTypeName }}</div>
          <div class="subtitle">ID: {{ d.equipmentId }}</div>
          <div class="muted">{{ shortDesc(d.description) }}</div>
          <div class="muted">Vendor: {{ d.supplierId }}</div>
          <div style="display:flex; gap:8px; margin-top:8px;">
            <button class="btn" @click.stop="openUsageLogs(d)">Usage Logs</button>
            <button class="btn" @click.stop="addUsage(d)">Add Usage</button>
            <button class="btn" @click.stop="openRepair(d)">Repair</button>
          </div>
        </div>
      </div>
      <div v-if="filteredDevices.length === 0" class="subtitle" style="padding:16px;">No data</div>
    </div>

    <!-- Usage Logs Modal -->
    <div v-if="usage.open" class="modal-backdrop">
      <div class="modal card">
        <div class="title-lg">Usage Logs - {{ usage.device.equipmentId }}</div>
        <div class="subtitle">Records for this device</div>

        <div v-if="usage.loading" class="muted" style="margin-top:12px;">Loading...</div>
        <div v-else-if="usage.error" class="muted" style="margin-top:12px; color:#b91c1c;">{{ usage.error }}</div>

        <div v-else style="margin-top:12px; display:grid; gap:8px;">
          <div v-for="u in usage.list" :key="u.logId" class="card" style="padding:12px;">
            <div><b>Time:</b> {{ fmt(u.time) }}</div>
            <div><b>Recorder:</b> {{ u.recorderId }}</div>
            <div><b>Remark:</b> {{ u.remark || '-' }}</div>
          </div>
          <div v-if="usage.list.length === 0" class="subtitle">No logs</div>
        </div>

        <div style="display:flex; gap:8px; justify-content:flex-end; margin-top:16px;">
          <button class="btn" @click="closeUsage">Close</button>
        </div>
      </div>
    </div>

    <!-- Add Usage Modal -->
    <div v-if="add.open" class="modal-backdrop">
      <div class="modal card">
        <div class="title-lg">Add Usage - {{ add.device.equipmentId }}</div>
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
            <input class="input" v-model="add.form.remark" placeholder="Enter notes" />
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
        <div class="title-lg">Repair - {{ repair.device.equipmentId }}</div>
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
        <div class="subtitle">ID: {{ detail.device.equipmentId }}</div>
        <div class="detail-grid">
          <div class="detail-image">
            <img :src="getDeviceImageUrl(detail.device.equipmentId)" @error="onDeviceImgError" alt="device" />
          </div>
          <div class="detail-info">
            <div style="display:grid; gap:8px;">
              <div><b>Type:</b> {{ detail.device.equipmentTypeName }}</div>
              <div><b>Status:</b> {{ detail.device.status }}</div>
              <div><b>Vendor:</b> {{ detail.device.supplierId }}</div>
              <div><b>Description:</b> {{ detail.device.description || 'No description' }}</div>
              <div><b>Diseases:</b> {{ (detail.device.diseases || []).join(', ') || '-' }}</div>
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
import { reactive, ref, computed, onMounted, watch } from 'vue'
import DateTimePicker from '@/components/DateTimePicker.vue'
import { getDeviceImageUrl, onDeviceImgError } from '@/utils/images.js'
import axios from 'axios'

const keyword = ref('')
const status = ref('')
const devices = reactive([])

// 设备筛选和状态
const filteredDevices = computed(() => {
  const kw = keyword.value.toLowerCase()
  return devices.filter(d => {
    const matchKw = !kw || `${d.equipmentId} ${d.equipmentTypeName} ${d.supplierId}`.toLowerCase().includes(kw)
    const matchStatus = !status.value || d.status === status.value
    return matchKw && matchStatus
  })
})

onMounted(() => {
  fetchDevices()
})

watch([keyword, status], () => {
  fetchDevices()
})

async function fetchDevices() {
  try {
    const response = await axios.get('/req/devices', {
      params: {
        keyword: keyword.value,
        statuses: status.value ? [status.value] : [],
        departmentIds: ['0001']  // 固定发送 departmentId 为 "0001"
      }
    })
    console.log('API Response:', response.data);
    devices.splice(0, devices.length, ...response.data)
  } catch (error) {
    console.error('Error fetching devices:', error)
  }
}

// Usage Log Modal
const usage = reactive({ open: false, device: {}, list: [], loading: false, error: '' })

function openUsageLogs(d) {
  usage.open = true
  usage.device = d
  usage.list = []
  usage.error = ''
  fetchUsageLogs(d.equipmentId)
}

function closeUsage() {
  usage.open = false
}

async function fetchUsageLogs(equipmentId) {
  usage.loading = true
  try {
    const response = await axios.get('/req/dept/usage/logs', { params: { equipmentId } })
    console.log('LOG Response:', response.data);
    usage.list = Array.isArray(response.data) ? response.data : []
  } catch (err) {
    console.error(err)
    usage.error = 'Failed to load logs'
  } finally {
    usage.loading = false
  }
}

// Add Usage Modal
const add = reactive({ open: false, device: {}, form: { start: '', end: '', purpose: '', remark: '', exception: '' }, saving: false, error: '' })

function addUsage(d) {
  add.open = true
  add.device = d
  add.form.remark = ''
  add.error = ''
}

function closeAdd() {
  add.open = false
}

async function saveUsage() {
  console.log("Save button clicked");
  if (!add.form.remark.trim()) {
    add.error = 'Remark is required'
    return
  }
  let tempRemark;
  if (!add.form.start.trim() || !add.form.end.trim()) {
    tempRemark = "note: " + add.form.remark.trim() + "; exception:" + add.form.exception.trim() + "; purpose:" + add.form.purpose.trim();
  }
  else{
    tempRemark = "note: " + add.form.remark.trim() + "; exception:" + add.form.exception.trim() + "; purpose:" + add.form.purpose.trim() + "; from" + add.form.start.trim() + " to " + add.form.end.trim();
  }
  add.saving = true
  add.error = ''
  try {
    await axios.post('/req/dept/usage/logs', {
      recorderId: '2',
      targetEquipmentId: add.device.equipmentId,
      remark: tempRemark,
    })
    closeAdd()
    // 保存成功后刷新当前设备的日志（如果日志面板开着）
    if (usage.open && usage.device.equipmentId === add.device.equipmentId) {
      fetchUsageLogs(add.device.equipmentId)
    }
  } catch (err) {
    console.error(err)
    add.error = 'Failed to save'
  } finally {
    add.saving = false
  }
}

// Repair Modal
const repair = reactive({ open: false, device: {}, form: { warranty: 'In Warranty', description: '' } })
function openRepair(d) { repair.open = true; repair.device = d }
function closeRepair() { repair.open = false }
function submitRepair() { alert('Repair submitted (demo only)'); closeRepair() }

// Device Detail Drawer
const detail = reactive({ open: false, device: {} })
function openDetail(d) { detail.open = true; detail.device = d }
function closeDetail() { detail.open = false }

function fmt(ts) { try { return new Date(ts).toLocaleString('en-US', { hour12: false }) } catch { return ts } }
function shortDesc(text) { const t = text || ''; return t.length > 100 ? `${t.slice(0, 100)}…` : t }
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
