package cn.bugstack.test.domain;

import cn.bugstack.domain.strategy.service.armory.IStrategyArmory;
import lombok.extern.slf4j.Slf4j;
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

    @Test
    public void assembleLotteryStrategyTest() {
        iStrategyArmory.assembleLotteryStrategy(100001L);
    }

    @Test
    public void getRandomAwardIdTest() {
        for (int i = 0; i < 100; i++) {
            log.info("测试结果：{} - 奖品ID值", iStrategyArmory.getRandomAwardId(100001L));
        }
    }
}
