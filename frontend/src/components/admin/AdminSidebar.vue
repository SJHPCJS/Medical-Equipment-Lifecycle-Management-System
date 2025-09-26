<template>
  <aside class="admin-sidebar">
    <div class="brand">
      <div class="logo"></div>
      <div class="name">Return False</div>
    </div>
    <nav class="menu">
      <RouterLink class="item" to="/admin/dashboard"><span class="icon">🏠</span>Dashboard</RouterLink>
      <RouterLink class="item" to="/admin/users"><span class="icon">👥</span>Users</RouterLink>
      <RouterLink class="item" to="/admin/orgs"><span class="icon">🏢</span>Departments</RouterLink>
      <RouterLink class="item" to="/admin/settings"><span class="icon">⚙️</span>Settings</RouterLink>
      <RouterLink class="item" to="/admin/logs"><span class="icon">🧾</span>Logs</RouterLink>
    </nav>
    <div class="bottom">
      <RouterLink class="item" to="/admin/account"><span class="icon">👤</span>Account</RouterLink>
      <button class="item link" @click="logout"><span class="icon">🚪</span>Logout</button>
    </div>
  </aside>
  
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
function logout() {
  localStorage.removeItem('demo_logged_in')
  localStorage.removeItem('account_id')
  localStorage.removeItem('role')
  localStorage.removeItem('demo_username')
  router.push('/login')
}
</script>

<style scoped>
.admin-sidebar {
  width: 248px;
  background: #ffffff;
  border-right: 1px solid #eef2f7;
  height: 100vh;
  position: sticky;
  top: 0;
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 18px 16px;
  box-sizing: border-box;
}
.brand { display: flex; align-items: center; gap: 10px; padding: 8px 8px; }
.logo { width: 22px; height: 22px; border-radius: 6px; background: linear-gradient(135deg, #60a5fa, #34d399); box-shadow: 0 6px 16px rgba(0,0,0,0.08); }
.name { font-weight: 800; color: #1f2937; font-size: var(--fs-xl); }
.menu { display: flex; flex-direction: column; gap: 6px; }
.item { display: flex; align-items: center; gap: 10px; color: #111827; text-decoration: none; padding: 10px 12px; border-radius: 12px; font-size: var(--fs-lg); }
.item.router-link-active { background: #f1f5f9; }
.icon { width: 18px; text-align: center; }
.bottom { margin-top: auto; display: flex; flex-direction: column; gap: 6px; }
.link { background: transparent; border: none; text-align: left; cursor: pointer; }
</style>



