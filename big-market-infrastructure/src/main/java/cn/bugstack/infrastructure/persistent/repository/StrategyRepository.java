package cn.bugstack.infrastructure.persistent.repository;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.infrastructure.persistent.dao.IStrategyAwardDao;
import cn.bugstack.infrastructure.persistent.po.StrategyAward;
import cn.bugstack.infrastructure.persistent.redis.IRedisService;
import cn.bugstack.types.common.Constants;
import org.redisson.api.RMap;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wangyuefan
 * @date 2024/7/9 14:29
 * @describe 策略仓储实现
 */
@Repository
public class StrategyRepository implements IStrategyRepository {

    @Resource
    private IStrategyAwardDao iStrategyAwardDao;
    @Resource
    private IRedisService iRedisService;

    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
        // 缓存的策略KEY,先从redis中获取
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_KEY + strategyId;
        List<StrategyAwardEntity> strategyAwardEntityList = iRedisService.getValue(cacheKey);

        if (strategyAwardEntityList != null && !strategyAwardEntityList.isEmpty()) {
            return strategyAwardEntityList;
        }

        // 否则从数据库查询
        List<StrategyAward> strategyAwardList = iStrategyAwardDao.queryStrategyAwardListByStrategyId(strategyId);
        // 转换对象
        strategyAwardEntityList = new ArrayList<>(strategyAwardList.size());
        for (StrategyAward strategyAward : strategyAwardList) {
            StrategyAwardEntity strategyAwardEntity = StrategyAwardEntity.builder()
                    .strategyId(strategyAward.getStrategyId())
                    .awardId(strategyAward.getAwardId())
                    .awardCount(strategyAward.getAwardCount())
                    .awardCountSurplus(strategyAward.getAwardCountSurplus())
                    .awardRate(strategyAward.getAwardRate())
                    .build();

            strategyAwardEntityList.add(strategyAwardEntity);
        }

        // 存储到redis中
        iRedisService.setValue(cacheKey, strategyAwardEntityList);
        return strategyAwardEntityList;
    }

    @Override
    public void storeStrategyAwardSearchRateMap(Long strategyId, Integer rangeRate, Map<Integer, Integer> strategyAwardSearchRateMap) {
        // 1.存储策略抽奖的概率范围值,如10000,用于生成10000以内的随机数
        iRedisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId, rangeRate);
        // 2.存储概率查找Map
        RMap<Integer, Integer> cacheMap = iRedisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId);
        cacheMap.putAll(strategyAwardSearchRateMap);
    }

    @Override
    public int getRangeRate(Long strategyId) {
        // 通过key直接从redis中获取即可
        return iRedisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId);
    }

    @Override
    public Integer getStrategyAwardAssemble(Long strategyId, int randomNum) {
        return iRedisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId, randomNum);
    }
}
