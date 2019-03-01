package com.github.kerubistan.kerub.stories.rest.auth

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
		plugin = ["pretty", "html:target/test-reports/auth", "json:target/test-reports/auth/cucuber.json"],
		features = ["classpath:stories/rest/authentication.feature"],
		glue = ["com.github.kerubistan.kerub.stories.rest.auth"]
) class AuthenticationStoriesIT