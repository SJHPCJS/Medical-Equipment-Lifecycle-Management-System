<template>
  <div class="ms" ref="root">
    <div class="control input" @click="toggle" :aria-expanded="isOpen ? 'true' : 'false'">
      <span v-if="selectedLabels.length === 0" class="placeholder">{{ placeholder }}</span>
      <span v-else class="value">
        {{ selectedLabels.length <= maxTagCount ? selectedLabels.join(', ') : `${selectedLabels.length} selected` }}
      </span>
      <svg class="caret" viewBox="0 0 20 20" aria-hidden="true"><path d="M5 7l5 6 5-6" fill="currentColor"/></svg>
    </div>
    <div v-if="isOpen" class="panel card" role="listbox">
      <div class="list">
        <label class="row" v-for="opt in options" :key="opt.value">
          <input type="checkbox" :value="opt.value" :checked="isChecked(opt.value)" @change="onToggle(opt.value, $event.target.checked)" />
          <span class="label">{{ opt.label }}</span>
        </label>
        <div v-if="options.length===0" class="empty">No options</div>
      </div>
      <div class="actions">
        <button class="btn" @click.stop="clear">Clear</button>
        <button class="btn btn-primary" @click.stop="close">OK</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'

const props = defineProps({
  modelValue: { type: Array, default: () => [] },
  options: { type: Array, default: () => [] }, // [{ value, label }]
  placeholder: { type: String, default: 'Select...' },
  maxTagCount: { type: Number, default: 2 },
})
const emit = defineEmits(['update:modelValue'])

const isOpen = ref(false)
const root = ref(null)

function toggle() { isOpen.value = !isOpen.value }
function close() { isOpen.value = false }
function outside(e) { if (!root.value) return; if (!root.value.contains(e.target)) isOpen.value = false }
onMounted(() => document.addEventListener('click', outside))
onBeforeUnmount(() => document.removeEventListener('click', outside))

function isChecked(v) { return props.modelValue.includes(v) }
function onToggle(v, checked) {
  const set = new Set(props.modelValue)
  if (checked) set.add(v); else set.delete(v)
  emit('update:modelValue', Array.from(set))
}
function clear() { emit('update:modelValue', []) }

const selectedLabels = computed(() =>
  props.options.filter(o => props.modelValue.includes(o.value)).map(o => o.label)
)
</script>

<style scoped>
.ms { position: relative; width: 100%; }
.control { display:flex; align-items:center; justify-content:space-between; gap:8px; cursor:pointer; }
.control .placeholder { color: var(--color-muted); }
.control .value { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.caret { width: 16px; height: 16px; color: var(--color-muted); flex: 0 0 auto; }

.panel { position:absolute; z-index: 50; top: calc(100% + 6px); left: 0; width: 100%; padding: 8px; max-height: 320px; overflow:auto; }
.list { display:flex; flex-direction:column; gap: 6px; }
.row { display:flex; align-items:center; gap:8px; padding: 6px 4px; border-radius: 8px; cursor: pointer; }
.row:hover { background: #f3f4f6; }
.label { user-select: none; }
.empty { color: var(--color-muted); text-align:center; padding: 12px 0; }
.actions { display:flex; gap:8px; justify-content:flex-end; margin-top:8px; }
</style>


