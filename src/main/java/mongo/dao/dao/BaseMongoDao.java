package mongo.dao.dao;

import com.mongodb.client.result.UpdateResult;
import mongo.dao.condition.BaseMongoCondition;
import mongo.dao.entity.Page;
import mongo.dao.entity.PageField;
import mongo.dao.entity.domain.BaseDomain;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.io.Serializable;
import java.util.List;

/**
 * MongoDao
 *
 * @author dddrecall
 * @date 2017年3月11日
 * @comment
 */
public interface BaseMongoDao {

    /**
     * 最大list大小
     */
    int MAX_LIST_SIZE = 1024 * 2;

    /**
     * 获取mongoTemplate
     *
     * @return MongoTemplate
     */
    MongoTemplate getMongoTemplate();

    /**
     * 增加实例
     *
     * @param doc domain
     */
    <E extends BaseDomain> E add(E doc);

    /**
     * 增加实例List
     *
     * @param list List
     */
    void addList(List<? extends BaseDomain> list);

    /**
     * 修改实例
     *
     * @param doc domain
     */
    <E extends BaseDomain> E update(E doc);

    /**
     * 通过主键获得对象
     *
     * @param c  clazz
     * @param id id
     * @return T
     */
    <T> T get(Class<T> c, Serializable id);

    /**
     * 查询数据列表
     *
     * @param condition 查询条件
     * @return List
     */
    <E> List<E> queryList(BaseMongoCondition condition);

    /**
     * 查询分页列表
     *
     * @param condition 查询条件
     * @return Page
     */
    <E> Page<E> queryPage(BaseMongoCondition condition);

    /**
     * 通过条件查询创建时间降序的那一条记录
     *
     * @param condition 查询条件
     * @return domain list
     */
    <T> T queryOne(BaseMongoCondition condition);

    /**
     * 查询List
     *
     * @param condition  查询条件
     * @param totalCount 最大数
     * @return domain list
     */
    <E> List<E> queryList(BaseMongoCondition condition, int totalCount);

    /**
     * 查询记录总数
     *
     * @param condition 查询条件
     * @return long
     */
    long queryCount(BaseMongoCondition condition);

    /**
     * 根据id更改并查询
     *
     * @param id          domain id的值
     * @param update      更新字段
     * @param returnNew   是否返回对象
     * @param entityClazz domain.class
     * @return domain
     */
    <E> E findAndModify(String id, Update update, boolean returnNew, Class<E> entityClazz);

    /**
     * 根据id更改并查询
     *
     * @param id          domain id的值
     * @param update      更新字段
     * @param options     选项
     * @param entityClazz domain.class
     * @return domain
     */
    <E> E findAndModify(String id, Update update, FindAndModifyOptions options, Class<E> entityClazz);

    /**
     * 根据id更改并查询
     *
     * @param query       查询条件
     * @param update      更新字段
     * @param returnNew   是否返回对象
     * @param entityClazz domain.class
     * @return domain
     */
    <E> E findAndModify(Query query, Update update, boolean returnNew, Class<E> entityClazz);

    /**
     * upsert
     *
     * @param id          domain id的值
     * @param update      更新字段
     * @param entityClazz domain.class
     * @return domain
     */
    UpdateResult updateOrInsert(String id, Update update, Class<?> entityClazz);

    /**
     * upsert 只能更新单行
     *
     * @param query       查询条件
     * @param update      更新字段
     * @param entityClazz 对应domain.class
     * @return domain
     */
    UpdateResult updateOrInsert(Query query, Update update, Class<?> entityClazz);

    /**
     * 更新第一条
     *
     * @param id          主键
     * @param update      更新字段
     * @param entityClazz 对应的domain.class
     */
    UpdateResult update(String id, Update update, Class<?> entityClazz);

    /**
     * 更新第一条
     *
     * @param query       查询条件
     * @param update      更新字段
     * @param entityClazz 对应的domain.class
     */
    UpdateResult update(Query query, Update update, Class<?> entityClazz);

    /**
     * 更新第一条
     *
     * @param query       查询条件
     * @param update      更新字段
     * @param entityClazz 对应的domain.class
     */
    UpdateResult updateFirst(Query query, Update update, Class<?> entityClazz);

    /**
     * 更新多条
     *
     * @param query       查询条件
     * @param update      更新字段
     * @param entityClazz 对应的domain.class
     */
    UpdateResult updateMulti(Query query, Update update, Class<?> entityClazz);

    /**
     * 递增集合的某个文档的某个属性
     *
     * @param id          文档ID
     * @param field       属性名
     * @param val         增加个数
     * @param entityClazz 集合Class
     * @return 结果
     */
    UpdateResult increase(String id, String field, int val, Class<? extends BaseDomain> entityClazz);

    /**
     * 聚合查询个数
     *
     * @param matchOperation 匹配
     * @param groupOperation 分组
     * @param entityClazz    集合Class
     * @return 个数
     */
    long queryCount(MatchOperation matchOperation, GroupOperation groupOperation, Class<? extends BaseDomain> entityClazz);

    /**
     * 聚合分页查询
     *
     * @param matchOperation 匹配条件
     * @param groupOperation 分组
     * @param sortOperation  排序
     * @param page           页码、页大小
     * @param entityClazz    集合Class
     * @param resultClass    结果Class
     * @param <T>            结果泛型
     * @return 分页结果
     */
    <T> Page<T> queryPage(MatchOperation matchOperation, GroupOperation groupOperation, SortOperation sortOperation, PageField page, Class<? extends BaseDomain> entityClazz, Class<T> resultClass);

}
