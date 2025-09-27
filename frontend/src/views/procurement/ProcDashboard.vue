<template>
    <div>
        <div class="grid">
            <StatCard label="Open Requisitions" :value="openReqs"/>
            <StatCard label="Pending Receiving" :value="pendingReceiving"/>
            <StatCard label="Budget Spent (USD)" :value="spent"/>
            <StatCard label="Vendors" :value="vendorCount"/>
        </div>
    </div>
</template>

<script setup>
import StatCard from '@/components/layout/StatCard.vue'
import {ref, computed, onMounted} from 'vue'

// 后端：/req/proc/orders  /req/proc/quotes  /req/proc/vendors
const orders = ref([])
const quotes = ref([])
const vendors = ref([])

async function loadOrders() {
    const r = await fetch('/req/proc/orders')
    const j = await r.json()
    orders.value = j.code === '000'
        ? (j.data || []).map(x => ({
            supplierId: String(x.supplier_id || x.supplierId),
            typeId: String(x.equipment_type_id || x.equipmentTypeId),
            count: Number(x.count || 0),
            status: String(x.status || 'under-review'),
        }))
        : []
}

async function loadQuotes() {
    const r = await fetch('/req/proc/quotes')
    const j = await r.json()
    quotes.value = j.code === '000'
        ? (j.data || []).map(x => ({
            supplierId: String(x.supplier_id || x.supplierId),
            typeId: String(x.equipment_type_id || x.equipmentTypeId),
            price: Number(x.price || 0),
        }))
        : []
}

async function loadVendors() {
    const r = await fetch('/req/proc/vendors')
    const j = await r.json()
    vendors.value = j.code === '000'
        ? (j.data || []).map(x => ({id: String(x.supplier_id || x.id)}))
        : []
}

function unitPriceOf(supplierId, typeId) {
    const q = quotes.value.find(q => q.supplierId === supplierId && q.typeId === typeId)
    return q ? Number(q.price || 0) : 0
}

// 指标：与后端状态一致（under-review / procuring / arrived / terminated）
const openReqs = computed(() => orders.value.filter(o => o.status === 'under-review').length)
const pendingReceiving = computed(() => orders.value.filter(o => o.status === 'arrived').length)
const spent = computed(() =>
    orders.value
        .filter(o => o.status !== 'terminated')
        .reduce((sum, o) => sum + unitPriceOf(o.supplierId, o.typeId) * (o.count || 0), 0)
)
const vendorCount = computed(() => vendors.value.length)

onMounted(async () => {
    await Promise.all([loadOrders(), loadQuotes(), loadVendors()])
})
</script>

<style scoped>
.grid {
    display: grid;
    gap: 16px;
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
}
</style>
