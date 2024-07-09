package cn.bugstack.domain.strategy.model.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wangyuefan
 * @date 2024/7/8 14:47
 * @describe 策略奖品
 */
@Data
@Builder
public class StrategyAwardEntity {

    /**
     * 抽奖策略ID
     */
    private Long strategyId;
    /**
     * 抽奖奖品ID - 内部流转使用
     */
    private Integer awardId;
    /**
     * 奖品库存总量
     */
    private Integer awardCount;
    /**
     * 奖品库存剩余
     */
    private Integer awardCountSurplus;
    /**
     * 奖品中奖概率
     */
    private BigDecimal awardRate;

}
