package cn.bugstack.domain.strategy.service.rule.chain.factory;

import cn.bugstack.domain.strategy.model.entity.StrategyEntity;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.rule.chain.ILogicChain;
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
            return logicChainMap.get("default");
        }

        ILogicChain logicChain = logicChainMap.get(ruledModels[0]);
        ILogicChain current = logicChain;
        for (int i = 1; i < ruledModels.length; i++) {
            ILogicChain nextChain = logicChainMap.get(ruledModels[i]);
            current = logicChain.appendNext(nextChain);
        }
        current.appendNext(logicChainMap.get("default"));

        return logicChain;
    }
}
