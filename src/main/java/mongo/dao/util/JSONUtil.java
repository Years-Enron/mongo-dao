package mongo.dao.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import mongo.dao.exception.BaseException;

import java.util.List;

/**
 * JSON Util
 *
 * @author recall
 * @date 2018/4/5
 */
public class JSONUtil {

    /**
     * 对象转JSON
     * @param object 对象
     * @return JSON
     */
    public static String toJSON(Object object) {
        try {
            return JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("JSON转换异常");
        }
    }

    /**
     * JSON转对象
     * @param json JSON
     * @param clazz 对象.class实例
     * @param <T> 对象泛型
     * @return 对象
     */
    public static <T> T toObject(String json, Class<T> clazz) {
        try {
            return JSON.parseObject(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("解析JSON异常");
        }
    }

    /**
     * JSON转List
     * @param json JSON
     * @param clazz List的元素类型
     * @param <T> List的元素泛型
     * @return List
     */
    public static <T> List<T> toList(String json, Class<T> clazz) {
        return JSON.parseArray(json,clazz);
    }
}
