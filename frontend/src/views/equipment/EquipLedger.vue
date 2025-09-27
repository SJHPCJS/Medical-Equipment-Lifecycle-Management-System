<template>
  <div class="card" style="padding:16px;">
    <div class="title-lg">Equipment Ledger</div>
    <div class="subtitle" style="margin-top:8px;">Manage equipment inventory (backed by server).</div>

    <!-- Filters -->
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
        <MultiSelect v-model="filters.departmentIds" :options="departments.map(d => ({ value: String(d.departmentId), label: d.departmentName }))" placeholder="All departments" />
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

    <!-- Device cards -->
    <div class="cards" style="margin-top:16px; display:grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap:12px;">
      <div v-for="d in filtered" :key="d.equipmentId" class="device-card card" @click="openDetail(d)" style="cursor:pointer; padding:0; overflow:hidden;">
        <div class="image-wrap">
          <img :src="getDeviceImageUrl(d.equipmentId)" @error="onDeviceImgError" alt="device" />
          <div class="status-badge" :data-status="d.status">{{ d.status }}</div>
        </div>
        <div style="padding:12px; display:grid; gap:6px;">
          <div class="title-md">{{ d.equipmentTypeName || d.equipmentTypeId }}</div>
          <div class="subtitle">ID: {{ d.equipmentId }}</div>
          <div class="subtitle">Status: {{ d.status }}</div>
          <div class="muted">{{ shortDesc(d.description || '') }}</div>
          <div class="muted">Vendor: {{ d.supplierId || '-' }}</div>
          <div class="muted">Dept: {{ departmentName(d.departmentId) || '-' }}</div>
          <div style="display:flex; gap:8px; margin-top:8px;">
            <button class="btn" @click.stop="upload('Manual', d)">Upload Manual</button>
            <button class="btn" @click.stop="upload('Warranty', d)">Upload Warranty</button>
            <button class="btn" @click.stop="deleteEquipmentPrompt(d)">Delete</button>

            <button
                v-if="d.status === 'assigning'"
                class="btn"
                @click.stop="openAssignDialog(d)">
              Assign department
            </button>
          </div>
        </div>
      </div>
      <div v-if="filtered.length===0" class="subtitle" style="padding:16px;">No data</div>
    </div>

    <!-- Drawer detail -->
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
              <div><b>Type:</b> {{ detail.device.equipmentTypeName || detail.device.equipmentTypeId }}</div>
              <div><b>Status:</b> {{ detail.device.status }}</div>
              <div><b>Department:</b> {{ detail.device.departmentName }}</div>
              <div><b>Vendor:</b> {{ detail.device.supplierId }}</div>
              <div><b>Description:</b> {{ detail.device.description || 'No description' }}</div>
              <div><b>Manual:</b>
                <a v-if="d.userManualPath" :href="d.userManualPath" target="_blank">open</a>
                <span v-else>-</span>
              </div>
              <div><b>Warranty:</b>
                <a v-if="d.warrantyCertificatePath" :href="d.warrantyCertificatePath" target="_blank">open</a>
                <span v-else>-</span>
              </div>
            </div>
            <div style="display:flex; gap:8px; margin-top:16px;">
              <button class="btn" @click="closeDetail">Close</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Assign department dialog -->
    <div v-if="assignDialogVisible" class="dialog-backdrop">
      <div class="dialog">
        <h3>Assign department</h3>
        <p>Please select the department to allocate: </p>
        <select v-model="selectedDept" class="select">
          <option disabled value="">Please select</option>
          <option v-for="dept in departments" :key="dept.departmentId" :value="dept.departmentId">
            {{ dept.departmentName }}
          </option>
        </select>
        <div class="actions">
          <button class="btn" @click="confirmAssign">Confirm</button>
          <button class="btn" @click="assignDialogVisible=false">Cancel</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from 'vue'
import axios from 'axios'
import MultiSelect from '@/components/MultiSelect.vue'
import { getDeviceImageUrl, onDeviceImgError } from '@/utils/images.js'

const state = reactive({ devices: [] })
const departments = ref([])
const statuses = ref([])

// assign dialog state
const assignDialogVisible = ref(false)
const selectedDevice = ref(null)
const selectedDept = ref("")

// filters
const filters = reactive({ keyword: '', types: [], statuses: [], departmentIds: [], vendors: [] })

// load from backend
async function loadData() {
  try {
    const [resDevices, resDepartments, resStatuses] = await Promise.all([
      axios.get('/req/devices'),
      axios.get('/req/departments'),
      axios.get('/req/device-statuses')
    ])
    state.devices = resDevices.data || []
    departments.value = resDepartments.data || []
    statuses.value = resStatuses.data || []
  } catch (err) {
    console.error('Failed to load device data.', err)
  }
}

onMounted(() => {
  loadData()
})

