package com.lp.transaction.server.enums;

/**
 * Created by 123 on 2016/8/3.
 */
public enum TrxMsgSendResState {
    SEND_SUCCESS(1, "发送成功"),
    SEND_FAILURE(2, "发送异常"),
    SEND_WITHOUT_MSG(3,"没有消息");

    private int state;
    private String desc;

    TrxMsgSendResState(int state, String desc) {
        this.state = state;
        this.desc = desc;
    }

    public int getState() {
        return state;
    }

    public String getDesc() {
        return desc;
    }
}
