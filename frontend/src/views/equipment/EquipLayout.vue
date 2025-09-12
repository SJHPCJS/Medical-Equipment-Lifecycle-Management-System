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
  { label: 'Dashboard', to: '/equipment/dashboard' },
  { label: 'Equipment Ledger', to: '/equipment/ledger' },
  { label: 'Onboarding & Profiling', to: '/equipment/onboarding' },
  { label: 'Repairs', to: '/equipment/repairs' },
  { label: 'Scrap Management', to: '/equipment/scrap' },
]
</script>

<style scoped>
.admin-page { display: grid; grid-template-columns: 240px 1fr; height: 100vh; }
.content { display: flex; flex-direction: column; gap: 16px; padding: 16px; }
.content-body { padding: 16px; }
</style>


