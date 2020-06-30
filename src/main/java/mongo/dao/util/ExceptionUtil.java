package mongo.dao.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常工具类
 */
public class ExceptionUtil {

    /**
     * 异常转String
     * @param e 异常
     * @return String
     */
    public static String toString(Throwable e){
        StringWriter exceptionWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(exceptionWriter);
        e.printStackTrace(printWriter);
        return exceptionWriter.toString();
    }

}
