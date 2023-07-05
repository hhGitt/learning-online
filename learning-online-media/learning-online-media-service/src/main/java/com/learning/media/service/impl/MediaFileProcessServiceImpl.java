package com.learning.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learning.media.mapper.MediaFilesMapper;
import com.learning.media.mapper.MediaProcessHistoryMapper;
import com.learning.media.mapper.MediaProcessMapper;
import com.learning.media.model.po.MediaFiles;
import com.learning.media.model.po.MediaProcess;
import com.learning.media.model.po.MediaProcessHistory;
import com.learning.media.service.MediaFileProcessService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author HH
 * @version 1.0
 * @description MediaFileProcessService实现
 * @date 2023/7/5 17:54
 **/
@Slf4j
@Service
public class MediaFileProcessServiceImpl implements MediaFileProcessService {

    @Autowired
    MediaProcessMapper mediaProcessMapper;
    @Autowired
    MediaFilesMapper mediaFilesMapper;
    @Autowired
    MediaProcessHistoryMapper mediaProcessHistoryMapper;

    @Override
    public List<MediaProcess> selectListByShardIndex(int shardTotal, int shardIndex, int count) {
        return mediaProcessMapper.selectListByShardIndex(shardTotal, shardIndex, count);
    }

    @Override
    public boolean startTask(long id) {
        int result = mediaProcessMapper.startTask(id);
        return result > 0;
    }

    @Override
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if (mediaProcess == null) {
            return;
        }
        // 任务执行失败
        // 更新media_file表中url
        if (status.equals("3")) {
            // 更新MediaProcess表状态
//            mediaProcess.setStatus("3");
//            mediaProcess.setFailCount(mediaProcess.getFailCount() + 1);
//            mediaProcess.setErrormsg(errorMsg);
            mediaProcessMapper.updateById(mediaProcess);
            LambdaQueryWrapper<MediaProcess> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(MediaProcess::getStatus, "3").eq(MediaProcess::getFailCount, mediaProcess.getFailCount() + 1).eq(MediaProcess::getErrormsg, errorMsg);
            mediaProcessMapper.update(mediaProcess, queryWrapper);
            return;
        }
        // 任务执行成功
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
        // 更新media_file表中url
        mediaFiles.setUrl(url);
        mediaFilesMapper.updateById(mediaFiles);
        // 更新MediaProcess表的状态
        mediaProcess.setStatus("2");
        mediaProcess.setFinishDate(LocalDateTime.now());
        mediaProcess.setUrl(url);
        mediaProcessMapper.updateById(mediaProcess);
        // 将MediaProcess表记录插入到MediaProcessHistory表中
        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtils.copyProperties(mediaProcess, mediaProcessHistory);
        mediaProcessHistoryMapper.insert(mediaProcessHistory);
        // 从mediaProcess表中删除任务
        mediaProcessMapper.deleteById(taskId);
    }
}
