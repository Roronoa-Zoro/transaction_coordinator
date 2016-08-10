package com.lp.transaction.server.service.impl;

import com.baomidou.framework.service.impl.SuperServiceImpl;
import com.lp.transaction.client.model.TransactionParticipantsVO;
import com.lp.transaction.server.dao.TransactionParticipantsMapper;
import com.lp.transaction.server.entity.TransactionParticipantsEntity;
import com.lp.transaction.server.service.TransactionParticipantsService;
import org.springframework.stereotype.Service;

/**
 * Created by 123 on 2016/8/2.
 */
@Service
public class TransactionParticipantsServiceImpl extends SuperServiceImpl<TransactionParticipantsMapper, TransactionParticipantsEntity>
                implements TransactionParticipantsService {

}
