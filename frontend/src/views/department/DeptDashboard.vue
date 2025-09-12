<template>
  <div>
    <div class="grid">
      <StatCard label="In-Use Devices" :value="inUse" />
      <StatCard label="Devices Under Repair" :value="underRepair" />
      <StatCard label="My Todos: Usage Logs" :value="todosUsage" />
      <StatCard label="My Todos: Repair Confirmations" :value="todosRepair" />
    </div>
  </div>
</template>

<script setup>
import StatCard from '@/components/layout/StatCard.vue'
import { deptDevices, usageTodos, repairConfirmTodos } from '@/mocks/department.js'

const inUse = deptDevices.filter(d => d.status === 'In Use').length
const underRepair = deptDevices.filter(d => d.status === 'Under Repair').length
const todosUsage = usageTodos.length
const todosRepair = repairConfirmTodos.length
</script>

<style scoped>
.grid { display: grid; gap: 16px; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); }
</style>


