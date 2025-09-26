<template>
  <div class="empty card" :style="{ padding: '24px', textAlign: 'center' }">
    <div class="art" />
    <div class="title">{{ title }}</div>
    <div class="hint">{{ hint }}</div>
    <div v-if="$slots.action" class="action"><slot name="action" /></div>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, default: 'No Data' },
  hint: { type: String, default: 'Try adjusting your filters or create a new item.' },
})
</script>

<style scoped>
.empty { display:flex; flex-direction:column; align-items:center; gap: 10px; }
.art { width: 160px; height: 100px; background: url('data:image/svg+xml;utf8,<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"320\" height=\"200\"><defs><linearGradient id=\"g\" x1=\"0\" x2=\"1\"><stop offset=\"0\" stop-color=\"%23e5e7eb\"/><stop offset=\"1\" stop-color=\"%23f3f4f6\"/></linearGradient></defs><rect width=\"320\" height=\"200\" rx=\"16\" fill=\"url(%23g)\"/><g fill=\"%23cbd5e1\"><rect x=\"24\" y=\"40\" width=\"272\" height=\"16\" rx=\"8\"/><rect x=\"24\" y=\"72\" width=\"184\" height=\"12\" rx=\"6\"/><rect x=\"24\" y=\"108\" width=\"272\" height=\"52\" rx=\"10\"/></g></svg>') center/contain no-repeat; opacity:.9; }
.title { font-weight: 800; }
.hint { color: var(--color-muted); font-size: var(--fs-sm); }
.action { margin-top: 8px; }
</style>
