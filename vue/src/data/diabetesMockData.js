export const mockPlans = {
  low: [
    { diet: '早餐：全麦面包2片+水煮蛋1个+无糖豆浆200ml（GI≈45，约320kcal）', exercise: '快走30分钟（约150kcal）', notes: '餐后1小时运动，保持血糖稳定' },
    { diet: '午餐：杂粮饭100g+清蒸鱼100g+蒜蓉西兰花150g（GI≈48，约420kcal）', exercise: '瑜伽20分钟', notes: '午餐七分饱，细嚼慢咽' },
    { diet: '晚餐：荞麦面80g+鸡胸肉炒青椒+凉拌黄瓜（GI≈46，约380kcal）', exercise: '散步30分钟', notes: '晚餐宜早，睡前3小时不进食' },
    { diet: '早餐：燕麦片30g+脱脂牛奶200ml+煮鸡蛋1个（GI≈42，约290kcal）', exercise: '晨间拉伸15分钟', notes: '燕麦选择整粒燕麦，避免即食型' },
    { diet: '午餐：糙米饭100g+虾仁豆腐+清炒菠菜（GI≈44，约400kcal）', exercise: '游泳30分钟', notes: '多摄入优质蛋白和膳食纤维' },
    { diet: '晚餐：玉米1根+番茄炒蛋+凉拌海带丝（GI≈50，约350kcal）', exercise: '太极拳20分钟', notes: '主食粗细搭配，每餐有蔬菜' },
    { diet: '早餐：全麦三明治+无糖酸奶150g+苹果半个（GI≈43，约310kcal）', exercise: '骑行30分钟', notes: '保持规律作息，定期监测空腹血糖' }
  ],
  medium: [
    { diet: '早餐：荞麦馒头1个+水煮蛋1个+无糖豆浆200ml（GI≈48，约300kcal）', exercise: '快走40分钟', notes: '严格控制精制碳水摄入' },
    { diet: '午餐：糙米饭80g+清蒸鲈鱼100g+炒苦瓜150g（GI≈46，约380kcal）', exercise: '力量训练20分钟', notes: '苦瓜有助于辅助控糖' },
    { diet: '晚餐：藜麦粥+凉拌鸡丝+炒生菜（GI≈42，约340kcal）', exercise: '散步40分钟', notes: '晚餐减少主食量，增加蔬菜比例' },
    { diet: '早餐：山药100g+煮鸡蛋1个+无糖豆浆（GI≈45，约270kcal）', exercise: '八段锦15分钟', notes: '山药代替部分主食，控制总量' },
    { diet: '午餐：杂粮饭80g+蒜蓉虾+清炒油麦菜（GI≈44，约370kcal）', exercise: '慢跑20分钟', notes: '每周至少运动5天，每次30分钟以上' },
    { diet: '晚餐：南瓜小米粥+凉拌豆腐+炒青菜（GI≈48，约310kcal）', exercise: '瑜伽25分钟', notes: '保持心情舒畅，避免情绪波动影响血糖' },
    { diet: '早餐：全麦吐司2片+无糖酸奶+小番茄（GI≈44，约290kcal）', exercise: '游泳25分钟', notes: '坚持记录饮食日记和血糖值' }
  ],
  high: [
    { diet: '早餐：燕麦片25g+脱脂牛奶200ml+水煮蛋蛋白2个（GI≈40，约240kcal）', exercise: '遵医嘱适度活动', notes: '严格遵医嘱用药，勿自行调整' },
    { diet: '午餐：杂粮饭60g+清蒸鱼80g+大量绿叶蔬菜（GI≈44，约330kcal）', exercise: '饭后散步15分钟', notes: '严格控制总热量摄入' },
    { diet: '晚餐：荞麦面50g+鸡胸肉50g+清炒苦瓜（GI≈43，约280kcal）', exercise: '床上伸展运动10分钟', notes: '监测餐后2小时血糖，记录异常波动' },
    { diet: '早餐：蒸南瓜150g+煮鸡蛋1个+无糖豆浆200ml（GI≈46，约260kcal）', exercise: '室内慢走15分钟', notes: '出现头晕眼花等症状立即测血糖' },
    { diet: '午餐：糙米饭60g+蒜蓉虾仁60g+炒西兰花200g（GI≈43，约310kcal）', exercise: '坐姿上肢运动15分钟', notes: '保持充足睡眠，避免熬夜' },
    { diet: '晚餐：小米粥150ml+蒸豆腐+炒青菜（GI≈47，约250kcal）', exercise: '深呼吸放松训练10分钟', notes: '每周至少复诊一次，及时调整方案' },
    { diet: '早餐：全麦面包1片+无糖酸奶150g+黄瓜半根（GI≈42，约220kcal）', exercise: '散步15分钟（有人陪同）', notes: '随身携带糖果或饼干，防止低血糖' }
  ]
}

