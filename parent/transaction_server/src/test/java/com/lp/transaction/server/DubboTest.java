package com.lp.transaction.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lp.transaction.client.api.TransactionClient;
import com.lp.transaction.client.model.TransactionRecordVO;
import com.lp.transaction.server.entity.TransactionRecordEntity;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.time.LocalDateTime;

/**
 * Created by 123 on 2016/8/8.
 */
public class DubboTest {

    @Test
    public void jsonTest() {
        TransactionRecordEntity entity = new TransactionRecordEntity();
        entity.setTrxState(1);
        entity.setTrxCreateTime(LocalDateTime.now());
        String json = JSON.toJSONString(entity);
        System.err.println(json);
        JSONObject json1 = JSON.parseObject(json);
//        TransactionRecordEntity newObj = JSON.parseObject(json, TransactionRecordEntity.class);
        TransactionRecordEntity newObj = JSON.toJavaObject(json1, TransactionRecordEntity.class);
        System.err.println(newObj);
    }

    @Test
    public void testDubbo() {
//        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("spring-jdbc.xml");
//        TransactionClient trxClient = ac.getBean(TransactionClient.class);
//        TransactionRecordVO trvo = new TransactionRecordVO();
//        trvo.setTrxPartiNum(1);
//        trvo.setTrxCallback("");
//        trvo.setTrxRollbackCallback("");
//        trvo.setTrxSubmitCallback("");
//        TransactionRecordVO remoteVo = trxClient.addMainTrx(trvo);
//        System.err.println(remoteVo);
    }
}
