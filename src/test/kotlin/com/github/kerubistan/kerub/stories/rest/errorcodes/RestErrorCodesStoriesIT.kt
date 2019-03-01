package com.github.kerubistan.kerub.stories.rest.errorcodes

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
		plugin = ["pretty", "html:target/test-reports/errorcodes"],
		features = ["classpath:stories/rest/errorcodes.feature"],
        glue = ["com.github.kerubistan.kerub.stories.rest.errorcodes"]
) class RestErrorCodesStoriesIT