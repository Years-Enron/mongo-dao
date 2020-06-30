package mongo.dao.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页返回
 *
 * @param <E>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Page<E> extends PageField implements Serializable {

    private static final long serialVersionUID = 1730123421731807246L;

    public static int DEFAULT_PAGE_SIZE = 10;

    public static int DEFAULT_PAGE_NO = 1;

    private long totalCount; // 数据总数 需赋值

    private List<E> rows;

    public Page() {
        this(DEFAULT_PAGE_NO, DEFAULT_PAGE_SIZE, 0, new ArrayList<>());
    }

    public Page(PageField pageField) {
        Integer limit = pageField.getLimit();
        Integer skip = pageField.getSkip();
        if (limit != null && skip != null && limit > 0) {
            this.limit = limit;
            this.skip = skip;
            this.pageNo = 0;
            this.pageSize = 0;
        } else {
            this.limit = 0;
            this.skip = 0;
            this.pageNo = pageField.getPageNo();
            this.pageSize = pageField.getPageSize();
        }
    }

    public Page(PageField pageField, long totalCount, List<E> rows) {
        this(pageField);
        this.totalCount = totalCount;
        this.rows = rows;
    }

    public Page(int pageNo, int pageSize, long totalCount, List<E> rows) {
        super();
        this.pageSize = pageSize;
        this.pageNo = pageNo;
        this.totalCount = totalCount;
        this.rows = rows;
    }

    public Page(Page page, List<E> rows) {
        this(page);
        this.totalCount = page.getTotalCount();
        this.rows = rows;
    }

    public static int getStartOfPage(int current, int number) {
        return (current - 1) * number;
    }

    /**
     * 获得总页数
     */
    public long getTotalPage() {
        if (pageNo == 0 && pageSize == 0) {
            return 0;
        }
        return totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
    }

}
