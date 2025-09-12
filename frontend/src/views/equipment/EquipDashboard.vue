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
      <div class="subtitle">Equipment Manager dashboard using frontend-only mock data.</div>
    </div>
  </div>
</template>

<script setup>
import StatCard from '@/components/layout/StatCard.vue'
import { devices, todayTickets } from '@/mocks/equipment.js'

const overview = {
  unassigned: devices.filter(d => d.status === 'Unassigned').length,
  inUse: devices.filter(d => d.status === 'In Use').length,
  underRepair: devices.filter(d => d.status === 'Under Repair').length,
  pendingScrap: devices.filter(d => d.status === 'Pending Scrap').length,
}

const today = todayTickets
</script>

<style scoped>
.grid { display: grid; gap: 16px; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); }
</style>


