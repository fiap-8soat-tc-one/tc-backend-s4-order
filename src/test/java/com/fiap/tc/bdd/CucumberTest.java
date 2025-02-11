package com.fiap.tc.bdd;

import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@CucumberContextConfiguration
@IncludeEngines("cucumber")
@Suite
@SelectClasspathResource("features")
@ConfigurationParameter(key = "cucumber.glue", value = "com.fiap.tc.bdd")
@ConfigurationParameter(key = "cucumber.plugin", value = "pretty, html:target/cucumber-reports/cucumber.html, json:target/cucumber-reports/cucumber.json")
public class CucumberTest {
}
