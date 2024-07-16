package cn.bugstack.domain.strategy.service.raffle;

import cn.bugstack.domain.strategy.model.entity.RaffleFactorEntity;
import cn.bugstack.domain.strategy.model.entity.RuleActionEntity;
import cn.bugstack.domain.strategy.model.entity.RuleMatterEntity;
import cn.bugstack.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.armory.IStrategyArmory;
import cn.bugstack.domain.strategy.service.rule.filter.ILogicFilter;
import cn.bugstack.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wangyuefan
 * @date 2024/7/12 14:33
 * @describe
 */
@Slf4j
@Service
public class DefaultRaffleStrategy extends AbstractRaffleStrategy {

    @Resource
    private DefaultLogicFactory logicFactory;

    public DefaultRaffleStrategy(IStrategyRepository iStrategyRepository, IStrategyArmory iStrategyArmory) {
        super(iStrategyRepository, iStrategyArmory);
    }

    @Override
    protected RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> doCheckRaffleBeforeLogic(RaffleFactorEntity raffleFactorEntity, String... logics) {
        // 过滤规则为空, 返回默认放行
        if (null == logics || 0 == logics.length) {
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }
        Map<String, ILogicFilter<RuleActionEntity.RaffleBeforeEntity>> logicFilterMap = logicFactory.openLogicFilter();

        // 黑名单优先进行过滤
        String ruleBlackList = Arrays.stream(logics)
                .filter(logic -> logic.contains(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode()))
                .findFirst()
                .orElse(null);

        if (StringUtils.isNotBlank(ruleBlackList)) {
            ILogicFilter<RuleActionEntity.RaffleBeforeEntity> logicFilter = logicFilterMap.get(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode());

            RuleMatterEntity ruleMatterEntity = RuleMatterEntity.builder()
                    .strategyId(raffleFactorEntity.getStrategyId())
                    .userId(raffleFactorEntity.getUserId())
                    .ruleModel(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode())
                    .build();
            RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity = logicFilter.filter(ruleMatterEntity);

            if (!ruleActionEntity.getCode().equals(RuleLogicCheckTypeVO.ALLOW.getCode())) {
                return ruleActionEntity;
            }
        }

        // 过滤其他规则
        List<String> ruleList = Arrays.stream(logics)
                .filter(logic -> !logic.equals(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode()))
                .collect(Collectors.toList());
        RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity = null;
        for (String ruleModel : ruleList) {
            ILogicFilter<RuleActionEntity.RaffleBeforeEntity> logicFilter = logicFilterMap.get(ruleModel);

            RuleMatterEntity ruleMatterEntity = RuleMatterEntity.builder()
                    .strategyId(raffleFactorEntity.getStrategyId())
                    .userId(raffleFactorEntity.getUserId())
                    .ruleModel(ruleModel)
                    .build();

            ruleActionEntity = logicFilter.filter(ruleMatterEntity);
            // 非放行结果则顺序过滤
            log.info("抽奖前规则过滤 userId: {} ruleModel: {} code: {} info: {}", raffleFactorEntity.getUserId(), ruleModel, ruleActionEntity.getCode(), ruleActionEntity.getInfo());
            if (!RuleLogicCheckTypeVO.ALLOW.getCode().equals(ruleActionEntity.getCode())) {
                return ruleActionEntity;
            }
        }

        return ruleActionEntity;
    }

    @Override
    protected RuleActionEntity<RuleActionEntity.RaffleCenterEntity> doCheckRaffleCenterLogic(RaffleFactorEntity raffleFactorEntity, String... logics) {
        // 过滤规则为空, 返回默认放行
        if (null == logics || 0 == logics.length) {
            return RuleActionEntity.<RuleActionEntity.RaffleCenterEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }
        Map<String, ILogicFilter<RuleActionEntity.RaffleCenterEntity>> logicFilterMap = logicFactory.openLogicFilter();

        RuleActionEntity<RuleActionEntity.RaffleCenterEntity> ruleActionEntity = null;
        for (String ruleModel : logics) {
            ILogicFilter<RuleActionEntity.RaffleCenterEntity> logicFilter = logicFilterMap.get(ruleModel);

            RuleMatterEntity ruleMatterEntity = RuleMatterEntity.builder()
                    .strategyId(raffleFactorEntity.getStrategyId())
                    .userId(raffleFactorEntity.getUserId())
                    .awardId(raffleFactorEntity.getAwardId())
                    .ruleModel(ruleModel)
                    .build();

            ruleActionEntity = logicFilter.filter(ruleMatterEntity);
            // 非放行结果则顺序过滤
            log.info("抽奖中规则过滤 userId: {} ruleModel: {} code: {} info: {}", raffleFactorEntity.getUserId(), ruleModel, ruleActionEntity.getCode(), ruleActionEntity.getInfo());
            if (!RuleLogicCheckTypeVO.ALLOW.getCode().equals(ruleActionEntity.getCode())) {
                return ruleActionEntity;
            }
        }

        return ruleActionEntity;
    }
}
