package com.learning.media.service;

import com.learning.base.model.PageParams;
import com.learning.base.model.PageResult;
import com.learning.media.model.dto.QueryMediaParamsDto;
import com.learning.media.model.dto.UploadFileParamsDto;
import com.learning.media.model.dto.UploadFileResultDto;
import com.learning.media.model.po.MediaFiles;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理业务类
 * @date 2022/9/10 8:55
 */
public interface MediaFileService {

    /**
     * @param pageParams          分页参数
     * @param queryMediaParamsDto 查询条件
     * @return PageResult
     * @description 媒资文件查询方法
     */
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    /**
     * 上传文件
     *
     * @param companyId           公司id
     * @param localFilePath       本地文件路径
     * @param uploadFileParamsDto 文件信息
     * @return 文件上传结果Dto
     */
    public UploadFileResultDto uploadFile(Long companyId, String localFilePath, UploadFileParamsDto uploadFileParamsDto);

    /**
     * 将文件信息添加到文件表
     *
     * @param companyId           机构id
     * @param fileMd5             文件md5值
     * @param uploadFileParamsDto 上传文件的信息
     * @param bucket              桶
     * @param objectName          对象名称
     * @return MediaFiles
     */
    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName);
}
