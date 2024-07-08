package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.Strategy;

import java.util.List;

/**
 * @author wangyuefan
 * @date 2024/7/8 14:57
 * @describe
 */
public interface IStrategyDao {

    /**
     * 查询策略列表
     *
     * @return 策略列表
     */
    List<Strategy> queryStrategyList();
}
