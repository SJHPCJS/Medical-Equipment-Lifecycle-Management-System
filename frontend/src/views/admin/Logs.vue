<template>
  <div class="card" style="padding:16px;">
    <div class="title-lg">Logs & Audit</div>
    <div class="subtitle" style="margin-top:8px;">Track system-level operations. Frontend-only mock.</div>

    <!-- Filters -->
    <div class="ui-toolbar" style="margin-top:16px; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));">
      <div>
        <label>Keyword</label>
        <input class="input" v-model="filters.keyword" placeholder="Search user/target/action" />
      </div>
      <div>
        <label>Start Date</label>
        <input class="input" v-model="filters.start" type="date" lang="en-US" />
      </div>
      <div>
        <label>End Date</label>
        <input class="input" v-model="filters.end" type="date" lang="en-US" />
      </div>
      <div style="display:flex; gap:8px;">
        <button class="btn" @click="resetFilters">Reset</button>
        <button class="btn btn-primary" @click="exportCsv">Export CSV</button>
      </div>
    </div>

    <!-- Table -->
    <div class="table-wrapper" style="margin-top:16px; overflow:auto;">
      <table class="ui-table" style="width:100%; max-width:100%; table-layout:auto;">
        <thead>
          <tr>
            <th>Time</th>
            <th>User ID</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading"><td colspan="3"><TableSkeleton :rows="6" /></td></tr>
          <tr v-else-if="paged.length===0">
            <td colspan="3"><EmptyState title="No logs" hint="Try changing date range or keyword." /></td>
          </tr>
          <tr v-else v-for="item in paged" :key="item.log_id">
            <td>{{ formatTime(item.log_time) }}</td>
            <td>{{ item.log_user_id }}</td>
            <td>{{ item.log_action }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div class="ui-pagination" style="margin-top:12px;">
      <button class="btn" :disabled="page===1" @click="page=1">First</button>
      <button class="btn" :disabled="page===1" @click="page--">Prev</button>
      <span style="color:var(--color-muted);">Page {{ page }} / {{ totalPages }}</span>
      <button class="btn" :disabled="page===totalPages" @click="page++">Next</button>
      <button class="btn" :disabled="page===totalPages" @click="page=totalPages">Last</button>
      <select class="input" style="width:auto;" v-model.number="pageSize">
        <option :value="5">5</option>
        <option :value="10">10</option>
        <option :value="20">20</option>
      </select>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import EmptyState from '@/components/admin/EmptyState.vue'
import TableSkeleton from '@/components/admin/TableSkeleton.vue'
const state = reactive({ logs: [] })
const loading = ref(true)

const filters = reactive({ keyword: '', start: '', end: '' })
const page = ref(1)
const pageSize = ref(10)

function formatTime(ts) { try { return new Date(ts).toLocaleString('en-US') } catch { return ts } }

const filtered = computed(() => {
  const kw = (filters.keyword || '').toLowerCase()
  const startMs = filters.start ? new Date(`${filters.start}T00:00:00`).getTime() : -Infinity
  const endMs = filters.end ? new Date(`${filters.end}T23:59:59.999`).getTime() : Infinity
  return state.logs.filter(l => {
    const t = new Date(l.log_time).getTime()
    const matchTime = t >= startMs && t <= endMs
    const join = `${l.log_user_id} ${l.log_action}`.toLowerCase()
    const matchKw = !kw || join.includes(kw)
    return matchTime && matchKw
  })
})

const totalPages = computed(() => Math.max(1, Math.ceil(filtered.value.length / pageSize.value)))
const paged = computed(() => {
  const start = (page.value - 1) * pageSize.value
  return filtered.value.slice(start, start + pageSize.value)
})

function resetFilters() {
  filters.keyword = ''
  filters.start = ''
  filters.end = ''
}

function exportCsv() {
  const rows = [['Time','User','Target','Action'], ...filtered.value.map(r => [r.timestamp, r.user, r.target, r.action])]
  const csv = rows.map(r => r.map(x => `"${String(x).replaceAll('"','""')}"`).join(',')).join('\n')
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'system-logs.csv'
  a.click()
  URL.revokeObjectURL(url)
}

// 替换为后端数据
async function refreshLogs() {
  try { loading.value = true; const r = await fetch('/req/admin/logs'); const j = await r.json(); if (j.code === '000') state.logs = j.data || [] } finally { loading.value = false }
}

onMounted(refreshLogs)
</script>
 

<style scoped>
</style>

