package cn.bugstack.domain.strategy.service.rule.tree.factory.engine.impl;

import cn.bugstack.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.model.vo.RuleTreeNodeLineVO;
import cn.bugstack.domain.strategy.model.vo.RuleTreeNodeVO;
import cn.bugstack.domain.strategy.model.vo.RuleTreeVO;
import cn.bugstack.domain.strategy.service.rule.tree.ILogicTreeNode;
import cn.bugstack.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import cn.bugstack.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author wangyuefan
 * @date 2024/7/17 11:19
 * @describe
 */
@Slf4j
public class DecisionTreeEngine implements IDecisionTreeEngine {

    private final Map<String, ILogicTreeNode> logicTreeNodeMap;
    private final RuleTreeVO ruleTreeVO;

    public DecisionTreeEngine(Map<String, ILogicTreeNode> logicTreeNodeMap, RuleTreeVO ruleTreeVO) {
        this.logicTreeNodeMap = logicTreeNodeMap;
        this.ruleTreeVO = ruleTreeVO;
    }

    @Override
    public DefaultTreeFactory.StrategyAwardVO process(String userId, Long strategyId, Integer awardId) {
        // 定义返回对象
        DefaultTreeFactory.StrategyAwardVO strategyAwardData = null;

        // 获取基础对象信息
        String nextNode = ruleTreeVO.getTreeRootRuleNode();
        Map<String, RuleTreeNodeVO> treeNodeMap = ruleTreeVO.getTreeNodeMap();

        // 获取起始节点, 记录了第一个要执行的规则
        RuleTreeNodeVO ruleTreeNode = treeNodeMap.get(nextNode);
        while (null != nextNode) {
            // 获取决策节点
            ILogicTreeNode logicTreeNode = logicTreeNodeMap.get(ruleTreeNode.getRuleKey());

            // 决策节点计算
            DefaultTreeFactory.TreeActionEntity logic = logicTreeNode.logic(userId, strategyId, awardId);
            RuleLogicCheckTypeVO checkType = logic.getCheckType();
            strategyAwardData = logic.getStrategyAwardVO();
            log.info("决策树引擎【{}】, treeId:{}, node:{}, code:{}", ruleTreeVO.getTreeName(), ruleTreeNode.getTreeId(), nextNode, checkType.getCode());

            // 获取下一个节点
            nextNode = nextNode(checkType.getCode(), ruleTreeNode.getTreeNodeLineVOList());
            ruleTreeNode = treeNodeMap.get(nextNode);
        }
        return strategyAwardData;
    }

    public String nextNode(String matterValue, List<RuleTreeNodeLineVO> ruleTreeNodeLineList) {
        if (null == ruleTreeNodeLineList || ruleTreeNodeLineList.isEmpty()) {
            return null;
        }

        for (RuleTreeNodeLineVO treeNodeLine : ruleTreeNodeLineList) {
            if (decisionLogic(matterValue, treeNodeLine)) {
                return treeNodeLine.getRuleNodeTo();
            }
        }
        throw new RuntimeException("决策树引擎，nextNode 计算失败，未找到可执行节点！");
    }

    public boolean decisionLogic(String matterValue, RuleTreeNodeLineVO nodeLine) {
        switch (nodeLine.getRuleLimitType()) {
            case EQUAL:
                return matterValue.equals(nodeLine.getRuleLimitValue().getCode());
            // 以下规则暂时不需要实现
            case GT:
            case LT:
            case GE:
            case LE:
            default:
                return false;
        }
    }

}
