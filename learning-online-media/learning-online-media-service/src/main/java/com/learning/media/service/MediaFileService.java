package com.learning.media.service;

import com.learning.base.model.PageParams;
import com.learning.base.model.PageResult;
import com.learning.base.model.RestResponse;
import com.learning.media.model.dto.QueryMediaParamsDto;
import com.learning.media.model.dto.UploadFileParamsDto;
import com.learning.media.model.dto.UploadFileResultDto;
import com.learning.media.model.po.MediaFiles;

import java.io.File;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理业务类
 * @date 2022/9/10 8:55
 */
public interface MediaFileService {

    /**
     * 根据媒资id查询文件信息
     * @param mediaId id
     * @return MediaFiles
     */
    MediaFiles getFileById(String mediaId);

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

    /**
     * 检查文件是否存在
     *
     * @param fileMd5 文件的md5
     * @return RestResponse<java.lang.Boolean> false不存在，true存在
     */
    public RestResponse<Boolean> checkFile(String fileMd5);

    /**
     * 检查分块是否存在
     *
     * @param fileMd5    文件的md5
     * @param chunkIndex 分块序号
     * @return RestResponse<Boolean> false不存在，true存在
     */
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);

    /**
     * 上传分块
     *
     * @param fileMd5            文件md5
     * @param chunk              分块序号
     * @param localChunkFilePath 分块文件本地路径
     * @return RestResponse
     */
    public RestResponse uploadChunk(String fileMd5, int chunk, String localChunkFilePath);

    /**
     * 合并分块
     *
     * @param companyId           机构id
     * @param fileMd5             文件md5
     * @param chunkTotal          分块总和
     * @param uploadFileParamsDto 文件信息
     * @return RestResponse
     */
    public RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto);

    /**
     * 从minio下载文件
     *
     * @param bucket     桶
     * @param objectName 对象名称
     * @return 下载后的文件
     */
    public File downloadFileFromMinIO(String bucket, String objectName);

    /**
     * 上传文件到minio
     *
     * @param localFilePath 文件本地路径
     * @param mimeType      设置媒体文件类型
     * @param bucket        桶
     * @param objectName    对象名
     * @return 上传状态
     */
    public boolean addMediaFilesToMinIO(String localFilePath, String mimeType, String bucket, String objectName);
}
