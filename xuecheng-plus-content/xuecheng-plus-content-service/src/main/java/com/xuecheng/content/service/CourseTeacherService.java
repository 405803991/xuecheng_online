package com.xuecheng.content.service;

import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

public interface CourseTeacherService {
    /**
     * 根据课程ID查询教师
     * @return
     */
    List<CourseTeacher> getCourseTeacher(Long courseId);

    CourseTeacher addCourseTeacher(CourseTeacher courseTeacher);

    CourseTeacher updateCourseTeacher(CourseTeacher courseTeacher);

    void deleteCourseTeacher(Long courseId, Long teacherId);
}
