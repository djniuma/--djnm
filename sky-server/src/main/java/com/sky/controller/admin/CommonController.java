package com.sky.controller.admin;


import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "通用接口")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;
    /**
     * 图片上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("图片上传")
    public Result<String> upload(MultipartFile file) {
        try {
        //设置图片名称 随机生成 + 原始图片后缀
        String originalFilename = file.getOriginalFilename(); // 原始图片名称
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")); // 获取到图片后缀
        String filename = UUID.randomUUID().toString() + suffix;

        //调用alioss上传图片
            String path = aliOssUtil.upload(file.getBytes(), filename);
            return Result.success(path);
        } catch (IOException e) {
            log.error("图片上传失败", e);
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }
}
