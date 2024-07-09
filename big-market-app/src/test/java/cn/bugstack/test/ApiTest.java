package cn.bugstack.test;

import cn.bugstack.infrastructure.persistent.dao.IAwardDao;
import cn.bugstack.infrastructure.persistent.po.Award;
import cn.bugstack.infrastructure.persistent.redis.IRedisService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Resource
    private IRedisService iRedisService;
}
