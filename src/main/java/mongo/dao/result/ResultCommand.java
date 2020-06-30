package mongo.dao.result;

/**
 * 有返回值
 * @author recall
 * @date 2018/4/22
 */
public interface ResultCommand<R> extends Command<R> {

    /**
     * 业务执行接口
     * @return 业务执行返回值
     */
    R execute();

}
