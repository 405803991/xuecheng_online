package com.xuecheng.content.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class CoursePreviewDto {
    private CourseBaseInfoDto courseBase;

    private List<TeachplanDto> teachplans;
}
