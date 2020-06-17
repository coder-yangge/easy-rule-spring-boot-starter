package com.jeasy.rule.spring;

import lombok.Data;

/**
 * @author yangge
 * @version 1.0.0
 * @title: RuleConfiguration
 * @date 2020/6/16 15:20
 */
@Data
public class RuleConfiguration {

    private String ruleId;

    private String ruleFileLocation;

    private RuleConfigType ruleConfigType;

    private RuleFactoryType ruleFactoryType;
}
