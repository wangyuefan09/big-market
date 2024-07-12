package cn.bugstack.domain.strategy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wangyuefan
 * @date 2024/7/11 16:27
 * @describe 规则过滤校验类型枚举
 */
@Getter
@AllArgsConstructor
public enum RuleLogicCheckTypeVO {

    ALLOW("0000", "放行；执行后续的流程，不受规则引擎的影响。"),
    TAKE_OVER("0001", "接管；后续的流程，受规则引擎执行结果的影响。");

    private final String code;
    private final String info;
}
