package com.cucn.springboot.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cucn.springboot.entity.DiabetesRecord; // 替换为糖尿病实体
import com.cucn.springboot.common.Result;
import com.cucn.springboot.mapper.DiabetesRecordMapper; // 替换为糖尿病Mapper
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("/echarts")
public class EchartsController {

    @Resource
    private DiabetesRecordMapper diabetesMapper; // 注入糖尿病Mapper,替换原 ResultMapper

    // 1. 统计未患?患病人数(对应前端图表的核心数据?
    @GetMapping("/members")
    public Result members() {
        ArrayList<Long> countList = new ArrayList<>();
        // 未患病(outcome=0?
        LambdaQueryWrapper<DiabetesRecord> normalWrapper = new LambdaQueryWrapper<>();
        normalWrapper.eq(DiabetesRecord::getOutcome, 0);
        countList.add(diabetesMapper.selectCount(normalWrapper));
        // 患病(outcome=1?
        LambdaQueryWrapper<DiabetesRecord> diseaseWrapper = new LambdaQueryWrapper<>();
        diseaseWrapper.eq(DiabetesRecord::getOutcome, 1);
        countList.add(diabetesMapper.selectCount(diseaseWrapper));
        return Result.success(countList); // 返回 [未患病人? 患病人数]
    }

    // 2. 按结果分组获取详细记录(如需详情页可保留?
    @GetMapping("/index")
    public Result index() {
        Map<Integer, List> map = new HashMap<>();
        // 未患病记录(outcome=0?
        LambdaQueryWrapper<DiabetesRecord> normalWrapper = new LambdaQueryWrapper<>();
        normalWrapper.eq(DiabetesRecord::getOutcome, 0);
        map.put(0, diabetesMapper.selectList(normalWrapper));
        // 患病记录(outcome=1?
        LambdaQueryWrapper<DiabetesRecord> diseaseWrapper = new LambdaQueryWrapper<>();
        diseaseWrapper.eq(DiabetesRecord::getOutcome, 1);
        map.put(1, diabetesMapper.selectList(diseaseWrapper));
        return Result.success(map);
    }

    // 3. 总检测人数(替换原"故障总数")
    @GetMapping("/totle")
    public Result totle() {
        Long totalCount = diabetesMapper.selectCount(null); // 统计所有记录数
        return Result.success(totalCount);
    }

    // 4. 今日检测人数(替换原"今日故障预测总数")
    @GetMapping("/totle1")
    public Result totle1() {
        List<DiabetesRecord> allRecords = diabetesMapper.selectList(new QueryWrapper<>());
        String today = DateUtil.today(); // 今日日期(格式:yyyy-MM-dd?
        Integer todayCount = 0;
        for (DiabetesRecord record : allRecords) {
            String recordDate = DateUtil.format(record.getCreateTime(), "yyyy-MM-dd");
            if (recordDate.equals(today)) {
                todayCount++;
            }
        }
        return Result.success(todayCount);
    }

    // 5. 最常见结果(未患病/患病哪个占比高,替换原"最多故障模型")
    @GetMapping("/totle3")
    public Result totle3() {
        Map<Integer, Long> resultMap = new HashMap<>();
        // 统计未患病数?
        LambdaQueryWrapper<DiabetesRecord> normalWrapper = new LambdaQueryWrapper<>();
        normalWrapper.eq(DiabetesRecord::getOutcome, 0);
        resultMap.put(0, diabetesMapper.selectCount(normalWrapper));
        // 统计患病数量
        LambdaQueryWrapper<DiabetesRecord> diseaseWrapper = new LambdaQueryWrapper<>();
        diseaseWrapper.eq(DiabetesRecord::getOutcome, 1);
        resultMap.put(1, diabetesMapper.selectCount(diseaseWrapper));
        // 找出数量多的结果?=未患病,1=患病?
        Integer maxKey = resultMap.get(0) >= resultMap.get(1) ? 0 : 1;
        return Result.success(maxKey);
    }
    @GetMapping("/bmiDiseaseCount")
    public Result getBmiDiseaseCount() {
        Map<String, Long> bmiData = new HashMap<>();

        // 1. BMI < 18.5(偏瘦)
        LambdaQueryWrapper<DiabetesRecord> thinWrapper = new LambdaQueryWrapper<>();
        thinWrapper.lt(DiabetesRecord::getBmi, 18.5)  // BMI<18.5
                .eq(DiabetesRecord::getOutcome, 1); // 仅统计患病的
        bmiData.put("偏瘦(<18.5)", diabetesMapper.selectCount(thinWrapper));

        // 2. 18.5 ?BMI < 24(正常)
        LambdaQueryWrapper<DiabetesRecord> normalWrapper = new LambdaQueryWrapper<>();
        normalWrapper.between(DiabetesRecord::getBmi, 18.5, 24)
                .eq(DiabetesRecord::getOutcome, 1);
        bmiData.put("正常(18.5-24)", diabetesMapper.selectCount(normalWrapper));

        // 3. 24 ?BMI < 28(超重)
        LambdaQueryWrapper<DiabetesRecord> overWeightWrapper = new LambdaQueryWrapper<>();
        overWeightWrapper.between(DiabetesRecord::getBmi, 24, 28)
                .eq(DiabetesRecord::getOutcome, 1);
        bmiData.put("超重(24-28)", diabetesMapper.selectCount(overWeightWrapper));

        // 4. BMI ?28(肥胖)
        LambdaQueryWrapper<DiabetesRecord> fatWrapper = new LambdaQueryWrapper<>();
        fatWrapper.ge(DiabetesRecord::getBmi, 28)
                .eq(DiabetesRecord::getOutcome, 1);
        bmiData.put("肥胖(?8)", diabetesMapper.selectCount(fatWrapper));

        return Result.success(bmiData);
    }
}