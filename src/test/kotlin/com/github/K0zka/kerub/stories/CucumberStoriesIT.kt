package com.github.K0zka.kerub.stories

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

RunWith(Cucumber::class)
CucumberOptions(
		plugin = arrayOf("pretty",
		                 "json:target/cucumber-reports/json",
		                 "html:target/cucumber-reports/html"
		                ),
		features = arrayOf("classpath:stories/general/host/host-management.feature",
		                   "classpath:stories/general/host/security.feature",
		                   "classpath:stories/general/vm/vms.feature",
		                   "classpath:stories/general/vm/security.feature",
		                   "classpath:stories/general/ui/*",
		                   "classpath:stories/general/power/*"
		                  )
               )
public class CucumberStoriesIT {
}