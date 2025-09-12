<template>
  <div>
    <div class="grid">
      <StatCard label="Open Requisitions" :value="openReqs" />
      <StatCard label="Pending Receiving" :value="pendingReceiving" />
      <StatCard label="Budget Spent (USD)" :value="spent" />
      <StatCard label="Vendors" :value="vendorCount" />
    </div>
  </div>
</template>

<script setup>
import StatCard from '@/components/layout/StatCard.vue'
import { plans, orders, budgetUsage, vendors } from '@/mocks/procurement.js'

const openReqs = plans.filter(p => p.status === 'Pending').length
const pendingReceiving = orders.filter(o => o.status === 'Confirmed Arrival').length
const spent = budgetUsage.totalSpent
const vendorCount = vendors.length
</script>

<style scoped>
.grid { display: grid; gap: 16px; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); }
</style>


