package com.lp.transaction.client.enums;

/**
 * Created by 123 on 2016/8/2.
 */
public enum TransactionState {
    UNKNOWN(1),
    COMMIT(2),
    ROLLBACK(3);
    private int state;

    TransactionState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
