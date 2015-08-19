package com.github.K0zka.kerub.stories.rest

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

RunWith(Cucumber::class)
CucumberOptions(
		plugin = arrayOf("pretty",
		                 "json:target/cucumber-reports/json",
		                 "html:target/cucumber-reports/html"
		                ),
		features = arrayOf("classpath:stories/rest/errorcodes.feature"),
        glue = arrayOf("com.github.K0zka.kerub.stories.rest")
               )
public class RestStoriesIT {
}