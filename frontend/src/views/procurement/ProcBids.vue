<template>
    <div class="card" style="padding:16px;">
        <div class="title-lg">Bids / RFQ</div>
        <div class="subtitle" style="margin-top:8px;">Compare supplier quotes by equipment type.</div>

        <!-- Filters -->
        <div class="ui-toolbar" style="margin-top:16px;">
            <div>
                <label>Equipment Type</label>
                <select class="input" v-model="filters.equipmentTypeId" @change="refresh">
                    <option value="">-- Select --</option>
                    <option v-for="t in types" :key="t.id" :value="t.id">{{ t.name }}</option>
                </select>
            </div>
            <input class="input" v-model="filters.keyword" placeholder="Search supplier"/>
            <div style="display:flex; gap:8px;">
                <button class="btn" @click="resetFilters">Reset</button>
                <button class="btn btn-primary" :disabled="!filters.equipmentTypeId" @click="openCreate">Add Quote
                </button>
            </div>
        </div>

        <!-- Quotes table -->
        <div class="table-wrapper" style="margin-top:16px; overflow:auto;">
            <table class="ui-table" style="width:100%; table-layout:auto;">
                <thead>
                <tr>
                    <th>Supplier</th>
                    <th>Equipment Type</th>
                    <th>Price</th>
                    <th style="width:240px;">Actions</th>
                </tr>
                </thead>
                <tbody>
                <tr v-if="loading">
                    <td colspan="4">
                        <TableSkeleton :rows="5"/>
                    </td>
                </tr>
                <tr v-else-if="paged.length===0">
                    <td colspan="4">
                        <EmptyState title="No quotes" hint="Try another type or add a quote."/>
                    </td>
                </tr>
                <tr v-else v-for="q in paged" :key="q.supplierId + '-' + q.equipmentTypeId">
                    <td>{{ supplierName(q.supplierId) }}</td>
                    <td>{{ typeName(q.equipmentTypeId) }}</td>
                    <td>{{ formatPrice(q.price) }}</td>
                    <td style="white-space:nowrap;">
                        <button class="btn btn-blue" @click="openEdit(q)">Edit</button>
                        <button class="btn btn-red" style="margin-left:8px;" @click="remove(q)">Delete</button>
                        <button class="btn btn-primary" style="margin-left:8px;" @click="openOrder(q)">Create Order
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

        <!-- Add/Edit Quote modal -->
        <div v-if="modal.open" class="ui-modal-backdrop">
            <div class="ui-modal card">
                <div class="title-lg">{{ modal.mode === 'create' ? 'Add Quote' : 'Edit Quote' }}</div>
                <div class="ui-form-grid">
                    <div>
                        <label>Supplier</label>
                        <select class="input" v-model="modal.form.supplierId">
                            <option v-for="s in suppliers" :key="s.id" :value="s.id">{{ s.name }}</option>
                        </select>
                    </div>
                    <div>
                        <label>Equipment Type</label>
                        <select class="input" v-model="modal.form.equipmentTypeId">
                            <option v-for="t in types" :key="t.id" :value="t.id">{{ t.name }}</option>
                        </select>
                    </div>
                    <div>
                        <label>Price</label>
                        <input class="input" type="number" min="0" step="1" v-model.number="modal.form.price"/>
                    </div>
                </div>
                <div style="display:flex; gap:8px; justify-content:flex-end; margin-top:16px;">
                    <button class="btn" @click="closeModal">Cancel</button>
                    <button class="btn btn-primary" @click="save">Save</button>
                </div>
            </div>
        </div>

        <!-- Create Order modal -->
        <div v-if="orderModal.open" class="ui-modal-backdrop">
            <div class="ui-modal card">
                <div class="title-lg">Create Procure Order</div>
                <div class="ui-form-grid">
                    <div>
                        <label>Supplier</label>
                        <input class="input" :value="supplierName(orderModal.form.supplierId)" disabled/>
                    </div>
                    <div>
                        <label>Equipment Type</label>
                        <input class="input" :value="typeName(orderModal.form.equipmentTypeId)" disabled/>
                    </div>
                    <div>
                        <label>Unit Price</label>
                        <input class="input" :value="formatPrice(orderModal.form.price)" disabled/>
                    </div>
                    <div>
                        <label>Count</label>
                        <input class="input" type="number" min="1" step="1" v-model.number="orderModal.form.count"/>
                    </div>
                </div>
                <div style="display:flex; gap:8px; justify-content:flex-end; margin-top:16px;">
                    <button class="btn" @click="closeOrder">Cancel</button>
                    <button class="btn btn-primary" @click="createOrder">Create</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import {reactive, ref, computed, onMounted, watch} from 'vue'
