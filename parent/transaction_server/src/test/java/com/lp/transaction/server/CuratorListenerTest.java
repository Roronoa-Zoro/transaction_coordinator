package com.lp.transaction.server;

import com.lp.transaction.server.coordinator.CoordinatorListener;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/9/13.
 */
public class CuratorListenerTest {

    String path = "/curator";

    CuratorFramework client;

    @Before
    public void init() {
        if (client == null) {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", retryPolicy);
            client.start();
        }
    }

    @Test
    public void createTest() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        startThread(latch);
        startThread(latch);
        TimeUnit.SECONDS.sleep(1l);
        latch.countDown();
        TimeUnit.SECONDS.sleep(10l);

    }

    private void startThread(CountDownLatch latch) {
        new Thread(() -> {

            String res = null;
            try {
                latch.await();
                res = client.create().creatingParentsIfNeeded().forPath(path);
                System.out.println(Thread.currentThread().getName() + " created===" + res);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }

    @Test
    public void listenerTest() throws Exception {

        CoordinatorListener listener = new CoordinatorListener(client, path, null, null);
//        client.create().forPath(path, "hello".getBytes());
//        client.delete().forPath(path);
        TimeUnit.MINUTES.sleep(5);
    }

    @Test
    public void stateAndDataTest() throws Exception {
        TreeCache node = new TreeCache(client, path);
        node.getListenable().addListener((client,event) -> {
            ChildData data;
            switch (event.getType()) {
                case NODE_ADDED:
                    data = event.getData();
                    System.out.println("NODE_ADDED : "+ data.getPath() +"  数据:"+ data.getData());
                    break;
                case NODE_UPDATED:
                    data = event.getData();
                    System.out.println("NODE_UPDATED : "+ data.getPath() +"  数据:"+ data.getData());
                    break;
                case NODE_REMOVED:
                    data = event.getData();
                    System.out.println("NODE_REMOVED : "+ data.getPath() +"  数据:"+ data.getData());
                    break;
                case CONNECTION_RECONNECTED:
                    System.out.println("CONNECTION_RECONNECTED : ");
                    break;
                case CONNECTION_LOST:
                    System.out.println("CONNECTION_LOST : ");
                    break;
                default:
                    break;
            }
        });
        node.start();
        TimeUnit.MINUTES.sleep(10);
    }

    @Test
    public void stateTest() throws Exception {
        PathChildrenCache node = new PathChildrenCache(client, path, true);
        node.getListenable().addListener((client, event) -> {
            ChildData data = event.getData();
            switch (event.getType()) {
                case CHILD_ADDED:
                    System.out.println("CHILD_ADDED : "+ data.getPath() +"  数据:"+ data.getData());
                    break;
                case CHILD_REMOVED:
                    System.out.println("CHILD_REMOVED : "+ data.getPath() +"  数据:"+ data.getData());
                    break;
                case CHILD_UPDATED:
                    System.out.println("CHILD_UPDATED : "+ data.getPath() +"  数据:"+ data.getData());
                    break;
                case CONNECTION_RECONNECTED:
                    System.out.println("CONNECTION_RECONNECTED : "+ data.getPath() +"  数据:"+ data.getData());
                case CONNECTION_LOST:
                    System.out.println("CONNECTION_LOST : "+ data.getPath() +"  数据:"+ data.getData());
                default:
                    break;
            }
        });
        node.start();
        TimeUnit.MINUTES.sleep(10);
    }
}
