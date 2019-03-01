package com.github.kerubistan.kerub.stories.websocket

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
		plugin = ["pretty", "html:target/test-reports/websocket"],
		features = ["classpath:stories/general/websocket/subscriptions.feature"],
		glue = ["com.github.kerubistan.kerub.stories.websocket", "com.github.kerubistan.kerub.stories.config", "com.github.kerubistan.kerub.stories.entities"]
)
class WebsocketNotificationsIT