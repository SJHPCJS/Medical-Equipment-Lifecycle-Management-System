<template>
  <div class="dashboard">
    <div class="grid">
      <StatCard label="Total Departments" :value="overall.departmentCount" />
      <StatCard label="Total Employees" :value="overall.employeeCount" />
      <StatCard label="Total Tickets" :value="overall.ticketCount" />
    </div>
  </div>
</template>

<script setup>
import StatCard from '@/components/layout/StatCard.vue'
import { reactive, onMounted } from 'vue'

const overall = reactive({ departmentCount: 0, employeeCount: 0, ticketCount: 0 })

async function loadOverall() {
  try {
    const r = await fetch('/req/admin/dashboard')
    const j = await r.json()
    if (j.code === '000' && j.data && j.data.overall) Object.assign(overall, j.data.overall)
  } catch {}
}

onMounted(loadOverall)
</script>

<style scoped>
.grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
}
</style>


