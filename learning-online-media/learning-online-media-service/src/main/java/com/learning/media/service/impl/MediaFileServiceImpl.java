package com.learning.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.learning.base.execption.LearningOnlineException;
import com.learning.media.mapper.MediaFilesMapper;
import com.learning.media.model.dto.UploadFileParamsDto;
import com.learning.media.model.dto.UploadFileResultDto;
import com.learning.media.service.MediaFileService;
import com.learning.base.model.PageParams;
import com.learning.base.model.PageResult;
import com.learning.media.model.dto.QueryMediaParamsDto;
import com.learning.media.model.po.MediaFiles;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/9/10 8:58
 */
@Slf4j
@Service
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    MediaFileService mediaFileServiceProxy;

    @Autowired
    MediaFilesMapper mediaFilesMapper;

    @Autowired
    MinioClient minioClient;

    // 普通文件
    @Value("${minio.bucket.files}")
    private String bucket_mediafiles;

    // 视频文件
    @Value("${minio.bucket.videofiles}")
    private String bucket_videofiles;

    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;

    }

    @Override
    public UploadFileResultDto uploadFile(Long companyId, String localFilePath, UploadFileParamsDto uploadFileParamsDto) {
        // 文件名
        String filename = uploadFileParamsDto.getFilename();
        // 得到拓展名
        String extension = filename.substring(filename.lastIndexOf("."));
        // 得到mimeType
        String mimeType = getMimeType(extension);
        String defaultFolderPath = getDefaultFolderPath();
        // 文件md5值
        String fileMd5 = getFileMd5(new File(localFilePath));
        String objectName = defaultFolderPath + fileMd5 + extension;
        boolean result = addMediaFilesToMinIO(localFilePath, mimeType, bucket_mediafiles, objectName);
        if (!result) {
            throw new LearningOnlineException("上传文件失败");
        }
        // 将文件信息保存到数据库,事务处理
        MediaFiles mediaFiles = mediaFileServiceProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_mediafiles, objectName);
        if (mediaFiles == null) {
            LearningOnlineException.cast("文件上传信息后保存失败");
        }
        // 准备返回对象
        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
        return uploadFileResultDto;
    }

    @Override
    @Transactional
    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName) {
        //从数据库查询文件
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            //拷贝基本信息
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            // 文件id
            mediaFiles.setId(fileMd5);
            // file_id
            mediaFiles.setFileId(fileMd5);
            // 机构id
            mediaFiles.setCompanyId(companyId);
            // 媒资文件访问地址
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
            // 桶
            mediaFiles.setBucket(bucket);
            // 存储路径
            mediaFiles.setFilePath(objectName);
            // 上传时间
            mediaFiles.setCreateDate(LocalDateTime.now());
            // 审核状态
            mediaFiles.setAuditStatus("002003");
            // 状态
            mediaFiles.setStatus("1");
            //保存文件信息到文件表
            int insert = mediaFilesMapper.insert(mediaFiles);
            if (insert <= 0) {
                log.error("保存文件信息到数据库失败,bucket:{},objectName:{}", bucket, objectName);
                LearningOnlineException.cast("保存文件信息失败");
                return null;
            }
        }
        return mediaFiles;
    }


    /**
     * 通过拓展名获取mimeType
     *
     * @param extension 拓展名
     * @return mimeType
     */
    private String getMimeType(String extension) {
        if (extension == null) {
            extension = "";
        }
        // 通过拓展名得到媒体资源类型mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // 通用mimeType，字节流
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    /**
     * 上传文件到minio
     *
     * @param localFilePath 文件本地路径
     * @param mimeType      设置媒体文件类型
     * @param bucket        桶
     * @param objectName    对象名
     * @return 上传状态
     */
    private boolean addMediaFilesToMinIO(String localFilePath, String mimeType, String bucket, String objectName) {
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder().bucket(bucket)
                    .filename(localFilePath)
                    .object(objectName)
                    .contentType(mimeType)
                    .build();
            minioClient.uploadObject(uploadObjectArgs);
            log.debug("上传文件到minio成功,bucket{},objectName:{}", bucket, objectName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件出错，bucket{},objectName:{},错误信息:{}", bucket, objectName, e.getMessage());

        }
        return false;
    }

    /**
     * 获取minio文件默认路径
     *
     * @return minio文件默认路径
     */
    private String getDefaultFolderPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String folder = sdf.format(new Date()).replace("-", "/") + "/";
        return folder;
    }

    /**
     * 获取文件md5值
     *
     * @param file 文件
     * @return md5值
     */
    private String getFileMd5(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            String fileMd5 = DigestUtils.md5Hex(fileInputStream);
            return fileMd5;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
