package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;

/**
 * @author SheYue
 */
public interface CoursePublishService {

    /**
     * @description 获取课程预览信息
     * @param courseId 课程ID
     */
    public CoursePreviewDto getCoursePreviewInfo(Long courseId);

    public void commitAudit(Long companyId, Long courseId);

    public void publish(Long companyId, Long courseId);


}
