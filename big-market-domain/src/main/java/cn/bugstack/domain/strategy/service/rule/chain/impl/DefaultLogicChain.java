package cn.bugstack.domain.strategy.service.rule.chain.impl;

import cn.bugstack.domain.strategy.service.armory.IStrategyArmory;
import cn.bugstack.domain.strategy.service.rule.chain.AbstractLogicChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wangyuefan
 * @date 2024/7/15 17:07
 * @describe 默认的责任链过滤
 */
@Slf4j
@Component("default")
public class DefaultLogicChain extends AbstractLogicChain {

    @Resource
    private IStrategyArmory iStrategyArmory;

    @Override
    public Integer logic(String userId, Long strategyId) {
        Integer awardId = iStrategyArmory.getRandomAwardId(strategyId);
        log.info("抽奖责任链---默认处理, userId:{}, strategyId:{}, ruleModel:{}, awardId:{}", userId, strategyId, ruleModel(), awardId);
        return awardId;
    }

    @Override
    protected String ruleModel() {
        return "default";
    }
}
