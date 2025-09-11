import { createRouter, createWebHistory } from 'vue-router'
// LocalStorage-based auth (no Pinia)

const Login = () => import('@/views/Login.vue')
const AdminLayout = () => import('@/views/admin/AdminLayout.vue')
const Dashboard = () => import('@/views/admin/Dashboard.vue')
const Users = () => import('@/views/admin/Users.vue')
const Roles = () => import('@/views/admin/Roles.vue')
const Orgs = () => import('@/views/admin/Orgs.vue')
const Settings = () => import('@/views/admin/Settings.vue')
const Logs = () => import('@/views/admin/Logs.vue')
const Notices = () => import('@/views/admin/Notices.vue')

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
        { path: 'roles', component: Roles },
        { path: 'orgs', component: Orgs },
        { path: 'settings', component: Settings },
        { path: 'logs', component: Logs },
        { path: 'notices', component: Notices },
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


