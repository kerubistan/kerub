package com.github.K0zka.kerub.stories.planner

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
				"classpath:stories/general/planner/planner.feature",
				"classpath:stories/general/planner/storage.feature",
				"classpath:stories/general/planner/power-save.feature",
				"classpath:stories/general/planner/host-failure.feature",
				"classpath:stories/general/planner/expectations/storage-notsame.feature",
				"classpath:stories/general/planner/expectations/memory-errorcorrection.feature",
				"classpath:stories/general/planner/expectations/memory-clockspeed.feature",
				"classpath:stories/general/planner/expectations/cpu-clockspeed.feature",
				"classpath:stories/general/planner/expectations/host-notsamehost.feature",
				"classpath:stories/general/planner/expectations/host-manufacturer.feature",
				"classpath:stories/general/planner/expectations/cpu-cachesize.feature",
				"classpath:stories/general/planner/expectations/vm-nomigration.feature"
		),
		glue = arrayOf("com.github.K0zka.kerub.stories.planner")
) class PlannerIT