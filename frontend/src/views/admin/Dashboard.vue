<template>
  <div class="dashboard">
    <div class="grid">
      <StatCard label="Total Departments" :value="totalDepartments" />
      <StatCard label="Total Employees" :value="totalEmployees" />
      <StatCard label="Total Tickets" :value="totalTickets" />
    </div>
    <div class="card" style="margin-top:16px; padding:16px;">
      <div class="subtitle">Data is loaded from backend APIs.</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import StatCard from '@/components/layout/StatCard.vue'

const totalDepartments = ref(0)
const totalEmployees = ref(0)
const totalTickets = ref(0)

async function load() {
  try {
    const resp = await fetch('/req/admin/overall')
    const json = await resp.json()
    if (json.code === '000') {
      totalDepartments.value = json.data.departmentCount || 0
      totalEmployees.value = json.data.employeeCount || 0
      totalTickets.value = json.data.ticketCount || 0
    }
  } catch (e) {
    console.error(e)
  }
}

onMounted(load)
</script>

<style scoped>
.grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
}
</style>


