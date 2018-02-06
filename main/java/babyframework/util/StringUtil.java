package babyframework.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符串工具操作
 * Created by sihanwang on 2017/8/19.
 */
public final class StringUtil {
    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        if (str != null) {
            str = str.trim();
        }
        return StringUtils.isEmpty(str);
    }

    /**
     * 判断字符串是否非空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }


    /**
     * 分割固定格式的字符串
     */
    public static String[] splitString(String str, String separator) {
        return StringUtils.splitByWholeSeparator(str, separator);
    }

    /**
     * 根据属性获取setter方法
     * @param fieldName
     * @return
     */
    public static String getSetterMethod(String fieldName) {
        return "set" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
    }

    /**
     * 根据属性获取getter方法
     * @param fieldName
     * @return
     */
    public static String getGetterMethod(String fieldName) {
        return "get" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
    }
}