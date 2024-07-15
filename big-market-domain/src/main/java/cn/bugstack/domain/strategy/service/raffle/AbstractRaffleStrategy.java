package cn.bugstack.domain.strategy.service.raffle;

import cn.bugstack.domain.strategy.model.entity.RaffleAwardEntity;
import cn.bugstack.domain.strategy.model.entity.RaffleFactorEntity;
import cn.bugstack.domain.strategy.model.entity.RuleActionEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyEntity;
import cn.bugstack.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.model.vo.StrategyAwardRuleModelVO;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.IRaffleStrategy;
import cn.bugstack.domain.strategy.service.armory.IStrategyArmory;
import cn.bugstack.domain.strategy.service.rule.factory.DefaultLogicFactory;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author wangyuefan
 * @date 2024/7/11 16:07
 * @describe 抽奖策略抽象类
 */
@Slf4j
public abstract class AbstractRaffleStrategy implements IRaffleStrategy {

    private final IStrategyRepository iStrategyRepository;
    private final IStrategyArmory iStrategyArmory;

    public AbstractRaffleStrategy(IStrategyRepository iStrategyRepository, IStrategyArmory iStrategyArmory) {
        this.iStrategyRepository = iStrategyRepository;
        this.iStrategyArmory = iStrategyArmory;
    }

    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity) {
        // 1.参数校验
        Long strategyId = raffleFactorEntity.getStrategyId();
        String userId = raffleFactorEntity.getUserId();
        if (null == strategyId || StringUtils.isEmpty(userId)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        // 2.查询策略
        StrategyEntity strategyEntity = iStrategyRepository.queryStrategyEntityByStrategyId(strategyId);

        // 3.抽奖前: 黑名单+规则过滤
        RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity = this.doCheckRaffleBeforeLogic(raffleFactorEntity, strategyEntity.ruleModels());

        // 4.判断走的过滤流程是否被接管了
        if (ruleActionEntity.getCode().equals(RuleLogicCheckTypeVO.TAKE_OVER.getCode())) {
            if (ruleActionEntity.getRuleModel().equals(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode())) {
                // 情况1: 黑名单 返回固定奖品的ID
                return RaffleAwardEntity.builder()
                        .awardId(ruleActionEntity.getData().getAwardId())
                        .build();

            } else if (ruleActionEntity.getRuleModel().equals(DefaultLogicFactory.LogicModel.RULE_WIGHT.getCode())) {
                // 情况2: 规则权重
                String ruleWeightValueKey = ruleActionEntity.getData().getRuleWeightValueKey();

                Integer awardId = iStrategyArmory.getRandomAwardId(strategyId, ruleWeightValueKey);
                return RaffleAwardEntity.builder()
                        .awardId(awardId)
                        .build();
            }
        }

        // 5.默认的抽奖规则
        Integer randomAwardId = iStrategyArmory.getRandomAwardId(strategyId);

        // 6.查询奖品的规则
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = iStrategyRepository.queryStrategyAwardRuleModels(strategyId, randomAwardId);

        // 7.抽奖中---规则过滤
        RuleActionEntity<RuleActionEntity.RaffleCenterEntity> centerRuleActionEntity = this.doCheckRaffleCenterLogic(
                RaffleFactorEntity.builder()
                        .userId(userId)
                        .strategyId(strategyId)
                        .awardId(randomAwardId)
                        .build(),
                strategyAwardRuleModelVO.raffleCenterRuleModels()
        );

        // 8.判断抽奖中的过滤规则是否被接管了
        if (centerRuleActionEntity.getCode().equals(RuleLogicCheckTypeVO.TAKE_OVER.getCode())) {
            log.info("【临时日志】中奖中规则过滤，通过抽奖后规则 rule_luck_award 走兜底奖励。");
            return RaffleAwardEntity.builder()
                    .awardDesc("中奖中规则过滤，通过抽奖后规则 rule_luck_award 走兜底奖励。")
                    .build();
        }

        return RaffleAwardEntity.builder()
                .awardId(randomAwardId)
                .build();
    }

    protected abstract RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> doCheckRaffleBeforeLogic(RaffleFactorEntity raffleFactorEntity, String... logics);

    protected abstract RuleActionEntity<RuleActionEntity.RaffleCenterEntity> doCheckRaffleCenterLogic(RaffleFactorEntity raffleFactorEntity, String... logics);
}
