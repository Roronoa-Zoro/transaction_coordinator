package com.lp.transaction.server.msg.converter;

import java.io.UnsupportedEncodingException;

/**
 * Created by 123 on 2016/8/2.
 */
public interface MessageConverter<T> {
     T convertFromByte(byte[] content) throws UnsupportedEncodingException;

    T convertFromString(String content);
}
