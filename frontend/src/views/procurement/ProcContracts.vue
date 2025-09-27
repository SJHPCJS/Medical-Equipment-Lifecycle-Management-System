<template>
    <div class="card" style="padding:16px;">
        <div class="title-lg">Contracts & Budget</div>
        <div class="subtitle" style="margin-top:8px;">Contracts are derived from procure orders; budget usage is
            calculated from quotes × quantity.
        </div>

        <!-- Budget panel -->
        <div class="ui-toolbar" style="margin-top:16px; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));">
            <div>
                <label>Annual Budget</label>
                <div style="display:flex; gap:8px;">
                    <input class="input" type="number" min="0" step="1" v-model.number="budget.total"/>
                    <button class="btn btn-primary" @click="saveBudget">Save</button>
                    <button class="btn" @click="resetBudget">Reset</button>
                </div>
            </div>
            <div class="card" style="padding:12px;">
                <div style="display:flex; justify-content:space-between; font-weight:700;">
                    <span>Committed</span><span>{{ fmtMoney(committed) }}</span>
                </div>
                <div style="display:flex; justify-content:space-between; margin-top:4px;">
                    <span style="color:var(--color-muted);">Remaining</span><span
                    style="color:var(--color-muted);">{{ fmtMoney(Math.max(0, budget.total - committed)) }}</span>
                </div>
                <div style="height:8px; background:#eee; border-radius:6px; margin-top:8px; overflow:hidden;">
                    <div :style="{width: progress+'%', height:'100%'}"></div>
                </div>
            </div>
            <div/>
        </div>

        <!-- Filters -->
        <div class="ui-toolbar" style="margin-top:12px;">
            <input class="input" v-model="filters.keyword" placeholder="Search supplier / type / contract id"/>
            <div>
                <label>Status</label>
                <select class="input" v-model="filters.status">
                    <option value="">All</option>
                    <option v-for="s in STATUSES" :key="s" :value="s">{{ s }}</option>
                </select>
            </div>
            <div>
                <label>Supplier</label>
                <select class="input" v-model="filters.supplierId">
                    <option value="">All</option>
                    <option v-for="s in suppliers" :key="s.id" :value="s.id">{{ s.name }}</option>
                </select>
            </div>
            <button class="btn" @click="resetFilters">Reset</button>
        </div>

        <!-- Table -->
        <div class="table-wrapper" style="margin-top:16px; overflow:auto;">
            <table class="ui-table" style="width:100%; table-layout:auto;">
                <thead>
                <tr>
                    <th style="min-width:120px;">Contract (Procure ID)</th>
                    <th>Supplier</th>
                    <th>Equipment Type</th>
                    <th>Qty</th>
                    <th>Unit Price</th>
                    <th>Amount</th>
                    <th>Status</th>
                    <th style="width:240px;">Actions</th>
                </tr>
                </thead>
                <tbody>
                <tr v-if="loading">
                    <td colspan="8">
                        <TableSkeleton :rows="6"/>
                    </td>
                </tr>
                <tr v-else-if="paged.length===0">
                    <td colspan="8">
                        <EmptyState title="No contracts" hint="Create orders from Bids page to see contracts here."/>
                    </td>
                </tr>
                <tr v-else v-for="c in paged" :key="c.procureId">
                    <td>#{{ c.procureId }}</td>
                    <td>{{ supplierName(c.supplierId) }}</td>
                    <td>{{ typeName(c.equipmentTypeId) }}</td>
                    <td>{{ c.count }}</td>
                    <td>{{ fmtMoney(c.unitPrice) }}</td>
                    <td>{{ fmtMoney(c.amount) }}</td>
                    <td>{{ c.status }}</td>
                    <td style="white-space:nowrap;">
                        <button class="btn" :disabled="c.status==='terminated'" @click="flow(c,'under-review')">Under
                            Review
                        </button>
                        <button class="btn btn-blue" :disabled="c.status==='procuring'" style="margin-left:6px;"
                                @click="flow(c,'procuring')">Start
                        </button>
                        <button class="btn btn-green" :disabled="c.status==='arrived'" style="margin-left:6px;"
                                @click="flow(c,'arrived')">Arrived
                        </button>
                        <button class="btn btn-red" :disabled="c.status==='terminated'" style="margin-left:6px;"
                                @click="flow(c,'terminated')">Terminate
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
import {reactive, ref, computed, onMounted, watch} from 'vue'
import EmptyState from '@/components/admin/EmptyState.vue'
import TableSkeleton from '@/components/admin/TableSkeleton.vue'

/** 与后端保持一致的订单状态枚举 */
const STATUSES = ['under-review', 'procuring', 'arrived', 'terminated']

const loading = ref(true)
const suppliers = ref([])          // [{id,name}]
const types = ref([])              // [{id,name}]
const quotes = ref([])             // [{supplierId,typeId,price}]
const state = reactive({orders: []}) // 列表数据

