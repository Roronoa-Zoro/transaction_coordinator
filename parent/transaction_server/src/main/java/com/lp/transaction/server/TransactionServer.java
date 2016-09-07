package com.lp.transaction.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * Created by 123 on 2016/8/3.
 */
@Slf4j
public class TransactionServer {
    private static final String signalPath = "E:/tmp/signal.txt";

    public static void main(String[] args) {
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("spring-jdbc.xml");

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                Path path = Paths.get(signalPath);
                boolean deletedSignalFile = false;
                while(!deletedSignalFile) {
                    try {
                        deletedSignalFile = Files.deleteIfExists(path);
                        log.info("monitored stop file:{}", deletedSignalFile);
                        if (deletedSignalFile) {
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        TimeUnit.MINUTES.sleep(3L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                ac.close();
            }
        });
    }
}
