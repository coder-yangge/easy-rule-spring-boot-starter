package com.jeasy.rule.spring;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.jeasy.rules.support.reader.RuleDefinitionReader;
import org.mvel2.ParserContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * @author yangge
 * @version 1.0.0
 * @title: MVELRuleObjectFactory
 * @description: TODO
 * @date 2020/6/17 10:23
 */
@Setter
@Getter
@Slf4j
public class MVELRuleObjectFactory extends Rules implements ObjectFactory {

    private ParserContext parserContext;

    private RuleDefinitionReader reader;

    private RuleConfiguration ruleConfiguration;

    public MVELRuleObjectFactory(RuleDefinitionReader reader, RuleConfiguration ruleConfiguration) {
        this.reader = reader;
        this.ruleConfiguration = ruleConfiguration;
    }

    @Override
    public Object getObject() throws BeansException {
        MVELRuleFactory ruleFactory = new MVELRuleFactory(reader, parserContext);
        try {
            return ruleFactory.createRules(new FileReader(ResourceUtils.getFile(ruleConfiguration.getRuleFileLocation())));
        } catch (FileNotFoundException e) {
            log.error("rule config id: {} configuration location {} not found", ruleConfiguration.getRuleId(), ruleConfiguration.getRuleFileLocation());
            e.printStackTrace();
        } catch (Exception e) {
            log.error("rule config id: {} config file illegal", ruleConfiguration.getRuleId(), ruleConfiguration.getRuleFileLocation());
            e.printStackTrace();
        }
        return null;
    }
}
