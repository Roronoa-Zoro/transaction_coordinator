package com.lp.transaction.server.vo;

import com.lp.transaction.client.enums.CallbackState;
import lombok.Data;

/**
 * Created by 123 on 2016/8/2.
 * 回调调用结果
 */
@Data
public class CallbackInvokeResVO {
    private boolean invokeResult;
    private CallbackState invokeState;
}
