package com.github.K0zka.kerub.stories.rest.auth

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
		plugin = arrayOf("pretty"),
		features = arrayOf("classpath:stories/rest/authentication.feature"),
		glue = arrayOf("com.github.K0zka.kerub.stories.rest.auth")
               ) class AuthenticationStoriesIT {
}