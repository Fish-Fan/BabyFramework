package babyframework.bean;

import babyframework.util.CollectionUtil;

import java.util.Map;

public class Param {
    private Map<String,Object> paramMap;

    public Param(Map<String,Object> map) {
        this.paramMap = map;
    }

    /**
     * 验证参数是否为空
     * @return
     */
    public boolean isEmpty() {
        return CollectionUtil.isEmpty(paramMap);
    }

    /**
     * 返回请求参数
     * @return
     */
    public Map<String,Object> getParamMap() {
        return paramMap;
    }
}
