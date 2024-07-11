package cn.bugstack.domain.strategy.model.entity;

import cn.bugstack.types.common.Constants;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author wangyuefan
 * @date 2024/7/11 10:23
 * @describe
 */
@Data
@Builder
public class StrategyEntity {

    /**
     * 抽奖策略ID
     */
    private Long strategyId;

    /**
     * 抽奖策略描述
     */
    private String strategyDesc;

    /**
     * 规则模型
     */
    private String ruleModels;

    /**
     * ruleModels是英文逗号分割的字符串,这里进行切割
     *
     * @return 字符串数组
     */
    public String[] ruleModels() {
        if (StringUtils.isBlank(ruleModels)) {
            return null;
        }
        return ruleModels.split(Constants.SPLIT);
    }

    /**
     * 获取规则权重
     *
     * @return 字符串类型的规则权重
     */
    public String getRuleWeight() {
        String[] ruledModels = this.ruleModels();
        for (String ruleModel : ruledModels) {
            if ("rule_weight".equals(ruleModel)) {
                return ruleModel;
            }
        }
        return null;
    }
}
