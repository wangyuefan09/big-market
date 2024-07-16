package cn.bugstack.domain.strategy.service.rule.filter.impl;

import cn.bugstack.domain.strategy.model.entity.RuleActionEntity;
import cn.bugstack.domain.strategy.model.entity.RuleMatterEntity;
import cn.bugstack.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.annotation.LogicStrategy;
import cn.bugstack.domain.strategy.service.rule.filter.ILogicFilter;
import cn.bugstack.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wangyuefan
 * @date 2024/7/15 13:38
 * @describe 抽奖n次后，解锁固定抽奖的奖励
 */
@Slf4j
@Component
@LogicStrategy(logicModel = DefaultLogicFactory.LogicModel.RULE_LOCK)
public class RuleLockLogFilter implements ILogicFilter<RuleActionEntity.RaffleCenterEntity> {

    @Resource
    private IStrategyRepository iStrategyRepository;

    // 定义一个用户虚拟的抽奖次数, 后续改成真实的
    private Long userRaffleCount = 0L;

    @Override
    public RuleActionEntity<RuleActionEntity.RaffleCenterEntity> filter(RuleMatterEntity ruleMatterEntity) {
        String userId = ruleMatterEntity.getUserId();
        Long strategyId = ruleMatterEntity.getStrategyId();
        String ruleModel = ruleMatterEntity.getRuleModel();
        log.info("抽奖中规则过滤-抽奖次数 userId:{} strategyId:{} ruleModel:{}", userId, strategyId, ruleModel);

        // 查询策略规则的值, 数据示例 1 2 3 4 5 6 自然数
        String ruleValue = iStrategyRepository.queryStrategyRuleValue(strategyId, ruleMatterEntity.getAwardId(), ruleModel);
        if (userRaffleCount >= Long.parseLong(ruleValue)) {
            return RuleActionEntity.<RuleActionEntity.RaffleCenterEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }

        return RuleActionEntity.<RuleActionEntity.RaffleCenterEntity>builder()
                .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                .build();
    }
}