// dynamic option lists
const types = computed(() => {
  return Array.from(new Set(state.devices.map(d => d.equipmentTypeName || d.equipmentTypeId).filter(Boolean)))
})
const vendorsList = computed(() => {
  return Array.from(new Set(state.devices.map(d => d.supplierId).filter(Boolean)))
})

// filtering
const filtered = computed(() => {
  const kw = (filters.keyword || '').toLowerCase()
  return state.devices.filter(d => {
    const matchKw = !kw || `${d.equipmentId || ''} ${d.equipmentTypeName || d.equipmentTypeId || ''} ${d.supplierId || ''}`.toLowerCase().includes(kw)
    const matchType = !filters.types.length || filters.types.includes(d.equipmentTypeName || d.equipmentTypeId)
    const matchStatus = !filters.statuses.length || filters.statuses.includes(d.status)
    const matchDept = !filters.departmentIds.length || filters.departmentIds.includes(String(d.departmentId))
    const matchVendor = !filters.vendors.length || filters.vendors.includes(d.supplierId)
    return matchKw && matchType && matchStatus && matchDept && matchVendor
  })
})

function resetFilters() {
  filters.keyword = ''
  filters.types = []
  filters.statuses = []
  filters.departmentIds = []
  filters.vendors = []
}

function departmentName(id) {
  if (id === null || id === undefined || id === '') return '-'
  const dep = departments.value.find(d => String(d.departmentId) === String(id))
  return dep ? dep.departmentName : '-'
}

// export CSV
function exportCsv() {
  const rows = [['Equipment ID','Type','Status','Department','Vendor'],
    ...filtered.value.map(d => [
      d.equipmentId, d.equipmentTypeName || d.equipmentTypeId, d.status, departmentName(d.departmentId), d.supplierId
    ])
  ]
  const csv = rows.map(r => r.map(x => `"${String(x ?? '').replaceAll('"','""')}"`).join(',')).join('\n')
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'equipment-ledger.csv'
  a.click()
  URL.revokeObjectURL(url)
}

// detail drawer
const detail = reactive({ open: false, device: {} })
async function openDetail(d) {
  try {
    const res = await axios.get(`/req/devices/${encodeURIComponent(d.equipmentId)}`)
    detail.device = res.data || d
  } catch (err) {
    console.warn('fetch detail failed, using passed object', err)
    detail.device = d
  }
  detail.open = true
}
function closeDetail() { detail.open = false; detail.device = {} }

// actions
async function upload(kind, d) {
  try {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = '*/*';
    input.onchange = async (e) => {
      const file = e.target.files[0];
      if (!file) return;

      const formData = new FormData();
      formData.append('kind', kind);
      formData.append('file', file);

      const res = await axios.post(`/req/devices/${encodeURIComponent(d.equipmentId)}/upload`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });

      alert(`${kind} uploaded successfully. URL: ${res.data.url}`);

      if (kind === 'Manual') d.userManualPath = res.data.url;
      else if (kind === 'Warranty') d.warrantyCertificatePath = res.data.url;
    };
    input.click();
  } catch (err) {
    console.error(err);
    alert('Upload failed');
  }
}


async function deleteEquipmentPrompt(d) {
  if (!confirm(`Delete equipment ${d.equipmentId}?`)) return
  try {
    await axios.delete(`/req/devices/${encodeURIComponent(d.equipmentId)}`)
    state.devices = state.devices.filter(x => x.equipmentId !== d.equipmentId)
  } catch (err) {
    console.error('Delete failed', err)
    alert('Delete failed')
  }
}

function shortDesc(text) { const t = text || ''; return t.length > 100 ? `${t.slice(0,100)}…` : t }

// assign department dialog
function openAssignDialog(device) {
  selectedDevice.value = device
  selectedDept.value = ""
  assignDialogVisible.value = true
}

async function confirmAssign() {
  if (!selectedDept.value) {
    alert("Please select a department")
    return
  }
  try {
    await axios.post(`/req/devices/${encodeURIComponent(selectedDevice.value.equipmentId)}/assign`, null, {
      params: { departmentId: selectedDept.value }
    })
    assignDialogVisible.value = false
    await loadData()
  } catch (err) {
    console.error("Assign failed", err)
    alert("Assign failed")
  }
}

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

.dialog-backdrop { position: fixed; top:0; left:0; right:0; bottom:0; background: rgba(0,0,0,0.4); display:flex; justify-content:center; align-items:center; }
.dialog { background:#fff; padding:20px; border-radius:8px; min-width:300px; display:grid; gap:12px; }
.actions { display:flex; justify-content:flex-end; gap:8px; }
.select { padding:6px; border:1px solid #ccc; border-radius:4px; }

@media (max-width: 800px) {
  .detail-grid { grid-template-columns: 1fr; }
}
</style>
