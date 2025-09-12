<template>
  <div class="card" style="padding:16px;">
    <div class="title-lg">Repair Management</div>
    <div class="subtitle" style="margin-top:8px;">Ticket pool from department users.</div>

    <div class="table-wrapper" style="margin-top:16px; overflow:auto;">
      <table class="table">
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
          <tr v-for="t in tickets" :key="t.id">
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
            <td>
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
import { reactive } from 'vue'
import { clone, repairTickets as seed } from '@/mocks/equipment.js'

const state = reactive({ tickets: clone(seed) })
const tickets = state.tickets
const statuses = ['Pending Review','In Repair','In Acceptance','Completed','Rejected']

function formatTime(ts) { try { return new Date(ts).toLocaleString() } catch { return ts } }

function save(t) { alert(`Saved ${t.id} (demo only)`) }
function complete(t) {
  t.status = 'Completed'
  t.finishedAt = new Date().toISOString()
}
</script>

<style scoped>
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { padding: 10px 12px; border-bottom: 1px solid #e5e7eb; text-align: left; white-space: nowrap; }
.table th { background: #f9fafb; font-weight: 700; }
</style>
