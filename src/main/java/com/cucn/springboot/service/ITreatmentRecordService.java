package com.cucn.springboot.service;

import com.cucn.springboot.entity.TreatmentRecord;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 诊疗档案服务接口
 */
public interface ITreatmentRecordService {

    /**
     * 分页查询列表
     */
    List<TreatmentRecord> getList(TreatmentRecord query);

    /**
     * 新增
     */
    void add(TreatmentRecord record);

    /**
     * 修改
     */
    void update(TreatmentRecord record);

    /**
     * 删除
     */
    void delete(Long id);

    /**
     * Excel模板下载
     */
    void downloadTemplate(HttpServletResponse response);

    /**
     * 批量导入Excel
     */
    List<String> importExcel(MultipartFile file);

    /**
     * 批量导出Excel
     */
    void exportExcel(TreatmentRecord query, HttpServletResponse response);
}
