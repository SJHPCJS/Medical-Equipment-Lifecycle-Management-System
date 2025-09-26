<template>
  <header class="topbar card">
    <div class="left">
      <div class="title">Medical Equipment Lifecycle Management System</div>
    </div>
    <div class="right">
      <span class="icon" title="Notifications">🔔</span>
      <span class="icon" title="Help">💬</span>
      <div class="avatar" :title="username">{{ initials }}</div>
      <div class="user">{{ username || 'Guest' }}</div>
    </div>
  </header>
  
</template>

<script setup>
import { computed } from 'vue'
const username = computed(() => localStorage.getItem('demo_username') || '')
const initials = computed(() => {
  const n = (username.value || '').trim()
  if (!n) return 'U'
  const parts = n.split(' ')
  if (parts.length >= 2) return (parts[0][0] + parts[1][0]).toUpperCase()
  return n.slice(0,1).toUpperCase()
})
</script>

<style scoped>
.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
}
.title {
  font-weight: 700;
}
.right {
  display: flex;
  gap: 12px;
  align-items: center;
}
.user {
  color: var(--color-muted);
  font-size: 14px;
}
.icon { font-size: 18px; cursor: default; }
.avatar { width: 28px; height: 28px; border-radius: 50%; background: #e5e7eb; color: #111; display: flex; align-items: center; justify-content: center; font-size: 12px; font-weight: 700; }
</style>


