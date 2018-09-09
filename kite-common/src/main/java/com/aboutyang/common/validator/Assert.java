package com.aboutyang.common.validator;

import com.aboutyang.common.exception.KiteException;
import org.apache.commons.lang3.StringUtils;

/**
 * 数据校验
 */
public abstract class Assert {

    public static void isBlank(String str, String message) {
        if (StringUtils.isBlank(str)) {
            throw new KiteException(message);
        }
    }

    public static void isNull(Object object, String message) {
        if (object == null) {
            throw new KiteException(message);
        }
    }
}
