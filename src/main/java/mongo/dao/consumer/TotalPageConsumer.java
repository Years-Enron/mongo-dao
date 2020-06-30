package mongo.dao.consumer;

import mongo.dao.entity.Page;

@FunctionalInterface
public interface TotalPageConsumer<T> {

    /**
     * 分页信息，只调用一次
     *
     * @param page 分页
     */
    default void page(Page<T> page) {
    }

    void accept(T t);

}
