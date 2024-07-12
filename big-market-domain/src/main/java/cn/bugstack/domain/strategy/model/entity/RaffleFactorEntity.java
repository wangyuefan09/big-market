package cn.bugstack.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangyuefan
 * @date 2024/7/11 16:00
 * @describe 抽奖因子实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleFactorEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 策略ID
     */
    private Long strategyId;

}
