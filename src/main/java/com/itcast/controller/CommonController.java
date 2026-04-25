package com.itcast.controller;

import com.itcast.properties.AliOssProperties;
import com.itcast.result.Result;
import com.itcast.utils.AliOssUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Tag(name = "通用接口", description = "文件上传等通用功能")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    @Operation(summary = "文件上传")
    public Result<String> upload(@RequestParam("files") MultipartFile file) {
        try {
            // 获取原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 生成唯一文件名
            String objectName = UUID.randomUUID().toString() + extension;

            // 上传到阿里云 OSS
            String url = aliOssUtil.upload(file.getBytes(), objectName);

            return Result.success(url);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
}
