package com.lp.transaction.server.enums;

/**
 * Created by 123 on 2016/8/10.
 * 主事务记录的完成情况
 */
public enum TrxBusinessState {
    //未完成
    Processing(1),
    //完成
    Completed(2),
    //无效记录
    Invalid(3);

    private int state;

    TrxBusinessState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
