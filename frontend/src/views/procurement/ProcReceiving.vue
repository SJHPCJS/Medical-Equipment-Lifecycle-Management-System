<template>
    <div class="card" style="padding:16px;">
        <div class="title-lg">Receiving & QA</div>
        <div class="subtitle" style="margin-top:8px;">Register arrivals, record serials, and hand off to Equipment
            team.
        </div>

        <!-- Filters -->
        <div class="ui-toolbar" style="margin-top:12px;">
            <input class="input" v-model="keyword" placeholder="Search by PO / vendor / type"/>
            <button class="btn" @click="keyword=''">Reset</button>
        </div>

        <!-- Table -->
        <div class="table-wrapper" style="margin-top:16px; overflow:auto;">
            <table class="ui-table" style="width:100%; table-layout:auto;">
                <thead>
                <tr>
                    <th style="min-width:100px;">PO</th>
                    <th>Equipment Type</th>
                    <th>Qty</th>
                    <th>Vendor</th>
                    <th>Status</th>
                    <th style="width:220px;">Actions</th>
                </tr>
                </thead>
                <tbody>
                <tr v-if="loading">
                    <td colspan="6">
                        <TableSkeleton :rows="6"/>
                    </td>
                </tr>
                <tr v-else-if="filtered.length===0">
                    <td colspan="6">
                        <EmptyState title="No arrivals" hint="Orders marked as 'arrived' will appear here."/>
                    </td>
                </tr>
                <tr v-else v-for="o in filtered" :key="o.procureId">
                    <td>#{{ o.procureId }}</td>
                    <td>{{ typeName(o.equipmentTypeId) }}</td>
                    <td>{{ o.count }}</td>
                    <td>{{ vendorName(o.supplierId) }}</td>
                    <td>{{ o.status }}</td>
                    <td style="white-space:nowrap;">
                        <button class="btn btn-green" @click="openRegister(o)">Register Receiving</button>
                        <button class="btn btn-red" style="margin-left:8px;" @click="closeOrder(o)">Close Order</button>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

        <!-- Register Receiving Modal -->
        <div v-if="modal.open" class="ui-modal-backdrop">
            <div class="ui-modal card">
                <div class="title-lg">Receiving Registration</div>
                <div class="ui-form-grid">
                    <div>
                        <label>PO</label>
                        <input class="input" :value="'#'+modal.order?.procureId" disabled/>
                    </div>
                    <div>
                        <label>Equipment Type</label>
                        <input class="input" :value="typeName(modal.order?.equipmentTypeId)" disabled/>
                    </div>
                    <div>
                        <label>Vendor</label>
                        <input class="input" :value="vendorName(modal.order?.supplierId)" disabled/>
                    </div>
                    <div>
                        <label>Department</label>
                        <select class="input" v-model="modal.form.departmentId">
                            <option v-for="d in departments" :key="d.id" :value="d.id">{{ d.name }}</option>
                        </select>
                    </div>
                    <div style="grid-column: 1 / -1;">
                        <label>Serial Numbers ({{ modal.order?.count }} items)</label>
                        <div style="display:grid; grid-template-columns: repeat(auto-fit, minmax(220px,1fr)); gap:8px;">
                            <input
                                v-for="(s, idx) in modal.form.serials"
                                :key="idx"
                                class="input"
                                v-model="modal.form.serials[idx]"
                                :placeholder="`Serial #${idx+1}`"
                            />
                        </div>
                        <div style="margin-top:8px; display:flex; gap:8px;">
                            <button class="btn" @click="clearSerials">Clear</button>
                            <button class="btn" @click="autofillSerials">Autofill</button>
                        </div>
                    </div>
                    <div style="grid-column: 1 / -1;">
                        <label>QA Notes</label>
                        <textarea class="input" rows="3" v-model="modal.form.qaNotes"
                                  placeholder="Optional notes..."></textarea>
                    </div>
                </div>
                <div style="display:flex; gap:8px; justify-content:flex-end; margin-top:16px;">
                    <button class="btn" @click="closeModal">Cancel</button>
                    <button class="btn btn-primary" @click="submitRegister">Submit</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import {ref, reactive, computed, onMounted} from 'vue'
import EmptyState from '@/components/admin/EmptyState.vue'
import TableSkeleton from '@/components/admin/TableSkeleton.vue'

/** 后端接口约定
 * GET  /req/proc/orders?status=arrived    -> 到货订单
 * GET  /req/proc/vendors                  -> 供应商
 * GET  /req/proc/equipmentTypes           -> 设备类型
 * GET  /req/admin/departments             -> 部门
 * POST /req/equip/register                -> { procure_id, equipment_type_id, supplier_id, department_id, serials:[], notes }
 * PUT  /req/proc/order/status             -> 关闭订单（可选）：{ procure_id, status: 'terminated' }
 */

const loading = ref(true)
const keyword = ref('')

