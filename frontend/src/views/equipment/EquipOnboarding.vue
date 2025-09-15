<template>
  <div class="card" style="padding:16px;">
    <div class="title-lg">Onboarding & Profiling</div>
    <div class="subtitle" style="margin-top:8px;">Receiving pending purchase orders for onboarding.</div>

    <div class="table-wrapper" style="margin-top:16px; overflow:auto;">
      <table class="table" style="table-layout:fixed; width:100%;">
        <thead>
          <tr>
            <th>Order ID</th>
            <th>Device Type</th>
            <th>Quantity</th>
            <th>Requester</th>
            <th style="width:160px;">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="o in orders" :key="o.id">
            <td>{{ o.id }}</td>
            <td>{{ o.type }}</td>
            <td>{{ o.quantity }}</td>
            <td>{{ o.requester }}</td>
            <td style="white-space:nowrap;">
              <button class="btn btn-green" @click="accept(o)">Accept</button>
              <button class="btn btn-red" style="margin-left:8px;" @click="reject(o)">Reject</button>
            </td>
          </tr>
          <tr v-if="orders.length===0">
            <td colspan="5" style="text-align:center; color:var(--color-muted);">No pending orders</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { clone, receivingOrders as seed } from '@/mocks/equipment.js'

const state = reactive({ orders: clone(seed) })
const orders = state.orders

function accept(o) { alert(`Demo only: accepted ${o.id}`); state.orders = state.orders.filter(x => x.id !== o.id) }
function reject(o) { alert(`Demo only: rejected ${o.id}`); state.orders = state.orders.filter(x => x.id !== o.id) }
</script>

<style scoped>
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { padding: 10px 12px; border-bottom: 1px solid #e5e7eb; text-align: left; white-space: normal; word-break: break-word; }
.table th { background: #f9fafb; font-weight: 700; }
</style>
