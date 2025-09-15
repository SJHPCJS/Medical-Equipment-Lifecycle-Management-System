<template>
  <div class="dtp">
    <div class="row">
      <select class="input" v-model.number="y">
        <option v-for="yy in years" :key="yy" :value="yy">{{ yy }}</option>
      </select>
      <select class="input" v-model.number="m">
        <option v-for="(mm, idx) in months" :key="idx" :value="idx+1">{{ mm }}</option>
      </select>
      <select class="input" v-model.number="d">
        <option v-for="dd in daysInMonth" :key="dd" :value="dd">{{ dd }}</option>
      </select>
    </div>
    <div class="row">
      <select class="input" v-model.number="hh">
        <option v-for="h in 24" :key="h-1" :value="h-1">{{ (h-1).toString().padStart(2,'0') }}</option>
      </select>
      <select class="input" v-model.number="mi">
        <option v-for="mm in minutesStep" :key="mm" :value="mm">{{ mm.toString().padStart(2,'0') }}</option>
      </select>
    </div>
  </div>
</template>

<script setup>
import { computed, watch, ref } from 'vue'

const props = defineProps({
  modelValue: { type: String, default: '' }, // ISO string
  minuteStep: { type: Number, default: 1 },
  startYear: { type: Number, default: 2020 },
  endYear: { type: Number, default: 2030 },
  hideMinutes: { type: Boolean, default: false },
})
const emit = defineEmits(['update:modelValue'])

const months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec']
const years = computed(() => {
  const arr = []
  for (let i = props.startYear; i <= props.endYear; i++) arr.push(i)
  return arr
})
const minutesStep = computed(() => {
  const step = Math.max(1, props.minuteStep)
  const arr = []
  for (let i = 0; i < 60; i += step) arr.push(i)
  return arr
})

const y = ref(2025)
const m = ref(9)
const d = ref(1)
const hh = ref(0)
const mi = ref(0)

const daysInMonth = computed(() => new Date(y.value, m.value, 0).getDate())

function pad(n) { return String(n).padStart(2,'0') }

function toIso() {
  const s = `${y.value}-${pad(m.value)}-${pad(d.value)}T${pad(hh.value)}:${pad(mi.value)}:00Z`
  return s
}

function fromIso(v) {
  try {
    if (!v) return
    const dt = new Date(v)
    if (isNaN(dt.getTime())) return
    y.value = dt.getUTCFullYear()
    m.value = dt.getUTCMonth() + 1
    d.value = dt.getUTCDate()
    hh.value = dt.getUTCHours()
    mi.value = props.hideMinutes ? 0 : dt.getUTCMinutes()
  } catch {}
}

watch([y,m,d,hh,mi], () => emit('update:modelValue', toIso()))
watch(() => props.modelValue, (v) => fromIso(v), { immediate: true })
</script>

<style scoped>
.dtp { display: grid; gap: 8px; }
.row { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 8px; }
.row + .row { grid-template-columns: repeat(2, minmax(0, 1fr)); }
</style>



