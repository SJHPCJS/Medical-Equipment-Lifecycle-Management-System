<template>
    <div class="admin-page">
        <Sidebar :items="menuItems"/>
        <main class="content">
            <Topbar/>
            <div class="content-body">
                <RouterView/>
            </div>
        </main>
    </div>
</template>

<script setup>
import Sidebar from '@/components/layout/Sidebar.vue'
import Topbar from '@/components/layout/Topbar.vue'

/* 覆盖采购模块的完整菜单 */
const menuItems = [
    {label: 'Dashboard', to: '/procurement/dashboard'},
    {label: 'Bids / RFQ', to: '/procurement/bids'},
    {label: 'Vendors', to: '/procurement/vendors'},
    {label: 'Purchase Requests', to: '/procurement/plans'},
    {label: 'Purchase Orders', to: '/procurement/orders'},
    {label: 'Contracts & Budget', to: '/procurement/contracts'},
    {label: 'Receiving', to: '/procurement/receiving'},
]
</script>

<style scoped>
.admin-page {
    display: grid;
    grid-template-columns: 240px 1fr;
    height: 100vh;
}

.content {
    display: flex;
    flex-direction: column;
    gap: 16px;
    padding: 16px;
}

.content-body {
    padding: 16px;
}
</style>
