package cn.bugstack.test.domain;

import cn.bugstack.domain.strategy.model.entity.RaffleAwardEntity;
import cn.bugstack.domain.strategy.model.entity.RaffleFactorEntity;
import cn.bugstack.domain.strategy.service.IRaffleStrategy;
import cn.bugstack.domain.strategy.service.armory.IStrategyArmory;
import cn.bugstack.domain.strategy.service.rule.impl.RuleLockLogFilter;
import cn.bugstack.domain.strategy.service.rule.impl.RuleWeightLogicFilter;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;

/**
 * @author wangyuefan
 * @date 2024/7/12 15:09
 * @describe
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleStrategyTest {

    @Resource
    private IRaffleStrategy iRaffleStrategy;
    @Resource
    private IStrategyArmory iStrategyArmory;
    @Resource
    private RuleWeightLogicFilter ruleWeightLogicFilter;
    @Resource
    private RuleLockLogFilter ruleLockLogFilter;

    @Before
    public void setUp() {
        // 装配策略
        // log.info("测试结果：{}", iStrategyArmory.assembleLotteryStrategy(100001L));
        log.info("测试结果：{}", iStrategyArmory.assembleLotteryStrategy(100003L));

        ReflectionTestUtils.setField(ruleWeightLogicFilter, "userScore", 7500L);
        ReflectionTestUtils.setField(ruleLockLogFilter, "userRaffleCount", 10L);
    }

    @Test
    public void performRaffleTest() {
        RaffleFactorEntity raffleFactor = RaffleFactorEntity.builder()
                .userId("wyf")
                .strategyId(100001L)
                .build();

        RaffleAwardEntity raffleAwardEntity = iRaffleStrategy.performRaffle(raffleFactor);

        log.info("请求参数：{}", JSON.toJSONString(raffleFactor));
        log.info("返回结果：{}", JSON.toJSONString(raffleAwardEntity));
    }

    @Test
    public void performRaffleBlackListTest() {
        RaffleFactorEntity raffleFactor = RaffleFactorEntity.builder()
                .userId("user001")
                .strategyId(100001L)
                .build();

        RaffleAwardEntity raffleAwardEntity = iRaffleStrategy.performRaffle(raffleFactor);

        log.info("请求参数：{}", JSON.toJSONString(raffleFactor));
        log.info("返回结果：{}", JSON.toJSONString(raffleAwardEntity));
    }

    @Test
    public void performRaffleLockTest() {
        RaffleFactorEntity raffleFactor = RaffleFactorEntity.builder()
                .userId("wyf")
                .strategyId(100003L)
                .build();

        RaffleAwardEntity raffleAwardEntity = iRaffleStrategy.performRaffle(raffleFactor);

        log.info("请求参数：{}", JSON.toJSONString(raffleFactor));
        log.info("返回结果：{}", JSON.toJSONString(raffleAwardEntity));
    }
}
