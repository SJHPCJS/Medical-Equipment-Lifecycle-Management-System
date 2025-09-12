<template>
  <div class="card" style="padding:16px;">
    <div class="title-lg">Logs & Audit</div>
    <div class="subtitle" style="margin-top:8px;">Track system-level operations. Frontend-only mock.</div>

    <!-- Filters -->
    <div class="filters" style="margin-top:16px; display:grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap:12px;">
      <input class="input" v-model="filters.keyword" placeholder="Search user/target/action" />
      <input class="input" v-model="filters.start" type="datetime-local" />
      <input class="input" v-model="filters.end" type="datetime-local" />
      <div style="display:flex; gap:8px;">
        <button class="btn" @click="resetFilters">Reset</button>
        <button class="btn btn-primary" @click="exportCsv">Export CSV</button>
      </div>
    </div>

    <!-- Table -->
    <div class="table-wrapper" style="margin-top:16px; overflow:auto;">
      <table class="table">
        <thead>
          <tr>
            <th>Time</th>
            <th>User</th>
            <th>Target</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in paged" :key="item.id">
            <td>{{ formatTime(item.timestamp) }}</td>
            <td>{{ item.user }}</td>
            <td>{{ item.target }}</td>
            <td>{{ item.action }}</td>
          </tr>
          <tr v-if="paged.length===0">
            <td colspan="4" style="text-align:center; color:var(--color-muted);">No data</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div class="pagination" style="display:flex; align-items:center; gap:8px; justify-content:flex-end; margin-top:12px;">
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
import { ref, reactive, computed } from 'vue'
import { clone, logs as seedLogs } from '@/mocks/admin.js'

const state = reactive({ logs: clone(seedLogs) })

const filters = reactive({ keyword: '', start: '', end: '' })
const page = ref(1)
const pageSize = ref(10)

function formatTime(ts) {
  try {
    return new Date(ts).toLocaleString()
  } catch { return ts }
}

const filtered = computed(() => {
  const kw = (filters.keyword || '').toLowerCase()
  const startMs = filters.start ? new Date(filters.start).getTime() : -Infinity
  const endMs = filters.end ? new Date(filters.end).getTime() : Infinity
  return state.logs.filter(l => {
    const t = new Date(l.timestamp).getTime()
    const matchTime = t >= startMs && t <= endMs
    const join = `${l.user} ${l.target} ${l.action}`.toLowerCase()
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
</script>

<style scoped>
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { padding: 10px 12px; border-bottom: 1px solid #e5e7eb; text-align: left; white-space: nowrap; }
.table th { background: #f9fafb; font-weight: 700; }
</style>