import EmptyState from '@/components/admin/EmptyState.vue'
import TableSkeleton from '@/components/admin/TableSkeleton.vue'

/* ---- state ---- */
const loading = ref(true)
const suppliers = ref([])
const types = ref([])
const state = reactive({quotes: []})

const filters = reactive({equipmentTypeId: '', keyword: ''})
const page = ref(1)
const pageSize = ref(10)

/* ---- computed / pagination ---- */
const filtered = computed(() => {
    const kw = (filters.keyword || '').toLowerCase()
    return state.quotes.filter(q => {
        const hitType = !filters.equipmentTypeId || q.equipmentTypeId === filters.equipmentTypeId
        const hitKw = !kw || supplierName(q.supplierId).toLowerCase().includes(kw)
        return hitType && hitKw
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

/* ---- helpers ---- */
function resetFilters() {
    filters.equipmentTypeId = '';
    filters.keyword = ''
}

function formatPrice(n) {
    try {
        return Number(n || 0).toLocaleString(undefined, {style: 'currency', currency: 'USD'})
    } catch {
        return String(n)
    }
}

function supplierName(id) {
    const s = suppliers.value.find(s => s.id === id);
    return s ? s.name : id
}

function typeName(id) {
    const t = types.value.find(t => t.id === id);
    return t ? t.name : id
}

/* ---- API ---- */
async function loadSuppliers() {
    const r = await fetch('/req/proc/vendors');
    const j = await r.json()
    if (j.code === '000') suppliers.value = (j.data || []).map(x => ({
        id: String(x.supplier_id || x.id),
        name: x.supplier_name || x.name,
        contact: x.contact || ''
    }))
}

async function loadTypes() {
    const r = await fetch('/req/proc/equipmentTypes');
    const j = await r.json()
    if (j.code === '000') types.value = (j.data || []).map(x => ({
        id: String(x.equipment_type_id || x.id),
        name: x.equipment_type_name || x.name
    }))
}

async function refresh() {
    loading.value = true
    try {
        const url = filters.equipmentTypeId ? `/req/proc/quotes?equipmentTypeId=${encodeURIComponent(filters.equipmentTypeId)}` : '/req/proc/quotes'
        const r = await fetch(url);
        const j = await r.json()
        if (j.code === '000') {
            state.quotes = (j.data || []).map(x => ({
                supplierId: String(x.supplier_id || x.supplierId),
                equipmentTypeId: String(x.equipment_type_id || x.equipmentTypeId),
                price: Number(x.price || 0),
            }))
        } else {
            state.quotes = []
        }
    } finally {
        loading.value = false
    }
}

/* ---- Add/Edit Quote ---- */
const modal = reactive({open: false, mode: 'create', form: {supplierId: '', equipmentTypeId: '', price: 0}})

function openCreate() {
    modal.open = true;
    modal.mode = 'create';
    modal.form = {
        supplierId: suppliers.value[0]?.id || '',
        equipmentTypeId: filters.equipmentTypeId || types.value[0]?.id || '',
        price: 0
    }
}

function openEdit(q) {
    modal.open = true;
    modal.mode = 'edit';
    modal.form = {supplierId: q.supplierId, equipmentTypeId: q.equipmentTypeId, price: q.price}
}

function closeModal() {
    modal.open = false
}

async function save() {
    const p = {...modal.form}
    if (!p.supplierId || !p.equipmentTypeId) return showDialog('Supplier & Equipment Type required')
    const method = modal.mode === 'create' ? 'POST' : 'PUT'
    const r = await fetch('/req/proc/quote', {
        method,
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({supplier_id: p.supplierId, equipment_type_id: p.equipmentTypeId, price: p.price})
    })
    const j = await r.json().catch(() => ({code: 'ERR'}))
    if (j.code !== '000') return showDialog(j.message || 'Failed to save')
    await refresh();
    closeModal()
}

async function remove(q) {
    if (!(await showConfirm(`Delete quote of ${supplierName(q.supplierId)} ?`))) return
    const r = await fetch(`/req/proc/quote?supplierId=${encodeURIComponent(q.supplierId)}&equipmentTypeId=${encodeURIComponent(q.equipmentTypeId)}`, {method: 'DELETE'})
    const j = await r.json().catch(() => ({code: 'ERR'}))
    if (j.code !== '000') return showDialog(j.message || 'Failed to delete')
    await refresh()
}

/* ---- Create order from a quote ---- */
const orderModal = reactive({open: false, form: {supplierId: '', equipmentTypeId: '', price: 0, count: 1}})

function openOrder(q) {
    orderModal.open = true;
    orderModal.form = {supplierId: q.supplierId, equipmentTypeId: q.equipmentTypeId, price: q.price, count: 1}
}

function closeOrder() {
    orderModal.open = false
}

async function createOrder() {
    const p = {...orderModal.form}
    const r = await fetch('/req/proc/order', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
            supplier_id: p.supplierId,
            equipment_type_id: p.equipmentTypeId,
            count: p.count,
            status: 'under-review'
        })
    })
    const j = await r.json().catch(() => ({code: 'ERR'}))
    if (j.code !== '000') return showDialog(j.message || 'Failed to create order')
    closeOrder()
}


function showDialog(message) {
    const o = document.createElement('div');
    o.style.position = 'fixed';
    o.style.inset = '0';
    o.style.background = 'rgba(0,0,0,0.35)';
    o.style.display = 'flex';
    o.style.alignItems = 'center';
    o.style.justifyContent = 'center';
    o.style.zIndex = '9999';
    const b = document.createElement('div');
    b.className = 'card';
    b.style.padding = '16px';
    b.style.maxWidth = '420px';
    b.style.minWidth = '280px';
    b.innerHTML = `<div style="font-weight:700;">Notice</div><div style="margin-top:8px;">${message}</div><div style="margin-top:12px; display:flex; justify-content:flex-end;"><button id="ok" class="btn btn-primary">OK</button></div>`;
    o.appendChild(b);
    document.body.appendChild(o);
    o.querySelector('#ok').addEventListener('click', () => {
        document.body.removeChild(o)
    })
}

function showConfirm(message) {
    return new Promise(res => {
        const o = document.createElement('div');
        o.style.position = 'fixed';
        o.style.inset = '0';
        o.style.background = 'rgba(0,0,0,0.35)';
        o.style.display = 'flex';
        o.style.alignItems = 'center';
        o.style.justifyContent = 'center';
        o.style.zIndex = '9999';
        const b = document.createElement('div');
        b.className = 'card';
        b.style.padding = '16px';
        b.style.maxWidth = '420px';
        b.style.minWidth = '280px';
        b.innerHTML = `<div style="font-weight:700;">Confirm</div><div style="margin-top:8px;">${message}</div><div style="margin-top:12px; display:flex; justify-content:flex-end; gap:8px;"><button id="c" class="btn">Cancel</button><button id="o" class="btn btn-primary">OK</button></div>`;
        o.appendChild(b);
        document.body.appendChild(o);
        o.querySelector('#c').addEventListener('click', () => {
            document.body.removeChild(o);
            res(false)
        });
        o.querySelector('#o').addEventListener('click', () => {
            document.body.removeChild(o);
            res(true)
        })
    })
}

onMounted(async () => {
    await Promise.all([loadSuppliers(), loadTypes()]);
    await refresh()
})
</script>

<style scoped>
</style>
