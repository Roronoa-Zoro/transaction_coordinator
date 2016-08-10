package com.lp.transaction.server.zkClient;

import javax.annotation.PostConstruct;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ZKClientManager {
	
    @Value("${zookeeper.server.address}")
    private String zkAddress;
    private static ZkClient zkClient;
    private static final int CONNECTION_TIMEOUT = 60000000;// 连接时间

    @PostConstruct
    public void init() {
        synchronized (this) {
            if (zkClient == null) {
                zkClient = new ZkClient(zkAddress, CONNECTION_TIMEOUT);// session失效时间默认30s
            }
        }
    }
    
    public ZkClient getZkClient(){
    	return zkClient;
    }
}
