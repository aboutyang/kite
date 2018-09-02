package com.aboutyang.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 自定义异常
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class KiteException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String msg;
    private int code = 500;

    public KiteException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public KiteException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public KiteException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public KiteException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


}
