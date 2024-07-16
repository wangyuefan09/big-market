package cn.bugstack.domain.strategy.service.rule.filter.impl;

import cn.bugstack.domain.strategy.model.entity.RuleActionEntity;
import cn.bugstack.domain.strategy.model.entity.RuleMatterEntity;
import cn.bugstack.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.annotation.LogicStrategy;
import cn.bugstack.domain.strategy.service.rule.filter.ILogicFilter;
import cn.bugstack.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import cn.bugstack.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author wangyuefan
 * @date 2024/7/11 17:11
 * @describe 黑名单规则过滤
 */
@Slf4j
@Component
@LogicStrategy(logicModel = DefaultLogicFactory.LogicModel.RULE_BLACKLIST)
public class RuleBlackListLogicFilter implements ILogicFilter<RuleActionEntity.RaffleBeforeEntity> {

    @Resource
    private IStrategyRepository iStrategyRepository;

    @Override
    public RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> filter(RuleMatterEntity ruleMatterEntity) {
        String userId = ruleMatterEntity.getUserId();
        Long strategyId = ruleMatterEntity.getStrategyId();
        String ruleModel = ruleMatterEntity.getRuleModel();
        log.info("规则过滤-黑名单 userId:{} strategyId:{} ruleModel:{}", userId, strategyId, ruleModel);

        // 查询策略规则的值, 数据示例 100:user001,user002
        String ruleValue = iStrategyRepository.queryStrategyRuleValue(strategyId, ruleMatterEntity.getAwardId(), ruleModel);
        String[] splitRuleValue = ruleValue.split(Constants.COLON);
        Integer awardId = Integer.valueOf(splitRuleValue[0]);

        // 黑名单列表
        List<String> blackList = Arrays.asList(splitRuleValue[1].split(Constants.SPLIT));
        boolean flag = blackList.stream()
                .anyMatch(data -> data.equals(userId));
        if (flag) {
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .ruleModel(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode())
                    .data(
                            RuleActionEntity.RaffleBeforeEntity.builder()
                                    .strategyId(ruleMatterEntity.getStrategyId())
                                    .awardId(awardId)
                                    .build()
                    )
                    .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                    .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                    .build();
        }

        return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                .build();
    }
}
