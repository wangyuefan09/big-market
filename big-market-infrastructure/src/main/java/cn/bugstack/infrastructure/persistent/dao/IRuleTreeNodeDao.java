package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.RuleTreeNode;

import java.util.List;

/**
 * @author wangyuefan
 * @date 2024/7/17 15:52
 * @describe
 */
public interface IRuleTreeNodeDao {

    List<RuleTreeNode> queryRuleTreeNodeListByTreeId(String treeId);
}
