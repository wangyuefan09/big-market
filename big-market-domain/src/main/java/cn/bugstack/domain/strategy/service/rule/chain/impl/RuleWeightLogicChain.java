package cn.bugstack.domain.strategy.service.rule.chain.impl;

import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.armory.IStrategyArmory;
import cn.bugstack.domain.strategy.service.rule.chain.AbstractLogicChain;
import cn.bugstack.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import cn.bugstack.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wangyuefan
 * @date 2024/7/15 17:07
 * @describe 权重的责任链过滤
 */
@Slf4j
@Component("rule_weight")
public class RuleWeightLogicChain extends AbstractLogicChain {

    @Resource
    private IStrategyArmory iStrategyArmory;
    @Resource
    private IStrategyRepository iStrategyRepository;
    // 定义一个用户虚拟的积分分数 后续换成真实的
    private Long userScore = 0L;

    /**
     * 权重责任链过滤
     *
     * @param userId     用户ID
     * @param strategyId 策略ID
     * @return 奖品ID
     */
    @Override
    public DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId) {
        log.info("抽奖责任链---权重开始, userId:{}, strategyId:{}, ruleModel:{}", userId, strategyId, ruleModel());
        // 查询规则值, 数据示例:4000:102,103,104,105 5000:102,103,104,105,106,107 6000:102,103,104,105,106,107,108,109
        String ruleValue = iStrategyRepository.queryStrategyRuleValue(strategyId, ruleModel());

        Map<Long, String> analyticalValueMap = getAnalyticalValue(ruleValue);
        if (null == analyticalValueMap || analyticalValueMap.isEmpty()) {
            return null;
        }

        // 获取当前用户最接近的积分水平
        Long currentLevel = getLevel(userScore, analyticalValueMap);
        if (null != currentLevel) {
            Integer awardId = iStrategyArmory.getRandomAwardId(strategyId, analyticalValueMap.get(currentLevel));
            log.info("抽奖责任链---权重过滤, userId:{}, strategyId:{}, ruleModel:{}, awardId:{}", userId, strategyId, ruleModel(), awardId);
            return DefaultChainFactory.StrategyAwardVO.builder()
                    .awardId(awardId)
                    .logicModel(ruleModel())
                    .build();
        }

        // 如果没有匹配到奖品, 则放行, 过滤其他责任链
        log.info("抽奖责任链---权重放行, userId:{}, strategyId:{}, ruleModel:{}", userId, strategyId, ruleModel());
        return next().logic(userId, strategyId);
    }

    @Override
    protected String ruleModel() {
        return DefaultChainFactory.LogicModel.RULE_WEIGHT.getCode();
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

        // 从大到小排序遍历key值, 找到第一个大于等于用户积分的key值, 也就是当前用户的积分档次
        return keyList.stream()
                .sorted(Comparator.reverseOrder())
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
