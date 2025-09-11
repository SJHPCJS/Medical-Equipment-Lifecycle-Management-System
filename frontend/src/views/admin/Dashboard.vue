<template>
  <div class="dashboard">
    <div class="grid">
      <StatCard label="Sign-ins Today" value="--" />
      <StatCard label="Failed Login Attempts" value="--" />
      <StatCard label="Total Departments" value="--" />
      <StatCard label="Total Employees" value="--" />
      <StatCard label="Total Tickets" value="--" />
    </div>
    <div class="card" style="margin-top:16px; padding:16px;">
      <div class="subtitle">Demo dashboard. Static placeholders, no backend connected.</div>
    </div>
  </div>
</template>

<script setup>
import StatCard from '@/components/layout/StatCard.vue'
</script>

<style scoped>
.grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
}
</style>


