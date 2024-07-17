package cn.bugstack.domain.strategy.service.rule.tree.factory;

import cn.bugstack.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.model.vo.RuleTreeVO;
import cn.bugstack.domain.strategy.service.rule.tree.ILogicTreeNode;
import cn.bugstack.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import cn.bugstack.domain.strategy.service.rule.tree.factory.engine.impl.DecisionTreeEngine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author wangyuefan
 * @date 2024/7/17 11:13
 * @describe 规则树工厂
 */
@Service
public class DefaultTreeFactory {

    private final Map<String, ILogicTreeNode> logicTreeNodeMap;

    public DefaultTreeFactory(Map<String, ILogicTreeNode> logicTreeNodeMap) {
        this.logicTreeNodeMap = logicTreeNodeMap;
    }

    public IDecisionTreeEngine openLogicTree(RuleTreeVO ruleTreeVO) {
        return new DecisionTreeEngine(logicTreeNodeMap, ruleTreeVO);
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TreeActionEntity {

        /**
         * 检查类型（放行、接管）
         */
        private RuleLogicCheckTypeVO checkType;

        /**
         * 策略奖品数据
         */
        private StrategyAwardVO strategyAwardVO;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StrategyAwardVO {

        /**
         * 奖品ID, 内部流转使用
         */
        private Integer awardId;

        /**
         * 抽奖奖品规则
         */
        private String awardRuleValue;
    }
}
