package com.github.kerubistan.kerub.stories.rest.auth

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
		plugin = arrayOf(
				"pretty",
				"html:target/test-reports/auth",
				"json:target/test-reports/auth/cucuber.json"
		),
		features = arrayOf("classpath:stories/rest/authentication.feature"),
		glue = arrayOf("com.github.kerubistan.kerub.stories.rest.auth")
) class AuthenticationStoriesIT