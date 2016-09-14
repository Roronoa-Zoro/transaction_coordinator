package com.lp.transaction.server.monitor;

import com.lp.transaction.server.coordinator.CoordinatorRegistry;
import com.lp.transaction.server.coordinator.TaskLifecycle;
import com.lp.transaction.server.enums.RunningState;
import com.lp.transaction.server.enums.TrxMsgSendResState;
import com.lp.transaction.server.service.TransactionRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/9/14.
 */
@Slf4j
@Component
public class MessageDispatcher implements TaskLifecycle {

    @Value("${trxMessagePublish.enabled}")
    private boolean publishedEnabled;// 是否需要运行
    @Autowired
    private TransactionRecordService trxRecordService;
    @Autowired
    private CoordinatorRegistry registry;

    private final String LISTENER_NAME = "PublishTrxMessage";
    private volatile RunningState runningState = RunningState.Stop;
    private ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(1, 2, 60L, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
    private Future<?> future;

    @Override
    public void start() {
        if (!publishedEnabled) {
            return;
        }
        registry.registerTask(this, LISTENER_NAME);
    }

    @Override
    public void stop() {
        registry.unregisterTask(this);
    }

    @Override
    public void startTask() {
        runningState = RunningState.Stop;
        if (poolExecutor.getActiveCount() > 0) {
            future.cancel(true);
        }
        int count = 0;
        if (future != null) {
            while (!future.isCancelled()) {
                //如果线程运行中，等待线程终止
                count++;
                if(count > 60){
                    //发送预警邮件
                    //TODO
                    throw new RuntimeException("");
                }
                try {
                    TimeUnit.SECONDS.sleep(2L);
                } catch (InterruptedException e) {
                    log.warn("轮询发送[事务消息发放] 等待原线程任务终止出错", e);
                }
            }//while
        }

        runningState = RunningState.Running;
        log.info("{} start to send message", Thread.currentThread().getName());
        future = poolExecutor.submit(() -> {
            try{
                while(!Thread.currentThread().isInterrupted() && runningState == RunningState.Running) {
                    TrxMsgSendResState res = trxRecordService.sendTrxParticipantMsg();
                    switch (res) {
                        case SEND_SUCCESS:
                            TimeUnit.MILLISECONDS.sleep(10L);
                            break;
                        case SEND_FAILURE:
                            TimeUnit.MILLISECONDS.sleep(100L);
                            break;
                        case SEND_WITHOUT_MSG:
                            TimeUnit.MILLISECONDS.sleep(3000L);
                            break;
                    }
                }
            }catch(Exception e) {
                log.error("发送[事务消息发放]异常", e);
                stop();
                throw new RuntimeException("轮询发送[理财单状态变更消息]异常退出", e);
            } finally {
                runningState = RunningState.Stop;
            }
        });
    }

    @Override
    public void stopTask() {
        log.info("[事务消息发放]停止运行");
        runningState = RunningState.Stop;
        if (future != null && !future.isCancelled()) {
            future.cancel(true);
        }
    }
}
