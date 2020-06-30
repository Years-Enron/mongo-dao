package mongo.dao.condition;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import mongo.dao.enumeration.QueryCacheType;
import mongo.dao.exception.BaseException;
import mongo.dao.util.RegexUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;

import java.io.Serializable;
import java.util.List;

/**
 * Mongo查询方法
 *
 * @author dddrecall
 * @date 2017年3月11日
 * @comment
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseMongoCondition extends BaseCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 全文索引字段
     * 使用此字段Condition需继承BaseDomainMongoCondition
     */
    private String text;

    /**
     * 排序字段
     */
    private String sortName;

    /**
     * 排序类型
     */
    private Direction sortType = Direction.DESC;

    /**
     * 多个排序字段
     */
    private List<Sort.Order> sortOrderList;

    /**
     * 是否缓存
     */
    private Boolean cache;

    /**
     * 查询缓存类型
     */
    private QueryCacheType cacheType = QueryCacheType.REDIS;

    /**
     * 去重字段
     */
    private String distinct;

    public BaseMongoCondition() {
    }

    /**
     * 获取文档class
     *
     * @return 文档Clazz
     */
    @JSONField(serialize = false)
    public abstract Class<?> getDocumentClass();

    /**
     * 获取查询参数
     *
     * @return 查询条件
     */
    @JSONField(serialize = false)
    public abstract Query getQuery();

    /**
     * 获取构建完成后的查询条件
     *
     * @return 查询条件
     */
    public Query getCompleteQuery() {
        Query query = getQuery();
        boolean isSort = false;
        String sortName = getSortName();

        if (StringUtils.isNotBlank(sortName)) {
            if (!RegexUtil.checkProperty(sortName)) {
                throw new BaseException(String.format("异常的排序属性[%s]", sortName));
            }
            isSort = true;
            query.with(Sort.by(getSortType(), sortName));
        }
        List<Sort.Order> sortOrderList = getSortOrderList();
        if (CollectionUtils.isNotEmpty(sortOrderList)) {
            isSort = true;
            for (Sort.Order order : sortOrderList) {
                String property = order.getProperty();
                if (!RegexUtil.checkProperty(property)) {
                    throw new BaseException(String.format("异常的排序属性[%s]", property));
                }
            }
            query.with(Sort.by(sortOrderList));
        }
        // 如果用了全文检索
        String text = getText();
        if (StringUtils.isNotBlank(text)) {
            // 添加全文搜索内容
            TextCriteria textCriteria = TextCriteria.forDefaultLanguage().matching(text);
            query.addCriteria(textCriteria);
            // 如果无排序，启用默认相似度排序
            if (!isSort) {
                return ((MongoQuery) query).sortByScore();
            }
        }
        return query;
    }

}
