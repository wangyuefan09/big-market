package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.RuleTreeNodeLine;

import java.util.List;

/**
 * @author wangyuefan
 * @date 2024/7/17 15:53
 * @describe
 */
public interface IRuleTreeNodeLineDao {

    List<RuleTreeNodeLine> queryRuleTreeNodeLineListByTreeId(String treeId);
}
