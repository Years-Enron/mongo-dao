package mongo.dao.dao;

import com.google.common.collect.Lists;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import mongo.dao.condition.BaseMongoCondition;
import mongo.dao.entity.Page;
import mongo.dao.entity.PageField;
import mongo.dao.entity.domain.BaseDomain;
import mongo.dao.enumeration.QueryCacheType;
import mongo.dao.exception.BaseException;
import mongo.dao.life.InsertLifeCycle;
import mongo.dao.life.UpdateLifeCycle;
import mongo.dao.util.ContextHolder;
import mongo.dao.util.JSONUtil;
import mongo.dao.util.MathUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * MongoDao
 *
 * @author dddrecall
 * @date 2017年3月11日
 * @comment
 */
@Slf4j
@Component
public class BaseMongoDaoImpl implements BaseMongoDao {

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private InsertLifeCycle insertLifeCycle;

    @Resource
    private UpdateLifeCycle updateLifeCycle;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    @Override
    public <E extends BaseDomain> E add(E doc) {
        if (doc == null) {
            throw new BaseException(" is null");
        }
        insertLifeCycle.before(doc);
        log.info("Insert[{}],domain:{}", doc.getClass().getSimpleName(), JSONUtil.toJSON(doc));
        mongoTemplate.insert(doc);
        return doc;
    }

    @Override
    public void addList(List<? extends BaseDomain> list) {
        if (CollectionUtils.isEmpty(list)) {
            throw new IllegalArgumentException("list is null");
        }
        list.forEach((Consumer<BaseDomain>) baseDomain -> insertLifeCycle.before(baseDomain));
        mongoTemplate.insertAll(list);
    }

    @Override
    public <E extends BaseDomain> E update(E doc) {
        updateLifeCycle.before(doc);
        log.info("Update[{}],id:{},domain:{}", doc.getClass().getSimpleName(), doc.getId(), JSONUtil.toJSON(doc));
        mongoTemplate.save(doc);
        return doc;
    }

    @Override
    public <T> T get(Class<T> c, Serializable id) {
        if (id == null) {
            throw new BaseException("id is null");
        }
        log.debug("Get[{}],id:{}", c.getSimpleName(), id);
        return mongoTemplate.findById(id, c);
    }

