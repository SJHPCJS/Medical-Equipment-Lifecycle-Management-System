<template>
  <div>
    <div class="grid">
      <StatCard label="Devices: Unassigned" :value="overview.unassigned" />
      <StatCard label="Devices: In Use" :value="overview.inUse" />
      <StatCard label="Devices: Under Repair" :value="overview.underRepair" />
      <StatCard label="Devices: Pending Scrap" :value="overview.pendingScrap" />
      <StatCard label="Today: Pending Response" :value="today.pendingResponse" />
      <StatCard label="Today: In Progress" :value="today.inProgress" />
      <StatCard label="Today: Pending Acceptance" :value="today.pendingAcceptance" />
    </div>
    <div class="card" style="margin-top:16px; padding:16px;">
      <div class="subtitle">Equipment Manager dashboard</div>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import axios from 'axios'
import StatCard from '@/components/layout/StatCard.vue'

const overview = reactive({
  unassigned: 0,
  inUse: 0,
  underRepair: 0,
  pendingScrap: 0,
})

const today = reactive({
  pendingResponse: 0,
  inProgress: 0,
  pendingAcceptance: 0,
})

async function loadDashboard() {
  try {
    const res = await axios.get('/req/dashboard/overview')
    Object.assign(overview, res.data.equipmentCounts)
    Object.assign(today, res.data.todayTickets)
  } catch (err) {
    console.error('Failed to load dashboard data', err)
  }
}

onMounted(() => {
  loadDashboard()
})
</script>

<style scoped>
.grid { display: grid; gap: 16px; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); }
</style>
