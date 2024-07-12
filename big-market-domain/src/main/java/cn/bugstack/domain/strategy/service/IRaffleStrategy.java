package cn.bugstack.domain.strategy.service;

import cn.bugstack.domain.strategy.model.entity.RaffleAwardEntity;
import cn.bugstack.domain.strategy.model.entity.RaffleFactorEntity;

/**
 * @author wangyuefan
 * @date 2024/7/11 15:58
 * @describe 策略抽奖接口
 */
public interface IRaffleStrategy {

    /**
     * 执行抽奖的接口
     *
     * @param raffleFactorEntity 抽奖因子实体（可增加参数）
     * @return 抽奖的奖品结果
     */
    RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity);
}
