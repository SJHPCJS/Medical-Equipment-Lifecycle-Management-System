<template>
  <aside class="sidebar">
    <div class="logo">{{ resolvedTitle }}</div>
    <nav class="menu">
      <RouterLink v-for="(i, idx) in items" :key="idx" class="item" :to="i.to">{{ i.label }}</RouterLink>
    </nav>
  </aside>
 </template>

<script setup>
import { useRoute } from 'vue-router'
import { computed } from 'vue'

const props = defineProps({
  title: { type: String, default: '' },
  items: { type: Array, default: () => [] },
})

const route = useRoute()
const resolvedTitle = computed(() => {
  if (props.title) return props.title
  const p = route.path || ''
  if (p.startsWith('/admin')) return 'System Administrator'
  if (p.startsWith('/equipment')) return 'Equipment Manager'
  if (p.startsWith('/department')) return 'Department User'
  if (p.startsWith('/procurement')) return 'Procurement Staff'
  return ''
})
</script>

<style scoped>
.sidebar {
  width: 240px;
  background: var(--color-primary);
  color: #fff;
  padding: 20px 16px;
  box-shadow: var(--shadow-md);
}

.logo {
  font-weight: 800;
  letter-spacing: 0.5px;
  margin-bottom: 20px;
}

.menu {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.item {
  color: #eef2ff;
  text-decoration: none;
  padding: 10px 12px;
  border-radius: 8px;
}

.item.router-link-active {
  background: rgba(255,255,255,0.18);
}
</style>


