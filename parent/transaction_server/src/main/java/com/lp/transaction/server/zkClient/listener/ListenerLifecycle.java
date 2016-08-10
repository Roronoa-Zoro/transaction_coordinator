package com.lp.transaction.server.zkClient.listener;


public interface ListenerLifecycle {

    void startListener();
    
    /**
     * 监听关闭，停止运行
     */
    void stopListener();
    
}
