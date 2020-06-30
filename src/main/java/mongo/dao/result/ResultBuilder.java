package mongo.dao.result;

import org.by.bbs.framework.enumeration.EResultCode;
import org.by.bbs.framework.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Result构建类
 */
public class ResultBuilder implements Serializable {

    private static final long serialVersionUID = -8153590796955513243L;
    protected static Logger logger = LoggerFactory.getLogger(ResultBuilder.class);

    /**
     * 构建方法
     *
     * @param cmd 业务封装内部类
     * @param <D> 业务返回值泛型类型
     * @return DataMap
     */
    public static <D> Result<D> build(Command<D> cmd) {
        D data = null;
        Result<D> result = new Result<>();
        if (cmd instanceof ResultCommand) {
            data = ((ResultCommand<D>) cmd).execute();
        } else if (cmd instanceof VoidCommand) {
            ((VoidCommand) cmd).execute();
        }
        result.setSuccess(true);
        result.setMessage("操作成功");
        result.setData(data);
        result.setCode(EResultCode.success.code);
        return result;
    }

    /**
     * 构建方法
     *
     * @param cmd 业务封装内部类
     * @param <D> 业务返回值泛型类型
     * @return DataMap
     */
    public static <D> Result<D> doCatch(Command<D> cmd) {
        D data = null;
        Result<D> result = new Result<>();
        try {
            if (cmd instanceof ResultCommand) {
                data = ((ResultCommand<D>) cmd).execute();
            } else if (cmd instanceof VoidCommand) {
                ((VoidCommand) cmd).execute();
            }
            result.setSuccess(true);
            result.setMessage("操作成功");
            result.setData(data);
            result.setCode(EResultCode.success.code);
        } catch (Throwable e) {
            if (e instanceof BaseException) {
                result.setMessage(e.getMessage());
            } else {
                result.setMessage("系统异常");
            }
            result.setSuccess(false);
            result.setData(data);
            result.setCode(EResultCode.success.code);
        }
        return result;
    }

}
