package cn.bugstack.domain.strategy.service.rule.chain.impl;

import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.rule.chain.AbstractLogicChain;
import cn.bugstack.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import cn.bugstack.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author wangyuefan
 * @date 2024/7/15 17:06
 * @describe 黑名单责任链过滤
 */
@Slf4j
@Component("rule_blacklist")
public class BlackListLogicChain extends AbstractLogicChain {

    @Resource
    private IStrategyRepository iStrategyRepository;

    /**
     * 黑名单责任链过滤
     *
     * @param userId     用户ID
     * @param strategyId 策略ID
     * @return 奖品ID
     */
    @Override
    public DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId) {
        log.info("抽奖责任链---黑名单开始, userId:{}, strategyId:{}, ruleModel:{}", userId, strategyId, ruleModel());
        // 查询规则值, 数据示例 100:user001,user002
        String ruleValue = iStrategyRepository.queryStrategyRuleValue(strategyId, ruleModel());
        String[] splitRuleValue = ruleValue.split(Constants.COLON);
        Integer awardId = Integer.valueOf(splitRuleValue[0]);

        // 黑名单列表
        List<String> blackList = Arrays.asList(splitRuleValue[1].split(Constants.SPLIT));
        boolean flag = blackList.stream()
                .anyMatch(data -> data.equals(userId));
        if (flag) {
            // 如果是黑名单, 直接返回固定的奖品ID
            log.info("抽奖责任链---黑名单接管, userId:{}, strategyId:{}, ruleModel:{}, awardId:{}", userId, strategyId, ruleModel(), awardId);
            return DefaultChainFactory.StrategyAwardVO.builder()
                    .awardId(awardId)
                    .logicModel(ruleModel())
                    .build();
        }

        // 不是黑名单则放行, 过滤其他责任链
        log.info("抽奖责任链---黑名单放行, userId:{}, strategyId:{}, ruleModel:{}", userId, strategyId, ruleModel());
        return next().logic(userId, strategyId);
    }

    @Override
    protected String ruleModel() {
        return DefaultChainFactory.LogicModel.RULE_BLACKLIST.getCode();
    }
}
