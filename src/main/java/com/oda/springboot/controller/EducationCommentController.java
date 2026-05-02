package com.oda.springboot.controller;

import com.oda.springboot.common.Result;
import com.oda.springboot.entity.EducationComment;
import com.oda.springboot.service.IEducationCommentService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/education-comment")
public class EducationCommentController {

    private final IEducationCommentService educationCommentService;

    public EducationCommentController(IEducationCommentService educationCommentService) {
        this.educationCommentService = educationCommentService;
    }

    /**
     * 获取所有留言
     */
    @GetMapping("/list")
    public Result<List<EducationComment>> getList() {
        List<EducationComment> list = educationCommentService.getAll();
        return Result.success(list);
    }

    /**
     * 根据ID获取单条留言
     */
    @GetMapping("/{id}")
    public Result<EducationComment> getById(@PathVariable Integer id) {
        EducationComment comment = educationCommentService.getById(id);
        return Result.success(comment);
    }

    /**
     * 新增留言
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody EducationComment comment) {
        educationCommentService.add(comment);
        return Result.success("留言成功");
    }

    /**
     * 删除留言
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        educationCommentService.delete(id);
        return Result.success("删除成功");
    }
}
