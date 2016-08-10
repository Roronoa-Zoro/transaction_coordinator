package com.lp.transaction.server.zkClient.listener;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.lp.transaction.server.zkClient.EnvUtils;
import com.lp.transaction.server.zkClient.ZKClientManager;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class ListenerManager {

    @Value("${application.node.name}")
    private String NODE_NAME;
    private static ZkClient zkClient;
    @Resource
    private ZKClientManager zKClientManager;
    
    @PostConstruct
    public void init() {
        synchronized (this) {
            if (zkClient == null) {
                zkClient = zKClientManager.getZkClient();
            }
        }
    }
    
    public boolean createFinanceNode(String listenerName){
    	String ip = EnvUtils.getThisIp();
        if (!zkClient.exists(NODE_NAME)) {
            try {
                zkClient.createPersistent(NODE_NAME);
            } catch (ZkException zkException) {
                log.error("create finance node error", zkException);
            }
        }
        boolean result = true;
        String path = NODE_NAME + "/" + listenerName;
        if (!zkClient.exists(path)) {
            try {
                zkClient.createEphemeral(path, ip);// 创建临时节点，节点数据为活跃节点ip
            } catch (ZkException zkException) {
                result = false;
            }
        } else {
            result = false;
        }
        return result;
    }

    public void tryListen(String listenerName, IZkDataListener zkDataListener,
            IZkStateListener expireListener) {
    	 String path = NODE_NAME + "/" + listenerName;
        zkClient.subscribeDataChanges(path, zkDataListener);
        // 处理超时
        zkClient.subscribeStateChanges(expireListener);
    }

    public void cancelListen(final String listenerName) {
        String path = NODE_NAME + "/" + listenerName;
        if (zkClient.exists(path)) {
            try {
                zkClient.delete(path);
            } catch (ZkException zkException) {
                log.error("cancel listener error", zkException);
            }
        }
    }

    public String getListnerNodeInfo(String listenerName) {
        String path = NODE_NAME + "/" + listenerName;
        String ip = zkClient.readData(path, true);
        return ip;
    }
}
