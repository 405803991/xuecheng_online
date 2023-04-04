package xjtu.stu.content.api;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xjtu.stu.base.model.PageParams;
import xjtu.stu.base.model.PageResult;
import xjtu.stu.content.model.dto.QueryCourseParamsDto;
import xjtu.stu.content.model.po.CourseBase;

@RestController
public class CourseBaseInfoController {

    @RequestMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParams) {

        return null;
    }


}
