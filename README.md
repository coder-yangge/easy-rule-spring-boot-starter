**规则引擎easyRules集成springboot**

**使用教程**

1. application.yml配置

   ```yaml
   spring:
     easy-rule:
       priority-threshold: 100
       skip-on-first-failed-rule: true
       skip-on-first-applied-rule: false
       skip-on-first-non-triggered-rule: false
       rules:
         - rule-id: "test"
           rule-file-location: "classpath:rule.json" #规则配置文件
           rule-config-type: JSON
           rule-factory-type: SPEL
   ```

2. 开启@EnableEasyRules注解

```java
@SpringBootTest
public class EasyRuleTest {

    @Autowired
    private RuleEngineTemplate ruleEngineTemplate;

    @Test
    public void test() {
        Person person = new Person("Tom", 23);
        Facts facts = new Facts();
        facts.put("person", person);
        ruleEngineTemplate.fire("test", facts); // test为配置文件中规则ID


    }
}

@Component
@Slf4j
public class Log {

    public void log(String message) {
        log.info(message);
    }

}
```

3. 规则配置文件

   ```json
   [{
     "name": "test",
     "description": "this is a test rule",
     "priority": 1,
     "compositeRuleType": "UnitRuleGroup",
     "composingRules": [
       {
         "name": "age-test",
         "description": "age test",
         "condition": "#person.getAge() >= 18",
         "priority": 2,
         "actions": [
           "@log.log(#person.getName() + \" is an adult\")"
         ]
       }
     ]}
   ]
   ```
