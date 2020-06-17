package com.jeasy.rule.spring;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.jeasy.rules.spel.SpELRuleFactory;
import org.jeasy.rules.support.reader.JsonRuleDefinitionReader;
import org.jeasy.rules.support.reader.RuleDefinitionReader;
import org.jeasy.rules.support.reader.YamlRuleDefinitionReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.expression.BeanResolver;
import org.springframework.util.ResourceUtils;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yangge
 * @version 1.0.0
 * @title: RuleEngineTemplate
 * @date 2020/6/16 15:31
 */
@Data
@Slf4j
public class RuleEngineTemplate implements InitializingBean {

    private EasyRuleEngineConfigProperties properties;

    private RulesEngine rulesEngine;

    private BeanResolver beanResolver;

    private Map<String, Rules> rules = new HashMap<>();


    /**
     * fire rule
     * @param rules 规则
     * @param facts 参数
     */
    public void fire(Rules rules, Facts facts) {
        rulesEngine.fire(rules, facts);
    }

    /**
     * fire rule use SpEl default
     * @param ruleId 规则ID
     * @param facts 参数
     */
    public void fire(String ruleId, Facts facts) {
        Rules rules = this.rules.get(ruleId);
        if (rules == null) {
            throw new RuntimeException("rule id: " + ruleId + "not define, please check");
        }
        rulesEngine.fire(rules, facts);
    }



    @Override
    public void afterPropertiesSet() throws Exception{
        if (properties.getRules().isEmpty()) {
            log.info("rule config is empty");
        }
        for (RuleConfiguration ruleConfiguration : properties.getRules()) {
            RuleDefinitionReader ruleDefinitionReader;
            switch (ruleConfiguration.getRuleConfigType()) {
                case JSON:
                    ruleDefinitionReader = new JsonRuleDefinitionReader();
                    break;
                case YAML:
                    ruleDefinitionReader = new YamlRuleDefinitionReader();
                    break;
                default:
                    throw new IllegalStateException("Illegal rule configType: " + ruleConfiguration.getRuleConfigType());
            }
            try {
                Rules rule = null;
                switch (ruleConfiguration.getRuleFactoryType()) {
                    case MVEL:
                        MVELRuleFactory mvelRuleFactory = new MVELRuleFactory(ruleDefinitionReader);
                        rule = mvelRuleFactory.createRules(new FileReader(ResourceUtils.getFile(ruleConfiguration.getRuleFileLocation())));
                        break;
                    case SPEL:
                        SpELRuleFactory spELRuleFactory = new SpELRuleFactory(ruleDefinitionReader, beanResolver);
                        rule = spELRuleFactory.createRules(new FileReader(ResourceUtils.getFile(ruleConfiguration.getRuleFileLocation())));
                        break;
                    default:
                        throw new IllegalStateException("Illegal rule factoryType: " + ruleConfiguration.getRuleConfigType());
                }
                rules.put(ruleConfiguration.getRuleId(), rule);
            } catch (FileNotFoundException e) {
                log.error("rule config id: {} configuration location {} not found", ruleConfiguration.getRuleId(), ruleConfiguration.getRuleFileLocation());
                e.printStackTrace();
                throw e;
            } catch (Exception e) {
                log.error("rule config id: {} config file illegal", ruleConfiguration.getRuleId(), ruleConfiguration.getRuleFileLocation());
                e.printStackTrace();
                throw e;
            }
        }
    }

}