const filters = reactive({keyword: '', status: '', supplierId: ''})
const page = ref(1)
const pageSize = ref(10)

/** 预算（本地保存） */
const budget = reactive({total: 0})

function loadBudget() {
    budget.total = Number(localStorage.getItem('melms_budget_total') || 0)
}

function saveBudget() {
    localStorage.setItem('melms_budget_total', String(budget.total || 0));
    toast('Budget saved (local)')
}

function resetBudget() {
    budget.total = 0;
    saveBudget()
}

function toast(m) {
    alert(m)
}

function showDialog(m) {
    alert(m)
}

function supplierName(id) {
    const s = suppliers.value.find(x => String(x.id) === String(id));
    return s ? s.name : id
}

function typeName(id) {
    const t = types.value.find(x => String(x.id) === String(id));
    return t ? t.name : id
}

function fmtMoney(n) {
    try {
        return Number(n || 0).toLocaleString(undefined, {style: 'currency', currency: 'USD'})
    } catch {
        return String(n)
    }
}

/** 单价查找 */
function unitPriceOf(supplierId, typeId) {
    const q = quotes.value.find(q => String(q.supplierId) === String(supplierId) && String(q.typeId) === String(typeId))
    return q ? Number(q.price || 0) : 0
}

/** 过滤、分页、预算 */
const enriched = computed(() => {
    return state.orders.map(o => {
        const price = unitPriceOf(o.supplierId, o.equipmentTypeId)
        return {...o, unitPrice: price, amount: price * Number(o.count || 0)}
    })
})

const filtered = computed(() => {
    const kw = (filters.keyword || '').toLowerCase()
    return enriched.value.filter(c => {
        const hitKw = !kw || [`#${c.procureId}`, supplierName(c.supplierId), typeName(c.equipmentTypeId)].some(s => (s || '').toLowerCase().includes(kw))
        const hitStatus = !filters.status || c.status === filters.status
        const hitSup = !filters.supplierId || String(c.supplierId) === String(filters.supplierId)
        return hitKw && hitStatus && hitSup
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

const committed = computed(() => enriched.value
    .filter(c => c.status !== 'terminated')
    .reduce((sum, c) => sum + (c.amount || 0), 0)
)
const progress = computed(() => {
    if (!budget.total) return 0
    return Math.min(100, Math.round(committed.value / budget.total * 100))
})

function resetFilters() {
    filters.keyword = '';
    filters.status = '';
    filters.supplierId = ''
}

/** 加载基础数据 */
async function loadSuppliers() {
    const r = await fetch('/req/proc/vendors');
    const j = await r.json();
    if (j.code === '000') suppliers.value = (j.data || []).map(x => ({
        id: String(x.supplier_id || x.id),
        name: x.supplier_name || x.name
    }))
}

async function loadTypes() {
    const r = await fetch('/req/proc/equipmentTypes');
    const j = await r.json();
    if (j.code === '000') types.value = (j.data || []).map(x => ({
        id: String(x.equipment_type_id || x.id),
        name: x.equipment_type_name || x.name
    }))
}

async function loadQuotes() {
    const r = await fetch('/req/proc/quotes');
    const j = await r.json();
    if (j.code === '000') quotes.value = (j.data || []).map(x => ({
        supplierId: String(x.supplier_id || x.supplierId),
        typeId: String(x.equipment_type_id || x.equipmentTypeId),
        price: Number(x.price || 0)
    }))
}

/** 加载订单（合同） */
async function refresh() {
    loading.value = true
    try {
        const r = await fetch('/req/proc/orders');
        const j = await r.json()
        if (j.code === '000') {
            state.orders = (j.data || []).map(x => ({
                procureId: Number(x.procure_id || x.procureId),
                equipmentTypeId: String(x.equipment_type_id || x.equipmentTypeId),
                count: Number(x.count || 0),
                supplierId: String(x.supplier_id || x.supplierId),
                status: String(x.status || 'under-review')
            }))
        } else {
            state.orders = []
        }
    } finally {
        loading.value = false
    }
}

/** 状态流转 */
async function flow(contract, toStatus) {
    if (contract.status === toStatus) return
    const body = {procure_id: contract.procureId, status: toStatus}
    const r = await fetch('/req/proc/order/status', {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(body)
    })
    const j = await r.json().catch(() => ({code: 'ERR'}))
    if (j.code !== '000') return showDialog(j.message || 'Failed to update status')
    contract.status = toStatus
}

onMounted(async () => {
    loadBudget()
    await Promise.all([loadSuppliers(), loadTypes(), loadQuotes()])
    await refresh()
})
</script>

<style scoped>
/* 进度条颜色（使用默认主题色系） */
div[style*="width:"][style*="%"][style*="height: 100%"] {
    background: #3b82f6;
}
</style>
