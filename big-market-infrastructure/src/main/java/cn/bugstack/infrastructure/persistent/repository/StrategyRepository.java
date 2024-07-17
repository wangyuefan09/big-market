package cn.bugstack.infrastructure.persistent.repository;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyRuleEntity;
import cn.bugstack.domain.strategy.model.vo.*;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.infrastructure.persistent.dao.*;
import cn.bugstack.infrastructure.persistent.po.*;
import cn.bugstack.infrastructure.persistent.redis.IRedisService;
import cn.bugstack.types.common.Constants;
import org.redisson.api.RMap;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
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
    private IStrategyRuleDao iStrategyRuleDao;
    @Resource
    private IStrategyDao iStrategyDao;
    @Resource
    private IStrategyAwardDao iStrategyAwardDao;
    @Resource
    private IRedisService iRedisService;
    @Resource
    private IRuleTreeDao iRuleTreeDao;
    @Resource
    private IRuleTreeNodeDao iRuleTreeNodeDao;
    @Resource
    private IRuleTreeNodeLineDao iRuleTreeNodeLineDao;

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
    public void storeStrategyAwardSearchRateMap(String key, Integer rangeRate, Map<Integer, Integer> strategyAwardSearchRateMap) {
        // 1.存储策略抽奖的概率范围值,如10000,用于生成10000以内的随机数
        iRedisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key, rangeRate);
        // 2.存储概率查找Map
        RMap<Integer, Integer> cacheMap = iRedisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key);
        cacheMap.putAll(strategyAwardSearchRateMap);
    }

    @Override
    public int getRangeRate(Long strategyId) {
        // 通过key直接从redis中获取即可
        return getRangeRate(strategyId + "");
    }

    @Override
    public int getRangeRate(String key) {
        // 通过key直接从redis中获取即可
        return iRedisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key);
    }

    @Override
    public Integer getStrategyAwardAssemble(String key, int randomNum) {
        return iRedisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key, randomNum);
    }

    @Override
    public StrategyEntity queryStrategyEntityByStrategyId(Long strategyId) {
        // 优先先从 redis 中获取
        String cacheKey = Constants.RedisKey.STRATEGY_KEY + strategyId;
        StrategyEntity strategyEntity = iRedisService.getValue(cacheKey);
        if (null != strategyEntity) {
            return strategyEntity;
        }

        // redis 中没有从数据库查询并保存
        Strategy strategy = iStrategyDao.queryStrategyById(strategyId);
        strategyEntity = StrategyEntity.builder()
                .strategyId(strategy.getStrategyId())
                .strategyDesc(strategy.getStrategyDesc())
                .ruleModels(strategy.getRuleModels())
                .build();

        // 保存到 redis 中
        iRedisService.setValue(cacheKey, strategyEntity);
        return strategyEntity;
    }

    @Override
    public StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleModel) {
        // 数据库交互, 查询策略规则
        StrategyRule strategyRule = iStrategyRuleDao.queryStrategyRule(strategyId, ruleModel);
        return StrategyRuleEntity.builder()
                .strategyId(strategyRule.getStrategyId())
                .awardId(strategyRule.getAwardId())
                .ruleType(strategyRule.getRuleType())
                .ruleModel(strategyRule.getRuleModel())
                .ruleValue(strategyRule.getRuleValue())
                .ruleDesc(strategyRule.getRuleDesc())
                .build();
    }

    @Override
    public String queryStrategyRuleValue(Long strategyId, String ruleModel) {
        return queryStrategyRuleValue(strategyId, null, ruleModel);
    }

    @Override
    public String queryStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel) {
        StrategyRule strategyRule = new StrategyRule();
        strategyRule.setStrategyId(strategyId);
        strategyRule.setAwardId(awardId);
        strategyRule.setRuleModel(ruleModel);
        return iStrategyRuleDao.queryStrategyValue(strategyRule);
    }

    @Override
    public StrategyAwardRuleModelVO queryStrategyAwardRuleModels(Long strategyId, Integer awardId) {
        String ruleModels = iStrategyAwardDao.queryStrategyAwardRuleModels(strategyId, awardId);
        return StrategyAwardRuleModelVO.builder()
                .ruleModels(ruleModels)
                .build();
    }

    @Override
    public RuleTreeVO queryRuleTreeByTreeId(String treeId) {
        // 优先从缓存获取
        String cacheKey = Constants.RedisKey.RULE_TREE_VO_KEY + treeId;
        RuleTreeVO ruleTreeVOCache = iRedisService.getValue(cacheKey);
        if (null != ruleTreeVOCache) {
            return ruleTreeVOCache;
        }

        // 从数据库获取
        RuleTree ruleTree = iRuleTreeDao.queryRuleTreeByTreeId(treeId);
        List<RuleTreeNode> ruleTreeNodes = iRuleTreeNodeDao.queryRuleTreeNodeListByTreeId(treeId);
        List<RuleTreeNodeLine> ruleTreeNodeLines = iRuleTreeNodeLineDao.queryRuleTreeNodeLineListByTreeId(treeId);

        // 1. tree node line 转换Map结构
        Map<String, List<RuleTreeNodeLineVO>> ruleTreeNodeLineMap = new HashMap<>();
        for (RuleTreeNodeLine ruleTreeNodeLine : ruleTreeNodeLines) {
            RuleTreeNodeLineVO ruleTreeNodeLineVO = RuleTreeNodeLineVO.builder()
                    .treeId(ruleTreeNodeLine.getTreeId())
                    .ruleNodeFrom(ruleTreeNodeLine.getRuleNodeFrom())
                    .ruleNodeTo(ruleTreeNodeLine.getRuleNodeTo())
                    .ruleLimitType(RuleLimitTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitType()))
                    .ruleLimitValue(RuleLogicCheckTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitValue()))
                    .build();

            List<RuleTreeNodeLineVO> ruleTreeNodeLineVOList = ruleTreeNodeLineMap.computeIfAbsent(ruleTreeNodeLine.getRuleNodeFrom(), k -> new ArrayList<>());
            ruleTreeNodeLineVOList.add(ruleTreeNodeLineVO);
        }

        // 2. tree node 转换为Map结构
        Map<String, RuleTreeNodeVO> treeNodeMap = new HashMap<>();
        for (RuleTreeNode ruleTreeNode : ruleTreeNodes) {
            RuleTreeNodeVO ruleTreeNodeVO = RuleTreeNodeVO.builder()
                    .treeId(ruleTreeNode.getTreeId())
                    .ruleKey(ruleTreeNode.getRuleKey())
                    .ruleDesc(ruleTreeNode.getRuleDesc())
                    .ruleValue(ruleTreeNode.getRuleValue())
                    .treeNodeLineVOList(ruleTreeNodeLineMap.get(ruleTreeNode.getRuleKey()))
                    .build();
            treeNodeMap.put(ruleTreeNode.getRuleKey(), ruleTreeNodeVO);
        }

        // 3. 构建 Rule Tree
        RuleTreeVO ruleTreeVODB = RuleTreeVO.builder()
                .treeId(ruleTree.getTreeId())
                .treeName(ruleTree.getTreeName())
                .treeDesc(ruleTree.getTreeDesc())
                .treeRootRuleNode(ruleTree.getTreeRootRuleKey())
                .treeNodeMap(treeNodeMap)
                .build();

        iRedisService.setValue(cacheKey, ruleTreeVODB);
        return ruleTreeVODB;

    }
}
