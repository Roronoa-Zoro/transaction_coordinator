package com.lp.transaction.server.msg.converter;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lp.transaction.server.constants.TransactionConstant;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

/**
 * Created by 123 on 2016/8/2.
 */
@Slf4j
public class DefaultMessageConverter<T> implements MessageConverter<T> {
    private Class<T> clz;

    public DefaultMessageConverter(Class<T> clz) {
        this.clz = clz;
    }

    public Class<T> getClz() {
        return clz;
    }

    public void setClz(Class<T> clz) {
        this.clz = clz;
    }

    @Override
    public T convertFromByte(byte[] content) throws UnsupportedEncodingException {
        String hrJsonString = new String(content, TransactionConstant.CHARSET_UTF8);
        log.debug("事务处理的消息:{}.", hrJsonString);
        return JSON.parseObject(hrJsonString, clz);
    }

    @Override
    public T convertFromString(String content) {
        return JSON.parseObject(content, clz);
    }
}
