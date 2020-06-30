package mongo.dao.util;

import com.google.common.base.Preconditions;
import mongo.dao.exception.BaseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * BeanUtil
 */
public class BeanUtil {


    private static Logger logger = LoggerFactory.getLogger(BeanUtil.class);

    /**
     * 复制List
     *
     * @param srcList  来源List
     * @param dscClazz 结果List泛型类型
     * @param <T>      来源类型泛型
     * @param <R>      结果类型泛型
     * @return 结果List
     */
    public static <T, R> List<R> copyList(List<T> srcList, Class<R> dscClazz) {
        return copyList(srcList, dscClazz, src -> {
            try {
                R result = dscClazz.newInstance();
                copyProperties(src, result);
                return result;
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    /**
     * 复制List
     *
     * @param srcList               来源List
     * @param resultClazz           目标List的 Clazz
     * @param gainResultValFunction 获取返回值的回掉函数
     * @param <T>                   来源泛型类型
     * @param <R>                   返回泛型类型
     * @return 复制后的List
     */
    public static <T, R> List<R> copyList(List<T> srcList, Class<R> resultClazz, Function<T, R> gainResultValFunction) {
        Preconditions.checkNotNull(resultClazz, "resultClazz不能为空哦");
        Preconditions.checkNotNull(srcList, "来源不能为空哦");
        List<R> resultList = new ArrayList<>();
        srcList.forEach(t -> {
            R r = gainResultValFunction.apply(t);
            resultList.add(r);
        });
        return resultList;
    }
    /**
     * 拷贝对象的属性到另一个对象   参考BeanUtils.copyProperties()实现
     *
     * @param source           源对象
     * @param target           返回目标类
     * @param ignoreProperties 忽略属性名称
     * @return target
     */
    public static <T> T copyProperties(Object source, T target, String... ignoreProperties) {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");
        Class<?> targetClass = target.getClass();
        Class<?> sourceClass = source.getClass();

        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(targetClass);
        PropertyDescriptor[] sourcePds = BeanUtils.getPropertyDescriptors(sourceClass);

        List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);
        if (targetPds.length <= sourcePds.length) {
            for (PropertyDescriptor targetPd : targetPds) {
                Method writeMethod = targetPd.getWriteMethod();
                if (writeMethod == null || (ignoreList != null && ignoreList.contains(targetPd.getName()))) {
                    continue;
                }
                PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(sourceClass, targetPd.getName());
                if (sourcePd == null) {
                    continue;
                }
                Method readMethod = sourcePd.getReadMethod();
                if (readMethod == null || !ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                    continue;
                }
                copyProperties(targetPd, readMethod, writeMethod, source, target, true);
            }
        } else {
            for (PropertyDescriptor sourcePd : sourcePds) {
                Method readMethod = sourcePd.getReadMethod();
                if (readMethod == null || (ignoreList != null && ignoreList.contains(sourcePd.getName()))) {
                    continue;
                }
                PropertyDescriptor targetPd = BeanUtils.getPropertyDescriptor(targetClass, sourcePd.getName());
                if (targetPd == null) {
                    continue;
                }
                Method writeMethod = targetPd.getWriteMethod();
                if (writeMethod == null || !ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                    continue;
                }
                copyProperties(targetPd, readMethod, writeMethod, source, target, true);
            }
        }
        return target;
    }

    /**
     * 拷贝对象的属性到另一个对象 (只拷贝源对象参数值不为空的属性，如果为空则跳过)
     *
     * @param source           源对象
     * @param target           返回目标类
     * @param ignoreProperties 忽略属性名称
     * @return target
     */
    public static <T> T copyPropertiesByNotNull(Object source, T target, String... ignoreProperties) {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");
        Class<?> targetClass = target.getClass();
        Class<?> sourceClass = source.getClass();

        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(targetClass);
        PropertyDescriptor[] sourcePds = BeanUtils.getPropertyDescriptors(sourceClass);

        List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);
        if (targetPds.length <= sourcePds.length) {
            for (PropertyDescriptor targetPd : targetPds) {
                Method writeMethod = targetPd.getWriteMethod();
                if (writeMethod == null || (ignoreList != null && ignoreList.contains(targetPd.getName()))) {
                    continue;
                }
                PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(sourceClass, targetPd.getName());
                if (sourcePd == null) {
                    continue;
                }
                Method readMethod = sourcePd.getReadMethod();
                if (readMethod == null || !ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                    continue;
                }
                copyProperties(targetPd, readMethod, writeMethod, source, target, false);
            }
        } else {
            for (PropertyDescriptor sourcePd : sourcePds) {
                Method readMethod = sourcePd.getReadMethod();
                if (readMethod == null || (ignoreList != null && ignoreList.contains(sourcePd.getName()))) {
                    continue;
                }
                PropertyDescriptor targetPd = BeanUtils.getPropertyDescriptor(targetClass, sourcePd.getName());
                if (targetPd == null) {
                    continue;
                }
                Method writeMethod = targetPd.getWriteMethod();
                if (writeMethod == null || !ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                    continue;
                }
                copyProperties(targetPd, readMethod, writeMethod, source, target, false);
            }
        }
        return target;
    }

    /**
     * 复制单个属性
     * @param targetPd 目标Pd
     * @param readMethod getter
     * @param writeMethod setter
     * @param source 值的来源
     * @param target 目标的来源
     * @param nullCopy 如果值为null,是否还复制
     */
    private static void copyProperties(PropertyDescriptor targetPd, Method readMethod, Method writeMethod, Object source, Object target, boolean nullCopy) {
        try {
            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                readMethod.setAccessible(true);
            }
            Object value = readMethod.invoke(source);
            if (!nullCopy || value == null || StringUtils.isBlank(value.toString())) return;
            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                writeMethod.setAccessible(true);
            }
            writeMethod.invoke(target, value);
        } catch (Throwable ex) {
            throw new BaseException("Could not copy property '" + targetPd.getName() + "' from source to target", ex);
        }
    }


}
