package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.Award;

import java.util.List;

/**
 * @author wangyuefan
 * @date 2024/7/8 14:56
 * @describe
 */
public interface IAwardDao {

    /**
     * 查询奖品列表
     *
     * @return 奖品列表
     */
    List<Award> queryAwardList();

}
