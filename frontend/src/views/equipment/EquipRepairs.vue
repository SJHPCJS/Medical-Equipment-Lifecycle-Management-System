<template>
  <div class="card" style="padding:16px;">
    <div class="title-lg">Repair Management</div>
    <div class="subtitle" style="margin-top:8px;">Ticket pool from department users.</div>

    <div class="filters" style="margin-top:16px; display:grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap:12px;">
      <div>
        <label>Status</label>
        <MultiSelect v-model="filters.statuses" :options="statuses.map(s => ({ value: s, label: s }))" placeholder="All status" />
      </div>
      <div>
        <label>Department</label>
        <MultiSelect v-model="filters.departments" :options="departmentList.map(d => ({ value: d, label: d }))" placeholder="All departments" />
      </div>
      <div style="display:flex; gap:8px; align-items:end;">
        <button class="btn" @click="resetFilters">Reset</button>
      </div>
    </div>

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
          <tr v-for="t in filtered" :key="t.id">
            <td>{{ t.id }}</td>
            <td>{{ formatTime(t.createdAt) }}</td>
            <td>{{ t.finishedAt ? formatTime(t.finishedAt) : '-' }}</td>
            <td><input class="input" v-model="t.notes" placeholder="Process notes" /></td>
            <td><input class="input" v-model.number="t.cost" type="number" min="0" step="1" style="width:120px;" /></td>
            <td><input class="input" v-model="t.result" placeholder="Result/Acceptance" /></td>
            <td>
              <select class="input" v-model="t.status">
                <option v-for="s in statuses" :key="s" :value="s">{{ s }}</option>
              </select>
            </td>
            <td>{{ t.department }}</td>
            <td style="white-space:nowrap;">
              <button class="btn" @click="save(t)">Save</button>
              <button class="btn" style="margin-left:8px;" @click="complete(t)" :disabled="t.status==='Completed'">Complete</button>
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
import { reactive, computed } from 'vue'
import MultiSelect from '@/components/MultiSelect.vue'
import { clone, repairTickets as seed } from '@/mocks/equipment.js'

const state = reactive({ tickets: clone(seed) })
const tickets = state.tickets
const statuses = ['Pending Review','In Repair','In Acceptance','Completed','Rejected']

const departmentList = computed(() => Array.from(new Set(tickets.map(t => t.department))))

const filters = reactive({ statuses: [], departments: [] })
const filtered = computed(() => tickets.filter(t => {
  const matchStatus = filters.statuses.length===0 || filters.statuses.includes(t.status)
  const matchDept = filters.departments.length===0 || filters.departments.includes(t.department)
  return matchStatus && matchDept
}))

function resetFilters() { filters.statuses = []; filters.departments = [] }

function formatTime(ts) { try { return new Date(ts).toLocaleString() } catch { return ts } }

function save(t) { alert(`Saved ${t.id} (demo only)`) }
function complete(t) {
  t.status = 'Completed'
  t.finishedAt = new Date().toISOString()
}
</script>

<style scoped>
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { padding: 10px 12px; border-bottom: 1px solid #e5e7eb; text-align: left; white-space: normal; word-break: break-word; }
.table th { background: #f9fafb; font-weight: 700; }
</style>
