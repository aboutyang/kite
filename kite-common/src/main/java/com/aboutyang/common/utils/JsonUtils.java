package com.aboutyang.common.utils;

import com.aboutyang.common.exception.JsonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 封装 json 操作， 不对外抛出检查异常。
 *
 * @author yangwang【yangwang@jd.com】
 * @date 2018/8/13 15:04
 */
@Component
public class JsonUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;
    private static ObjectMapper objectMapper;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        JsonUtils.applicationContext = applicationContext;

        ObjectMapper om = applicationContext.getBean(ObjectMapper.class);
        if (om != null) {
            JsonUtils.objectMapper = om;
        }else {
            JsonUtils.objectMapper = new ObjectMapper();
        }
    }


    /**
     * Object转成JSON数据
     */
    public static String toJson(Object object) {
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
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new JsonException("json to clazz", e);
        }
    }

}
