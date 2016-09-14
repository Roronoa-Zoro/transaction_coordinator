package com.lp.transaction.server.coordinator;

/**
 * Created by Administrator on 2016/9/14.
 * 定义任务
 */
public interface TaskLifecycle {

    void start();

    void stop();

    void startTask();

    void stopTask();
}
