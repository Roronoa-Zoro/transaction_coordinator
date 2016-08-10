package com.lp.transaction.server.exceptions;

/**
 * Created by 123 on 2016/8/9.
 */
public class MonitorTrxException extends RuntimeException {
    public MonitorTrxException() {
    }

    public MonitorTrxException(String message) {
        super(message);
    }

    public MonitorTrxException(String message, Throwable cause) {
        super(message, cause);
    }
}
