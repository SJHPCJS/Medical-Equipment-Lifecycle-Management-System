<template>
  <div class="card" style="padding:16px;">
    <div class="title-lg">Scrap Management</div>
    <div class="subtitle" style="margin-top:8px;">Manage scrap applications and records (mock data).</div>

    <div class="table-wrapper" style="margin-top:16px; overflow:auto;">
      <table class="table" style="table-layout:fixed; width:100%;">
        <thead>
          <tr>
            <th>Scrap ID</th>
            <th>Device</th>
            <th>Reason</th>
            <th>Status</th>
            <th>Record</th>
            <th>Photo</th>
            <th style="width:240px;">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="s in state.list" :key="s.id">
            <td>{{ s.id }}</td>
            <td>{{ s.deviceId }}</td>
            <td style="white-space:normal;">{{ s.reason }}</td>
            <td>{{ s.status }}</td>
            <td><input class="input" v-model="s.record" placeholder="Disposal record" /></td>
            <td>
              <div class="scrap-photo">
                <img v-if="s.photoUrl" :src="s.photoUrl" alt="scrap" />
                <div v-else class="muted">No photo</div>
              </div>
            </td>
            <td>
              <label class="btn">
                Upload Photo
                <input type="file" accept="image/*" @change="onUploadPhoto($event, s)" style="display:none;" />
              </label>
              <button class="btn btn-green" style="margin-left:8px;" @click="approve(s)" v-if="s.status==='Pending'">Approve</button>
              <button class="btn btn-red" style="margin-left:8px;" @click="reject(s)" v-if="s.status==='Pending'">Reject</button>
              <button class="btn btn-blue" style="margin-left:8px;" v-if="s.status!=='Pending'" @click="view(s)">View</button>
            </td>
          </tr>
          <tr v-if="state.list.length===0">
            <td colspan="7" style="text-align:center; color:var(--color-muted);">No applications</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
  
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import axios from 'axios'

const state = reactive({ list: [] })

onMounted(async () => {
  const res = await axios.get('/req/scrap')
  state.list = res.data
})

async function approve(s) {
  await axios.post(`/req/scrap/${s.scrapId}/approve`)
  s.status = 'Approved'
}
async function reject(s) {
  await axios.post(`/req/scrap/${s.scrapId}/reject`)
  s.status = 'Rejected'
}
function view(s) {
  window.open(s.photoUrl, '_blank')
}

async function onUploadPhoto(e, s) {
  const file = e.target.files && e.target.files[0]
  if (!file) return
  const formData = new FormData()
  formData.append('file', file)
  const res = await axios.post(`/req/scrap/${s.scrapId}/upload`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
  s.photoUrl = res.data
  e.target.value = ''
}
</script>


<style scoped>
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { padding: 10px 12px; border-bottom: 1px solid #e5e7eb; text-align: left; white-space: normal; word-break: break-word; }
.table th { background: #f9fafb; font-weight: 700; }

.scrap-photo { width: 120px; height: 68px; background: #f3f4f6; display:flex; align-items:center; justify-content:center; overflow:hidden; border-radius:6px; }
.scrap-photo img { width: 100%; height: 100%; object-fit: cover; display:block; }
</style>


