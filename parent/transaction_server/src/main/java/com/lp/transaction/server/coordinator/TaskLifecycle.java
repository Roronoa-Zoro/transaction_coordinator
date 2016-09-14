package com.lp.transaction.server.coordinator;

/**
 * Created by Administrator on 2016/9/14.
 * 定义任务,
 * start, stop 用来作为lifecycle 接口方法
 * startTask, stopTask 用来做实际任务执行的  接口方法
 */
public interface TaskLifecycle {

    void start();

    void stop();

    void startTask();

    void stopTask();
}
