package com.github.kerubistan.kerub.stories.authorization

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
		plugin = ["pretty", "html:target/test-reports/planner", "json:target/test-reports/planner/cucumber.json"],
		features = ["classpath:stories/general/authorization/accounts.feature", "classpath:stories/general/authorization/projects.feature"],
		glue = ["com.github.kerubistan.kerub.stories.authorization"]
)
class AuthorizationIT