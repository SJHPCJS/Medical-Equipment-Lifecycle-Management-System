<template>
  <div class="admin-page">
    <AdminSidebar />
    <main class="content">
      <AdminTopbar />
      <div class="page-header">
        <div class="heading">
          <div class="page-title">{{ pageTitle }}</div>
          <div class="page-date">{{ today }}</div>
        </div>
        <div v-if="showCharts" class="stats-grid">
          <div class="card stat-card">
            <div class="chart-title">Employees by Department</div>
            <apexchart :key="chartKeys.dept" height="200" type="bar" :options="charts.deptBar.options" :series="charts.deptBar.series" />
          </div>
          <div class="card stat-card">
            <div class="chart-title">Users by Role</div>
            <apexchart :key="chartKeys.roles" height="200" type="donut" :options="charts.rolesPie.options" :series="charts.rolesPie.series" />
          </div>
          <div class="card stat-card">
            <div class="chart-title">Tickets by Category</div>
            <apexchart :key="chartKeys.tickets" height="200" type="bar" :options="charts.ticketsBar.options" :series="charts.ticketsBar.series" />
          </div>
        </div>
      </div>
      <div class="content-body">
        <RouterView />
      </div>
    </main>
  </div>
  
 </template>

<script setup>
import AdminSidebar from '@/components/admin/AdminSidebar.vue'
import AdminTopbar from '@/components/admin/AdminTopbar.vue'
import { useRoute } from 'vue-router'
import { computed, reactive, onMounted, ref, watch } from 'vue'

// menu 由 AdminSidebar 维护

const route = useRoute()
const pageTitle = computed(() => route.meta.title || 'Admin')
const showCharts = computed(() => Boolean(route.meta.showCharts))

function formatDate(d) {
  try {
    return d.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' })
  } catch {
    return ''
  }
}
const today = formatDate(new Date())

const charts = reactive({
  deptBar: {
    options: {
      chart: { toolbar: { show: false } },
      plotOptions: { bar: { columnWidth: '55%' } },
      xaxis: { categories: [], labels: { show: true } },
      dataLabels: { enabled: true },
      colors: ['#3b82f6']
    },
    series: [{ name: 'Employees', data: [] }]
  },
  rolesPie: {
    options: {
      labels: [],
      legend: { show: true, position: 'bottom' },
      dataLabels: { enabled: true },
      colors: ['#60a5fa','#34d399','#fbbf24','#f87171']
    },
    series: []
  },
  ticketsBar: {
    options: {
      chart: { toolbar: { show: false } },
      plotOptions: { bar: { columnWidth: '55%' } },
      xaxis: { categories: [], labels: { show: true } },
      dataLabels: { enabled: true },
      colors: ['#10b981']
    },
    series: [{ name: 'Tickets', data: [] }]
  },
})

const chartsLoaded = ref(false)
const chartKeys = reactive({ dept: 0, roles: 0, tickets: 0 })

async function loadCharts() {
  try {
    const r = await fetch('/req/admin/dashboard')
    const j = await r.json()
    const data = j.data || {}
    // overall 不在这里用，Dashboard.vue 会单独显示卡片
    const departments = Array.isArray(data.departments) ? data.departments : []
    charts.deptBar.options.xaxis.categories = departments.map(d=>String(d.name || 'Dept'))
    charts.deptBar.series[0].data = departments.map(d=>Number(d.count || 0))
    chartKeys.dept++

    const usersByRole = Array.isArray(data.usersByRole) ? data.usersByRole : []
    const roleMap = { 'Admin':'System Admin', 'E-Manager':'Equip Manager', 'D-User':'Dept User', 'P-Staff':'Proc Staff', 'SYS_ADMIN':'System Admin','EQUIP_MANAGER':'Equip Manager','DEPT_USER':'Dept User','PROC_STAFF':'Proc Staff' }
    charts.rolesPie.options.labels = usersByRole.map(r => String(roleMap[r.role] || r.role || 'Unknown'))
    charts.rolesPie.series = usersByRole.map(r => Number(r.count || 0))
    chartKeys.roles++

    const ticketCategories = Array.isArray(data.ticketCategories) ? data.ticketCategories : []
    charts.ticketsBar.options.xaxis.categories = ticketCategories.map(t => String(t.name))
    charts.ticketsBar.series[0].data = ticketCategories.map(t => Number(t.count || 0))
    chartKeys.tickets++
    chartsLoaded.value = true
  } catch {}
}

onMounted(() => {
  if (showCharts.value) loadCharts()
})

watch(() => showCharts.value, (v) => { if (v && !chartsLoaded.value) loadCharts() })
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
.page-header {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.heading {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
}
.page-title { font-size: 28px; font-weight: 800; }
.page-date { color: var(--color-muted); font-size: 14px; font-weight: 600; }
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
}
.stat-card { padding: 8px; }
.chart-title { font-weight: 700; margin: 12px 12px 0; font-size: 14px; }
.content-body {
  padding: 16px;
  background: transparent;
}
</style>


