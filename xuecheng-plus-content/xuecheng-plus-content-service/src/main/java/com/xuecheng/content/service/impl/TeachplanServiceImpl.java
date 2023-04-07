package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * @description 课程计划service接口实现类
 * @author Mr.M
 * @date 2022/9/9 11:14
 * @version 1.0
 */

@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Override
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        //通过课程计划id判断新增和修改
        Long teachplanId = saveTeachplanDto.getId();
        if (teachplanId == null) {
            // 新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            // 确定排序字段，找同级节点个数，排序字段就是个数+1
            Long courseId = saveTeachplanDto.getCourseId();
            Long parentid = saveTeachplanDto.getParentid();
            Integer count = getTeachplanCount(courseId, parentid);
            teachplan.setOrderby(count + 1);

            teachplanMapper.insert(teachplan);
        } else {
            // 更新
            Teachplan teachplan = teachplanMapper.selectById(teachplanId);
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
        }
    }

    private Integer getTeachplanCount(Long courseId, Long parentid) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper = queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentid);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }
}
