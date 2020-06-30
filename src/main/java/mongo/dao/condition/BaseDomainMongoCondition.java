package mongo.dao.condition;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import mongo.dao.exception.BaseException;
import mongo.dao.util.RegexUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * BaseDomain的Mongo查询条件
 *
 * @author recall
 */
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class BaseDomainMongoCondition extends BaseMongoCondition {

    /**
     * id
     */
    private String id;

    /**
     * id不为
     */
    private String notId;

    /**
     * in id List
     */
    private Collection<String> inId;

    /**
     * 创建人id
     */
    private String createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 创建人id集合
     */
    private Collection<String> inCreateUserId;

    /**
     * 创建人名称集合
     */
    private Collection<String> inCreateUserName;

    /**
     * 模糊搜索创建人名称
     */
    private String likeCreateUserName;

    /**
     * 创建时间-大于等于
     */
    private Date gtEqCreateDate;

    /**
     * 创建时间-小于等于
     */
    private Date ltEqCreateDate;

    /**
     * 是否删除
     */
    private Boolean deleted = false;

    /**
     * 范围时间类型（必须是大小写字母）
     */
    private String timeRange;

    /**
     * 范围时间开始
     */
    private Date timeRangeStart;

    /**
     * 范围时间结束
     */
    private Date timeRangeEnd;

    @JsonIgnore
    @JSONField(deserialize = false)
    private List<Field> extendFields;

    @Override
    public Query getQuery() {
        Criteria criteria = new Criteria();

        if (deleted != null) {
            criteria.and("deleted").is(deleted);
        }

        if (StringUtils.isNotBlank(id) || CollectionUtils.isNotEmpty(inId) ||
                StringUtils.isNotBlank(notId)) {
            Criteria idCriteria = criteria.and("id");
            if (StringUtils.isNotBlank(id)) {
                idCriteria.is(id);
            }
            if (CollectionUtils.isNotEmpty(inId)) {
                idCriteria.in(inId);
            }
            if (StringUtils.isNotBlank(notId)) {
                idCriteria.ne(notId);
            }
        }

        if (StringUtils.isNotBlank(createUserId) || CollectionUtils.isNotEmpty(inCreateUserId)) {
            Criteria createUserIdCriteria = criteria.and("createUserId");
            if (StringUtils.isNotBlank(createUserId)) {
                createUserIdCriteria.is(createUserId);
            }
            if (CollectionUtils.isNotEmpty(inCreateUserId)) {
                createUserIdCriteria.in(inCreateUserId);
            }
        }

        if (StringUtils.isNotBlank(createUserName) || CollectionUtils.isNotEmpty(inCreateUserName) || StringUtils.isNotBlank(likeCreateUserName)) {
            Criteria createUserNameCriteria = criteria.and("createUserName");
            if (StringUtils.isNotBlank(createUserName)) {
                createUserNameCriteria.is(createUserName);
            }
            if (CollectionUtils.isNotEmpty(inCreateUserName)) {
                createUserNameCriteria.in(inCreateUserName);
            }
            if (StringUtils.isNotBlank(likeCreateUserName)) {
                createUserNameCriteria.regex(String.format(".*?%s.*", likeCreateUserName));
            }
        }

        if (gtEqCreateDate != null || ltEqCreateDate != null) {
            Criteria createDateCriteria = criteria.and("createDate");
            if (gtEqCreateDate != null) {
                createDateCriteria.gte(gtEqCreateDate);
            }
            if (ltEqCreateDate != null) {
                createDateCriteria.lte(ltEqCreateDate);
            }
        }

        if (StringUtils.isNotBlank(timeRange)) {
            if (!RegexUtil.checkProperty(timeRange)) {
                throw new BaseException(String.format("异常时间属性[%s]", timeRange));
            }
            if ((timeRangeStart != null || timeRangeEnd != null)) {
                Criteria timeTypeCriteria = criteria.and(timeRange);
                if (timeRangeStart != null) {
                    timeTypeCriteria.gte(timeRangeStart);
                }
                if (timeRangeEnd != null) {
                    timeTypeCriteria.lte(timeRangeEnd);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(extendFields)) {
            for (Field extendField : extendFields) {
                String name = extendField.getName();
                Field.Operator operator = extendField.getOperator();
                Object value = extendField.getValue();
                switch (operator) {
                    case EQ:
                        criteria.and(name).is(value);
                        break;
                    case IN:
                        criteria.and(name).in((Collection) value);
                        break;
                    default:
                        throw new BaseException("未实现的操作器");
                }
            }
        }

        Query query = new MongoQuery();
        dynamicCondition(query, criteria);
        query.addCriteria(criteria);
        return query;
    }

    /**
     * 动态条件
     *
     * @param query    query
     * @param criteria criteria
     */
    public abstract void dynamicCondition(Query query, Criteria criteria);

}
