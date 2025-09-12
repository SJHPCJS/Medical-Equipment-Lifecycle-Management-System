<template>
  <div class="card" style="padding:16px;">
    <div class="title-lg">Scrap Management</div>
    <div class="subtitle" style="margin-top:8px;">Scrap application approvals and disposal records.</div>

    <div class="table-wrapper" style="margin-top:16px; overflow:auto;">
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Device</th>
            <th>Reason</th>
            <th>Status</th>
            <th>Record</th>
            <th style="width:180px;">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="s in list" :key="s.id">
            <td>{{ s.id }}</td>
            <td>{{ s.deviceId }}</td>
            <td>{{ s.reason }}</td>
            <td>{{ s.status }}</td>
            <td><input class="input" v-model="s.record" placeholder="Disposal record" /></td>
            <td>
              <button class="btn" @click="approve(s)">Approve</button>
              <button class="btn" style="margin-left:8px;" @click="reject(s)">Reject</button>
            </td>
          </tr>
          <tr v-if="list.length===0">
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
const list = state.list

function approve(s) { s.status = 'Approved' }
function reject(s) { s.status = 'Rejected' }
</script>

<style scoped>
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { padding: 10px 12px; border-bottom: 1px solid #e5e7eb; text-align: left; white-space: nowrap; }
.table th { background: #f9fafb; font-weight: 700; }
</style>
