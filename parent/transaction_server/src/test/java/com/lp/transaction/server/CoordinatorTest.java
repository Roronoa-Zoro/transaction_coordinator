package com.lp.transaction.server;


import com.lp.transaction.server.monitor.MessageDispatcher;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/9/14.
 */
public class CoordinatorTest {

    @Test
    public void coordinatorTest() throws InterruptedException {
        ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring-jdbc.xml");
        MessageDispatcher md = ac.getBean(MessageDispatcher.class);
        md.start();

        TimeUnit.MINUTES.sleep(15);
    }
}
