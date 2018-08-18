package com.jd.cis.common.exception;

/**
 * @author yangwang【yangwang@jd.com】
 * @date 2018/8/13 15:03
 */
public class JsonException extends RuntimeException {

    public JsonException(String msg) {
        super(msg);
    }

    public JsonException(String msg, Throwable e) {
        super(msg, e);
    }

}
