<template>
  <aside class="sidebar">
    <div class="logo">MELMS</div>
    <nav class="menu">
      <RouterLink class="item" to="/admin/dashboard">Dashboard</RouterLink>
      <RouterLink class="item" to="/admin/users">Users & Access</RouterLink>
      <RouterLink class="item" to="/admin/roles">Roles & Permissions</RouterLink>
      <RouterLink class="item" to="/admin/orgs">Organization & Departments</RouterLink>
      <RouterLink class="item" to="/admin/settings">System Settings</RouterLink>
      <RouterLink class="item" to="/admin/logs">Logs & Audit</RouterLink>
      <RouterLink class="item" to="/admin/notices">Announcements & Help</RouterLink>
    </nav>
  </aside>
</template>

<script setup>
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


