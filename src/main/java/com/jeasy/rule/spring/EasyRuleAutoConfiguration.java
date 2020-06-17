package com.jeasy.rule.spring;

import lombok.extern.slf4j.Slf4j;
import org.jeasy.rules.api.*;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author yangge
 * @version 1.0.0
 * @title: EasyRuleAutoConfiguration
 * @date 2020/6/16 14:49
 */
@Slf4j
@EnableConfigurationProperties(EasyRuleEngineConfigProperties.class)
public class EasyRuleAutoConfiguration implements BeanFactoryAware {

    @Autowired(required = false)
    private List<RuleListener> ruleListeners;

    @Autowired(required = false)
    private List<RulesEngineListener> rulesEngineListeners;

    private BeanFactory beanFactory;

    @Bean
    @ConditionalOnMissingBean
    public RulesEngineParameters rulesEngineParameters(EasyRuleEngineConfigProperties properties) {
        RulesEngineParameters parameters = new RulesEngineParameters();
        parameters.setPriorityThreshold(properties.getPriorityThreshold());
        parameters.setSkipOnFirstAppliedRule(properties.isSkipOnFirstAppliedRule());
        parameters.setSkipOnFirstFailedRule(properties.isSkipOnFirstFailedRule());
        parameters.setSkipOnFirstNonTriggeredRule(properties.isSkipOnFirstNonTriggeredRule());
        return parameters;
    }

    @Bean
    @ConditionalOnMissingBean(RulesEngine.class)
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public RulesEngine rulesEngine(RulesEngineParameters rulesEngineParameters) {
        DefaultRulesEngine rulesEngine = new DefaultRulesEngine(rulesEngineParameters);
        if (!CollectionUtils.isEmpty(ruleListeners)) {
            rulesEngine.registerRuleListeners(ruleListeners);
        }
        if (!CollectionUtils.isEmpty(rulesEngineListeners)) {
            rulesEngine.registerRulesEngineListeners(rulesEngineListeners);
        }
        return rulesEngine;
    }

    @Bean
    @ConditionalOnMissingBean
    public BeanResolver beanResolver() {
        return new BeanFactoryResolver(beanFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public RuleEngineTemplate ruleEngineTemplate(EasyRuleEngineConfigProperties properties, RulesEngine rulesEngine) {
        RuleEngineTemplate ruleEngineTemplate = new RuleEngineTemplate();
        ruleEngineTemplate.setBeanResolver(beanResolver());
        ruleEngineTemplate.setProperties(properties);
        ruleEngineTemplate.setRulesEngine(rulesEngine);
        return ruleEngineTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public RuleListener defaultRuleListener() {
        return new RuleListener() {
            @Override
            public boolean beforeEvaluate(Rule rule, Facts facts) {
                return true;
            }

            @Override
            public void afterEvaluate(Rule rule, Facts facts, boolean evaluationResult) {

            }

            @Override
            public void onEvaluationError(Rule rule, Facts facts, Exception exception) {

            }

            @Override
            public void beforeExecute(Rule rule, Facts facts) {
                log.info("DefaultRulesListener: rule name: {} rule desc: {}", rule.getName(), rule.getDescription() + facts.toString());

            }

            @Override
            public void onSuccess(Rule rule, Facts facts) {

            }

            @Override
            public void onFailure(Rule rule, Facts facts, Exception exception) {

            }
        };
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
