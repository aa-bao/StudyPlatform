# 前端架构与核心实现

## 目录

- [前端架构](#前端架构)
  - [路由与权限](#路由与权限)
  - [布局策略](#布局策略)
  - [状态管理](#状态管理)
- [核心实现细节](#核心实现细节)
  - [多对多关系管理](#多对多关系管理)
  - [树形结构处理](#树形结构处理)
  - [拖拽排序实现](#拖拽排序实现)
  - [ECharts 雷达图实现](#echarts-雷达图实现)
  - [KaTeX 数学公式渲染](#katex-数学公式渲染)
- [开发规范](#开发规范)

---

## 前端架构

### 路由与权限

#### 路由结构

```javascript
const routes = [
  {
    path: '/login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/user',
    component: () => import('@/views/layout/UserLayout.vue'),
    children: [
      { path: 'dashboard', component: () => import('@/views/Dashboard.vue') },
      { path: 'correction-notebook', component: () => import('@/views/CorrectionNotebook.vue') },
      { path: 'profile', component: () => import('@/views/UserProfile.vue') },
      { path: 'subject', component: () => import('@/views/SubjectList.vue') },
      { path: 'single-practice/:subjectId', component: () => import('@/views/quiz/SinglePractice.vue') },
      { path: 'topic-drill', component: () => import('@/views/quiz/TopicDrill.vue') },
      { path: 'mock-exam', component: () => import('@/views/quiz/MockExam.vue') }
    ]
  },
  {
    path: '/admin',
    component: () => import('@/views/layout/AdminLayout.vue'),
    children: [
      { path: 'home', component: () => import('@/views/admin/AdminHome.vue') },
      { path: 'users-manage', component: () => import('@/views/admin/UserManage.vue') },
      { path: 'mistake-monitor', component: () => import('@/views/admin/MistakeMonitor.vue') },
      { path: 'subjects-manage', component: () => import('@/views/admin/SubjectManage.vue') },
      { path: 'books-manage', component: () => import('@/views/admin/BookManage.vue') },
      { path: 'questions-manage', component: () => import('@/views/admin/QuestionManage.vue') }
    ]
  }
]
```

#### 导航守卫

```javascript
router.beforeEach((to, from, next) => {
  const role = localStorage.getItem('role')
  const userStr = localStorage.getItem('user')
  let userRole = null
  let isLogin = false

  if (role && userStr) {
    try {
      const userObj = JSON.parse(userStr)
      if (userObj.role === role) {
        userRole = role
        isLogin = true
      }
    } catch (e) {
      console.error('用户信息解析失败', e)
    }
  }

  // 路由逻辑判断
  if (to.path === '/login') {
    if (isLogin) {
      next(userRole === 'admin' ? '/admin/home' : '/user/dashboard')
    } else {
      next()
    }
  } else if (to.path.startsWith('/admin')) {
    if (!isLogin) {
      next('/login')
    } else if (userRole !== 'admin') {
      ElMessage.error("权限不足，无法进入管理后台")
      next('/user/dashboard')
    } else {
      next()
    }
  } else {
    if (!isLogin) {
      next('/login')
    } else {
      next()
    }
  }
})
```

### 布局策略

#### AdminLayout（管理员布局）

- **侧边栏**: 管理功能导航菜单
- **顶栏**: 欢迎语 + 用户头像下拉菜单（退出登录）
- **内容区**: 管理功能页面

#### UserLayout（用户布局）

- **侧边栏**: 浅色主题，学习功能导航菜单
- **顶栏**: 用户信息 + 消息通知
- **内容区**: 学习功能页面

### 状态管理

#### user.js (Pinia Store)

```javascript
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null,
    token: null
  }),

  actions: {
    setUserInfo(userInfo) {
      this.userInfo = userInfo
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },

    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },

    clearUserInfo() {
      this.userInfo = null
      this.token = null
      localStorage.removeItem('userInfo')
      localStorage.removeItem('token')
    }
  }
})
```

#### exam.js (Pinia Store - 考试会话状态)

```javascript
import { defineStore } from 'pinia'

export const useExamStore = defineStore('exam', {
  state: () => ({
    sessionId: null,
    paperId: null,
    answers: {},
    currentQuestionIndex: 0
  }),

  actions: {
    setSession(sessionId, paperId) {
      this.sessionId = sessionId
      this.paperId = paperId
    },

    setAnswer(questionId, answer) {
      this.answers[questionId] = answer
    },

    clearSession() {
      this.sessionId = null
      this.paperId = null
      this.answers = {}
      this.currentQuestionIndex = 0
    }
  }
})
```

---

## 核心实现细节

### 多对多关系管理

#### 设计理念

使用映射表（`map_` 前缀）实现灵活的多对多关系，避免外键锁死，支持一道题属于多本书、多个科目。

#### 实现要点

1. **原子性操作**: 使用 `@Transactional` 保证多表操作的原子性
2. **级联查询**: 通过 JOIN 实现高效查询
3. **前端级联选择**: 使用 `el-cascader` 的 `multiple` 属性支持多选

```vue
<el-cascader
    v-model="formData.subjectIds"
    :options="subjectTree"
    :props="{ multiple: true, emitPath: false }"
    placeholder="请选择关联科目"
/>
```

### 树形结构处理

#### 场景

科目体系支持 多层级结构（具体考试科目 → 知识模块 → 知识点 → 题型），并支持基于 scope 的多对多关系映射。

#### 1. 递归构建树

从扁平数据转换为树形结构：

```java
public List<SubjectDTO> getTree(Long userId, Integer rootId) {
    // 1. 获取所有科目
    List<Subject> allSubjects = list();

    // 2. 创建 DTO 对象
    Map<Integer, SubjectDTO> dtoMap = new HashMap<>();
    for (Subject s : allSubjects) {
        SubjectDTO dto = new SubjectDTO();
        BeanUtils.copyProperties(s, dto);
        dto.setChildren(new ArrayList<>());
        dtoMap.put(s.getId(), dto);
    }

    // 3. 构建树结构
    List<SubjectDTO> resultRoots = new ArrayList<>();
    for (Subject s : allSubjects) {
        SubjectDTO dto = dtoMap.get(s.getId());

        // 关联到父节点
        if (s.getParentId() != null && s.getParentId() != 0) {
            SubjectDTO parent = dtoMap.get(s.getParentId());
            if (parent != null) {
                parent.getChildren().add(dto);
            }
        }

        // 检查是否为目标根节点
        if (targetRootIds.contains(s.getId())) {
            resultRoots.add(dto);
        }
    }

    // 4. 递归汇总统计数据
    for (SubjectDTO root : resultRoots) {
        aggregateCounts(root);
    }

    return resultRoots;
}
```

#### 2. Scope 多对多关系过滤

在 `BookManage.vue` 中实现动态过滤：

```javascript
const filterSubjectTreeForCascader = (tree) => {
    const nodeMap = new Map()

    // 扁平化所有节点
    const collectNodes = (node) => {
        nodeMap.set(node.id, { ...node, children: [] })
        if (node.children && node.children.length > 0) {
            node.children.forEach(child => collectNodes(child))
        }
    }
    tree.forEach(node => collectNodes(node))

    // 构建树：基于 scope 字段动态分配子节点
    const buildTree = (examSpecId) => {
        const children = []

        nodeMap.forEach(node => {
            // 情况1：基于 scope 的多对多关系（Level 2/3 学科）
            if ((node.level === '2' || node.level === '3') && node.parentId === 0) {
                const belongsToSpec = !node.scope || node.scope.split(',')
                    .map(s => s.trim()).includes(examSpecId.toString())

                if (belongsToSpec) {
                    children.push(childNode)
                }
            }
            // 情况2：传统的 parent_id 关系
            else if (node.parentId === examSpecId) {
                children.push(childNode)
            }
        })

        return children
    }
}
```

#### 3. 虚拟分组节点创建

在 `getManageTree()` 中实现：

```java
public List<SubjectDTO> getManageTree() {
    // 1. 创建虚拟分组
    SubjectDTO englishGroup = createVNode(-2, "英语");
    SubjectDTO mathGroup = createVNode(-3, "数学");

    Map<Integer, SubjectDTO> vMap = new HashMap<>();
    vMap.put(-2, englishGroup);
    vMap.put(-3, mathGroup);

    // 2. 处理 Level 1 节点
    for (SubjectDTO dto : allDTOs) {
        if ("1".equals(dto.getLevel())) {
            int vId = getMathOrEnglishVId(dto.getId());
            if (vId != 0) {
                // 挂载到虚拟分组下
                vMap.get(vId).getChildren().add(dto);
            }
        }
    }

    // 3. 处理 scope 节点（parent_id=0 且有 scope）
    for (SubjectDTO dto : allDTOs) {
        if ((dto.getParentId() == null || dto.getParentId() == 0)
            && StringUtils.hasText(dto.getScope())) {
            String[] scopeIds = dto.getScope().split(",");
            for (String scopeIdStr : scopeIds) {
                Integer scopeId = Integer.parseInt(scopeIdStr.trim());
                SubjectDTO parentSpec = dtoMap.get(scopeId);
                if (parentSpec != null) {
                    parentSpec.getChildren().add(cloneNode);
                }
            }
        }
    }
}
```

#### 4. 递归排序

每层按 sort 字段排序，递归处理子节点：

```java
private void sortTree(List<SubjectDTO> nodes) {
    if (nodes == null || nodes.isEmpty()) {
        return;
    }

    // 对当前层级的节点按 sort 排序（sort 为 null 的排在最后）
    nodes.sort((a, b) -> {
        Integer sortA = a.getSort() != null ? a.getSort() : 9999;
        Integer sortB = b.getSort() != null ? b.getSort() : 9999;
        return sortA.compareTo(sortB);
    });

    // 递归排序子节点
    for (SubjectDTO node : nodes) {
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            sortTree(node.getChildren());
        }
    }
}
```

### 拖拽排序实现

#### 技术栈

Element Plus `el-tree` 组件的 `draggable` 属性。

#### 拖拽逻辑

```javascript
const allowDrop = (draggingNode, dropNode, type) => {
  if (type === 'prev' || type === 'next') {
    return draggingNode.data.parentId === dropNode.data.parentId
  }
  return true  // inner 类型允许
}

const handleNodeDrop = async (draggingNode, dropNode, dropType, event) => {
  const updates = []
  let newParentId, newSort

  if (dropType === 'inner') {
    newParentId = dropNode.data.id
    newSort = dropNode.data.children?.length || 0
  } else if (dropType === 'prev' || dropType === 'next') {
    newParentId = dropNode.data.parentId || 0
    const siblings = dropNode.parent.data.children || dropNode.parent.data
    const index = siblings.findIndex(node => node.id === dropNode.data.id)
    newSort = dropType === 'prev' ? index : index + 1
  }

  updates.push({
    id: draggingNode.data.id,
    parentId: newParentId,
    sort: newSort
  })

  await request.post('/subject/batch-update-sort', updates)
  await loadTree()
}
```

### ECharts 雷达图实现

#### 场景

用户学习统计可视化，展示正确率、覆盖度、活跃度三个维度。

#### ECharts 配置

```javascript
const option = {
  radar: {
    indicator: [
      { name: '高数', max: 100 },
      { name: '线代', max: 100 },
      { name: '概率', max: 100 }
    ]
  },
  series: [{
    type: 'radar',
    data: [
      {
        value: [85, 70, 90],  // 正确率
        name: '正确率'
      },
      {
        value: [60, 50, 75],  // 覆盖度
        name: '覆盖度'
      },
      {
        value: [40, 30, 50],  // 活跃度
        name: '活跃度'
      }
    ]
  }]
}
```

### KaTeX 数学公式渲染

#### 后端存储

题目内容和解析支持 LaTeX 语法，存储为纯文本。

#### 前端渲染

1. **安装依赖**: `npm install katex`
2. **导入样式**: `import 'katex/dist/katex.min.css'`
3. **渲染公式**:

```vue
<template>
    <div v-html="renderedContent" class="katex-content"></div>
</template>

<script setup>
import katex from 'katex'

const renderedContent = computed(() => {
  return question.content.replace(/\$\$(.*?)\$\$/g, (match, formula) => {
    return katex.renderToString(formula, {
      throwOnError: false,
      displayMode: true  // 块级公式
    })
  }).replace(/\$(.*?)\$/g, (match, formula) => {
    return katex.renderToString(formula, {
      throwOnError: false,
      displayMode: false  // 行内公式
    })
  })
})
</script>
```

#### 公式类型

- **块级公式**: `$$...$$` → 独立一行，居中显示
- **行内公式**: `$...$` → 嵌入文本中显示

---

## 开发规范

### 代码规范

#### 后端

1. **命名规范**:
   - 类名：大驼峰（PascalCase）
   - 方法名、变量名：小驼峰（camelCase）
   - 常量：全大写下划线分隔（UPPER_SNAKE_CASE）

2. **注释规范**:
   - 类和方法添加 JavaDoc 注释
   - 复杂逻辑添加行内注释

3. **异常处理**:
   - 使用 `GlobalExceptionHandler` 统一处理异常
   - 业务异常抛出 `BusinessException`

#### 前端

1. **命名规范**:
   - 组件名：大驼峰（PascalCase）
   - 文件名：短横线分隔（kebab-case）
   - 变量名、函数名：小驼峰（camelCase）

2. **组件规范**:
   - 使用 Vue 3 Composition API
   - 单文件组件结构：`<template>` → `<script setup>` → `<style scoped>`

3. **样式规范**:
   - 使用 scoped 样式避免污染
   - 优先使用 Element Plus 主题变量

### 认证与授权

#### 前端

- 依赖 `localStorage` (`role`, `userInfo`) 进行路由权限检查
- 路由守卫拦截未授权访问

#### 后端

- 使用 Spring Security 进行安全控制
- JWT Token 机制（待实现）

### 环境配置

#### 开发环境

- API 代理: `request.js` 中硬编码 `baseURL` 为 `http://localhost:8081`
- 数据库: MySQL 8.0 本地实例

#### 生产环境

- 需配置环境变量
- API URL 替换为生产环境地址

### JSON 处理

- 后端使用 `JacksonTypeHandler` 处理复杂字段（如 `Question` 实体中的 `options`, `tags`）
- 自动将 JSON 字符串映射为 Java List

### 图标系统

- 使用 SVG 图标 (`assets/icons/`)
- 通过 `vite-svg-loader` 加载，需在导入时添加 `?url` 后缀
- 利用 CSS `filter` 属性实现图标颜色动态切换

```javascript
import icon from '@/assets/icons/icon.svg?url'
```

### 前端请求拦截器

```javascript
// request.js
import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: 'http://localhost:8081',
  timeout: 10000
})

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data

    if (res.code === 200) {
      return res.data  // 自动解包 data
    } else {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
  },
  error => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
```

### 统一响应格式

#### Result.java

```java
public class Result<T> {
    private Integer code; // 200 成功，500 失败
    private String msg;   // 提示信息
    private T data;       // 具体数据

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }
}
```

#### 响应示例

```json
// 成功响应
{
  "code": 200,
  "msg": "成功",
  "data": {
    "id": 1,
    "name": "高数"
  }
}

// 失败响应
{
  "code": 500,
  "msg": "题目不存在",
  "data": null
}
```
