<template>
  <div class="card" style="padding:16px;">
    <div class="title-lg">Repair Management</div>
    <div class="subtitle" style="margin-top:8px;">Ticket pool from department users.</div>

    <!-- Filters -->
    <div class="filters" style="margin-top:16px; display:grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap:12px;">
      <div>
        <label>Status</label>
        <MultiSelect v-model="filters.statuses" :options="statuses.map(s => ({ value: s, label: statusLabels[s] }))" placeholder="All status" />
      </div>
      <div>
        <label>Department</label>
        <MultiSelect v-model="filters.departments" :options="departmentList.map(d => ({ value: d, label: d }))" placeholder="All departments" />
      </div>
      <div style="display:flex; gap:8px; align-items:end;">
        <button class="btn" @click="resetFilters">Reset</button>
      </div>
    </div>

    <!-- Ticket table -->
    <div class="table-wrapper" style="margin-top:16px; overflow:auto;">
      <table class="table" style="table-layout:fixed; width:100%;">
        <thead>
        <tr>
          <th>ID</th>
          <th>Created</th>
          <th>Finished</th>
          <th>Notes</th>
          <th>Cost</th>
          <th>Result</th>
          <th>Status</th>
          <th>Dept</th>
          <th style="width:200px;">Actions</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="t in filtered" :key="t.ticketId">
          <td>{{ t.ticketId }}</td>
          <td>{{ formatTime(t.createdAt) }}</td>
          <td>{{ t.finishedAt ? formatTime(t.finishedAt) : '-' }}</td>
          <td><input class="input" v-model="t.notes" placeholder="Process notes" /></td>
          <td><input class="input" v-model.number="t.cost" type="number" min="0" step="1" style="width:120px;" /></td>
          <td><input class="input" v-model="t.result" placeholder="Result/Acceptance" /></td>
          <td>
            <select class="input" v-model="t.status">
              <option v-for="s in allowedStatusOptions(t.status)" :key="s" :value="s">{{ statusLabels[s] }}</option>
            </select>
          </td>
          <td>{{ t.departmentName }}</td>
          <td style="white-space:nowrap;">
            <button class="btn" @click="save(t)">Save</button>
            <button class="btn" style="margin-left:8px;" @click="advanceStatus(t)" :disabled="t.status==='completed' || t.status==='rejected'">Advance</button>
          </td>
        </tr>
        <tr v-if="tickets.length===0">
          <td colspan="9" style="text-align:center; color:var(--color-muted);">No tickets</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import MultiSelect from '@/components/MultiSelect.vue'
import axios from 'axios'

// Ticket data
const tickets = ref([])

// 状态列表
const statuses = ['pending','in-progress','under-acceptance','completed','rejected']

// 显示标签
const statusLabels = {
  pending: 'Pending Review',
  'in-progress': 'In Repair',
  'under-acceptance': 'In Acceptance',
  completed: 'Completed',
  rejected: 'Rejected'
}

// Department 列表
const departmentList = computed(() => Array.from(new Set(tickets.value.map(t => t.departmentName))))

// Filters
const filters = ref({ statuses: [], departments: [] })
const filtered = computed(() => tickets.value.filter(t => {
  const matchStatus = filters.value.statuses.length===0 || filters.value.statuses.includes(t.status)
  const matchDept = filters.value.departments.length===0 || filters.value.departments.includes(t.departmentName)
  return matchStatus && matchDept
}))

// 页面初始化
onMounted(async () => {
  const res = await axios.get('/req/repair-tickets')
  tickets.value = res.data
})

// Filter helpers
function resetFilters() { filters.value.statuses = []; filters.value.departments = [] }
function formatTime(ts) { try { return new Date(ts).toLocaleString() } catch { return ts } }

// 保存 ticket
async function save(t) {
  await axios.put(`/req/repair-tickets/${t.ticketId}`, t)
}

// 根据当前状态决定允许的下拉状态选项
function allowedStatusOptions(current) {
  switch(current) {
    case 'pending': return ['pending','in-progress','rejected']
    case 'in-progress': return ['in-progress','under-acceptance']
    case 'under-acceptance': return ['under-acceptance','completed','rejected']
    default: return [current]
  }
}

// 状态流转逻辑（Manager操作）
async function advanceStatus(t) {
  if (t.status === 'pending') {
    const next = confirm('Approve repair? OK=In Progress, Cancel=Reject') ? 'in-progress' : 'rejected'
    t.status = next
  } else if (t.status === 'in-progress') {
    t.status = 'under-acceptance'
  } else if (t.status === 'under-acceptance') {
    const next = confirm('Accept repair? OK=Completed, Cancel=Rejected') ? 'completed' : 'rejected'
    t.status = next
    if(next==='completed') t.finishedAt = new Date().toISOString()
  }
  await axios.post(`/req/repair-tickets/${t.ticketId}/advance`, { status: t.status })
}
</script>

<style scoped>
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { padding: 10px 12px; border-bottom: 1px solid #e5e7eb; text-align: left; white-space: normal; word-break: break-word; }
.table th { background: #f9fafb; font-weight: 700; }
.input { width: 100%; padding: 4px 6px; border: 1px solid #ccc; border-radius: 4px; }
.btn { padding: 6px 12px; border: none; background-color: #3b82f6; color: white; border-radius: 4px; cursor: pointer; }
.btn:disabled { background-color: #ccc; cursor: not-allowed; }
</style>
