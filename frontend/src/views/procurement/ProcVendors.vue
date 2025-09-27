<template>
    <div class="card" style="padding:16px;">
        <div class="title-lg">Vendors</div>
        <div class="subtitle" style="margin-top:8px;">
            Manage suppliers and their offers (equipment type ↔ price).
        </div>

        <!-- Toolbar -->
        <div class="ui-toolbar" style="margin-top:12px;">
            <input class="input" v-model="keyword" placeholder="Search by supplier / type"/>
            <div style="display:flex; gap:8px;">
                <button class="btn" @click="keyword=''">Reset</button>
                <button class="btn btn-primary" @click="openNewOffer">Add Offer</button>
                <button class="btn" @click="openNewSupplier">Add Supplier</button>
            </div>
        </div>

        <!-- Table -->
        <div class="table-wrapper" style="margin-top:16px; overflow:auto;">
            <table class="ui-table" style="table-layout:auto; width:100%;">
                <thead>
                <tr>
                    <th>Supplier</th>
                    <th>Contact</th>
                    <th>Equipment Type</th>
                    <th>Price</th>
                    <th style="width:220px;">Actions</th>
                </tr>
                </thead>
                <tbody>
                <tr v-if="loading">
                    <td colspan="5">
                        <TableSkeleton :rows="6"/>
                    </td>
                </tr>
                <tr v-else-if="rows.length===0">
                    <td colspan="5">
                        <EmptyState title="No vendors" hint="Add a supplier or offer to get started."/>
                    </td>
                </tr>
                <tr v-else v-for="r in rows" :key="r.key">
                    <td>{{ r.supplierName }}</td>
                    <td>{{ r.contact || '-' }}</td>
                    <td>{{ r.typeName }}</td>
                    <td>{{ money(r.price) }}</td>
                    <td style="white-space:nowrap;">
                        <button class="btn btn-blue" @click="openEditOffer(r)">Edit Offer</button>
                        <button class="btn btn-red" style="margin-left:8px;" @click="removeOffer(r)">Delete Offer
                        </button>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

        <!-- Add/Edit Offer Modal -->
        <div v-if="offerModal.open" class="modal-backdrop">
            <div class="modal card">
                <div class="title-lg">{{ offerModal.mode === 'create' ? 'Add Offer' : 'Edit Offer' }}</div>
                <div class="form-grid">
                    <div>
                        <label>Supplier</label>
                        <select class="input" v-model="offerModal.form.supplierId">
                            <option v-for="s in suppliers" :key="s.id" :value="s.id">{{ s.name }}</option>
                        </select>
                    </div>
                    <div>
                        <label>Equipment Type</label>
                        <select class="input" v-model="offerModal.form.equipmentTypeId">
                            <option v-for="t in types" :key="t.id" :value="t.id">{{ t.name }}</option>
                        </select>
                    </div>
                    <div>
                        <label>Price</label>
                        <input class="input" type="number" min="0" step="1" v-model.number="offerModal.form.price"/>
                    </div>
                </div>
                <div style="display:flex; gap:8px; justify-content:flex-end; margin-top:16px;">
                    <button class="btn" @click="closeOffer">Cancel</button>
                    <button class="btn btn-primary" @click="saveOffer">Save</button>
                </div>
            </div>
        </div>

        <!-- Add Supplier Modal -->
        <div v-if="supplierModal.open" class="modal-backdrop">
            <div class="modal card">
                <div class="title-lg">Add Supplier</div>
                <div class="form-grid">
                    <div>
                        <label>Supplier Name</label>
                        <input class="input" v-model="supplierModal.form.name" placeholder="Supplier name"/>
                    </div>
                    <div>
                        <label>Contact</label>
                        <input class="input" v-model="supplierModal.form.contact" placeholder="Contact person / phone"/>
                    </div>
                </div>
                <div style="display:flex; gap:8px; justify-content:flex-end; margin-top:16px;">
                    <button class="btn" @click="closeNewSupplier">Cancel</button>
                    <button class="btn btn-primary" @click="saveNewSupplier">Save</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import {ref, reactive, computed, onMounted} from 'vue'
import EmptyState from '@/components/admin/EmptyState.vue'
import TableSkeleton from '@/components/admin/TableSkeleton.vue'

/** 后端接口（与项目其余页面一致）：
 *  GET  /req/proc/vendors                    -> [{supplier_id,supplier_name,contact}]
 *  POST /req/proc/vendor                     -> {supplier_name, contact}
 *  GET  /req/proc/equipmentTypes             -> [{equipment_type_id,equipment_type_name}]
 *  GET  /req/proc/quotes                     -> [{supplier_id,equipment_type_id,price}]
 *  POST /req/proc/quote                      -> {supplier_id,equipment_type_id,price}
 *  PUT  /req/proc/quote                      -> {supplier_id,equipment_type_id,price}
 *  DELETE /req/proc/quote?supplierId=&equipmentTypeId=
 */

