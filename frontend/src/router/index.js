import { createRouter, createWebHistory } from 'vue-router'
// LocalStorage-based auth (no Pinia)

const Login = () => import('@/views/Login.vue')
const AdminLayout = () => import('@/views/admin/AdminLayout.vue')
const Dashboard = () => import('@/views/admin/Dashboard.vue')
const Users = () => import('@/views/admin/Users.vue')
// removed Roles route
const Orgs = () => import('@/views/admin/Orgs.vue')
const Settings = () => import('@/views/admin/Settings.vue')
const Logs = () => import('@/views/admin/Logs.vue')
// removed Notices route

// Equipment Manager routes
const EquipLayout = () => import('@/views/equipment/EquipLayout.vue')
const EquipDashboard = () => import('@/views/equipment/EquipDashboard.vue')
const EquipLedger = () => import('@/views/equipment/EquipLedger.vue')
const EquipOnboarding = () => import('@/views/equipment/EquipOnboarding.vue')
const EquipStatusTransfer = () => import('@/views/equipment/EquipStatusTransfer.vue')
const EquipDemands = () => import('@/views/equipment/EquipDemands.vue')
const EquipMaintenance = () => import('@/views/equipment/EquipMaintenance.vue')
const EquipRepairs = () => import('@/views/equipment/EquipRepairs.vue')
const EquipScrap = () => import('@/views/equipment/EquipScrap.vue')

// Department User routes
const DeptLayout = () => import('@/views/department/DeptLayout.vue')
const DeptDashboard = () => import('@/views/department/DeptDashboard.vue')
const DeptMyDevices = () => import('@/views/department/DeptMyDevices.vue')
const DeptUsage = () => import('@/views/department/DeptUsage.vue')
const DeptRepair = () => import('@/views/department/DeptRepair.vue')
const DeptRequests = () => import('@/views/department/DeptRequests.vue')

// Procurement Staff routes
const ProcLayout = () => import('@/views/procurement/ProcLayout.vue')
const ProcDashboard = () => import('@/views/procurement/ProcDashboard.vue')
const ProcPlans = () => import('@/views/procurement/ProcPlans.vue')
const ProcVendors = () => import('@/views/procurement/ProcVendors.vue')
const ProcBids = () => import('@/views/procurement/ProcBids.vue')
const ProcOrders = () => import('@/views/procurement/ProcOrders.vue')
const ProcReceiving = () => import('@/views/procurement/ProcReceiving.vue')
const ProcContracts = () => import('@/views/procurement/ProcContracts.vue')

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', component: Login, meta: { public: true } },
    {
      path: '/admin',
      component: AdminLayout,
      children: [
        { path: '', redirect: '/admin/dashboard' },
        { path: 'dashboard', component: Dashboard },
        { path: 'users', component: Users },
        { path: 'orgs', component: Orgs },
        { path: 'settings', component: Settings },
        { path: 'logs', component: Logs },
        // removed notices
      ],
    },
    {
      path: '/equipment',
      component: EquipLayout,
      children: [
        { path: '', redirect: '/equipment/dashboard' },
        { path: 'dashboard', component: EquipDashboard },
        { path: 'ledger', component: EquipLedger },
        { path: 'onboarding', component: EquipOnboarding },
        { path: 'status-transfer', component: EquipStatusTransfer },
        { path: 'demands', component: EquipDemands },
        { path: 'maintenance', component: EquipMaintenance },
        { path: 'repairs', component: EquipRepairs },
        { path: 'scrap', component: EquipScrap },
      ],
    },
    {
      path: '/department',
      component: DeptLayout,
      children: [
        { path: '', redirect: '/department/dashboard' },
        { path: 'dashboard', component: DeptDashboard },
        { path: 'my-devices', component: DeptMyDevices },
        { path: 'usage', component: DeptUsage },
        { path: 'repair', component: DeptRepair },
        { path: 'requests', component: DeptRequests },
      ],
    },
    {
      path: '/procurement',
      component: ProcLayout,
      children: [
        { path: '', redirect: '/procurement/dashboard' },
        { path: 'dashboard', component: ProcDashboard },
        { path: 'plans', component: ProcPlans },
        { path: 'vendors', component: ProcVendors },
        { path: 'bids', component: ProcBids },
        { path: 'orders', component: ProcOrders },
        { path: 'receiving', component: ProcReceiving },
        { path: 'contracts', component: ProcContracts },
      ],
    },
    { path: '/:pathMatch(.*)*', redirect: '/login' },
  ],
})

router.beforeEach((to, _from, next) => {
  const isLoggedIn = Boolean(localStorage.getItem('demo_logged_in'))
  if (to.meta.public) return next()
  if (to.path.startsWith('/admin')) {
    if (isLoggedIn) return next()
    return next('/login')
  }
  next()
})

export default router


