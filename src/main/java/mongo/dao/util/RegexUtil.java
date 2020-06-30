package mongo.dao.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 正则工具类
 *
 * @author dddrecall
 * @date 2017年2月17日
 * @comment
 */
public class RegexUtil {

    /**
     * 判断是否是字母
     *
     * @param str 传入字符串
     * @return 是字母返回true，否则返回false
     */
    public static boolean isAlpha(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        return str.matches("[a-zA-Z]+");
    }

    /**
     * 检测属性名
     *
     * @param property 属性名
     * @return 属性名
     */
    public static boolean checkProperty(String property) {
        char[] chars = property.toCharArray();

        for (char chr : chars) {
            if (chr >= 'a' && chr <= 'z') {
            } else if (chr >= 'A' && chr <= 'Z') {
            } else if (chr == '.' || chr == ':' || chr == '-') {
            } else if (Character.UnicodeScript.of(chr) == Character.UnicodeScript.HAN) {
            } else {
                return false;
            }
        }
        return true;
    }
}
