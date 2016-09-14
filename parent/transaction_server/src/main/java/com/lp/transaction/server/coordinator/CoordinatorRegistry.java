package com.lp.transaction.server.coordinator;

import com.lp.transaction.server.util.EnvUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/9/14.
 */
@Component
@Slf4j
public class CoordinatorRegistry {

    @Value("${application.node.name}")
    private String basePath;

    private ConcurrentMap<TaskLifecycle, CoordinatorListener> registeredTask;
    CuratorFramework client;

    @PostConstruct
    public void init() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", retryPolicy);
        client.start();
        registeredTask = new ConcurrentHashMap<>();
    }

    public void registerTask(TaskLifecycle taskLifecycle, String path) {
        try {
            if (registeredTask.get(taskLifecycle) == null) {
                String nodeData = EnvUtils.getThisIp() + "-" + System.currentTimeMillis();
                CoordinatorListener listener = new CoordinatorListener(client, basePath + path, taskLifecycle, nodeData);
                registeredTask.put(taskLifecycle, listener);
            }
            Stat stat = client.checkExists().forPath(basePath + path);
            if (stat != null) {
                //其他节点已经获取的任务执行权限
                log.info("other thread get chance to run task:{}");
                return;
            }

            boolean created = createTaskNode(basePath + path, registeredTask.get(taskLifecycle).getNodeData());
            if (!created) {
                return;
            }
            taskLifecycle.startTask();
        } catch (Exception e) {
            log.error("start run task fails", e);
        }
    }

    private boolean createTaskNode(String path, String data) {
        for (int i = 0; i < 3; i++) {
            try{
                client.create().withMode(CreateMode.EPHEMERAL).forPath(path, data.getBytes());
                log.info("current thread get chance to run task");
                return true;
            } catch (Exception e) {
                log.info("create task node fails", e);
                try{
                    Stat stat = client.checkExists().forPath(path);
                    if (stat != null) {
                        return false;
                    }
                    TimeUnit.SECONDS.sleep(1L);
                } catch (Exception ie) {
                    log.info("check exist for path:{} fails", path, ie);
                }
            }
        }
        return false;
    }

    public void unregisterTask(TaskLifecycle taskLifecycle) {
        log.info("unregister a task");
        registeredTask.remove(taskLifecycle);
        taskLifecycle.stopTask();
    }
}
