<template>
  <div class="card" style="padding:16px;">
    <div class="title-lg">Account</div>
    <div class="subtitle" style="margin-top:8px;">Manage your profile and security.</div>

    <div class="grid">
      <div class="section card" style="padding:16px;">
        <div style="font-weight:700;">Profile</div>
        <div class="form">
          <label>Display Name</label>
          <input class="input" v-model="profile.name" placeholder="Your name" />
          <label>Email</label>
          <input class="input" v-model="profile.email" placeholder="you@example.com" />
          <div style="display:flex; gap:8px; justify-content:flex-end;">
            <button class="btn" @click="resetProfile">Reset</button>
            <button class="btn btn-primary" @click="saveProfile">Save changes</button>
          </div>
        </div>
      </div>

      <div class="section card" style="padding:16px;">
        <div style="font-weight:700;">Security</div>
        <div class="subtitle">Update your password regularly to keep your account secure.</div>
        <div style="display:flex; justify-content:flex-end; margin-top:8px;">
          <button class="btn btn-primary" @click="openPwdModal">Change password</button>
        </div>
      </div>
    </div>

    <!-- Change password modal -->
    <div v-if="pwdModal" class="ui-modal-backdrop">
      <div class="ui-modal card">
        <div class="title-lg">Change Password</div>
        <div class="ui-form-grid">
          <div>
            <label>Current Password</label>
            <input class="input" :type="show.cur ? 'text' : 'password'" v-model="pwd.current" />
          </div>
          <div>
            <label>New Password</label>
            <input class="input" :type="show.new ? 'text' : 'password'" v-model="pwd.next" />
          </div>
          <div>
            <label>Confirm New Password</label>
            <input class="input" :type="show.confirm ? 'text' : 'password'" v-model="pwd.confirm" />
          </div>
          <div class="row" style="grid-column: 1 / -1;">
            <label><input type="checkbox" v-model="show.cur" /> Show current</label>
            <label><input type="checkbox" v-model="show.new" /> Show new</label>
            <label><input type="checkbox" v-model="show.confirm" /> Show confirm</label>
          </div>
        </div>
        <div style="display:flex; gap:8px; justify-content:flex-end; margin-top:16px;">
          <button class="btn" @click="closePwdModal">Cancel</button>
          <button class="btn btn-primary" @click="changePassword">Update</button>
        </div>
      </div>
    </div>
  </div>
  
</template>

<script setup>
import { reactive, onMounted, ref } from 'vue'

const profile = reactive({ name: '', email: '' })
const pwd = reactive({ current: '', next: '', confirm: '' })
const show = reactive({ cur: false, new: false, confirm: false })
const pwdModal = ref(false)

function toast(msg) {
  alert(msg)
}

function resetProfile() {
  const name = localStorage.getItem('demo_username') || ''
  const email = localStorage.getItem('email') || ''
  profile.name = name
  profile.email = email
}

async function saveProfile() {
  localStorage.setItem('demo_username', profile.name || '')
  localStorage.setItem('email', profile.email || '')
  toast('已保存到本地（演示）')
}

function resetPwdForm() { pwd.current = ''; pwd.next = ''; pwd.confirm = '' }
function openPwdModal() { pwdModal.value = true }
function closePwdModal() { pwdModal.value = false; resetPwdForm(); show.cur=false; show.new=false; show.confirm=false }

async function changePassword() {
  if (!pwd.next || pwd.next !== pwd.confirm) return toast('Passwords do not match')
  const accountId = localStorage.getItem('account_id') || ''
  if (!accountId) return toast('Not logged in')
  try {
    const resp = await fetch('/req/admin/resetPwd', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ accountId, newPwd: pwd.next }) })
    const json = await resp.json()
    if (json.code === '000') { toast('Password updated'); closePwdModal() } else { toast(json.message || 'Failed to update') }
  } catch {
    toast('Network error')
  }
}

onMounted(() => {
  resetProfile()
})
</script>

<style scoped>
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
  margin-top: 16px;
}
.form { display: flex; flex-direction: column; gap: 10px; margin-top: 12px; }
.row { display: flex; gap: 16px; align-items: center; }
</style>



