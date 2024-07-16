package cn.bugstack.domain.strategy.service.rule.chain.impl;

import cn.bugstack.domain.strategy.service.rule.chain.ILogicChain;

/**
 * @author wangyuefan
 * @date 2024/7/16 14:51
 * @describe
 */
public interface ILogicChainAmory {

    ILogicChain appendNext(ILogicChain next);

    ILogicChain next();
}
