package cn.bugstack.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangyuefan
 * @date 2024/7/11 16:12
 * @describe 规则物料实体, 用于过滤规则的必要参数信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleMatterEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 策略ID
     */
    private Long strategyId;

    /**
     * 抽奖奖品ID
     */
    private Integer awardId;

    /**
     * 抽奖规则类型
     */
    private String ruleModel;
}
