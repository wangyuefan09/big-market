package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.StrategyAward;

import java.util.List;

/**
 * @author wangyuefan
 * @date 2024/7/8 14:58
 * @describe
 */
public interface IStrategyAwardDao {

    /**
     * 查询策略奖品列表
     *
     * @return 策略奖品列表
     */
    List<StrategyAward> queryStrategyAwardList();

}
