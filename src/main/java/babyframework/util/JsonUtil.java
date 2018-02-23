package babyframework.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 依赖于fastjson
 */
public final class JsonUtil {
    private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    /**
     * 将object转换为json
     * @param object
     * @param <T>
     * @return
     */
    public static <T> String toJson(T object) {
        return JSON.toJSONString(object);
    }

    /**
     * 将json转换为object
     * @param json
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T toObject(String json,Class<T> type) {
        return JSON.parseObject(json,type);
    }


}
