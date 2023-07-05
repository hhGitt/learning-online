package com.learning.media.service;

import com.learning.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author HH
 * @version 1.0
 * @description 任务处理
 * @date 2023/7/5 17:53
 **/
public interface MediaFileProcessService {

    /**
     * 获取待处理任务
     *
     * @param shardTotal 分片序号
     * @param shardIndex 分片总数
     * @param count      获取记录数
     * @return 待处理任务
     */
    List<MediaProcess> selectListByShardIndex(int shardTotal, int shardIndex, int count);

    /**
     * 开启一个任务
     *
     * @param id 任务id
     * @return true开启任务成功，false开启任务失败
     */
    public boolean startTask(long id);

    /**
     * 保存任务结果
     *
     * @param taskId   任务id
     * @param status   任务状态
     * @param fileId   文件id
     * @param url      url
     * @param errorMsg 错误信息
     */
    void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg);


}
