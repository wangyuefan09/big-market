package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.StrategyRule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wangyuefan
 * @date 2024/7/8 14:58
 * @describe
 */
public interface IStrategyRuleDao {

    /**
     * 查询策略规则列表
     *
     * @return 策略规则列表
     */
    List<StrategyRule> queryStrategyRuleList();

    /**
     * 查询策略规则实体
     *
     * @param strategyId 策略ID
     * @param ruleModel  规则类型
     * @return 策略规则实体
     */
    StrategyRule queryStrategyRule(@Param("strategyId") Long strategyId, @Param("ruleModel") String ruleModel);

    /**
     * 查询策略规则值
     *
     * @param strategyRule 策略规则实体
     * @return 规则值
     */
    String queryStrategyValue(@Param("strategyRule") StrategyRule strategyRule);
}
