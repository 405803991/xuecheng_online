package xjtu.stu.base.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
public class PageResult<T> implements Serializable {
    private List<T> items;

    private long counts;

    private long page;

    private long pageSize;
}
