<template>
  <div class="card" style="padding:16px;">
    <div class="title-lg">Vendors</div>
    <div class="subtitle" style="margin-top:8px;">Manage vendor catalog by item (SKU as index).</div>

    <div class="table-wrapper" style="margin-top:16px; overflow:auto;">
      <table class="table" style="table-layout:fixed; width:100%;">
        <thead>
          <tr>
            <th>Vendor</th>
            <th>Contact</th>
            <th>Address</th>
            <th>SKU</th>
            <th>Item</th>
            <th>Price</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in flattened" :key="row.vendorId + row.sku">
            <td>{{ row.vendor }}</td>
            <td>{{ row.contact }}</td>
            <td>{{ row.address }}</td>
            <td>{{ row.sku }}</td>
            <td>{{ row.name }}</td>
            <td>{{ formatPrice(row.price) }}</td>
          </tr>
          <tr v-if="flattened.length===0">
            <td colspan="6" style="text-align:center; color:var(--color-muted);">No vendors</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div style="margin-top:16px;">
      <button class="btn btn-primary" @click="openNew">Add Vendor Offer</button>
    </div>

    <div v-if="modal.open" class="modal-backdrop">
      <div class="modal card">
        <div class="title-lg">Add Vendor Offer</div>
        <div class="form-grid">
          <div>
            <label>Vendor Name</label>
            <input class="input" v-model="modal.form.name" placeholder="Vendor name" />
          </div>
          <div>
            <label>Contact</label>
            <input class="input" v-model="modal.form.contact" placeholder="Contact person" />
          </div>
          <div>
            <label>Address</label>
            <input class="input" v-model="modal.form.address" placeholder="Address" />
          </div>
          <div>
            <label>SKU</label>
            <input class="input" v-model="modal.form.sku" placeholder="e.g. ECG-01" />
          </div>
          <div>
            <label>Item Name</label>
            <input class="input" v-model="modal.form.itemName" placeholder="Item name" />
          </div>
          <div>
            <label>Price</label>
            <input class="input" type="number" min="0" v-model.number="modal.form.price" />
          </div>
        </div>
        <div style="display:flex; gap:8px; justify-content:flex-end; margin-top:16px;">
          <button class="btn" @click="closeNew">Cancel</button>
          <button class="btn btn-primary" @click="saveNew">Save</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, computed } from 'vue'
import { clone, vendors as seed } from '@/mocks/procurement.js'

const state = reactive({ vendors: clone(seed) })
const flattened = computed(() => {
  const rows = []
  for (const v of state.vendors) {
    for (const i of v.items) {
      rows.push({ vendorId: v.id, vendor: v.name, contact: v.contact, address: v.address, sku: i.sku, name: i.name, price: i.price })
    }
  }
  return rows
})

function formatPrice(n) { return `$${n.toLocaleString()}` }

const modal = reactive({ open: false, form: { name: '', contact: '', address: '', sku: '', itemName: '', price: 0 } })
function openNew() { modal.open = true; modal.form = { name: '', contact: '', address: '', sku: '', itemName: '', price: 0 } }
function closeNew() { modal.open = false }
function saveNew() {
  const id = `V-${String(Math.floor(Math.random()*100000)).padStart(3,'0')}`
  state.vendors.push({ id, name: modal.form.name, contact: modal.form.contact, address: modal.form.address, items: [{ sku: modal.form.sku, name: modal.form.itemName, price: modal.form.price }] })
  closeNew()
}
</script>

<style scoped>
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { padding: 10px 12px; border-bottom: 1px solid #e5e7eb; text-align: left; white-space: normal; word-break: break-word; }
.table th { background: #f9fafb; font-weight: 700; }
.modal-backdrop { position: fixed; inset: 0; background: rgba(0,0,0,0.35); display:flex; align-items:center; justify-content:center; padding:16px; }
.modal { width: min(720px, 100%); padding: 16px; }
.form-grid { margin-top: 16px; display:grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 12px; }
</style>


