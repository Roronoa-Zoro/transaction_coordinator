package com.lp.transaction.server.enums;

/**
 * Created by Administrator on 2016/9/7.
 * 事务参与者类型
 */
public enum TrxParticipantType {

    TrxInitiator(1, "事务发起者/主事务"),
    TrxParticipant(2, "事务协同参与者");

    private int type;
    private String desc;

    TrxParticipantType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
