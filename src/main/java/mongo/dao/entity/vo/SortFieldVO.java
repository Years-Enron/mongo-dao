package mongo.dao.entity.vo;

import lombok.Data;

/**
 * 排序字段
 *
 * @author recall
 * @date 2018/4/5
 */
@Data
public class SortFieldVO {

    /**
     * 排序字段
     */
    private String field;

    /**
     * 排序类型ASC,DSC ESORT
     */
    private String order;

}
