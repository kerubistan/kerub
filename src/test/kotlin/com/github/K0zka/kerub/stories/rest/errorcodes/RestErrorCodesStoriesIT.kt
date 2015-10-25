package com.github.K0zka.kerub.stories.rest.errorcodes

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
		plugin = arrayOf("pretty"),
		features = arrayOf("classpath:stories/rest/errorcodes.feature"),
        glue = arrayOf("com.github.K0zka.kerub.stories.rest.errorcodes")
               )
public class RestErrorCodesStoriesIT {
}