package com.lp.transaction.server.zkClient.listener;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkDataListener;

@Slf4j
public class CommonZkDataListener implements IZkDataListener {

    private ListenerLifecycle fetchListener;

    public CommonZkDataListener(ListenerLifecycle fetchListener) {
        this.fetchListener = fetchListener;
    }

    @Override
    public void handleDataDeleted(String dataPath) throws Exception {
        log.info("节点数据被删除，启动监听");
        fetchListener.startListener();
    }

    @Override
    public void handleDataChange(String dataPath, Object data) throws Exception {
        log.info("节点数据更新，暂不处理. path:{},data:{}",dataPath,data.toString() );
    }
}