export const mockRecipe = {
  meals: [
    {
      label: '早餐', icon: '🌅', gi: 'GI≈48', totalCal: '总热量≈460kcal',
      foods: [
        { name: '全麦面包', portion: '2片', cal: '约160kcal' },
        { name: '水煮鸡蛋', portion: '1个', cal: '约70kcal' },
        { name: '无糖豆浆', portion: '200ml', cal: '约60kcal' },
        { name: '凉拌黄瓜', portion: '100g', cal: '约30kcal' },
        { name: '小番茄', portion: '5颗', cal: '约40kcal' },
        { name: '脱脂牛奶', portion: '100ml', cal: '约35kcal' }
      ]
    },
    {
      label: '午餐', icon: '☀️', gi: 'GI≈52', totalCal: '总热量≈520kcal',
      foods: [
        { name: '杂粮饭', portion: '100g', cal: '约130kcal' },
        { name: '清蒸鲈鱼', portion: '120g', cal: '约120kcal' },
        { name: '蒜蓉西兰花', portion: '150g', cal: '约55kcal' },
        { name: '凉拌木耳', portion: '80g', cal: '约35kcal' },
        { name: '豆腐汤', portion: '1碗', cal: '约60kcal' },
        { name: '杂粮饭搭配', portion: '荞麦馒头半个', cal: '约50kcal' }
      ]
    },
    {
      label: '晚餐', icon: '🌆', gi: 'GI≈50', totalCal: '总热量≈420kcal',
      foods: [
        { name: '荞麦面条', portion: '80g', cal: '约110kcal' },
        { name: '鸡胸肉炒青椒', portion: '120g', cal: '约130kcal' },
        { name: '清炒生菜', portion: '150g', cal: '约30kcal' },
        { name: '凉拌海带丝', portion: '80g', cal: '约20kcal' },
        { name: '番茄蛋花汤', portion: '1碗', cal: '约50kcal' },
        { name: '蒸南瓜', portion: '100g', cal: '约22kcal' }
      ]
    }
  ]
}

export const quickQuestions = [
  {
    category: '基础认知',
    questions: [
      '空腹血糖正常值是多少？超过多少算糖尿病？',
      '糖尿病能根治吗？有没有最新的治疗方法？'
    ]
  },
  {
    category: '风险评估',
    questions: [
      '我妈妈是糖尿病，我会不会遗传？'
    ]
  },
  {
    category: '症状识别',
    questions: [
      '糖尿病早期有哪些症状需要注意？',
      '经常觉得口渴、尿多，是不是糖尿病前兆？',
      '最近脚有点麻，是不是糖尿病神经病变？'
    ]
  },
  {
    category: '治疗与生活管理',
    questions: [
      '我刚刚确诊2型糖尿病，应该先控制饮食还是直接吃药？',
      '晚饭没吃，为什么早上空腹血糖还是高？',
      '得了糖尿病还能吃米饭和面食吗？怎么吃比较好？',
      '糖尿病人可以吃水果吗？哪些水果比较安全？'
    ]
  }
]
