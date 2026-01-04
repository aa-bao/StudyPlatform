package org.example.kaoyanplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.kaoyanplatform.common.Result;
import org.example.kaoyanplatform.entity.Collection;
import org.example.kaoyanplatform.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/collection")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    // 添加或更新收藏
    @PostMapping("/add")
    public Result addCollection(@RequestBody Collection collection) {
        // 检查是否已收藏
        LambdaQueryWrapper<Collection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Collection::getUserId, collection.getUserId())
                .eq(Collection::getQuestionId, collection.getQuestionId());

        Collection exist = collectionService.getOne(wrapper);
        if (exist != null) {
            // 更新标签
            exist.setTags(collection.getTags());
            collectionService.updateById(exist);
            return Result.success("收藏标签已更新");
        } else {
            // 新增
            collection.setCreateTime(new Date());
            collectionService.save(collection);
            return Result.success("收藏成功");
        }
    }

    // 取消收藏
    @PostMapping("/remove")
    public Result removeCollection(@RequestBody Collection collection) {
        LambdaQueryWrapper<Collection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Collection::getUserId, collection.getUserId())
                .eq(Collection::getQuestionId, collection.getQuestionId());

        collectionService.remove(wrapper);
        return Result.success("已取消收藏");
    }

    // 检查是否已收藏
    @GetMapping("/check")
    public Result checkCollection(@RequestParam Long userId, @RequestParam Long questionId) {
        LambdaQueryWrapper<Collection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Collection::getUserId, userId)
                .eq(Collection::getQuestionId, questionId);

        Collection exist = collectionService.getOne(wrapper);
        if (exist != null) {
            return Result.success(exist);
        } else {
            return Result.success(null); // 返回null表示未收藏
        }
    }

    // 获取用户的收藏列表（可选，预留）
    @GetMapping("/list")
    public Result getList(@RequestParam Long userId) {
        LambdaQueryWrapper<Collection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Collection::getUserId, userId)
                .orderByDesc(Collection::getCreateTime);
        return Result.success(collectionService.list(wrapper));
    }

    // 获取用户所有收藏标签（去重）
    @GetMapping("/tags")
    public Result getTags(@RequestParam Long userId) {
        // 1. 查询该用户的所有收藏记录
        LambdaQueryWrapper<Collection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Collection::getUserId, userId);
        List<Collection> list = collectionService.list(wrapper);

        // 2. 提取所有非空标签，扁平化处理并去重
        List<String> tags = list.stream()
                .filter(c -> c.getTags() != null) // 过滤掉没有标签的记录
                .flatMap(c -> c.getTags().stream()) // 将 List<List<String>> 展平为 Stream<String>
                .distinct() // 去重
                .collect(Collectors.toList()); // 收集为 List

        return Result.success(tags);
    }
}
