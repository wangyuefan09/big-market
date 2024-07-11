package cn.bugstack.domain.strategy.service;

/**
 * @author wangyuefan
 * @date 2024/7/9 14:22
 * @describe 策略装配库（兵工厂） 负责初始化策略计算
 */
public interface IStrategyArmory {

    /**
     * 装配抽奖策略
     *
     * @param strategyId 策略ID
     */
    boolean assembleLotteryStrategy(Long strategyId);

    /**
     * 获取一个随机的奖品ID
     *
     * @param strategyId 策略ID
     * @return 奖品ID
     */
    Integer getRandomAwardId(Long strategyId);

    /**
     * 获取一个随机的奖品ID
     *
     * @param strategyId      策略ID
     * @param ruleWeightValue 规则权重值
     * @return 奖品ID
     */
    Integer getRandomAwardId(Long strategyId, String ruleWeightValue);

}
