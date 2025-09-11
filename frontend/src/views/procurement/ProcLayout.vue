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
  { label: 'Dashboard', to: '/procurement/dashboard' },
  { label: 'Plans & Requisitions', to: '/procurement/plans' },
  { label: 'Vendors', to: '/procurement/vendors' },
  { label: 'Bids / RFQ', to: '/procurement/bids' },
  { label: 'Purchase Orders', to: '/procurement/orders' },
  { label: 'Receiving & QA', to: '/procurement/receiving' },
  { label: 'Contracts & Budget', to: '/procurement/contracts' },
]
</script>

<style scoped>
.admin-page { display: grid; grid-template-columns: 240px 1fr; height: 100vh; }
.content { display: flex; flex-direction: column; gap: 16px; padding: 16px; }
.content-body { padding: 16px; }
</style>


