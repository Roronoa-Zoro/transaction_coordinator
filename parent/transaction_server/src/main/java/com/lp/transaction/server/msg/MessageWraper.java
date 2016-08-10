package com.lp.transaction.server.msg;

import com.lp.transaction.server.enums.TopicInfo;
import lombok.Data;

/**
 * Created by 123 on 2016/8/2.
 */
@Data
public class MessageWraper<T> {
    /**
     * 消息UUID
     */
    private String uuid;
    /**
     * 消息类型
     */
    private TopicInfo type;
    /**
     * 消息体
     */
    private T msg;
}
