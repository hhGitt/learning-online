package com.learning.media.api;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.learning.base.model.PageParams;
import com.learning.base.model.PageResult;
import com.learning.media.model.dto.QueryMediaParamsDto;
import com.learning.media.model.dto.UploadFileParamsDto;
import com.learning.media.model.dto.UploadFileResultDto;
import com.learning.media.model.po.MediaFiles;
import com.learning.media.service.MediaFileService;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理接口
 * @date 2022/9/6 11:29
 */
@Slf4j
@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
public class MediaFilesController {

    @Autowired
    MediaFileService mediaFileService;


    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto) {
        Long companyId = 1232141425L;
        return mediaFileService.queryMediaFiels(companyId, pageParams, queryMediaParamsDto);

    }

    @ApiOperation("上传图片")
    @RequestMapping(value = "/upload/coursefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadFileResultDto upload(@RequestPart("filedata") MultipartFile filedata) throws IOException {
        // 准备上传文件信息
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        // 原文件名称
        uploadFileParamsDto.setFilename(filedata.getOriginalFilename());
        // 文件大小
        uploadFileParamsDto.setFileSize(filedata.getSize());
        // 文件类型
        uploadFileParamsDto.setFileType("001001");
        // 创建一个临时文件
        File tempFile = File.createTempFile("minio", ".temp");
        filedata.transferTo(tempFile);
        // 调用service上传图片
        Long companyId = 1232141425L;
        // 文件路径
        String localFilePath = tempFile.getAbsolutePath();
        UploadFileResultDto uploadFileResultDto = mediaFileService.uploadFile(companyId, localFilePath, uploadFileParamsDto);
        return uploadFileResultDto;
    }


}
