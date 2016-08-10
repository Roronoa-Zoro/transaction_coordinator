package com.lp.transaction.server.service;

import com.lp.transaction.client.enums.CallbackState;
import com.lp.transaction.server.exceptions.RestException;
import com.lp.transaction.server.vo.CallbackInvokeResVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;

/**
 * Created by 123 on 2016/8/2.
 */
@Service
@Slf4j
public class RestService {
    private RestTemplate template = new RestTemplate();

    @Retryable(value={ResourceAccessException.class, RestException.class}, backoff = @Backoff(delay = 1000, multiplier = 1.5))
    public CallbackInvokeResVO invoke(String url, Object args) {
        CallbackState res = template.postForObject(url, args, CallbackState.class);

        if (CallbackState.CallbackCommitFailure == res || CallbackState.CallbackRollbackFailure == res) {
            throw new RestException();
        }
        CallbackInvokeResVO vo = new CallbackInvokeResVO();
        vo.setInvokeResult(true);
        vo.setInvokeState(res);

        return vo;
    }

    public CallbackState monitorTrxStatus(String url, Object args) {
        CallbackState res = template.postForObject(url, args, CallbackState.class);
        return res;
    }

    @Recover
    public CallbackInvokeResVO recoverFromRestException(RestException re) {
        log.warn("recover from RestException", re);
        return  returnResult();
    }

    @Recover
    public CallbackInvokeResVO recoverFromRestException(ResourceAccessException ce) {
        log.warn("recover from ResourceAccessException", ce);
        return  returnResult();
    }

    private CallbackInvokeResVO returnResult() {
        CallbackInvokeResVO vo = new CallbackInvokeResVO();
        vo.setInvokeResult(true);
        vo.setInvokeState(CallbackState.CallbackFailure);
        return vo;
    }
}