const loading = ref(true)
const keyword = ref('')

const suppliers = ref([]) // [{id,name,contact}]
const types = ref([])     // [{id,name}]
const quotes = ref([])    // [{supplierId,typeId,price}]

function money(n) {
    try {
        return Number(n || 0).toLocaleString(undefined, {style: 'currency', currency: 'USD'})
    } catch {
        return String(n)
    }
}

function sName(id) {
    return suppliers.value.find(s => s.id === id)?.name || id
}

function sContact(id) {
    return suppliers.value.find(s => s.id === id)?.contact || ''
}

function tName(id) {
    return types.value.find(t => t.id === id)?.name || id
}

/* 展示为扁平行 */
const rows = computed(() => {
    const kw = (keyword.value || '').toLowerCase()
    return quotes.value
        .map(q => ({
            key: `${q.supplierId}-${q.typeId}`,
            supplierId: q.supplierId,
            supplierName: sName(q.supplierId),
            contact: sContact(q.supplierId),
            equipmentTypeId: q.typeId,
            typeName: tName(q.typeId),
            price: q.price
        }))
        .filter(r => {
            if (!kw) return true
            return [r.supplierName, r.typeName].some(x => (x || '').toLowerCase().includes(kw))
        })
})

/* ---------- API ---------- */
async function loadSuppliers() {
    const r = await fetch('/req/proc/vendors');
    const j = await r.json()
    suppliers.value = j.code === '000'
        ? (j.data || []).map(x => ({
            id: String(x.supplier_id || x.id),
            name: x.supplier_name || x.name || '-',
            contact: x.contact || ''
        }))
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

/* ---------- Offer modal ---------- */
const offerModal = reactive({
    open: false,
    mode: 'create', // 'create' | 'edit'
    form: {supplierId: '', equipmentTypeId: '', price: 0}
})

function openNewOffer() {
    offerModal.open = true
    offerModal.mode = 'create'
    offerModal.form = {
        supplierId: suppliers.value[0]?.id || '',
        equipmentTypeId: types.value[0]?.id || '',
        price: 0
    }
}

function openEditOffer(row) {
    offerModal.open = true
    offerModal.mode = 'edit'
    offerModal.form = {
        supplierId: row.supplierId,
        equipmentTypeId: row.equipmentTypeId,
        price: row.price
    }
}

function closeOffer() {
    offerModal.open = false
}

async function saveOffer() {
    const p = {...offerModal.form}
    if (!p.supplierId || !p.equipmentTypeId) return alert('Supplier & Equipment Type are required')
    const method = offerModal.mode === 'create' ? 'POST' : 'PUT'
    const r = await fetch('/req/proc/quote', {
        method,
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({supplier_id: p.supplierId, equipment_type_id: p.equipmentTypeId, price: p.price})
    })
    const j = await r.json().catch(() => ({code: 'ERR'}))
    if (j.code !== '000') return alert(j.message || 'Failed to save offer')
    await loadQuotes()
    closeOffer()
}

async function removeOffer(row) {
    if (!confirm(`Delete offer: ${row.supplierName} - ${row.typeName}?`)) return
    const r = await fetch(`/req/proc/quote?supplierId=${encodeURIComponent(row.supplierId)}&equipmentTypeId=${encodeURIComponent(row.equipmentTypeId)}`, {method: 'DELETE'})
    const j = await r.json().catch(() => ({code: 'ERR'}))
    if (j.code !== '000') return alert(j.message || 'Failed to delete offer')
    await loadQuotes()
}

/* ---------- Supplier modal ---------- */
const supplierModal = reactive({
    open: false,
    form: {name: '', contact: ''}
})

function openNewSupplier() {
    supplierModal.open = true;
    supplierModal.form = {name: '', contact: ''}
}

function closeNewSupplier() {
    supplierModal.open = false
}

async function saveNewSupplier() {
    const p = supplierModal.form
    if (!p.name || !p.name.trim()) return alert('Supplier name is required')
    const r = await fetch('/req/proc/vendor', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({supplier_name: p.name.trim(), contact: p.contact || ''})
    })
    const j = await r.json().catch(() => ({code: 'ERR'}))
    if (j.code !== '000') return alert(j.message || 'Failed to add supplier')
    await loadSuppliers()
    closeNewSupplier()
}

/* init */
onMounted(async () => {
    loading.value = true
    try {
        await Promise.all([loadSuppliers(), loadTypes(), loadQuotes()])
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
    white-space: normal;
    word-break: break-word;
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
