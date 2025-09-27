<template>
  <div>
    <div class="grid">
      <StatCard label="In-Use Devices" :value="inUse" />
      <StatCard label="Devices Under Repair" :value="underRepair" />
      <StatCard label="Today Usage Logs" :value="todosUsage" />
      <StatCard label="Unfinished Repairments" :value="todosRepair" />
    </div>
  </div>
</template>

<script setup>
import StatCard from '@/components/layout/StatCard.vue'
import { ref, onMounted } from 'vue'
import axios from 'axios'

// 初始化状态
const inUse = ref(0)
const underRepair = ref(0)
const todosUsage = ref(0)
const todosRepair = ref(0)

// 当组件加载完成时，发起请求获取数据
onMounted(async () => {
  try {
    const response = await axios.get('/req/dept/dashboard/stats')
    console.log('API Response:', response.data); // 调试输出返回数据
    // 更新前端数据显示
    inUse.value = response.data.inUse;
    underRepair.value = response.data.underRepair;
    todosUsage.value = response.data.todosUsage;
    todosRepair.value = response.data.todosRepair;
  } catch (error) {
    console.error('Error fetching dashboard data:', error);
  }
});
</script>

<style scoped>
.grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
}
</style>
