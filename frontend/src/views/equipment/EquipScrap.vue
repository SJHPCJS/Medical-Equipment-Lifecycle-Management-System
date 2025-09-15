<template>
  <div class="card" style="padding:16px;">
    <div class="title-lg">Scrap Management</div>
    <div class="subtitle" style="margin-top:8px;">Manage scrap applications and records (mock data).</div>

    <div class="table-wrapper" style="margin-top:16px; overflow:auto;">
      <table class="table" style="table-layout:fixed; width:100%;">
        <thead>
          <tr>
            <th>Scrap ID</th>
            <th>Device</th>
            <th>Reason</th>
            <th>Status</th>
            <th>Record</th>
            <th style="width:200px;">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="s in state.list" :key="s.id">
            <td>{{ s.id }}</td>
            <td>{{ s.deviceId }}</td>
            <td style="white-space:normal;">{{ s.reason }}</td>
            <td>{{ s.status }}</td>
            <td><input class="input" v-model="s.record" placeholder="Disposal record" /></td>
            <td>
              <button class="btn btn-green" @click="approve(s)" v-if="s.status==='Pending'">Approve</button>
              <button class="btn btn-red" style="margin-left:8px;" @click="reject(s)" v-if="s.status==='Pending'">Reject</button>
              <button class="btn btn-blue" v-if="s.status!=='Pending'" @click="view(s)">View</button>
            </td>
          </tr>
          <tr v-if="state.list.length===0">
            <td colspan="6" style="text-align:center; color:var(--color-muted);">No applications</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
  
</template>

<script setup>
import { reactive } from 'vue'
import { clone, scrapApplications as seed } from '@/mocks/equipment.js'

const state = reactive({ list: clone(seed) })

function approve(s) { s.status = 'Approved' }
function reject(s) { s.status = 'Rejected' }
function view(s) { alert(`View ${s.id} (demo only)`) }
</script>

<style scoped>
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { padding: 10px 12px; border-bottom: 1px solid #e5e7eb; text-align: left; white-space: normal; word-break: break-word; }
.table th { background: #f9fafb; font-weight: 700; }
</style>


