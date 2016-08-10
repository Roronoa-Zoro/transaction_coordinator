package com.lp.transaction.client.enums;

/**
 * Created by 123 on 2016/8/2.
 */
public enum TransactionVersionState {
    Init(1),
    Processing(2),
    Done(3);

    private int version;

    TransactionVersionState(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }
}
