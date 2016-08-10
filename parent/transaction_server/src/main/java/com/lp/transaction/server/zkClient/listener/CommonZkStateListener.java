package com.lp.transaction.server.zkClient.listener;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkStateListener;
import org.apache.zookeeper.Watcher.Event.KeeperState;

@Slf4j
public class CommonZkStateListener implements IZkStateListener {
    private ListenerLifecycle fetchListener;

    public CommonZkStateListener(ListenerLifecycle fetchListener) {
        this.fetchListener = fetchListener;
    }
    @Override
    public void handleNewSession() throws Exception {
    	log.info("节点创建新的session，启动监听");
        fetchListener.startListener();// 重连上zookeeper后需要尝试启动监听，因为可能只有一个节点
    }

    @Override
    public void handleSessionEstablishmentError(Throwable error) throws Exception {

    }

    @Override
    public void handleStateChanged(KeeperState state) throws Exception {
        if (state == KeeperState.Expired) {// 超时则停掉线程，其他实例可能会启动线程
            log.info("节点超时，停止监听");
        	fetchListener.stopListener();
        } else {
            log.info("节点状态变更，state:{},暂不处理", state.getIntValue());
        }
    }

}
