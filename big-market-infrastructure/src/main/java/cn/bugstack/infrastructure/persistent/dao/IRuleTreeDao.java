package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.RuleTree;

/**
 * @author wangyuefan
 * @date 2024/7/17 15:52
 * @describe
 */
public interface IRuleTreeDao {

    RuleTree queryRuleTreeByTreeId(String treeId);
}
