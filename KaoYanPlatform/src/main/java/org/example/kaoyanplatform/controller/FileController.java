package org.example.kaoyanplatform.controller;

import org.example.kaoyanplatform.common.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {

    // 定义头像保存的本地路径（确保该目录存在）
    private String uploadPath = System.getProperty("user.dir") + "/uploads/";

    @PostMapping("/upload")
    public Result upload(MultipartFile file) throws IOException {
        if (file.isEmpty()) return Result.error("文件不能为空");

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + suffix;

        File dest = new File(uploadPath + fileName);
        if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();

        file.transferTo(dest);

        // 返回文件的访问路径 (前端需要配置静态资源映射)
        String url = "http://localhost:8081/uploads/" + fileName;
        return Result.success(url);
    }
}