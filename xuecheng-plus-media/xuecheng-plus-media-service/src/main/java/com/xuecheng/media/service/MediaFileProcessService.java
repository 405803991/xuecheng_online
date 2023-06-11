package com.xuecheng.media.service;

import com.xuecheng.media.model.po.MediaProcess;

import java.util.List;

public interface MediaFileProcessService {

    public List<MediaProcess> getMediaProcessList(int sharedIndex, int sharedTotal, int count);

    public boolean startTask(Long id);

    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg);


}
