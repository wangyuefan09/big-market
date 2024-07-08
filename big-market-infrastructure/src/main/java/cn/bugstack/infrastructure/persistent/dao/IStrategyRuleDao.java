package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.StrategyRule;

import java.util.List;

/**
 * @author wangyuefan
 * @date 2024/7/8 14:58
 * @describe
 */
public interface IStrategyRuleDao {

    /**
     * 查询策略规则列表
     * @return 策略规则列表
     */
    List<StrategyRule> queryStrategyRuleList();

}
