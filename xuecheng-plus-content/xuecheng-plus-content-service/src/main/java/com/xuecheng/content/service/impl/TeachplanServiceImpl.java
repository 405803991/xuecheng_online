package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

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

    @Override
    public void deleteTeachplan(long teachplanId) {

        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if (teachplan == null) {
            XueChengPlusException.cast("课程不存在！");
        }
        if (teachplan.getGrade().equals(1)) {
            //大章节，判断是否有小节
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getParentid, teachplanId);
            Integer count = teachplanMapper.selectCount(queryWrapper);
            if (count > 0) {
                XueChengPlusException.cast("课程计划信息还有子级信息，无法操作");
            }
            teachplanMapper.deleteById(teachplanId);
        } else {
            teachplanMapper.deleteById(teachplanId);
            LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TeachplanMedia::getTeachplanId, teachplanId);
            teachplanMediaMapper.delete(queryWrapper);
        }

    }

    @Transactional
    @Override
    public void moveTeachplan(String moveMethod, long teachplanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getStatus, 1)
                    .eq(Teachplan::getParentid, teachplan.getParentid());
        if (moveMethod.equals("moveup")) {
            queryWrapper.lt(Teachplan::getOrderby, teachplan.getOrderby())
                        .orderByDesc(Teachplan::getOrderby);
        } else if (moveMethod.equals("movedown")) {
            queryWrapper.gt(Teachplan::getOrderby, teachplan.getOrderby())
                        .orderByAsc(Teachplan::getOrderby);
        }
        queryWrapper.last("limit 1");
        Teachplan targetPlan = teachplanMapper.selectOne(queryWrapper);
        if(targetPlan == null) {
            XueChengPlusException.cast("不可再移动");
        } else {
            int order = teachplan.getOrderby();
            teachplan.setOrderby(targetPlan.getOrderby());
            targetPlan.setOrderby(order);
            teachplanMapper.updateById(targetPlan);
            teachplanMapper.updateById(teachplan);
        }
    }

    private Integer getTeachplanCount(Long courseId, Long parentid) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper = queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentid);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }
}
