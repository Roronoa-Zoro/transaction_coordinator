package com.lp.transaction.server.monitor;

import com.lp.transaction.client.enums.CallbackState;
import com.lp.transaction.server.service.RestService;
import com.lp.transaction.server.vo.CallbackInvokeResVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/9/6.
 */
@Component
public class TrxParticipantMonitorPool {

    private ThreadPoolExecutor executor;

    @PostConstruct
    public void initExecutor() {
        executor = new ThreadPoolExecutor(3, 30, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }

    @Autowired
    private RestService restService;

    public Future<CallbackState> submitMonitorTask(String url, long arg) {
        Future<CallbackState> future = executor.submit(() -> {
            CallbackState res = restService.monitorTrxStatus(url, arg);
            return res;
        });
        return future;
    }

    public Future<CallbackInvokeResVO> submitCommitOrRollbackTask(String url, long arg) {
        Future<CallbackInvokeResVO> future = executor.submit(() -> {
            CallbackInvokeResVO res = restService.invoke(url, arg);
            return res;
        });
        return future;
    }

}
