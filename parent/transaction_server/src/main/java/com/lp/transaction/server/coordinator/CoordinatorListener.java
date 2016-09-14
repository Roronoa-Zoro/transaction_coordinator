package com.lp.transaction.server.coordinator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;


/**
 * Created by Administrator on 2016/9/13.
 * zk监听器
 */
public class CoordinatorListener implements TreeCacheListener {

    private TaskLifecycle taskLifecycle;
    private TreeCache treeCache;
    private String data;

    public CoordinatorListener(CuratorFramework client, String path,
                               TaskLifecycle taskLifecycle, String data) throws Exception {
        this.taskLifecycle = taskLifecycle;
        this.data = data;
        this.treeCache = new TreeCache(client, path);
        this.treeCache.getListenable().addListener(this);
        this.treeCache.start();
    }

    public String getNodeData() {
        return data;
    }

    @Override
    public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
        switch (event.getType()) {
            case NODE_REMOVED:
                String nodeData = new String(event.getData().getData());
                //被删除的节点是当前机器创建的
                if (data.equals(nodeData)) {
                    //先停止本机任务, 然后再竞争
                    taskLifecycle.stop();
                    taskLifecycle.start();
                } else {
                    //被删除的节点是其他机器创建的节点, 直接竞争
                    taskLifecycle.start();
                }
                break;
            case CONNECTION_RECONNECTED:
                taskLifecycle.start();
                break;
            case CONNECTION_LOST:
                System.out.println("CONNECTION_LOST : ");
                taskLifecycle.stop();
                break;
            default:
                break;
        }
    }

}
