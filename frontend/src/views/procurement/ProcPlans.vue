<template>
  <div class="card" style="padding:16px;">
    <div class="title-lg">Purchase Requests</div>
    <div class="subtitle" style="margin-top:8px;">Review requests and assign vendors to create purchase orders.</div>

    <div class="table-wrapper" style="margin-top:16px; overflow:auto;">
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Spec</th>
            <th>Qty</th>
            <th>Department</th>
            <th>Status</th>
            <th style="width:220px;">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in state.plans" :key="p.id">
            <td>{{ p.id }}</td>
            <td>{{ p.name }}</td>
            <td>{{ p.spec }}</td>
            <td>{{ p.qty }}</td>
            <td>{{ p.department }}</td>
            <td>{{ p.status }}</td>
            <td>
              <button class="btn" @click="reject(p)" v-if="p.status==='Pending'">Reject</button>
              <button class="btn" style="margin-left:8px;" @click="openAssign(p)" v-if="p.status==='Pending'">Create PO</button>
              <button class="btn" v-else @click="view(p)">View</button>
            </td>
          </tr>
          <tr v-if="state.plans.length===0">
            <td colspan="7" style="text-align:center; color:var(--color-muted);">No requests</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="modal.open" class="modal-backdrop">
      <div class="modal card">
        <div class="title-lg">Assign Vendor / Create PO</div>
        <div class="form-grid">
          <div>
            <label>Vendor</label>
            <select class="input" v-model="modal.form.vendorId">
              <option v-for="v in vendors" :key="v.id" :value="v.id">{{ v.name }}</option>
            </select>
          </div>
          <div>
            <label>Item</label>
            <select class="input" v-model="modal.form.sku">
              <option v-for="i in vendorItems" :key="i.sku" :value="i.sku">{{ i.name }} ({{ i.sku }})</option>
            </select>
          </div>
          <div>
            <label>Quantity</label>
            <input class="input" type="number" min="1" v-model.number="modal.form.qty" />
          </div>
          <div>
            <label>Total Price (USD)</label>
            <input class="input" :value="totalPrice" disabled />
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
import { reactive, computed } from 'vue'
import { clone, plans as seedPlans, vendors, calcPrice, orders as seedOrders } from '@/mocks/procurement.js'

const state = reactive({ plans: clone(seedPlans), orders: clone(seedOrders) })

function reject(p) { p.status = 'Rejected' }
function view(p) { alert(`View ${p.id} (demo only)`) }

const modal = reactive({ open: false, plan: null, form: { vendorId: '', sku: '', qty: 1 } })
const vendorItems = computed(() => vendors.find(v => v.id === modal.form.vendorId)?.items || [])
const totalPrice = computed(() => calcPrice(modal.form.vendorId, modal.form.sku, modal.form.qty))

function openAssign(p) {
  modal.open = true
  modal.plan = p
  const firstVendor = vendors[0]
  const firstSku = firstVendor?.items?.[0]?.sku || ''
  modal.form = { vendorId: firstVendor?.id || '', sku: firstSku, qty: p.qty }
}
function closeAssign() { modal.open = false }
function createPO() {
  const id = `PO-${String(Math.floor(Math.random()*100000)).padStart(4,'0')}`
  state.orders.push({ id, sku: modal.form.sku, itemName: vendorItems.value.find(i => i.sku === modal.form.sku)?.name || '', qty: modal.form.qty, vendorId: modal.form.vendorId, price: totalPrice.value, status: 'Confirmed Arrival' })
  modal.plan.status = 'Assigned'
  closeAssign()
}
</script>

<style scoped>
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { padding: 10px 12px; border-bottom: 1px solid #e5e7eb; text-align: left; white-space: nowrap; }
.table th { background: #f9fafb; font-weight: 700; }
.modal-backdrop { position: fixed; inset: 0; background: rgba(0,0,0,0.35); display:flex; align-items:center; justify-content:center; padding:16px; }
.modal { width: min(720px, 100%); padding: 16px; }
.form-grid { margin-top: 16px; display:grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 12px; }
</style>
