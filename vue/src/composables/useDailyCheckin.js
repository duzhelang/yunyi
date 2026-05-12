import { ref, computed } from 'vue'
import request from '@/utils/request'

/**
 * 每日打卡组合式函数
 * 管理4个打卡任务的状态、进度计算和后端同步
 */
export function useDailyCheckin() {
  // 默认4个打卡任务
  const checkList = ref([
    { text: '早餐吃了粗粮/蔬菜', done: false },
    { text: '餐后散步 20 分钟', done: false },
    { text: '喝够 8 杯水', done: false },
    { text: '今晚 23:00 前睡觉', done: false }
  ])

  // 已完成任务百分比
  const progressPercent = computed(() => {
    const done = checkList.value.filter(t => t.done).length
    return Math.round((done / checkList.value.length) * 100)
  })

  /**
   * 切换打卡状态
   * @param {number} idx - 任务索引
   */
  function toggleCheck(idx) {
    checkList.value[idx].done = !checkList.value[idx].done
    const taskId = 'habit_' + idx
    request.post('/api/treatment/checkin', {
      profileId: 0,
      taskId,
      status: checkList.value[idx].done
    }).catch(() => {})
  }

  return {
    checkList,
    progressPercent,
    toggleCheck
  }
}
