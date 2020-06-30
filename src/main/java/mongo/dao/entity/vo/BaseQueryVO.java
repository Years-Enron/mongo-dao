package mongo.dao.entity.vo;

import lombok.Data;
import mongo.dao.entity.Page;

import java.io.Serializable;
import java.util.List;

/**
 * 查询条件父类
 *
 * @author recall(liuzehang)
 * @date 2017年1月2日
 */
@Data
public class BaseQueryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页
     */
    public int pageNo = Page.DEFAULT_PAGE_NO;

    /**
     * 一页的大小
     */
    public int pageSize = Page.DEFAULT_PAGE_SIZE;

    /**
     * 排序属性列表
     */
    public List<SortFieldVO> sortFieldList;

}
