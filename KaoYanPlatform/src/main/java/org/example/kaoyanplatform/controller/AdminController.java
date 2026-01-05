package org.example.kaoyanplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.kaoyanplatform.common.Result;
import org.example.kaoyanplatform.entity.Question;
import org.example.kaoyanplatform.service.MistakeRecordService;
import org.example.kaoyanplatform.service.QuestionService;
import org.example.kaoyanplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private UserService userService;
    @Autowired
    private MistakeRecordService wrongBookService;

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(@RequestParam(required = false) Integer userId) {
        Map<String, Object> map = new HashMap<>();

        // 1. 处理卡片统计数据
        if (userId != null) {
            // 普通用户模式
            map.put("questionCount", questionService.count()); // 题库依然显示总数
            map.put("userCount", 1);                           // 个人视角
            // 练习总量统计该用户的错题记录数（或者你以后有专门的 ExamRecord 表）
            map.put("exerciseCount", wrongBookService.count(new QueryWrapper<org.example.kaoyanplatform.entity.MistakeRecord>().eq("user_id", userId)));
            map.put("todayActive", "个人模式");
        } else {
            // 管理员模式：显示全局数据
            map.put("questionCount", questionService.count());
            map.put("userCount", userService.count());
            map.put("exerciseCount", 1024); // 模拟全局总量
            map.put("todayActive", 56);
        }

        // 2. 饼图数据（科目分布通常看题库总量，不需要区分用户，除非你想看用户做题的分布）
        List<Map<String, Object>> seriesData = new ArrayList<>();
        seriesData.add(Map.of("name", "政治", "value", questionService.count(new QueryWrapper<Question>().eq("subject_id", 1))));
        seriesData.add(Map.of("name", "英语", "value", questionService.count(new QueryWrapper<Question>().eq("subject_id", 2))));
        seriesData.add(Map.of("name", "数学", "value", questionService.count(new QueryWrapper<Question>().eq("subject_id", 3))));
        seriesData.add(Map.of("name", "408专业课", "value", questionService.count(new QueryWrapper<Question>().eq("subject_id", 4))));
        map.put("subjectData", seriesData);

        return Result.success(map);
    }
}




