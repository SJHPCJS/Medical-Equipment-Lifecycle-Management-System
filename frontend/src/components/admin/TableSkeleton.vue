<template>
  <div class="card" :style="{ padding: '12px' }">
    <div class="skeleton" style="height:18px; width: 30%; border-radius:8px;"></div>
    <div style="margin-top:12px; display:grid; gap:8px;">
      <div v-for="i in rows" :key="i" class="skeleton" style="height:36px; border-radius:10px;"></div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({ rows: { type: Number, default: 6 } })
const rows = props.rows
</script>
