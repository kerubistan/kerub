package com.github.K0zka.kerub.stories.authorization

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
		plugin = arrayOf(
				"pretty",
				"html:target/test-reports/planner",
				"json:target/test-reports/planner/cucumber.json"
		),
		features = arrayOf(
				"classpath:stories/general/authorization/accounts.feature",
				"classpath:stories/general/authorization/projects.feature"
		),
		glue = arrayOf("com.github.K0zka.kerub.stories.authorization")
)
class AuthorizationIT