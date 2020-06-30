package mongo.dao.entity;

import lombok.Data;
import mongo.dao.entity.Page;

/**
 * 分页属性
 * @author recall
 * @date 2018/5/22
 * @comment
 */
@Data
public class PageField {

    // 页码分页
    protected int pageSize = Page.DEFAULT_PAGE_SIZE;
    protected int pageNo = Page.DEFAULT_PAGE_NO;

    // skip,limit分页
    protected Integer skip;
    protected Integer limit;

}
