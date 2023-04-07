package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;

import java.util.List;

public class TeachplanDto extends Teachplan {

    TeachplanMedia teachplanMedia;

    List<TeachplanDto> teachPlanTreeNodes;

}
