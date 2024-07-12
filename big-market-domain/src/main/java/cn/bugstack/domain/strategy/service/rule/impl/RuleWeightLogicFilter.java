package cn.bugstack.domain.strategy.service.rule.impl;

import cn.bugstack.domain.strategy.model.entity.RuleActionEntity;
import cn.bugstack.domain.strategy.model.entity.RuleMatterEntity;
import cn.bugstack.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.annotation.LogicStrategy;
import cn.bugstack.domain.strategy.service.rule.ILogicFilter;
import cn.bugstack.domain.strategy.service.rule.factory.DefaultLogicFactory;
import cn.bugstack.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wangyuefan
 * @date 2024/7/11 17:48
 * @describe 权重值规则过滤
 */
@Slf4j
@Component
@LogicStrategy(logicModel = DefaultLogicFactory.LogicModel.RULE_WIGHT)
public class RuleWeightLogicFilter implements ILogicFilter<RuleActionEntity.RaffleBeforeEntity> {

    @Resource
    private IStrategyRepository iStrategyRepository;
    // 定义一个用户虚拟的积分分数 后续换成真实的
    private Long userScore = 4500L;

    @Override
    public RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> filter(RuleMatterEntity ruleMatterEntity) {
        String userId = ruleMatterEntity.getUserId();
        Long strategyId = ruleMatterEntity.getStrategyId();
        String ruleModel = ruleMatterEntity.getRuleModel();
        log.info("规则过滤-权重范围 userId:{} strategyId:{} ruleModel:{}", userId, strategyId, ruleModel);

        // 查询规则值, 数据示例:4000:102,103,104,105 5000:102,103,104,105,106,107 6000:102,103,104,105,106,107,108,109
        String ruleValue = iStrategyRepository.queryStrategyRuleValue(strategyId, ruleMatterEntity.getAwardId(), ruleModel);
        Map<Long, String> analyticalValueMap = getAnalyticalValue(ruleValue);
        // 如果查询的规则值为空, 则直接放行
        if (null == analyticalValueMap || analyticalValueMap.isEmpty()) {
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }

        // 根据 4500 的积分, 得出当前用户的积分水平是在哪个档次
        Long currentLevel = getLevel(userScore, analyticalValueMap);

        // 有规则权重值, 需要过滤出来被接管
        if (null != currentLevel) {
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .data(
                            RuleActionEntity.RaffleBeforeEntity.builder()
                                    .strategyId(strategyId)
                                    .ruleWeightValueKey(analyticalValueMap.get(currentLevel))
                                    .build()
                    )
                    .ruleModel(DefaultLogicFactory.LogicModel.RULE_WIGHT.getCode())
                    .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                    .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                    .build();
        }

        return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                .build();
    }

    /**
     * 根据用户积分和规则值, 计算当前用户的积分档次, 也就是在 Map 中的 key 值
     *
     * @param userScore          用户积分
     * @param analyticalValueMap 规则值Map集合 key为积分, value为奖品ID
     * @return 当前用户的积分档次
     */
    private Long getLevel(Long userScore, Map<Long, String> analyticalValueMap) {
        // 取出key值并排序
        List<Long> keyList = new ArrayList<>(analyticalValueMap.keySet());
        Collections.sort(keyList);

        // 遍历key值, 找到第一个大于等于用户积分的key值, 也就是当前用户的积分档次
        return keyList.stream()
                .filter(key -> userScore >= key)
                .findFirst()
                .orElse(null);
    }

    /**
     * 解析规则值
     *
     * @param ruleValue 规则值
     * @return 规则值的Map集合
     */
    private Map<Long, String> getAnalyticalValue(String ruleValue) {
        return Arrays.stream(ruleValue.split(Constants.SPACE))
                .filter(ruleValueKey -> ruleValueKey != null && !ruleValueKey.isEmpty())
                .map(ruleValueKey -> {
                    String[] parts = ruleValueKey.split(Constants.COLON);
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("rule_weight rule_rule invalid input format: " + ruleValueKey);
                    }
                    return new AbstractMap.SimpleEntry<>(Long.parseLong(parts[0]), ruleValueKey);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (existing, replacement) -> existing));
    }
}
