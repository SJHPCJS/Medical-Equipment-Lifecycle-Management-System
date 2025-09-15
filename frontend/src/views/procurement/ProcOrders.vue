<template>
  <div class="card" style="padding:16px;">
    <div class="title-lg">Purchase Orders</div>
    <div class="subtitle" style="margin-top:8px;">Update order status and push to equipment team for onboarding.</div>

    <div class="table-wrapper" style="margin-top:16px; overflow:auto;">
      <table class="table" style="table-layout:fixed; width:100%;">
        <thead>
          <tr>
            <th>PO ID</th>
            <th>SKU</th>
            <th>Item</th>
            <th>Qty</th>
            <th>Vendor</th>
            <th>Total Price</th>
            <th>Status</th>
            <th style="width:180px;">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="o in state.orders" :key="o.id">
            <td>{{ o.id }}</td>
            <td>{{ o.sku }}</td>
            <td>{{ o.itemName }}</td>
            <td>{{ o.qty }}</td>
            <td>{{ vendorName(o.vendorId) }}</td>
            <td>{{ formatPrice(o.price) }}</td>
            <td>{{ o.status }}</td>
            <td style="white-space:nowrap;">
              <button class="btn btn-green" @click="confirmArrival(o)">Confirm Arrival</button>
            </td>
          </tr>
          <tr v-if="state.orders.length===0">
            <td colspan="8" style="text-align:center; color:var(--color-muted);">No orders</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { clone, orders as seed, vendors } from '@/mocks/procurement.js'

const state = reactive({ orders: clone(seed) })

function vendorName(id) { return vendors.find(v => v.id === id)?.name || '-' }
function formatPrice(n) { return `$${n.toLocaleString()}` }
function setStatus(o, s) { o.status = s }
function confirmArrival(o) {
  setStatus(o, 'Confirmed Arrival')
  alert(`Pushed ${o.id} to Equipment Manager (demo only)`)
}
</script>

<style scoped>
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { padding: 10px 12px; border-bottom: 1px solid #e5e7eb; text-align: left; white-space: nowrap; }
.table th { background: #f9fafb; font-weight: 700; }
</style>


