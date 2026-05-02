package com.oda.springboot.controller;

import com.oda.springboot.common.Result;
import com.oda.springboot.entity.DiabetesEducation;
import com.oda.springboot.service.IDiabetesEducationService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/diabetes-education")
public class DiabetesEducationController {

    private final IDiabetesEducationService diabetesEducationService;

    public DiabetesEducationController(IDiabetesEducationService diabetesEducationService) {
        this.diabetesEducationService = diabetesEducationService;
    }

    /**
     * 获取所有科普内容
     */
    @GetMapping("/list")
    public Result<List<DiabetesEducation>> getList() {
        List<DiabetesEducation> list = diabetesEducationService.getAll();
        return Result.success(list);
    }

    /**
     * 根据ID获取单条科普内容
     */
    @GetMapping("/{id}")
    public Result<DiabetesEducation> getById(@PathVariable Integer id) {
        DiabetesEducation education = diabetesEducationService.getById(id);
        return Result.success(education);
    }

    /**
     * 根据篇章ID获取科普内容
     */
    @GetMapping("/section/{sectionId}")
    public Result<DiabetesEducation> getBySectionId(@PathVariable Integer sectionId) {
        DiabetesEducation education = diabetesEducationService.getBySectionId(sectionId);
        return Result.success(education);
    }

    /**
     * 新增科普内容
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody DiabetesEducation education) {
        diabetesEducationService.add(education);
        return Result.success("新增成功");
    }

    /**
     * 修改科普内容
     */
    @PostMapping("/update")
    public Result<String> update(@RequestBody DiabetesEducation education) {
        diabetesEducationService.update(education);
        return Result.success("修改成功");
    }

    /**
     * 删除科普内容
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        diabetesEducationService.delete(id);
        return Result.success("删除成功");
    }
}
