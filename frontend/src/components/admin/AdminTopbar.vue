<template>
  <header class="admin-topbar wide">
    <div class="left">
      <div class="title">Medical Equipment Lifecycle Management System</div>
    </div>
    <div class="right">
      <button class="icon" title="Notifications">🔔</button>
      <button class="icon" title="Help">💬</button>
      <div class="role">{{ roleText }}</div>
      <div class="avatar">{{ initials }}</div>
      <div class="username">{{ username || 'Guest' }}</div>
    </div>
  </header>
  
</template>

<script setup>
import { computed } from 'vue'
const username = computed(() => localStorage.getItem('demo_username') || '')
const role = computed(() => localStorage.getItem('role') || '')
const roleText = computed(() => {
  const r = role.value
  if (r === 'Admin' || r === 'SYS_ADMIN') return 'Administrator'
  if (r === 'E-Manager' || r === 'EQUIP_MANAGER') return 'Equipment Manager'
  if (r === 'D-User' || r === 'DEPT_USER') return 'Department User'
  if (r === 'P-Staff' || r === 'PROC_STAFF') return 'Procurement Staff'
  return r
})
const initials = computed(() => {
  const n = (username.value || '').trim(); if (!n) return 'U'; const parts = n.split(' '); return (parts[0][0] + (parts[1]?.[0] || '')).toUpperCase()
})
</script>

<style scoped>
.admin-topbar { background: #ffffff; border: 1px solid #eef2f7; border-radius: var(--radius-xl); padding: 12px 18px; display:flex; align-items:center; justify-content: space-between; }
.admin-topbar.wide { padding: 16px 24px; }
.title { font-weight: 800; font-size: var(--fs-xl); }
.right { display:flex; align-items:center; gap: 12px; }
.icon { background: #f3f4f6; border: none; border-radius: 12px; width: 36px; height: 36px; display:flex; align-items:center; justify-content:center; cursor: pointer; }
.role { color: #6b7280; font-size: 12px; }
.avatar { width: 30px; height: 30px; border-radius: 50%; background:#e5e7eb; display:flex; align-items:center; justify-content:center; font-size:12px; font-weight:700; }
.username { color: #111827; font-size: 14px; }
</style>



