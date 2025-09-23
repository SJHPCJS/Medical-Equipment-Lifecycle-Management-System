<template>
  <div class="card" style="padding:16px;">
    <div class="title-lg">Onboarding & Profiling</div>
    <div class="subtitle" style="margin-top:8px;">Receiving pending purchase orders for onboarding.</div>

    <div class="cards" style="margin-top:16px; display:grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap:12px;">
      <div v-for="o in orders" :key="o.id" class="card" style="padding:0; overflow:hidden;">
        <div class="image-wrap">
          <img :src="placeholder" alt="device placeholder" />
          <div class="status-badge">PO</div>
        </div>
        <div style="padding:12px; display:grid; gap:6px;">
          <div class="title-md">{{ o.type }}</div>
          <div class="subtitle">Order: {{ o.id }}</div>
          <div class="muted">Quantity: {{ o.quantity }}</div>
          <div class="muted">Requester: {{ o.requester }}</div>
          <div style="display:flex; gap:8px; margin-top:8px;">
            <button class="btn btn-green" @click="accept(o)">Accept</button>
            <button class="btn btn-red" @click="reject(o)">Reject</button>
          </div>
        </div>
      </div>
      <div v-if="orders.length===0" class="subtitle" style="padding:16px;">No pending orders</div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { clone, receivingOrders as seed } from '@/mocks/equipment.js'
import { DEFAULT_DEVICE_PLACEHOLDER as placeholder } from '@/utils/images.js'

const state = reactive({ orders: clone(seed) })
const orders = state.orders

function accept(o) { alert(`Demo only: accepted ${o.id}`); state.orders = state.orders.filter(x => x.id !== o.id) }
function reject(o) { alert(`Demo only: rejected ${o.id}`); state.orders = state.orders.filter(x => x.id !== o.id) }
</script>

<style scoped>
.image-wrap { position: relative; width: 100%; aspect-ratio: 16/9; background: #f3f4f6; }
.image-wrap img { width: 100%; height: 100%; object-fit: cover; display:block; }
.status-badge { position: absolute; left: 12px; top: 12px; background: rgba(0,0,0,0.6); color: #fff; padding: 2px 8px; border-radius: 999px; font-size: 12px; }
</style>
