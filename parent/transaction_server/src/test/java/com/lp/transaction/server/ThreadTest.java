package com.lp.transaction.server;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by 123 on 2016/8/3.
 */
public class ThreadTest {

    @Test
    public void pathTest() throws IOException {
        Path path = Paths.get("E:/tmp/signal.txt");
        System.err.println(path);
        System.err.println(Files.deleteIfExists(path));
    }
    @Test
    public void poolTest() throws Exception {
//        ScheduledThreadPoolExecutor poolExecutor = new ScheduledThreadPoolExecutor(1);
//        poolExecutor.scheduleAtFixedRate(() -> {
//            System.err.println(Thread.currentThread().getName() + " print something at" + LocalDateTime.now());
//        },1000L, 5000L, TimeUnit.MILLISECONDS);

        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(1, 2, 60L, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        TimeUnit.SECONDS.sleep(5L);
        System.err.println(poolExecutor.getActiveCount());
        TimeUnit.SECONDS.sleep(5L);
        System.err.println(poolExecutor.getActiveCount());
        TimeUnit.SECONDS.sleep(5L);
        System.err.println(poolExecutor.getActiveCount());

        TimeUnit.MINUTES.sleep(5L);
    }


}
