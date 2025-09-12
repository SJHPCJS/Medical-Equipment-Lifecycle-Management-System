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
  { label: 'Dashboard', to: '/department/dashboard' },
  { label: 'My Devices', to: '/department/my-devices' },
  { label: 'Repair Request', to: '/department/repair' },
  { label: 'Equipment Requests', to: '/department/requests' },
]
</script>

<style scoped>
.admin-page { display: grid; grid-template-columns: 240px 1fr; height: 100vh; }
.content { display: flex; flex-direction: column; gap: 16px; padding: 16px; }
.content-body { padding: 16px; }
</style>


