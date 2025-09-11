<template>
  <header class="topbar card">
    <div class="left">
      <div class="title">System Administrator</div>
    </div>
    <div class="right">
      <div class="user">{{ username || 'Guest' }}</div>
      <button class="btn btn-primary" @click="handleLogout">Sign Out</button>
    </div>
  </header>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { computed } from 'vue'
const router = useRouter()
const username = computed(() => localStorage.getItem('demo_username') || '')

function handleLogout() {
  localStorage.removeItem('demo_logged_in')
  localStorage.removeItem('demo_username')
  router.push('/login')
}
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
</style>


