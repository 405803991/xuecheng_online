package com.xuecheng.media.service.jobhandler;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.xuecheng.base.utils.Mp4VideoUtil;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileProcessService;
import com.xuecheng.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
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


@Slf4j
@Component
public class VideoTask {

    @Autowired
    MediaFileProcessService mediaFileProcessService;

    @Autowired
    MediaFileService mediaFileService;

    @Value("${videoprocess.ffmpegpath}")
    String ffmpegpath;


    /**
     * 视频处理任务
     */
    @XxlJob("videoJobHandler")
    public void videoJobHandler() throws Exception {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex(); // 执行器序号， start from 0
        int shardTotal = XxlJobHelper.getShardTotal(); // 执行器总数

        // cpu核心数
        int processors = Runtime.getRuntime().availableProcessors();

        // 查询任务
        List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal, 4);
        int size = mediaProcessList.size();
        if (size <= 0) {
            log.debug("任务数不足");
            return;
        }
        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        // 让每个线程执行完再结束该方法，才能进行下一次调度。
        CountDownLatch countDownLatch = new CountDownLatch(size);
        mediaProcessList.forEach(mediaProcess -> {
            // 任务加入线程池
            executorService.execute(()->{
                try {
                    // 任务id和文件id
                    Long taskId = mediaProcess.getId();
                    String fileId = mediaProcess.getFileId();
                    // 开启任务
                    boolean b = mediaFileProcessService.startTask(taskId); // 数据库乐观锁
                    if (!b) {
                        log.debug("抢占任务失败，任务id{}", taskId);
                        return;
                    }
                    // minio桶
                    String bucket = mediaProcess.getBucket();
                    String objName = mediaProcess.getFilePath();

                    // 下载视频到本地处理
                    File file = mediaFileService.downloadFileFromMinIO(bucket, objName);
                    if (file == null) {
                        log.debug("下载视频出错， 任务id{}, bucket_id{}, object_name{}", taskId, bucket, objName);
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "下载视频出错");
                        return;
                    }
                    // 执行视频转码
                    // 原avi视频路径
                    String absolutePath = file.getAbsolutePath();
                    // 转换后名称
                    String mp4_name = fileId + ".mp4";
                    // 创建临时文件
                    File mp4File = null;
                    try {
                        mp4File = File.createTempFile("minio", ".mp4");
                    } catch (IOException e) {
                        log.debug("创建临时文件异常， {}", e.getMessage());
                        // 保存任务处理失败的结果
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "创建临时文件异常");
                        return;
                    }
                    // 转换后mp4文件路径
                    String mp4_path = mp4File.getAbsolutePath();
                    // 创建工具对象
                    Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpegpath, absolutePath, mp4_name, mp4_path);
                    // 开始视频转码
                    String result = mp4VideoUtil.generateMp4();
                    if (!result.equals("success")) {
                        log.debug("视频转码失败， 原因：{}", result);
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, result);
                        return;
                    }
                    // 转码成功，上传到minio
                    // 获取mp4文件的url
                    String objectName = getFilePath(fileId, ".mp4");
                    String url = "/" + bucket + "/" + objectName;
                    boolean b1 = mediaFileService.addMediaFilesToMinIO(mp4_path, "video/mp4", bucket, objName);
                    if (!b1) {
                        log.error("上传到minio失败， taskid:{}", taskId);
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "上传到minio失败");
                    }
                    // 保存结果
                    mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "2", fileId, url, null);
                } finally {
                    // 计数器-1
                    countDownLatch.countDown();
                }
            });
        });

        countDownLatch.await(30, TimeUnit.MINUTES);

    }

    private String getFilePath(String fileMd5,String fileExt){
        return   fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }


}
