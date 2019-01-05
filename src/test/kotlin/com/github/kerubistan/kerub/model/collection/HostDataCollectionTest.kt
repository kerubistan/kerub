package com.github.kerubistan.kerub.model.collection

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testOtherHost
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class HostDataCollectionTest {
	@Test
	fun validation() {
		assertThrows<IllegalStateException>("host id of dyn and stat must be the same") {
			HostDataCollection(
					stat = testHost,
					dynamic = hostUp(testOtherHost)
			)
		}
		assertThrows<IllegalStateException>("host id of config and stat must be the same") {
			HostDataCollection(
					stat = testHost,
					dynamic = hostUp(testHost),
					config = HostConfiguration(
							id = testOtherHost.id
					)
			)
		}
	}
}