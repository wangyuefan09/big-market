package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.StrategyAward;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wangyuefan
 * @date 2024/7/8 14:58
 * @describe
 */
public interface IStrategyAwardDao {

    /**
     * 查询策略奖品列表
     *
     * @return 策略奖品列表
     */
    List<StrategyAward> queryStrategyAwardList();

    /**
     * 通过策略ID查询策略奖品列表
     *
     * @param strategyId 策略ID
     * @return 策略奖品列表
     */
    List<StrategyAward> queryStrategyAwardListByStrategyId(@Param("strategyId") Long strategyId);

    /**
     * 查询奖品的规则模型
     *
     * @param strategyId 策略ID
     * @param awardId    奖品ID
     * @return 字符串类型的规则模型
     */
    String queryStrategyAwardRuleModels(@Param("strategyId") Long strategyId, @Param("awardId") Integer awardId);
}
