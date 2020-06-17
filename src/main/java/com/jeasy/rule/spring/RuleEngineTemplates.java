package com.jeasy.rule.spring;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.spel.SpELRuleFactory;
import org.jeasy.rules.support.reader.JsonRuleDefinitionReader;
import org.jeasy.rules.support.reader.RuleDefinitionReader;
import org.jeasy.rules.support.reader.YamlRuleDefinitionReader;
import org.mvel2.ParserContext;
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
public class RuleEngineTemplates implements InitializingBean {

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
        fire(ruleId, facts, RuleFactoryType.SPEL, null);
    }

    /**
     * fire rule use mVEL
     * @param ruleId 规则ID
     * @param facts 参数
     * @param
     */
    public void fire(String ruleId, Facts facts, ParserContext parserContext ) {
        fire(ruleId, facts, RuleFactoryType.MVEL, parserContext);
    }

    /**
     * fire rule
     * @param ruleId 规则ID
     * @param facts 参数
     * @param factoryType 规则配置类型
     */
    public void fire(String ruleId, Facts facts, RuleFactoryType factoryType, ParserContext parserContext) {
        Rules rules = this.rules.get(ruleId);
        if (rules == null) {
            throw new RuntimeException("rule id: " + ruleId + "not define, please check");
        }
        if (factoryType == RuleFactoryType.MVEL) {
            MVELRuleObjectFactory objectFactory=  (MVELRuleObjectFactory)rules;
            objectFactory.setParserContext(parserContext);
            rules = (Rules)objectFactory.getObject();
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
                switch (ruleConfiguration.getRuleFactoryType()) {
                    case MVEL:
                        rules.put(ruleConfiguration.getRuleId(), new MVELRuleObjectFactory(ruleDefinitionReader, ruleConfiguration));
                        break;
                    case SPEL:
                        SpELRuleFactory spELRuleFactory = new SpELRuleFactory(ruleDefinitionReader, beanResolver);
                        Rules rule = spELRuleFactory.createRules(new FileReader(ResourceUtils.getFile(ruleConfiguration.getRuleFileLocation())));
                        rules.put(ruleConfiguration.getRuleId(), rule);
                        break;
                    default:
                        throw new IllegalStateException("Illegal rule factoryType: " + ruleConfiguration.getRuleConfigType());
                }
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
