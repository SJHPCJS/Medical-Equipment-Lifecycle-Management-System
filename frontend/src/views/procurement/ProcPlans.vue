<template>
    <div class="card" style="padding:16px;">
        <div class="title-lg">Purchase Requests</div>
        <div class="subtitle" style="margin-top:8px;">
            Review requests (orders in <code>under-review</code>) and assign vendors to create purchase orders.
        </div>

        <!-- Filters -->
        <div class="ui-toolbar" style="margin-top:12px;">
            <input class="input" v-model="keyword" placeholder="Search by ID / type / supplier"/>
            <button class="btn" @click="keyword=''">Reset</button>
        </div>

        <div class="table-wrapper" style="margin-top:16px; overflow:auto;">
            <table class="ui-table" style="table-layout:auto; width:100%;">
                <thead>
                <tr>
                    <th style="min-width:100px;">Req ID</th>
                    <th>Equipment Type</th>
                    <th>Qty</th>
                    <th>Supplier</th>
                    <th>Unit Price</th>
                    <th>Amount</th>
                    <th>Status</th>
                    <th style="width:220px;">Actions</th>
                </tr>
                </thead>
                <tbody>
                <tr v-if="loading">
                    <td colspan="8">
                        <TableSkeleton :rows="6"/>
                    </td>
                </tr>
                <tr v-else-if="filtered.length===0">
                    <td colspan="8" style="text-align:center; color:var(--color-muted);">
                        No requests
                    </td>
                </tr>
                <tr v-else v-for="p in filtered" :key="p.procureId">
                    <td>#{{ p.procureId }}</td>
                    <td>{{ typeName(p.equipmentTypeId) }}</td>
                    <td>{{ p.count }}</td>
                    <td>{{ supplierName(p.supplierId) }}</td>
                    <td>{{ money(unitPriceOf(p.supplierId, p.equipmentTypeId)) }}</td>
                    <td>{{ money(unitPriceOf(p.supplierId, p.equipmentTypeId) * p.count) }}</td>
                    <td>{{ p.status }}</td>
                    <td style="white-space:nowrap;">
                        <button class="btn btn-red" @click="reject(p)">Reject</button>
                        <button class="btn btn-green" style="margin-left:8px;" @click="openAssign(p)">Assign Vendor
                        </button>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

        <!-- Assign vendor / create PO -->
        <div v-if="modal.open" class="modal-backdrop">
            <div class="modal card">
                <div class="title-lg">Assign Vendor / Create PO</div>
                <div class="form-grid">
                    <div>
                        <label>Request ID</label>
                        <input class="input" :value="'#'+modal.req?.procureId" disabled/>
                    </div>
                    <div>
                        <label>Equipment Type</label>
                        <input class="input" :value="typeName(modal.req?.equipmentTypeId)" disabled/>
                    </div>
                    <div>
                        <label>Supplier</label>
                        <select class="input" v-model="modal.form.supplierId">
                            <option v-for="v in suppliers" :key="v.id" :value="v.id">{{ v.name }}</option>
                        </select>
                    </div>
                    <div>
                        <label>Quantity</label>
                        <input class="input" type="number" min="1" v-model.number="modal.form.count"/>
                    </div>
                    <div>
                        <label>Unit Price</label>
                        <input class="input"
                               :value="money(unitPriceOf(modal.form.supplierId, modal.req?.equipmentTypeId))" disabled/>
                    </div>
                    <div>
                        <label>Amount</label>
                        <input class="input"
                               :value="money(unitPriceOf(modal.form.supplierId, modal.req?.equipmentTypeId) * (modal.form.count||0))"
                               disabled/>
                    </div>
                </div>
                <div style="display:flex; gap:8px; justify-content:flex-end; margin-top:16px;">
                    <button class="btn" @click="closeAssign">Cancel</button>
                    <button class="btn btn-primary" @click="createPO">Create</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import {ref, reactive, computed, onMounted} from 'vue'
import TableSkeleton from '@/components/admin/TableSkeleton.vue'

/**
 * 约定后端接口：
 *  - GET  /req/proc/orders            -> 全量订单；本页取 status='under-review' 视为“请购单”
 *  - PUT  /req/proc/order/status      -> {procure_id, status} 变更状态（用于 Reject）
 *  - PUT  /req/proc/order/assign      -> {procure_id, supplier_id, count} 指派供应商并更新数量（需要在后端新增）
 *  - GET  /req/proc/vendors           -> 供应商
 *  - GET  /req/proc/equipmentTypes    -> 设备类型（用于名称展示）
 *  - GET  /req/proc/quotes            -> 单价（supplier_id + equipment_type_id -> price）
 */

const loading = ref(true)
const keyword = ref('')

