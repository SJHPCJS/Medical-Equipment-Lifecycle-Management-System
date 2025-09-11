<template>
  <div class="stat card">
    <div class="label">{{ label }}</div>
    <div class="value">{{ value }}</div>
  </div>
</template>

<script setup>
defineProps({
  label: { type: String, required: true },
  value: { type: [String, Number], default: '-' },
})
</script>

<style scoped>
.stat {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.label {
  font-size: 12px;
  color: var(--color-muted);
}
.value {
  font-size: 24px;
  font-weight: 800;
}
</style>


