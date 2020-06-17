package com.jeasy.rule.spring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author yangge
 * @version 1.0.0
 * @title: EnableEasyRules
 * @date 2020/6/16 16:05
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EasyRuleAutoConfiguration.class)
public @interface EnableEasyRules {

}
