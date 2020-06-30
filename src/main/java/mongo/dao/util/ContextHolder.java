package mongo.dao.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 上下文
 *
 * @author dddrecall
 * @date 2017年3月27日
 * @comment
 */
public class ContextHolder {


    /**
     * sessionId key
     */
    private static final String SESSION_ID_KEY = "SESSIONID_KEY";

    /**
     * threadLocal存储
     */
    private static ThreadLocal<Map<Object, Object>> threadLocal = new ThreadLocal<>();

    /**
     * 获取map
     *
     * @return 上下文Map
     */
    private static Map<Object, Object> getContextMap() {
        Map<Object, Object> map = threadLocal.get();
        if (map == null) {
            map = new HashMap<>();
            threadLocal.set(map);
        }
        return map;
    }

    /**
     * put
     *
     * @param key key
     * @param val val
     * @return map
     */
    public static Map<Object, Object> set(Object key, Object val) {
        Map<Object, Object> contextMap = ContextHolder.getContextMap();
        contextMap.put(key, val);
        return contextMap;
    }

    /**
     * get
     *
     * @param key key
     * @return 获取
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Object key) {
        Map<Object, Object> contextMap = ContextHolder.getContextMap();
        return (T) contextMap.get(key);
    }

    /**
     * 获取sessionId
     *
     * @return sessionId
     */
    public static String getSessionId() {
        return (String) ContextHolder.get(SESSION_ID_KEY);
    }

    /**
     * 设置sessionId
     *
     * @param sessionId session
     */
    public static void setSessionId(String sessionId) {
        ContextHolder.set(SESSION_ID_KEY, sessionId);
    }

}
