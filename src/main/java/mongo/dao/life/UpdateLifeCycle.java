package mongo.dao.life;

import mongo.dao.entity.domain.BaseDomain;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * 更新生命周期
 */
public interface UpdateLifeCycle {

    /**
     * 更新之前处理
     *
     * @param query       更新条件
     * @param update      更新属性
     * @param entityClazz 更新实体
     */
    void before(Query query, Update update, Class<?> entityClazz);

    /**
     * 更新之前处理
     *
     * @param domain 更新之前处理
     */
    <E extends BaseDomain> void before(E domain);
}