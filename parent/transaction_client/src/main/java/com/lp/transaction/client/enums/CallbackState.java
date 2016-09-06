package com.lp.transaction.client.enums;

/**
 * Created by 123 on 2016/8/4.
 */
public enum CallbackState {
    CallbackCommitSuccess(1, "回调提交成功"),
    CallbackCommitFailure(2, "回调提交失败"),
    CallbackRollbackSuccess(3, "回调回滚成功"),
    CallbackRollbackFailure(4, "回调回滚失败"),
    CallbackFailure(5, "回调失败"),
    CallbackIllegal(6, "回调非法"),
    CallbackPreCommit(7, "处于预提交阶段"),
    CallbackRollback(8, "本地事务已回滚");

    private int state;
    private String desc;

    CallbackState(int state, String desc) {
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
