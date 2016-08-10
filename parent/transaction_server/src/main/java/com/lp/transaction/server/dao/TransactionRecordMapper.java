package com.lp.transaction.server.dao;

import com.baomidou.mybatisplus.mapper.AutoMapper;
import com.lp.transaction.server.entity.TransactionRecordEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by 123 on 2016/8/2.
 */
public interface TransactionRecordMapper extends AutoMapper<TransactionRecordEntity> {
    List<TransactionRecordEntity> queryRecordByState(@Param("state") int state);
}
