package com.github.kerubistan.kerub.stories

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
		plugin = ["pretty", "html:target/test-reports/etc"],
		features = ["classpath:stories/general/host/host-management.feature", "classpath:stories/general/host/security.feature", "classpath:stories/general/vm/vms.feature", "classpath:stories/general/vm/security.feature", "classpath:stories/general/ui/*", "classpath:stories/general/power/*"],
        glue = ["com.github.kerubistan.kerub.stories.host"]
) class CucumberStoriesIT