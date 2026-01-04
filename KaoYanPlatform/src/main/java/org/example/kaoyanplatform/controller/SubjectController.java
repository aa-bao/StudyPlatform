package org.example.kaoyanplatform.controller;

import org.example.kaoyanplatform.common.Result;
import org.example.kaoyanplatform.mapper.SubjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subject")
public class SubjectController {

    @Autowired
    private SubjectMapper subjectMapper;

    @GetMapping("/list")
    public Result getList() {
        return Result.success(subjectMapper.selectList(null));
    }
}