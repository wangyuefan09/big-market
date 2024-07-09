package cn.bugstack.domain.strategy.repository;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author wangyuefan
 * @date 2024/7/9 14:27
 * @describe 策略仓储接口
 */
public interface IStrategyRepository {

    /**
     * 查询策略奖品列表
     *
     * @param strategyId 策略ID
     * @return 策略奖品列表
     */
    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    /**
     * 存储抽奖概率查询Map集合到Redis中
     *
     * @param strategyId                 策略ID
     * @param rangeRate                  概率范围
     * @param strategyAwardSearchRateMap 抽奖概率查询Map
     */
    void storeStrategyAwardSearchRateMap(Long strategyId, Integer rangeRate, Map<Integer, Integer> strategyAwardSearchRateMap);

    /**
     * 获取概率的范围值
     *
     * @param strategyId 策略ID
     * @return 范围值
     */
    int getRangeRate(Long strategyId);

    /**
     * 通过一个随机数获取奖品ID
     *
     * @param strategyId 策略ID
     * @param randomNum  随机数
     * @return 奖品ID
     */
    Integer getStrategyAwardAssemble(Long strategyId, int randomNum);
}
