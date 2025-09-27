<template>
    <div class="card" style="padding:16px;">
        <div class="title-lg">Purchase Orders</div>
        <div class="subtitle" style="margin-top:8px;">Update order status and push to equipment team for onboarding.
        </div>

        <!-- Filters -->
        <div class="ui-toolbar" style="margin-top:12px;">
            <input class="input" v-model="filters.keyword" placeholder="Search by PO / vendor / type"/>
            <div>
                <label>Status</label>
                <select class="input" v-model="filters.status">
                    <option value="">All</option>
                    <option v-for="s in STATUSES" :key="s" :value="s">{{ s }}</option>
                </select>
            </div>
            <button class="btn" @click="resetFilters">Reset</button>
        </div>

        <div class="table-wrapper" style="margin-top:16px; overflow:auto;">
            <table class="ui-table" style="table-layout:auto; width:100%;">
                <thead>
                <tr>
                    <th>PO ID</th>
                    <th>Equipment Type</th>
                    <th>Qty</th>
                    <th>Vendor</th>
                    <th>Unit Price</th>
                    <th>Total Price</th>
                    <th>Status</th>
                    <th style="width:260px;">Actions</th>
                </tr>
                </thead>
                <tbody>
                <tr v-if="loading">
                    <td colspan="8">
                        <TableSkeleton :rows="6"/>
                    </td>
                </tr>
                <tr v-else-if="paged.length===0">
                    <td colspan="8" style="text-align:center; color:var(--color-muted);">No orders</td>
                </tr>
                <tr v-else v-for="o in paged" :key="o.procureId">
                    <td>#{{ o.procureId }}</td>
                    <td>{{ typeName(o.equipmentTypeId) }}</td>
                    <td>{{ o.count }}</td>
                    <td>{{ vendorName(o.supplierId) }}</td>
                    <td>{{ money(unitPriceOf(o.supplierId, o.equipmentTypeId)) }}</td>
                    <td>{{ money(unitPriceOf(o.supplierId, o.equipmentTypeId) * o.count) }}</td>
                    <td>{{ o.status }}</td>
                    <td style="white-space:nowrap;">
                        <button class="btn" :disabled="o.status==='under-review'" @click="flow(o,'under-review')">Under
                            Review
                        </button>
                        <button class="btn btn-blue" style="margin-left:6px;" :disabled="o.status==='procuring'"
                                @click="flow(o,'procuring')">Start
                        </button>
                        <button class="btn btn-green" style="margin-left:6px;" :disabled="o.status==='arrived'"
                                @click="flow(o,'arrived')">Confirm Arrival
                        </button>
                        <button class="btn btn-red" style="margin-left:6px;" :disabled="o.status==='terminated'"
                                @click="flow(o,'terminated')">Terminate
                        </button>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

        <!-- Pagination -->
        <div class="ui-pagination" style="margin-top:12px;">
            <button class="btn" :disabled="page===1" @click="page=1">First</button>
            <button class="btn" :disabled="page===1" @click="page--">Prev</button>
            <span style="color:var(--color-muted);">Page {{ page }} / {{ totalPages }}</span>
            <button class="btn" :disabled="page===totalPages" @click="page++">Next</button>
            <button class="btn" :disabled="page===totalPages" @click="page=totalPages">Last</button>
            <select class="input" style="width:auto;" v-model.number="pageSize">
                <option :value="5">5</option>
                <option :value="10">10</option>
                <option :value="20">20</option>
            </select>
        </div>
    </div>
</template>

<script setup>
import {ref, reactive, computed, onMounted, watch} from 'vue'
import TableSkeleton from '@/components/admin/TableSkeleton.vue'

/** 与后端一致的状态值 */
const STATUSES = ['under-review', 'procuring', 'arrived', 'terminated']

const loading = ref(true)
const orders = ref([])     // [{procureId, equipmentTypeId, count, supplierId, status}]
const vendors = ref([])    // [{id,name}]
const types = ref([])      // [{id,name}]
const quotes = ref([])     // [{supplierId,typeId,price}]

