package mongo.dao.result;

/**
 * 无返回值
 * @author recall
 * @date 2018/4/22
 */
public interface VoidCommand extends Command<Object> {

    /**
     * 业务执行接口
     */
    void execute();

}
