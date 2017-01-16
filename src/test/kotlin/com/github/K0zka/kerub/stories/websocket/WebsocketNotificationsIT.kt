package com.github.K0zka.kerub.stories.websocket

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
		plugin = arrayOf("pretty", "html:target/test-reports/websocket"),
		features = arrayOf("classpath:stories/general/websocket/subscriptions.feature"),
		glue = arrayOf(
				"com.github.K0zka.kerub.stories.websocket",
				"com.github.K0zka.kerub.stories.config",
				"com.github.K0zka.kerub.stories.entities"
		)
)
class WebsocketNotificationsIT