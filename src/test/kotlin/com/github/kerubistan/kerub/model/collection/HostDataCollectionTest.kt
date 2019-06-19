package com.github.kerubistan.kerub.model.collection

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testOtherHost
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class HostDataCollectionTest {

	@Test
	fun updateDynamic() {
		val original = HostDataCollection(
				stat = testHost,
				dynamic = hostUp(testHost)
		)
		val updated = original.updateDynamic { it.copy(systemCpu = 1) }

		assertNotEquals(updated, original.dynamic)
	}

	@Test
	fun updateWithDynamic() {
		val original = HostDataCollection(
				stat = testHost,
				dynamic = hostUp(testHost)
		)
		val updated = original.updateWithDynamic { it.copy(systemCpu = 1) }

		assertEquals(original.stat, updated.stat)
		assertNotEquals(original.dynamic, updated.dynamic)
	}

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