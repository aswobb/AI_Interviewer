package com.app.sns.aiproduct.util;

import java.util.Collection;
import java.util.Map;

public class EmptyUtil {

    /**
     * 判断字符串是否为空
     * @param str 待判断的字符串
     * @return true：字符串为空；false：字符串不为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 判断对象是否为null
     * @param obj 待判断的对象
     * @return true：对象为null；false：对象不为null
     */
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    /**
     * 判断数组是否为null或为空数组
     * @param array 待判断的数组
     * @return true：数组为null或为空数组；false：数组不为null且不为空数组
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断集合是否为null或为空集合
     * @param collection 待判断的集合
     * @return true：集合为null或为空集合；false：集合不为null且不为空集合
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断Map是否为null或为空Map
     * @param map 待判断的Map
     * @return true：Map为null或为空Map；false：Map不为null且不为空Map
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

}

