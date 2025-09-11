<template>
  <div class="admin-page">
    <Sidebar />
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


