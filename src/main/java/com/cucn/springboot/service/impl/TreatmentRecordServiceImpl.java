package com.cucn.springboot.service.impl;

import com.alibaba.excel.EasyExcel;
import com.cucn.springboot.entity.TreatmentRecord;
import com.cucn.springboot.mapper.TreatmentRecordMapper;
import com.cucn.springboot.service.ITreatmentRecordService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class TreatmentRecordServiceImpl implements ITreatmentRecordService {

    private final TreatmentRecordMapper treatmentRecordMapper;

    // 构造器注入,和你现有Service实现类风格一?
    public TreatmentRecordServiceImpl(TreatmentRecordMapper treatmentRecordMapper) {
        this.treatmentRecordMapper = treatmentRecordMapper;
    }

    @Override
    public List<TreatmentRecord> getList(TreatmentRecord query) {
        return treatmentRecordMapper.selectList(query);
    }

    @Override
    public void add(TreatmentRecord record) {
        // 校验身份证号重复
        if (record.getIdCard() != null && !record.getIdCard().isEmpty()) {
            TreatmentRecord exist = treatmentRecordMapper.selectByIdCard(record.getIdCard());
            if (exist != null) {
                throw new RuntimeException("该身份证号的患者档案已存在");
            }
        }
        treatmentRecordMapper.insert(record);
    }

    @Override
    public void update(TreatmentRecord record) {
        treatmentRecordMapper.updateById(record);
    }

    @Override
    public void delete(Long id) {
        treatmentRecordMapper.deleteById(id);
    }

    @Override
    public void downloadTemplate(HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("诊疗档案导入模板", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

            // 生成空模?
            EasyExcel.write(response.getOutputStream(), TreatmentRecord.class)
                    .sheet("诊疗档案模板")
                    .doWrite(new ArrayList<>());
        } catch (IOException e) {
            throw new RuntimeException("模板下载失败");
        }
    }

    @Override
    public List<String> importExcel(MultipartFile file) {
        try {
            // 读取Excel数据
            List<TreatmentRecord> list = EasyExcel.read(file.getInputStream())
                    .head(TreatmentRecord.class)
                    .sheet()
                    .doReadSync();

            if (list.isEmpty()) {
                throw new RuntimeException("导入数据不能为空");
            }

            int successCount = 0;
            List<String> failMsg = new ArrayList<>();
            List<TreatmentRecord> saveList = new ArrayList<>();

            // 数据校验
            for (int i = 0; i < list.size(); i++) {
                TreatmentRecord record = list.get(i);
                int rowNum = i + 2; // Excel行号(表头是第1行)

                // 校验必填项
                if (record.getPatientName() == null || record.getPatientName().isEmpty()) {
                    failMsg.add("第" + rowNum + "行:患者姓名不能为空");
                    continue;
                }

                // 校验身份证号重复(数据库中)
                if (record.getIdCard() != null && !record.getIdCard().isEmpty()) {
                    TreatmentRecord exist = treatmentRecordMapper.selectByIdCard(record.getIdCard());
                    if (exist != null) {
                        failMsg.add("第" + rowNum + "行:身份证号" + record.getIdCard() + "已存在");
                        continue;
                    }
                    // 校验导入文件内重复
                    long repeatCount = saveList.stream()
                            .filter(item -> record.getIdCard().equals(item.getIdCard()))
                            .count();
                    if (repeatCount > 0) {
                        failMsg.add("第" + rowNum + "行:身份证号" + record.getIdCard() + "在文件中重复");
                        continue;
                    }
                }

                saveList.add(record);
                successCount++;
            }

            // 批量保存
            if (!saveList.isEmpty()) {
                treatmentRecordMapper.batchInsert(saveList);
            }

            // 拼接返回结果
            failMsg.add(0, "导入完成:成功" + successCount + "条,失败" + failMsg.size() + "条");
            return failMsg;

        } catch (IOException e) {
            throw new RuntimeException("导入文件解析失败");
        }
    }

    @Override
    public void exportExcel(TreatmentRecord query, HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("诊疗档案数据", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

            // 查询数据并导?
            List<TreatmentRecord> list = treatmentRecordMapper.selectList(query);
            EasyExcel.write(response.getOutputStream(), TreatmentRecord.class)
                    .sheet("诊疗档案数据")
                    .doWrite(list);
        } catch (IOException e) {
            throw new RuntimeException("导出失败");
        }
    }
}