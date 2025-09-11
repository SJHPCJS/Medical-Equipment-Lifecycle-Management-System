<template>
  <div>
    <div class="grid">
      <StatCard label="Devices In Department" value="--" />
      <StatCard label="Bookable Devices" value="--" />
      <StatCard label="Pending Usage Logs" value="--" />
      <StatCard label="Pending Repair Confirmations" value="--" />
    </div>
  </div>
</template>

<script setup>
import StatCard from '@/components/layout/StatCard.vue'
</script>

<style scoped>
.grid { display: grid; gap: 16px; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); }
</style>