const suppliers = ref([])  // [{id,name}]
const types = ref([])      // [{id,name}]
const quotes = ref([])     // [{supplierId,typeId,price}]
const requests = ref([])   // 仅拉取 status='under-review' 的订单作为请购单

function money(n) {
    try {
        return Number(n || 0).toLocaleString(undefined, {style: 'currency', currency: 'USD'})
    } catch {
        return String(n)
    }
}

function supplierName(id) {
    const v = suppliers.value.find(v => String(v.id) === String(id));
    return v ? v.name : (id === '0000' ? '-' : id)
}

function typeName(id) {
    const t = types.value.find(t => String(t.id) === String(id));
    return t ? t.name : id
}

function unitPriceOf(supplierId, typeId) {
    const q = quotes.value.find(q => q.supplierId === String(supplierId) && q.typeId === String(typeId))
    return q ? Number(q.price || 0) : 0
}

const filtered = computed(() => {
    const kw = (keyword.value || '').toLowerCase()
    return requests.value.filter(p => {
        if (!kw) return true
        return [`#${p.procureId}`, supplierName(p.supplierId), typeName(p.equipmentTypeId)]
            .some(s => (s || '').toLowerCase().includes(kw))
    })
})

/* -------- APIs -------- */
async function loadVendors() {
    const r = await fetch('/req/proc/vendors');
    const j = await r.json();
    suppliers.value = j.code === '000' ? (j.data || []).map(x => ({
        id: String(x.supplier_id || x.id),
        name: x.supplier_name || x.name || '-'
    })) : []
}

async function loadTypes() {
    const r = await fetch('/req/proc/equipmentTypes');
    const j = await r.json();
    types.value = j.code === '000' ? (j.data || []).map(x => ({
        id: String(x.equipment_type_id || x.id),
        name: x.equipment_type_name || x.name || '-'
    })) : []
}

async function loadQuotes() {
    const r = await fetch('/req/proc/quotes');
    const j = await r.json();
    quotes.value = j.code === '000' ? (j.data || []).map(x => ({
        supplierId: String(x.supplier_id || x.supplierId),
        typeId: String(x.equipment_type_id || x.equipmentTypeId),
        price: Number(x.price || 0)
    })) : []
}

async function loadRequests() {
    const r = await fetch('/req/proc/orders');
    const j = await r.json()
    const all = j.code === '000' ? (j.data || []).map(x => ({
        procureId: Number(x.procure_id || x.procureId),
        equipmentTypeId: String(x.equipment_type_id || x.equipmentTypeId),
        count: Number(x.count || 0),
        supplierId: String(x.supplier_id || x.supplierId || '0000'),
        status: String(x.status || 'under-review'),
    })) : []
    requests.value = all.filter(x => x.status === 'under-review')
}

/* Reject -> status = terminated */
async function reject(p) {
    const r = await fetch('/req/proc/order/status', {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({procure_id: p.procureId, status: 'terminated'})
    })
    const j = await r.json().catch(() => ({code: 'ERR'}))
    if (j.code !== '000') return alert(j.message || 'Failed to reject')
    requests.value = requests.value.filter(x => x.procureId !== p.procureId)
}

/* Assign vendor modal */
const modal = reactive({open: false, req: null, form: {supplierId: '', count: 1}})

function openAssign(p) {
    modal.open = true
    modal.req = p
    modal.form.supplierId = suppliers.value[0]?.id || '0000'
    modal.form.count = p.count || 1
}

function closeAssign() {
    modal.open = false
}

async function createPO() {
    // 指派供应商并更新数量（需要后端提供 /req/proc/order/assign）
    const body = {procure_id: modal.req.procureId, supplier_id: modal.form.supplierId, count: modal.form.count}
    const r = await fetch('/req/proc/order/assign', {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(body)
    })
    const j = await r.json().catch(() => ({code: 'ERR'}))
    if (j.code !== '000') return alert(j.message || 'Failed to assign vendor')

    // 成功后把状态推进到 procuring
    const r2 = await fetch('/req/proc/order/status', {
        method: 'PUT', headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({procure_id: modal.req.procureId, status: 'procuring'})
    })
    const j2 = await r2.json().catch(() => ({code: 'ERR'}))
    if (j2.code !== '000') return alert(j2.message || 'Failed to move status')

    // 从“请求列表”移除
    requests.value = requests.value.filter(x => x.procureId !== modal.req.procureId)
    closeAssign()
}

/* init */
onMounted(async () => {
    loading.value = true
    try {
        await Promise.all([loadVendors(), loadTypes(), loadQuotes(), loadRequests()])
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

.modal-backdrop {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.35);
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 16px;
}

.modal {
    width: min(720px, 100%);
    padding: 16px;
}

.form-grid {
    margin-top: 16px;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
    gap: 12px;
}
</style>
