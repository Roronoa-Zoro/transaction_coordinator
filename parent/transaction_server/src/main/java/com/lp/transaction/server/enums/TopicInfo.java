package com.lp.transaction.server.enums;

/**
 * Created by 123 on 2016/8/2.
 */
public enum TopicInfo {
    UnkownTopic("test", "_unknown"),
    CommitTopic("test", "_commit"),
    RollbackTopic("test", "_rollback");

    private String topic;
    private String tag;

    TopicInfo(String topic, String tag) {
        this.topic = topic;
        this.tag = tag;
    }

    public String getTopic() {
        return topic;
    }

    public String getTag() {
        return tag;
    }
}
