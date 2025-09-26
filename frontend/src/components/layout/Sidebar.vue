<template>
  <aside class="sidebar">
    <div class="brand">
      <div class="mark"></div>
      <div class="brand-name">Return False</div>
    </div>
    <div class="section-title">{{ resolvedTitle }}</div>
    <nav class="menu">
      <RouterLink v-for="(i, idx) in items" :key="idx" class="item" :to="i.to">{{ i.label }}</RouterLink>
    </nav>
    <div class="bottom">
      <RouterLink class="item" :to="accountPath">Account</RouterLink>
      <button class="item linklike" @click="handleLogout">Logout</button>
    </div>
  </aside>
 </template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { computed } from 'vue'

const props = defineProps({
  title: { type: String, default: '' },
  items: { type: Array, default: () => [] },
})

const route = useRoute()
const router = useRouter()
const resolvedTitle = computed(() => {
  if (props.title) return props.title
  const p = route.path || ''
  if (p.startsWith('/admin')) return 'System Administrator'
  if (p.startsWith('/equipment')) return 'Equipment Manager'
  if (p.startsWith('/department')) return 'Department User'
  if (p.startsWith('/procurement')) return 'Procurement Staff'
  return ''
})

const accountPath = computed(() => '/admin/account')

function handleLogout() {
  localStorage.removeItem('demo_logged_in')
  localStorage.removeItem('account_id')
  localStorage.removeItem('role')
  localStorage.removeItem('demo_username')
  router.push('/login')
}
</script>

<style scoped>
.sidebar {
  width: 240px;
  background: var(--color-primary);
  color: #fff;
  padding: 20px 16px;
  box-shadow: var(--shadow-md);
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.brand { display: flex; align-items: center; gap: 10px; }
.mark { width: 20px; height: 20px; background: linear-gradient(135deg, #5eead4, #60a5fa); border-radius: 6px; box-shadow: 0 6px 16px rgba(0,0,0,0.15); }
.brand-name { font-weight: 800; letter-spacing: 0.2px; }
.section-title { color: #e5e7eb; font-size: 12px; opacity: 0.9; margin-top: 4px; }

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

.linklike { background: transparent; border: none; text-align: left; cursor: pointer; }

.bottom { margin-top: auto; display: flex; flex-direction: column; gap: 8px; }
</style>