const filters = reactive({keyword: '', status: ''})
const page = ref(1)
const pageSize = ref(10)

/* ------- helpers ------- */
function money(n) {
    try {
        return Number(n || 0).toLocaleString(undefined, {style: 'currency', currency: 'USD'})
    } catch {
        return String(n)
    }
}

function vendorName(id) {
    const v = vendors.value.find(v => String(v.id) === String(id));
    return v ? v.name : id
}

function typeName(id) {
    const t = types.value.find(t => String(t.id) === String(id));
    return t ? t.name : id
}

function unitPriceOf(supplierId, typeId) {
    const q = quotes.value.find(q => q.supplierId === String(supplierId) && q.typeId === String(typeId))
    return q ? Number(q.price || 0) : 0
}

function resetFilters() {
    filters.keyword = '';
    filters.status = ''
}

/* ------- computed & pagination ------- */
const filtered = computed(() => {
    const kw = (filters.keyword || '').toLowerCase()
    return orders.value.filter(o => {
        const hitKw = !kw || [`#${o.procureId}`, vendorName(o.supplierId), typeName(o.equipmentTypeId)]
            .some(s => (s || '').toLowerCase().includes(kw))
        const hitStatus = !filters.status || o.status === filters.status
        return hitKw && hitStatus
    })
})
const totalPages = computed(() => Math.max(1, Math.ceil(filtered.value.length / pageSize.value)))
watch([filtered, pageSize], () => {
    if (page.value > totalPages.value) page.value = totalPages.value
})
const paged = computed(() => {
    const start = (page.value - 1) * pageSize.value
    return filtered.value.slice(start, start + pageSize.value)
})

/* ------- API ------- */
async function loadOrders() {
    const r = await fetch('/req/proc/orders');
    const j = await r.json()
    orders.value = j.code === '000'
        ? (j.data || []).map(x => ({
            procureId: Number(x.procure_id || x.procureId),
            equipmentTypeId: String(x.equipment_type_id || x.equipmentTypeId),
            count: Number(x.count || 0),
            supplierId: String(x.supplier_id || x.supplierId),
            status: String(x.status || 'under-review'),
        }))
        : []
}

async function loadVendors() {
    const r = await fetch('/req/proc/vendors');
    const j = await r.json()
    vendors.value = j.code === '000'
        ? (j.data || []).map(x => ({id: String(x.supplier_id || x.id), name: x.supplier_name || x.name || '-'}))
        : []
}

async function loadTypes() {
    const r = await fetch('/req/proc/equipmentTypes');
    const j = await r.json()
    types.value = j.code === '000'
        ? (j.data || []).map(x => ({
            id: String(x.equipment_type_id || x.id),
            name: x.equipment_type_name || x.name || '-'
        }))
        : []
}

async function loadQuotes() {
    const r = await fetch('/req/proc/quotes');
    const j = await r.json()
    quotes.value = j.code === '000'
        ? (j.data || []).map(x => ({
            supplierId: String(x.supplier_id || x.supplierId),
            typeId: String(x.equipment_type_id || x.equipmentTypeId),
            price: Number(x.price || 0)
        }))
        : []
}

async function flow(o, toStatus) {
    if (o.status === toStatus) return
    const r = await fetch('/req/proc/order/status', {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({procure_id: o.procureId, status: toStatus})
    })
    const j = await r.json().catch(() => ({code: 'ERR'}))
    if (j.code === '000') o.status = toStatus
    else alert(j.message || 'Failed to update status')
}

/* ------- init ------- */
onMounted(async () => {
    loading.value = true
    try {
        await Promise.all([loadVendors(), loadTypes(), loadQuotes(), loadOrders()])
    } finally {
        loading.value = false
    }
})
</script>

<style scoped>
.ui-table {
    width: 100%;
    border-collapse: collapse;
}

.ui-table th, .ui-table td {
    padding: 10px 12px;
    border-bottom: 1px solid #e5e7eb;
    text-align: left;
    white-space: nowrap;
}

.ui-table th {
    background: #f9fafb;
    font-weight: 700;
}
</style>
