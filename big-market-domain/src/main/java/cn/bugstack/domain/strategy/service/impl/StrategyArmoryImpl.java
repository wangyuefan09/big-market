package cn.bugstack.domain.strategy.service.impl;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyRuleEntity;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.IStrategyArmory;
import cn.bugstack.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wangyuefan
 * @date 2024/7/9 14:25
 * @describe 策略装配库（兵工厂） 负责初始化策略计算
 */
@Slf4j
@Service
public class StrategyArmoryImpl implements IStrategyArmory {

    @Resource
    private IStrategyRepository iStrategyRepository;

    @Override
    public boolean assembleLotteryStrategy(Long strategyId) {
        // 1.根据策略ID查询策略奖品列表
        List<StrategyAwardEntity> strategyAwardEntityList = iStrategyRepository.queryStrategyAwardList(strategyId);
        // 2.装配抽奖策略
        assembleLotteryStrategy(strategyId + "", strategyAwardEntityList);

        // 3.权重策略的配置 - 适用于 rule_weight 权重规则配置
        StrategyEntity strategyEntity = iStrategyRepository.queryStrategyEntityByStrategyId(strategyId);
        if (null == strategyEntity) {
            return true;
        }
        String ruleModel = strategyEntity.getRuleWeight();

        // 4.通过 ruleModel 向策略规则表中查询策略规则
        StrategyRuleEntity strategyRuleEntity = iStrategyRepository.queryStrategyRule(strategyId, ruleModel);
        if (null == strategyRuleEntity) {
            throw new AppException("404", "策略规则不存在");
        }
        Map<String, List<Integer>> ruleWeightValueMap = strategyRuleEntity.getRuleWeightValues();

        // ruleWeightValueMap 存放的权重规则为 Map<权重值, 奖品ID>
        // 5.根据 ruleWeightValueMap 装配权重策略
        for (String key : ruleWeightValueMap.keySet()) {
            List<Integer> ruleWeightList = ruleWeightValueMap.get(key);
            // 从 strategyAwardEntityList 中将 ruleWeightList 中存在的奖品ID抽取出来
            List<StrategyAwardEntity> awardEntityList = strategyAwardEntityList.stream()
                    .filter(strategyAwardEntity -> ruleWeightList.contains(strategyAwardEntity.getAwardId()))
                    .collect(Collectors.toList());
            // 再进行策略装配
            assembleLotteryStrategy(strategyId + "_" + key, awardEntityList);
        }

        return true;
    }

    /**
     * 抽离的装配抽奖策略方法
     *
     * @param key                     key值
     * @param strategyAwardEntityList 策略奖品列表
     */
    private void assembleLotteryStrategy(String key, List<StrategyAwardEntity> strategyAwardEntityList) {
        // 1.获取最小概率值
        BigDecimal minAwardRate = strategyAwardEntityList.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        // 2.获取总概率值
        BigDecimal totalAwardRate = strategyAwardEntityList.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3.用总概率值除以最小概率值,获得概率的范围,是百分位、千分位、万分位等
        BigDecimal rangeRate = totalAwardRate.divide(minAwardRate, 0, RoundingMode.CEILING);

        // 4.生成奖品的概率查找表,用于快速查找奖品,占位越多,概率越大
        List<Integer> strategyAwardSearchRateList = new ArrayList<>(rangeRate.intValue());
        for (StrategyAwardEntity entity : strategyAwardEntityList) {
            Integer awardId = entity.getAwardId();
            BigDecimal awardRate = entity.getAwardRate();

            // 计算每个奖品填充到概率查找表的数量,循环填充
            int count = awardRate.multiply(rangeRate).setScale(0, RoundingMode.CEILING).intValue();
            for (int i = 0; i < count; i++) {
                strategyAwardSearchRateList.add(awardId);
            }
        }

        // 5.将概率表的顺序打乱
        Collections.shuffle(strategyAwardSearchRateList);

        // 6.生成Map集合
        Map<Integer, Integer> strategyAwardSearchRateMap = new HashMap<>(strategyAwardSearchRateList.size());
        for (int i = 0; i < strategyAwardSearchRateList.size(); i++) {
            strategyAwardSearchRateMap.put(i, strategyAwardSearchRateList.get(i));
        }

        // 7.存储到redis中
        iStrategyRepository.storeStrategyAwardSearchRateMap(key, strategyAwardSearchRateList.size(), strategyAwardSearchRateMap);
    }


    @Override
    public Integer getRandomAwardId(Long strategyId) {
        // 1.从仓储服务的redis中获取概率查找表中的范围值
        int rangeRate = iStrategyRepository.getRangeRate(strategyId);
        // 2.根据范围值生成一个随机数,然后返回奖品ID
        return iStrategyRepository.getStrategyAwardAssemble(strategyId + "", new SecureRandom().nextInt(rangeRate));
    }

    @Override
    public Integer getRandomAwardId(Long strategyId, String ruleWeightValue) {
        String key = strategyId + "_" + ruleWeightValue;
        // 1.从仓储服务的redis中获取概率查找表中的范围值
        int rangeRate = iStrategyRepository.getRangeRate(key);
        // 2.根据范围值生成一个随机数,然后返回奖品ID
        return iStrategyRepository.getStrategyAwardAssemble(key, new SecureRandom().nextInt(rangeRate));
    }
}
