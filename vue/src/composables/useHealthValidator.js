export function useHealthValidator() {

  const RULES = {
    glucose: { normal: [70, 110], warning: [50, 200], extreme: [20, 500], unit: 'mg/dL' },
    bmi: { normal: [18.5, 23.9], warning: [15, 32], extreme: [10, 50], unit: '' },
    bloodPressure: { normal: [60, 120], warning: [50, 180], extreme: [30, 250], unit: 'mmHg' },
    age: { normal: [18, 65], warning: [10, 90], extreme: [1, 150], unit: '岁' },
    insulin: { normal: [16, 166], warning: [5, 300], extreme: [0, 1000], unit: 'mU/L' },
    skinThickness: { normal: [10, 40], warning: [5, 60], extreme: [0, 100], unit: 'mm' }
  }

  function validateField(field, value) {
    const rule = RULES[field]
    if (!rule) return { level: 'normal', message: '' }
    if (value === null || value === undefined || value === '') {
      return { level: 'info', message: '请填写此字段' }
    }
    if (value < rule.extreme[0] || value > rule.extreme[1]) {
      return { level: 'error', message: `数值超出合理范围 (${rule.extreme[0]}-${rule.extreme[1]}${rule.unit})，请确认` }
    }
    if (value < rule.warning[0] || value > rule.warning[1]) {
      return { level: 'warning', message: `数值异常 (正常范围 ${rule.normal[0]}-${rule.normal[1]}${rule.unit})` }
    }
    if (value < rule.normal[0] || value > rule.normal[1]) {
      return { level: 'caution', message: `数值偏高/偏低 (正常范围 ${rule.normal[0]}-${rule.normal[1]}${rule.unit})` }
    }
    return { level: 'normal', message: '正常' }
  }

  function evaluateGlucose(val) {
    if (!val && val !== 0) return { text: '', class: '', icon: '' }
    if (val < 70) return { text: '偏低（低血糖风险）', class: 'danger', icon: 'Warning' }
    if (val <= 110) return { text: '正常', class: 'success', icon: 'Check' }
    if (val < 126) return { text: '糖尿病前期', class: 'warning', icon: 'InfoFilled' }
    return { text: '过高（疑似糖尿病）', class: 'danger', icon: 'Warning' }
  }

  function evaluateBP(val) {
    if (!val) return { text: '', class: '', icon: '' }
    if (val < 90) return { text: '偏低', class: 'warning', icon: 'InfoFilled' }
    if (val <= 120) return { text: '理想', class: 'success', icon: 'Check' }
    if (val < 140) return { text: '正常偏高', class: 'warning', icon: 'InfoFilled' }
    return { text: '高血压', class: 'danger', icon: 'Warning' }
  }

  function evaluateBMI(bmi) {
    if (!bmi) return { text: '', class: '', icon: '' }
    if (bmi < 18.5) return { text: '偏瘦', class: 'warning', icon: 'InfoFilled' }
    if (bmi <= 23.9) return { text: '正常', class: 'success', icon: 'Check' }
    if (bmi < 28) return { text: '超重', class: 'warning', icon: 'InfoFilled' }
    return { text: '肥胖', class: 'danger', icon: 'Warning' }
  }

  return {
    RULES, validateField, evaluateGlucose, evaluateBP, evaluateBMI
  }
}
