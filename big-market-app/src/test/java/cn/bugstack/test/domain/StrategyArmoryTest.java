package cn.bugstack.test.domain;

import cn.bugstack.domain.strategy.service.IStrategyArmory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author wangyuefan
 * @date 2024/7/9 15:57
 * @describe
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyArmoryTest {

    @Resource
    private IStrategyArmory iStrategyArmory;

    @Before
    public void assembleLotteryStrategyTest() {
        iStrategyArmory.assembleLotteryStrategy(100001L);
    }

    @Test
    public void getRandomAwardIdTest() {
        for (int i = 0; i < 10; i++) {
            log.info("测试结果：{} - 奖品ID值", iStrategyArmory.getRandomAwardId(100001L));
        }
    }

    @Test
    public void getRandomAwardIdRuleWeightTest() {
        log.info("测试结果：{} - 4000 策略结果", iStrategyArmory.getRandomAwardId(100001L, "4000:102,103,104,105"));
        log.info("测试结果：{} - 5000 策略结果", iStrategyArmory.getRandomAwardId(100001L, "5000:102,103,104,105,106,107"));
        log.info("测试结果：{} - 6000 策略结果", iStrategyArmory.getRandomAwardId(100001L, "6000:102,103,104,105,106,107,108,109"));

    }
}
