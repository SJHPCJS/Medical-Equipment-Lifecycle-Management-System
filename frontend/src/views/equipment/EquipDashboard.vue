<template>
  <div>
    <div class="grid">
      <StatCard label="In Use" value="--" />
      <StatCard label="Under Repair" value="--" />
      <StatCard label="Pending Scrap" value="--" />
      <StatCard label="Warranty Expiring Soon" value="--" />
      <StatCard label="Planned Maintenance" value="--" />
      <StatCard label="Today's Tickets" value="--" />
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
.grid { display: grid; gap: 16px; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); }
</style>