    @Override
    public <E> List<E> queryList(BaseMongoCondition condition) {
        log.debug("queryList[{}],condition:{}", condition.getDocumentClass().getSimpleName(), JSONUtil.toJSON(condition));
        @SuppressWarnings("unchecked")
        List<E> list = (List<E>) mongoTemplate.find(condition.getCompleteQuery().limit(MAX_LIST_SIZE),
                condition.getDocumentClass());
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> Page<E> queryPage(BaseMongoCondition condition) {
        if (log.isDebugEnabled()) {
            log.debug("queryPage[{}],condition:{}", condition.getDocumentClass().getSimpleName(), JSONUtil.toJSON(condition));
        }
        long totalCount = this.queryCount(condition);
        if (totalCount <= 0) {
            return new Page<>();
        }

        // 跳过多少行数据
        int skip;
        // 最多多少行数据
        int limit;
        if (condition.getLimit() != null && condition.getSkip() != null && condition.getLimit() > 0) {
            // 精确分页
            skip = condition.getSkip();
            limit = condition.getLimit();
        } else {
            // 页码分页
            int pageNo = condition.getPageNo();
            int pageSize = condition.getPageSize();
            skip = Page.getStartOfPage(pageNo, pageSize);
            limit = pageSize;
        }

        // limit 最大不能大于MAX_LIST_SIZE
        limit = Math.min(MAX_LIST_SIZE, limit);

        Query completeQuery = condition.getCompleteQuery();
        completeQuery.skip(skip).limit(limit);

        Boolean cache = condition.getCache();
        QueryCacheType cacheType = condition.getCacheType();
        String cacheKey = null;

        List<E> list = null;
        if (cache != null && cache) {
            cacheKey = getConditionCacheKey(condition, completeQuery, "queryPage");
            if (cacheType == QueryCacheType.REDIS) {
                String resultJSON = stringRedisTemplate.opsForValue().get(cacheKey);
                if (StringUtils.isNotBlank(resultJSON)) {
                    list = (List<E>) JSONUtil.toList(resultJSON, condition.getDocumentClass());
                }
            } else if (cacheType == QueryCacheType.THREAD) {
                list = ContextHolder.get(cacheKey);
            }
        }
        if (CollectionUtils.isEmpty(list)) {
            String distinct = condition.getDistinct();
            if (StringUtils.isNotBlank(distinct)) {
                list = (List<E>) mongoTemplate.findDistinct(completeQuery, distinct, condition.getDocumentClass(), condition.getDocumentClass());
            } else {
                list = (List<E>) mongoTemplate.find(completeQuery, condition.getDocumentClass());
            }
            if (cache != null && cache) {
                if (cacheType == QueryCacheType.REDIS) {
                    stringRedisTemplate.opsForValue().setIfAbsent(cacheKey, JSONUtil.toJSON(list), MathUtil.random(10, 60), TimeUnit.SECONDS);
                } else if (cacheType == QueryCacheType.THREAD) {
                    ContextHolder.set(cacheKey, list);
                }
            }
        }
        return new Page<>(condition, totalCount, list);
    }

    /**
     * 获取条件缓存Key
     *
     * @param condition     查询条件
     * @param completeQuery 查询条件
     * @return 缓存Key
     */
    private String getConditionCacheKey(BaseMongoCondition condition, Query completeQuery, String type) {
        return condition.getDocumentClass().getSimpleName() + ":" + DigestUtils.sha256Hex(JSONUtil.toJSON(completeQuery)) + ":" + type;
    }

    @Override
    public <T> T queryOne(BaseMongoCondition condition) {
        log.debug("QueryOne[{}],condition:{}", condition.getDocumentClass().getSimpleName(), JSONUtil.toJSON(condition));
        Query completeQuery = condition.getCompleteQuery();
        @SuppressWarnings("unchecked")
        T t = (T) mongoTemplate.findOne(completeQuery, condition.getDocumentClass());
        return t;
    }

    @Override
    public <E> List<E> queryList(BaseMongoCondition condition, int totalCount) {
        log.debug("QueryList[{}],condition:{},totalCount:{}", condition.getDocumentClass().getSimpleName(), JSONUtil.toJSON(condition), totalCount);
        @SuppressWarnings("unchecked")
        List<E> list = (List<E>) mongoTemplate.find(condition.getCompleteQuery().limit(totalCount),
                condition.getDocumentClass());
        return list;
    }

    @Override
    public long queryCount(BaseMongoCondition condition) {
        if (log.isDebugEnabled()) {
            log.debug("QueryCount[{}],condition:{}", condition.getDocumentClass().getSimpleName(), JSONUtil.toJSON(condition));
        }

        Query completeQuery = condition.getCompleteQuery();
        Boolean cache = condition.getCache();
        QueryCacheType cacheType = condition.getCacheType();

        String cacheKey = null;
        Long totalCount = null;
        if (cache != null && cache) {
            // 暂存pageNo
            int pageNo = condition.getPageNo();
            // 修改pageNo为默认1
            condition.setPageNo(1);
            // 获取缓存key
            cacheKey = getConditionCacheKey(condition, completeQuery, "queryCount");
            // 恢复pageNo
            condition.setPageNo(pageNo);
            if (cacheType == QueryCacheType.REDIS) {
                String resultJSON = stringRedisTemplate.opsForValue().get(cacheKey);
                if (StringUtils.isNotBlank(resultJSON)) {
                    totalCount = Long.parseLong(resultJSON);
                }
            } else if (cacheType == QueryCacheType.THREAD) {
                totalCount = ContextHolder.get(cacheKey);
            }
        }
        if (totalCount == null) {
            totalCount = mongoTemplate.count(completeQuery, condition.getDocumentClass());
            if (cache != null && cache) {
                if (cacheType == QueryCacheType.REDIS) {
                    stringRedisTemplate.opsForValue().setIfAbsent(cacheKey, totalCount.toString(), MathUtil.random(10, 60), TimeUnit.SECONDS);
                } else if (cacheType == QueryCacheType.THREAD) {
                    ContextHolder.set(cacheKey, totalCount);
                }
            }
        }
        return totalCount;
    }

    @Override
    public <E> E findAndModify(String id, Update update, boolean returnNew, Class<E> entityClazz) {
        if (StringUtils.isBlank(id)) {
            throw new BaseException("id null");
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        return findAndModify(query, update, returnNew, entityClazz);
    }

    @Override
    public <E> E findAndModify(String id, Update update, FindAndModifyOptions options, Class<E> entityClazz) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id)).maxTime(Duration.ofMinutes(4));
        updateLifeCycle.before(query, update, entityClazz);
        log.info("FindAndModify[{}],query:{},update:{}", entityClazz.getSimpleName(), JSONUtil.toJSON(query), JSONUtil.toJSON(update));
        return mongoTemplate.findAndModify(query, update, options, entityClazz);
    }

    @Override
    public <E> E findAndModify(Query query, Update update, boolean returnNew, Class<E> entityClazz) {
        query.maxTime(Duration.ofMinutes(4));
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(returnNew);// 返回update前的文档还是update后(true)
        updateLifeCycle.before(query, update, entityClazz);
        log.info("FindAndModify[{}],query:{},update:{}", entityClazz.getSimpleName(), JSONUtil.toJSON(query), JSONUtil.toJSON(update));
        return mongoTemplate.findAndModify(query, update, options, entityClazz);
    }

    @Override
    public UpdateResult updateOrInsert(String id, Update update, Class<?> entityClazz) {
        if (StringUtils.isBlank(id)) {
            throw new BaseException("id is null");
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        return updateOrInsert(query, update, entityClazz);
    }

    @Override
    public UpdateResult updateOrInsert(Query query, Update update, Class<?> entityClazz) {
        updateLifeCycle.before(query, update, entityClazz);
        log.info("UpdateOrInsert[{}],query:{},update:{}", entityClazz.getSimpleName(), JSONUtil.toJSON(query.getQueryObject()), JSONUtil.toJSON(update.getUpdateObject()));
        return mongoTemplate.upsert(query, update, entityClazz);
    }

    @Override
    public UpdateResult update(String id, Update update, Class<?> entityClazz) {
        if (StringUtils.isBlank(id)) {
            throw new BaseException("id is null");
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        return updateFirst(query, update, entityClazz);
    }

    @Override
    public UpdateResult update(Query query, Update update, Class<?> entityClazz) {
        return updateFirst(query, update, entityClazz);
    }

    @Override
    public UpdateResult updateFirst(Query query, Update update, Class<?> entityClazz) {
        updateLifeCycle.before(query, update, entityClazz);
        log.info("UpdateFirst[{}],query:{},update:{}", entityClazz.getSimpleName(), JSONUtil.toJSON(query.getQueryObject()), JSONUtil.toJSON(update.getUpdateObject()));
        return mongoTemplate.updateFirst(query, update, entityClazz);
    }

    @Override
    public UpdateResult updateMulti(Query query, Update update, Class<?> entityClazz) {
        updateLifeCycle.before(query, update, entityClazz);
        log.info("UpdateMulti[{}],query:{},update:{}", entityClazz.getSimpleName(), JSONUtil.toJSON(query.getQueryObject()), JSONUtil.toJSON(update.getUpdateObject()));
        return mongoTemplate.updateMulti(query, update, entityClazz);
    }

    @Override
    public UpdateResult increase(String id, String field, int val, Class<? extends BaseDomain> entityClazz) {
        Update update = new Update();
        update.inc(field, val);
        log.info("increase[{}],id:{},field:{},val:{}", entityClazz.getSimpleName(), id, field, val);
        return updateOrInsert(id, update, entityClazz);
    }

    @Override
    public long queryCount(MatchOperation matchOperation, GroupOperation groupOperation, Class<? extends BaseDomain> entityClazz) {
        List<AggregationOperation> operations = Lists.newArrayListWithExpectedSize(3);
        operations.add(matchOperation);
        if (groupOperation != null) {
            operations.add(groupOperation);
        }
        operations.add(Aggregation.group().count().as("count"));
        AggregationResults<HashMap> results = mongoTemplate.aggregate(Aggregation.newAggregation(operations), entityClazz, HashMap.class);
        List<HashMap> mappedResults = results.getMappedResults();
        if (CollectionUtils.isEmpty(mappedResults)) {
            return 0;
        }
        HashMap hashMap = mappedResults.get(0);
        if (MapUtils.isEmpty(hashMap)) {
            return 0;
        }
        Object count = hashMap.get("count");
        if (!(count instanceof Number)) {
            return 0;
        }
        return ((Number) count).longValue();
    }

    @Override
    public <T> Page<T> queryPage(MatchOperation matchOperation, GroupOperation groupOperation, SortOperation sortOperation, PageField page, Class<? extends BaseDomain> entityClazz, Class<T> resultClass) {
        List<AggregationOperation> operations = Lists.newArrayListWithExpectedSize(5);
        operations.add(matchOperation);
        if (groupOperation != null) {
            operations.add(groupOperation);
        }
        if (sortOperation != null) {
            operations.add(sortOperation);
        }

        // 查询总个数
        long totalCount = queryCount(matchOperation, groupOperation, entityClazz);

        // 页码分页
        int pageNo = page.getPageNo();
        int pageSize = page.getPageSize();

        // 跳过多少行数据
        long skip = Page.getStartOfPage(pageNo, pageSize);

        // 最多多少行数据
        // limit 最大不能大于MAX_LIST_SIZE
        int limit = Math.min(MAX_LIST_SIZE, pageSize);

        operations.add(Aggregation.skip(skip));
        operations.add(Aggregation.limit(limit));

        AggregationResults<T> aggregateResult = mongoTemplate.aggregate(Aggregation.newAggregation(operations), entityClazz, resultClass);
        List<T> mappedResults = aggregateResult.getMappedResults();
        return new Page<>(pageNo, pageSize, totalCount, mappedResults);
    }

}
