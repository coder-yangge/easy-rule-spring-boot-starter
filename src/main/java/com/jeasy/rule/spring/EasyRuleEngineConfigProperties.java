package com.jeasy.rule.spring;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author yangge
 * @version 1.0.0
 * @date 2020/6/16 14:55
 */
@Data
@ConfigurationProperties(prefix = "spring.easy-rule")
public class EasyRuleEngineConfigProperties {

    private boolean skipOnFirstAppliedRule;

    private boolean skipOnFirstFailedRule;

    private boolean skipOnFirstNonTriggeredRule;

    private int priorityThreshold;

    private List<RuleConfiguration> rules;
}