const orders = ref([])       // [{procureId, equipmentTypeId, count, supplierId, status}]
const vendors = ref([])      // [{id,name}]
const types = ref([])        // [{id,name}]
const departments = ref([])  // [{id,name}]

function vendorName(id) {
    const v = vendors.value.find(v => String(v.id) === String(id));
    return v ? v.name : id
}

function typeName(id) {
    const t = types.value.find(t => String(t.id) === String(id));
    return t ? t.name : id
}

const filtered = computed(() => {
    const kw = (keyword.value || '').toLowerCase()
    return orders.value.filter(o => {
        const inKw = !kw || [`#${o.procureId}`, vendorName(o.supplierId), typeName(o.equipmentTypeId)]
            .some(s => (s || '').toLowerCase().includes(kw))
        return inKw
    })
})

/* -------- Modal state -------- */
const modal = reactive({
    open: false,
    order: null,
    form: {departmentId: '', serials: [], qaNotes: ''}
})

function openRegister(o) {
    modal.open = true
    modal.order = o
    modal.form.departmentId = departments.value[0]?.id || ''
    // 初始化序列号输入框数量 = 到货数量
    modal.form.serials = Array.from({length: Number(o.count || 0)}, () => '')
    modal.form.qaNotes = ''
}

function closeModal() {
    modal.open = false
}

function clearSerials() {
    modal.form.serials = modal.form.serials.map(() => '')
}

function autofillSerials() {
    const prefix = `SN-${String(modal.order?.procureId || '').padStart(4, '0')}-`
    modal.form.serials = modal.form.serials.map((_, i) => `${prefix}${String(i + 1).padStart(3, '0')}`)
}

/* -------- API calls -------- */
async function loadArrivals() {
    const r = await fetch('/req/proc/orders')
    const j = await r.json()
    const all = j.code === '000'
        ? (j.data || []).map(x => ({
            procureId: Number(x.procure_id || x.procureId),
            equipmentTypeId: String(x.equipment_type_id || x.equipmentTypeId),
            count: Number(x.count || 0),
            supplierId: String(x.supplier_id || x.supplierId),
            status: String(x.status || 'under-review')
        }))
        : []
    orders.value = all.filter(x => x.status === 'arrived')
}

async function loadVendors() {
    const r = await fetch('/req/proc/vendors');
    const j = await r.json()
    vendors.value = j.code === '000' ? (j.data || []).map(x => ({
        id: String(x.supplier_id || x.id),
        name: x.supplier_name || x.name || '-'
    })) : []
}

async function loadTypes() {
    const r = await fetch('/req/proc/equipmentTypes');
    const j = await r.json()
    types.value = j.code === '000' ? (j.data || []).map(x => ({
        id: String(x.equipment_type_id || x.id),
        name: x.equipment_type_name || x.name || '-'
    })) : []
}

async function loadDepartments() {
    const r = await fetch('/req/admin/departments');
    const j = await r.json()
    departments.value = j.code === '000' ? (j.data || []).map(x => ({
        id: String(x.id || x.department_id || x.departmentId),
        name: x.name || x.department_name || x.departmentName
    })) : []
}

/* 提交登记：把设备入库（序列号）交给后端 */
async function submitRegister() {
    if (!modal.order) return
    const missing = modal.form.serials.findIndex(s => !String(s || '').trim())
    if (missing !== -1) return alert(`Serial #${missing + 1} is required`)
    const uniq = new Set(modal.form.serials.map(s => s.trim()))
    if (uniq.size !== modal.form.serials.length) return alert('Serial numbers must be unique')

    const body = {
        procure_id: modal.order.procureId,
        equipment_type_id: modal.order.equipmentTypeId,
        supplier_id: modal.order.supplierId,
        department_id: modal.form.departmentId,
        serials: modal.form.serials,
        notes: modal.form.qaNotes || ''
    }
    const r = await fetch('/req/equip/register', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(body)
    })
    const j = await r.json().catch(() => ({code: 'ERR'}))
    if (j.code !== '000') return alert(j.message || 'Failed to register receiving')

    alert('Receiving registered and sent to Equipment Manager.')
    closeModal()
}

/* 可选：登记完成后关闭订单（将 arrived -> terminated） */
async function closeOrder(o) {
    if (!confirm(`Close order #${o.procureId}?`)) return
    const r = await fetch('/req/proc/order/status', {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({procure_id: o.procureId, status: 'terminated'})
    })
    const j = await r.json().catch(() => ({code: 'ERR'}))
    if (j.code !== '000') return alert(j.message || 'Failed to close order')
    // 从列表移除
    orders.value = orders.value.filter(x => x.procureId !== o.procureId)
}

/* init */
onMounted(async () => {
    loading.value = true
    try {
        await Promise.all([loadVendors(), loadTypes(), loadDepartments(), loadArrivals()])
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
