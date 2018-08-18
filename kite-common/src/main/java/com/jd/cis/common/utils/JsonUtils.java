package com.jd.cis.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jd.cis.common.exception.JsonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 封装 json 操作， 不对外抛出检查异常。
 *
 * @author yangwang【yangwang@jd.com】
 * @date 2018/8/13 15:04
 */
@Component
public class JsonUtils {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Object转成JSON数据
     */
    public String toJson(Object object) {
        try {
            if (object instanceof Integer || object instanceof Long || object instanceof Float ||
                    object instanceof Double || object instanceof Boolean || object instanceof String) {
                return String.valueOf(object);
            }
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new JsonException("object to json", e);
        }
    }

    /**
     * JSON数据，转成Object
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new JsonException("json to clazz", e);
        }
    }

}
