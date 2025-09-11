<template>
  <div>
    <div class="grid">
      <StatCard label="Open Requisitions" value="--" />
      <StatCard label="Pending Receiving" value="--" />
      <StatCard label="Budget Utilization" value="--" />
      <StatCard label="Vendors" value="--" />
    </div>
  </div>
</template>

<script setup>
import StatCard from '@/components/layout/StatCard.vue'
</script>

<style scoped>
.grid { display: grid; gap: 16px; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); }
</style>


