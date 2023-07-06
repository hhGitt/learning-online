package com.learning.media.service.jobhandler;

import com.learning.base.utils.Mp4VideoUtil;
import com.learning.media.mapper.MediaProcessMapper;
import com.learning.media.model.po.MediaProcess;
import com.learning.media.service.MediaFileProcessService;
import com.learning.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author HH
 * @version 1.0
 * @description
 * @date 2023/7/5 18:34
 **/

@Slf4j
@Component
public class VideoTask {
    @Autowired
    MediaFileProcessService mediaFileProcessService;
    @Autowired
    MediaFileService mediaFileService;

    @Value("${videoprocess.ffmpegpath}")
    String ffmpeg_path;

    /**
     * 视频处理任务
     */
    @XxlJob("videoJobHandler")
    public void videoJobHandler() throws Exception {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();  // 执行器序号，从0开始
        int shardTotal = XxlJobHelper.getShardTotal();  // 执行器总数
        // 确定cpu核心数
        int processors = Runtime.getRuntime().availableProcessors();
        // 查询任务
        List<MediaProcess> mediaProcesses = mediaFileProcessService.selectListByShardIndex(shardTotal, shardIndex, processors);
        // 创建线程池
        int size = mediaProcesses.size();
        log.debug("取到视频处理任务数:" + size);
        if (size <= 0) {
            return;
        }
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        // 使用计数器
        CountDownLatch countDownLatch = new CountDownLatch(size);
        mediaProcesses.forEach(mediaProcess -> {
            // 将任务加入线程池
            executorService.execute(() -> {
                try {
                    Long taskId = mediaProcess.getId();
                    // fileId就是md5
                    String fileId = mediaProcess.getFileId();
                    // 开启任务
                    boolean b = mediaFileProcessService.startTask(taskId);
                    if (!b) {
                        log.debug("抢占任务失败,任务id:{}", taskId);
                        // 保存失败结果
                        return;
                    }
                    String bucket = mediaProcess.getBucket();
                    String objectName = mediaProcess.getFilePath();
                    // 下载minio视频到本地
                    File file = mediaFileService.downloadFileFromMinIO(bucket, objectName);
                    if (file == null) {
                        log.debug("下载视频出错,任务id:{},bucket:{},objectName:{}", taskId, bucket, objectName);
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "下载视频出错");
                        return;
                    }
                    // 原avi视频路径
                    String video_path = file.getAbsolutePath();
                    // 创建临时文件作为转换后文件
                    File mp4File = null;
                    try {
                        mp4File = File.createTempFile("minio", ".mp4");
                    } catch (IOException e) {
                        log.debug("创建临时文件异常,{}", e.getMessage());
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "创建临时文件异常");
                        return;
                    }
                    // 转换后mp4文件的名称
                    String mp4_name = fileId + ".mp4";
                    // 转换后mp4路径
                    String mp4_path = mp4File.getAbsolutePath();
                    //创建工具类对象
                    Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpeg_path, video_path, mp4_name, mp4_path);
                    //开始视频转换，成功将返回success
                    String result = videoUtil.generateMp4();
                    if (!result.equals("success")) {
                        log.debug("视频转码失败,原因:{},bucket:{},objectName:{}", result, bucket, objectName);
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, result);
                        return;
                    }
                    // 上传到minio
                    boolean b1 = mediaFileService.addMediaFilesToMinIO(mp4_path, "video/mp4", bucket, getFilePathByMd5(fileId,".mp4"));
                    if (!b1) {
                        log.debug("上传mp4到minio失败,taskId:{}", taskId);
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "上传mp4到minio失败");
                        return;
                    }
                    // mp4文件url
                    String url = getFilePathByMd5(fileId, ".mp4");
                    // 保存任务状态成功
                    mediaFileProcessService.saveProcessFinishStatus(taskId, "2", fileId, url, null);
                } finally {
                    // 计数器减一
                    countDownLatch.countDown();
                }
            });
        });
        // 阻塞,指定最大限度等待时间
        countDownLatch.await(30, TimeUnit.MINUTES);
    }

    private String getFilePathByMd5(String fileMd5, String fileExt) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }

}
