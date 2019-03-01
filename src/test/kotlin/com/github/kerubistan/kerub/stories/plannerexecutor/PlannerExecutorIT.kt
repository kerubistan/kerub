package com.github.kerubistan.kerub.stories.plannerexecutor

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
		plugin = ["pretty", "html:target/test-reports/planner-executor"],
		features = ["classpath:stories/general/planner-executor/planner-executor.feature"],
		glue = ["com.github.kerubistan.kerub.stories.plannerexecutor"])
class PlannerExecutorIT