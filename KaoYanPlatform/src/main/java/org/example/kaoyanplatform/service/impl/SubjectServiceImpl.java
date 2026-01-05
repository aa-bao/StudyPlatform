package org.example.kaoyanplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.example.kaoyanplatform.entity.MapQuestionSubject;
import org.example.kaoyanplatform.entity.Subject;
import org.example.kaoyanplatform.entity.UserProgress;
import org.example.kaoyanplatform.entity.dto.SubjectDTO;
import org.example.kaoyanplatform.mapper.MapQuestionSubjectMapper;
import org.example.kaoyanplatform.mapper.SubjectMapper;
import org.example.kaoyanplatform.mapper.UserProgressMapper;
import org.example.kaoyanplatform.service.SubjectService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 科目Service实现类
 * 使用映射表（map_question_subject）管理题目与科目的关系
 * 使用映射表（map_subject_book）管理书本与科目的关系
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {

    @Autowired
    private MapQuestionSubjectMapper mapQuestionSubjectMapper;

    @Autowired
    private UserProgressMapper userProgressMapper;

    @Override
    public List<SubjectDTO> getTree(Long userId, Integer rootId) {
        // 1. 获取所有科目
        List<Subject> allSubjects = list();

        // 2. 获取每个科目的题目数量（使用新的映射表）
        QueryWrapper<MapQuestionSubject> qsWrapper = new QueryWrapper<>();
        qsWrapper.select("subject_id", "count(*) as count");
        qsWrapper.groupBy("subject_id");
        List<Map<String, Object>> qsCounts = mapQuestionSubjectMapper.selectMaps(qsWrapper);
        Map<Integer, Integer> questionCountMap = new HashMap<>();
        for (Map<String, Object> map : qsCounts) {
            questionCountMap.put((Integer) map.get("subject_id"), ((Long) map.get("count")).intValue());
        }

        // 3. 获取用户进度
        Map<Integer, Integer> finishedCountMap = new HashMap<>();
        if (userId != null) {
            QueryWrapper<UserProgress> upWrapper = new QueryWrapper<>();
            upWrapper.eq("user_id", userId);
            List<UserProgress> progressList = userProgressMapper.selectList(upWrapper);
            for (UserProgress up : progressList) {
                finishedCountMap.put(up.getSubjectId(), up.getFinishedCount());
            }
        }

        // 4. 转换为 DTO 并构建树结构
        Map<Integer, SubjectDTO> dtoMap = new HashMap<>();
        List<Integer> targetRootIds = new ArrayList<>();

        // 确定目标根节点
        if (rootId != null) {
            // 检查是否为书籍
            List<Integer> mappedIds = baseMapper.getSubjectIdsByBookId(rootId);
            if (mappedIds != null && !mappedIds.isEmpty()) {
                targetRootIds.addAll(mappedIds);
            } else {
                // 如果不是书籍，使用 rootId 的子节点
                for (Subject s : allSubjects) {
                    if (s.getParentId() != null && s.getParentId().equals(rootId)) {
                        targetRootIds.add(s.getId());
                    }
                }
            }
        } else {
            // 如果 rootId 为空，返回一级科目
            for (Subject s : allSubjects) {
                if ("1".equals(s.getLevel())) {
                    targetRootIds.add(s.getId());
                }
            }
        }

        // 创建 DTO 对象
        for (Subject s : allSubjects) {
            SubjectDTO dto = new SubjectDTO();
            BeanUtils.copyProperties(s, dto);
            dto.setChildren(new ArrayList<>());
            dto.setQuestionCount(questionCountMap.getOrDefault(s.getId(), 0));
            dto.setFinishedCount(finishedCountMap.getOrDefault(s.getId(), 0));
            dtoMap.put(s.getId(), dto);
        }

        // 构建树结构
        List<SubjectDTO> resultRoots = new ArrayList<>();
        for (Subject s : allSubjects) {
            SubjectDTO dto = dtoMap.get(s.getId());
            
            // 关联到父节点（如果父节点存在于 map 中）
            if (s.getParentId() != null && s.getParentId() != 0) {
                SubjectDTO parent = dtoMap.get(s.getParentId());
                if (parent != null) {
                    parent.getChildren().add(dto);
                }
            }

            // 检查当前节点是否为目标根节点
            if (targetRootIds.contains(s.getId())) {
                resultRoots.add(dto);
            }
        }

        // 5. 递归汇总统计数据
        for (SubjectDTO root : resultRoots) {
            aggregateCounts(root);
        }

        return resultRoots;
    }

    private void aggregateCounts(SubjectDTO node) {
        if (node.getChildren() == null || node.getChildren().isEmpty()) {
            return;
        }
        int totalQ = node.getQuestionCount();
        int totalF = node.getFinishedCount();

        for (SubjectDTO child : node.getChildren()) {
            aggregateCounts(child);
            totalQ += child.getQuestionCount();
            totalF += child.getFinishedCount();
        }
        node.setQuestionCount(totalQ);
        node.setFinishedCount(totalF);
    }

    @Override
    public List<Integer> getDescendantIds(Integer subjectId) {
        List<Subject> all = list();
        List<Integer> result = new ArrayList<>();
        result.add(subjectId);
        collectDescendants(subjectId, all, result);
        return result;
    }

    private void collectDescendants(Integer parentId, List<Subject> all, List<Integer> result) {
        for (Subject s : all) {
            if (s.getParentId() != null && s.getParentId().equals(parentId)) {
                result.add(s.getId());
                collectDescendants(s.getId(), all, result);
            }
        }
    }
}
