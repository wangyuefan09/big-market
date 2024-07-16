package cn.bugstack.domain.strategy.service.annotation;

import cn.bugstack.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wangyuefan
 * @date 2024/7/11 16:44
 * @describe 策略自定义注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogicStrategy {

    DefaultLogicFactory.LogicModel logicModel();

}
