package cn.bugstack.domain.strategy.service.rule.tree.impl;

import cn.bugstack.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.service.rule.tree.ILogicTreeNode;
import cn.bugstack.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author wangyuefan
 * @date 2024/7/17 10:40
 * @describe 抽奖兜底奖励树节点
 */
@Slf4j
@Component("rule_luck_award")
public class RuleLuckAwardLogicTreeNode implements ILogicTreeNode {

    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId) {
        return DefaultTreeFactory.TreeActionEntity.builder()
                .checkType(RuleLogicCheckTypeVO.TAKE_OVER)
                .strategyAwardVO(
                        DefaultTreeFactory.StrategyAwardVO.builder()
                                .awardId(101)
                                .awardRuleValue("1,100")
                                .build()
                )
                .build();
    }

}
