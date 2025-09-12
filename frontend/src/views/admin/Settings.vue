<template>
  <div class="card" style="padding:16px;">
    <div class="title-lg">System Settings</div>
    <p class="subtitle" style="margin-top:8px;">UI only. Maintenance mode button is non-functional.</p>

    <div class="settings-grid">
      <div class="setting-item card" style="padding:16px;">
        <div style="font-weight:700;">Maintenance Mode</div>
        <div class="subtitle">When enabled, only Admins can access the system.</div>
        <div style="display:flex; gap:8px; margin-top:12px;">
          <button class="btn btn-primary" @click="toggleMaintenance">Open Maintenance Mode</button>
          <span style="color:var(--color-muted);">(Demo only)</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
function toggleMaintenance() {
  alert('Demo only: maintenance mode is not implemented in frontend.')
}
</script>

<style scoped>
.settings-grid {
  margin-top: 16px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 12px;
}
</style>

