package com.cucn.springboot.controller;

import com.cucn.springboot.common.Result;
import com.cucn.springboot.entity.TreatmentRecord;
import com.cucn.springboot.service.ITreatmentRecordService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 诊疗档案控制器
 */
@RestController
@RequestMapping("/treatment-record")
public class TreatmentRecordController {

    private final ITreatmentRecordService treatmentRecordService;

    public TreatmentRecordController(ITreatmentRecordService treatmentRecordService) {
        this.treatmentRecordService = treatmentRecordService;
    }

    /**
     * 查询列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getList(TreatmentRecord query, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        List<TreatmentRecord> list = treatmentRecordService.getList(query);
        // 模拟分页处理
        int total = list.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        List<TreatmentRecord> pageList = list.subList(start, end);
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", pageList);
        result.put("total", total);
        
        return Result.success(result);
    }
    


    /**
     * 新增诊疗档案
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody TreatmentRecord record) {
        treatmentRecordService.add(record);
        return Result.success("新增成功");
    }

    /**
     * 修改诊疗档案
     */
    @PostMapping("/update")
    public Result<String> update(@RequestBody TreatmentRecord record) {
        treatmentRecordService.update(record);
        return Result.success("修改成功");
    }

    /**
     * 删除诊疗档案
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        treatmentRecordService.delete(id);
        return Result.success("删除成功");
    }

    /**
     * 下载导入模板
     */
    @GetMapping("/download-template")
    public void downloadTemplate(HttpServletResponse response) {
        treatmentRecordService.downloadTemplate(response);
    }

    /**
     * 批量导入Excel
     */
    @PostMapping("/import")
    public Result<List<String>> importExcel(@RequestParam("file") MultipartFile file) {
        List<String> result = treatmentRecordService.importExcel(file);
        return Result.success(result);
    }

    /**
     * 批量导出Excel
     */
    @GetMapping("/export")
    public void exportExcel(TreatmentRecord query, HttpServletResponse response) {
        treatmentRecordService.exportExcel(query, response);
    }
}