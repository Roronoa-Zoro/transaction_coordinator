package com.lp.transaction.server.monitor;

import com.lp.transaction.server.enums.RunningState;
import com.lp.transaction.server.enums.TrxMsgSendResState;
import com.lp.transaction.server.service.TransactionRecordService;
import com.lp.transaction.server.zkClient.listener.CommonZkDataListener;
import com.lp.transaction.server.zkClient.listener.CommonZkStateListener;
import com.lp.transaction.server.zkClient.listener.ListenerLifecycle;
import com.lp.transaction.server.zkClient.listener.ListenerManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;

/**
 * Created by 123 on 2016/8/2.
 */
@Slf4j
@Component
class MessagePublisher implements ListenerLifecycle {

    @Value("${trxMessagePublish.enabled}")
    private boolean publishedEnabled;// 是否需要运行

    @Autowired
    private ListenerManager listenerManager;
    @Autowired
    private TransactionRecordService trxRecordService;

    private CommonZkDataListener commonZkDataListener;
    private CommonZkStateListener commonZkStateListener;
    private final String LISTENER_NAME = "PublishTrxMessage";
    private volatile RunningState runningState = RunningState.Stop;
    private Thread thread;
    private ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(1, 2, 60L, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
    private Future<?> future;

    @PostConstruct
    public void init(){
        if (false == publishedEnabled) {
            log.info("[事务消息发放]配置不启动，启动结束");
            return;
        }
        commonZkDataListener = new CommonZkDataListener(this);
        commonZkStateListener = new CommonZkStateListener(this);
        listenerManager.tryListen(LISTENER_NAME, commonZkDataListener, commonZkStateListener);
        startListener();
    }

    @Override
    public void startListener() {
        log.info("[事务消息发放]启动开始");
        boolean isListen = listenerManager.createFinanceNode(LISTENER_NAME);
        //创建节点未成功说明已有程序正在运行，程序终止
        if(!isListen){
            log.info("监听抢占失败，已有其他实例:{}在监听", listenerManager.getListnerNodeInfo(LISTENER_NAME));
            return;
        }
        //TODO 实际的逻辑
        sendTrxParticipantMsg();
        log.info("[事务消息发放]启动完成");
    }

    @Override
    public void stopListener() {
        log.info("[事务消息发放]停止运行");
        runningState = RunningState.Stop;
        if (future != null && !future.isCancelled()) {
            future.cancel(true);
        }
    }

    private void sendTrxParticipantMsg() {
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
                    Thread.sleep(1000l);
                } catch (InterruptedException e) {
                    log.warn("轮询发送[事务消息发放] 等待原线程任务终止出错", e);
                }
            }//while
        }

        runningState = RunningState.Running;
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
                listenerManager.cancelListen(LISTENER_NAME);
                throw new RuntimeException("轮询发送[理财单状态变更消息]异常退出", e);
            } finally {
                runningState = RunningState.Stop;
            }
        });
    }
}
