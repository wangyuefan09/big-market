package cn.bugstack.domain.strategy.service.rule.chain.factory;

import cn.bugstack.domain.strategy.model.entity.StrategyEntity;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.rule.chain.ILogicChain;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author wangyuefan
 * @date 2024/7/16 14:50
 * @describe 责任链工厂
 */
@Slf4j
@Service
public class DefaultChainFactory {

    private final Map<String, ILogicChain> logicChainMap;
    private final IStrategyRepository iStrategyRepository;

    public DefaultChainFactory(Map<String, ILogicChain> logicChainMap, IStrategyRepository iStrategyRepository) {
        this.logicChainMap = logicChainMap;
        this.iStrategyRepository = iStrategyRepository;
    }

    public ILogicChain openLogicChain(Long strategyId) {
        StrategyEntity strategy = iStrategyRepository.queryStrategyEntityByStrategyId(strategyId);
        String[] ruledModels = strategy.ruleModels();

        if (null == ruledModels || 0 == ruledModels.length) {
            return logicChainMap.get(LogicModel.RULE_DEFAULT.getCode());
        }

        ILogicChain logicChain = logicChainMap.get(ruledModels[0]);
        ILogicChain current = logicChain;
        for (int i = 1; i < ruledModels.length; i++) {
            ILogicChain nextChain = logicChainMap.get(ruledModels[i]);
            current = logicChain.appendNext(nextChain);
        }
        current.appendNext(logicChainMap.get(LogicModel.RULE_DEFAULT.getCode()));

        return logicChain;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StrategyAwardVO {
        /**
         * 抽奖奖品ID - 内部流转使用
         */
        private Integer awardId;
        /**
         *
         */
        private String logicModel;
    }

    @Getter
    @AllArgsConstructor
    public enum LogicModel {

        RULE_DEFAULT("rule_default", "默认抽奖"),
        RULE_BLACKLIST("rule_blacklist", "黑名单抽奖"),
        RULE_WEIGHT("rule_weight", "权重规则"),
        ;

        private final String code;
        private final String info;

    }

}
