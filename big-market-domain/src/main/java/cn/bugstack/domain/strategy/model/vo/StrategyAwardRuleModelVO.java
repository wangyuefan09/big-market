package cn.bugstack.domain.strategy.model.vo;

import cn.bugstack.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import cn.bugstack.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

/**
 * @author wangyuefan
 * @date 2024/7/15 15:07
 * @describe
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyAwardRuleModelVO {

    private String ruleModels;

    public String[] raffleCenterRuleModels() {
        String[] ruleModelValues = ruleModels.split(Constants.SPLIT);
        return Arrays.stream(ruleModelValues)
                .filter(DefaultLogicFactory.LogicModel::isCenter)
                .toArray(String[]::new);
    }
}
