package cn.bugstack.domain.strategy.model.entity;

import cn.bugstack.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import lombok.*;

/**
 * @author wangyuefan
 * @date 2024/7/11 16:22
 * @describe 规则动作实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleActionEntity<T extends RuleActionEntity.RaffleEntity> {

    private String code = RuleLogicCheckTypeVO.ALLOW.getCode();
    private String info = RuleLogicCheckTypeVO.ALLOW.getInfo();
    private String ruleModel;
    private T data;

    static public class RaffleEntity {

    }

    /**
     * 抽奖前
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static public class RaffleBeforeEntity extends RaffleEntity {

        /**
         * 策略ID
         */
        private Long strategyId;

        /**
         * 奖品ID
         */
        private Integer awardId;

        /**
         * 规则权重值
         */
        private String ruleWeightValueKey;
    }

    /**
     * 抽奖中
     */
    static public class RaffleCenterEntity extends RaffleEntity {

    }

    /**
     * 抽奖后
     */
    static public class RaffleAfterEntity extends RaffleEntity {

    }

}
