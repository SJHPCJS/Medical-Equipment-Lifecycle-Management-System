<template>
  <div class="login-page">
    <div class="overlay">
      <div class="brand">Medical Equipment Lifecycle Management System</div>
      <div class="login-card card">
        <div class="title-lg">Sign in</div>
        <form class="form" @submit.prevent="onSubmit">
          <label>Name</label>
          <input class="input" v-model="name" placeholder="Enter name" />
          <label>Password</label>
          <input class="input" type="password" v-model="password" placeholder="Enter password" />
          <div class="actions">
            <button class="btn btn-primary" type="submit">Sign In</button>
            <a class="forgot" :href="mailtoHref">Forgot password?</a>
          </div>
          <div v-if="error" class="error">{{ error }}</div>
        </form>
      </div>
    </div>
  </div>
  
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const name = ref('')
const password = ref('')
const error = ref('')
const mailtoHref = 'mailto:support@example.com?subject=Forgot%20Password'

async function onSubmit() {
  error.value = ''
  try {
    const resp = await fetch(`/req/account/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name: name.value, password: password.value }),
    })
    if (!resp.ok) throw new Error('HTTP')
    const json = await resp.json()
    if (json && json.code === '000') {
      const id = String(json.data?.account_id || '')
      const role = String(json.data?.role || '')
      const usernameFromApi = String(json.data?.username || name.value)
      if (!id || !role) throw new Error('Empty id/role')
      localStorage.setItem('demo_logged_in', '1')
      localStorage.setItem('account_id', id)
      localStorage.setItem('role', role)
      localStorage.setItem('demo_username', usernameFromApi)
      if (role === 'Admin' || role === 'SYS_ADMIN') return router.push('/admin')
      if (role === 'E-Manager' || role === 'EQUIP_MANAGER') return router.push('/equipment')
      if (role === 'D-User' || role === 'DEPT_USER') return router.push('/department')
      if (role === 'P-Staff' || role === 'PROC_STAFF') return router.push('/procurement')
      return router.push('/admin')
    } else {
      error.value = 'Invalid username or password.'
    }
  } catch (e) {
    error.value = 'Network error. Please try again.'
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background-image: url('https://images.unsplash.com/photo-1496307653780-42ee777d4833?q=80&w=2069&auto=format&fit=crop');
  background-size: cover;
  background-position: center;
}
.overlay {
  min-height: 100vh;
  width: 100%;
  background: rgba(0, 0, 0, 0.35);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 24px;
  padding: 24px;
  box-sizing: border-box;
}
.brand {
  color: #fff;
  font-size: clamp(20px, 3vw, 32px);
  font-weight: 800;
  text-shadow: 0 2px 8px rgba(0,0,0,0.2);
}
.login-card {
  width: clamp(520px, 36vw, 720px);
  padding: clamp(20px, 2.5vw, 32px);
}
.form {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 16px;
}
.actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8px;
}
.forgot {
  font-size: 13px;
  color: #fff;
  text-decoration: underline;
}
.error {
  color: #ffd1d1;
  font-size: 13px;
  margin-top: 8px;
}
</style>


