package mongo.dao.life;

import mongo.dao.entity.domain.BaseDomain;

/**
 * 新增生命周期
 * @author recall
 * @date 2019/7/16
 * @comment
 */
public interface InsertLifeCycle {

    /**
     * 之前
     * @param domain domain
     * @param <E> 泛型
     */
    <E extends BaseDomain> void before(E domain);
}
