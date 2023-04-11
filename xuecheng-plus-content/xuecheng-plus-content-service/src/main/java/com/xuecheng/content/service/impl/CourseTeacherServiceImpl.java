package com.xuecheng.content.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.introspector.BeanAccess;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {

    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    @Override
    public List<CourseTeacher> getCourseTeacher(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(queryWrapper);
        return courseTeachers;
    }

    @Override
    public CourseTeacher addCourseTeacher(CourseTeacher courseTeacher) {
        CourseTeacher teacher = new CourseTeacher();
        BeanUtils.copyProperties(courseTeacher, teacher);
        teacher.setCreateDate(LocalDateTime.now());
        courseTeacherMapper.insert(teacher);
        return courseTeacherMapper.selectById(teacher.getId());
    }

    @Override
    public CourseTeacher updateCourseTeacher(CourseTeacher courseTeacher) {
        CourseTeacher targetCourseTeacher = courseTeacherMapper.selectById(courseTeacher.getId());
        BeanUtils.copyProperties(courseTeacher, targetCourseTeacher);
        courseTeacherMapper.updateById(targetCourseTeacher);
        return courseTeacherMapper.selectById(targetCourseTeacher.getId());
    }

    @Override
    public void deleteCourseTeacher(Long courseId, Long teacherId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        queryWrapper.eq(CourseTeacher::getId, teacherId);
        courseTeacherMapper.delete(queryWrapper);
    }
}
