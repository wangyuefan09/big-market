package cn.bugstack.domain.strategy.service.rule.chain;

/**
 * @author wangyuefan
 * @date 2024/7/15 16:58
 * @describe
 */
public abstract class AbstractLogicChain implements ILogicChain{

    private ILogicChain next;

    @Override
    public ILogicChain appendNext(ILogicChain next) {
        this.next = next;
        return next;
    }

    @Override
    public ILogicChain next() {
        return next;
    }

    protected abstract String ruleModel();
}
