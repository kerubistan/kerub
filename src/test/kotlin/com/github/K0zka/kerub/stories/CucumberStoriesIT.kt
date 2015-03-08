package com.github.K0zka.kerub.stories

import org.junit.runner.RunWith
import cucumber.api.junit.Cucumber
import cucumber.api.CucumberOptions

RunWith(javaClass<Cucumber>())
CucumberOptions(
		plugin = array("pretty",
		               "json:target/cucumber-reports/json",
		               "html:target/cucumber-reports/html"
		              ),
		features = array(
				"classpath:stories/general/host/host-management.feature",
				"classpath:stories/general/host/security.feature",
				"classpath:stories/general/vm/vms.feature",
				"classpath:stories/general/vm/security.feature",
				"classpath:stories/general/ui/*",
				"classpath:stories/general/power/*"
		                )
               )
public class CucumberStoriesIT {
}