<template>
  <div class="dashboard">
    <div class="grid">
      <StatCard label="Total Departments" :value="totalDepartments" />
      <StatCard label="Total Employees" :value="totalEmployees" />
      <StatCard label="Total Tickets" :value="totalTickets" />
    </div>
    <div class="card" style="margin-top:16px; padding:16px;">
      <div class="subtitle">System Administrator dashboard using frontend-only mock data.</div>
    </div>
  </div>
</template>

<script setup>
import StatCard from '@/components/layout/StatCard.vue'
import { departments, users, ticketsSummary } from '@/mocks/admin.js'

const totalDepartments = departments.length
const totalEmployees = users.length
const totalTickets = ticketsSummary.totalTickets
</script>

<style scoped>
.grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
}
</style>


