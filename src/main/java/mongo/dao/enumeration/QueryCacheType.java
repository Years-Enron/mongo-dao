package mongo.dao.enumeration;

import lombok.AllArgsConstructor;

/**
 * 查询缓存类型
 */
@AllArgsConstructor
public enum QueryCacheType {

    /**
     * Redis级别
     */
    REDIS,

    /**
     * Thread级别
     */
    THREAD;

}
