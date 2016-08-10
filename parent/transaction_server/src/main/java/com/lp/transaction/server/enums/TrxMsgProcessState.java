package com.lp.transaction.server.enums;

/**
 * Created by 123 on 2016/8/3.
 */
public enum TrxMsgProcessState {
    Process_Not_Allowed(1, "不能处理"),
    Process_Allowed(2, "可以处理"),
    Process_Done(3, "处理完成"),
    Process_Illegal(4, "非法,不处理");

    private int status;
    private String desc;

    TrxMsgProcessState(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
