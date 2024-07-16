package cn.bugstack.domain.strategy.service.rule.chain;

import cn.bugstack.domain.strategy.service.rule.chain.impl.ILogicChainAmory;

/**
 * @author wangyuefan
 * @date 2024/7/15 16:56
 * @describe 抽奖规则链接口
 */
public interface ILogicChain extends ILogicChainAmory {

    /**
     * 责任链接口
     *
     * @param userId     用户ID
     * @param strategyId 策略ID
     * @return 奖品ID
     */
    Integer logic(String userId, Long strategyId);

}
