package xjtu.stu.base.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PageParams {

    private Long pageNo = 1L;
    private Long pageSize = 30L;
}
