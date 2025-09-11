<template>
  <div class="admin-page">
    <Sidebar :items="menuItems" />
    <main class="content">
      <Topbar />
      <div class="content-body">
        <RouterView />
      </div>
    </main>
  </div>
</template>

<script setup>
import Sidebar from '@/components/layout/Sidebar.vue'
import Topbar from '@/components/layout/Topbar.vue'

const menuItems = [
  { label: 'Dashboard', to: '/admin/dashboard' },
  { label: 'Users & Access', to: '/admin/users' },
  { label: 'Roles & Permissions', to: '/admin/roles' },
  { label: 'Organization & Departments', to: '/admin/orgs' },
  { label: 'System Settings', to: '/admin/settings' },
  { label: 'Logs & Audit', to: '/admin/logs' },
  { label: 'Announcements & Help', to: '/admin/notices' },
]
</script>

<style scoped>
.admin-page {
  display: grid;
  grid-template-columns: 240px 1fr;
  height: 100vh;
  gap: 0;
}
.content {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px;
}
.content-body {
  padding: 16px;
  background: transparent;
}
</style>


