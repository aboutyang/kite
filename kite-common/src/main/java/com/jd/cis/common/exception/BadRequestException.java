package com.jd.cis.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author yangwang【yangwang@jd.com】
 * @date 2018/8/13 15:16
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends  RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}