package com.lp.transaction.server.exceptions;

/**
 * Created by 123 on 2016/8/2.
 */
public class RestException extends RuntimeException {
    public RestException() {
    }

    public RestException(String message) {
        super(message);
    }

    public RestException(String message, Throwable cause) {
        super(message, cause);
    }
}
