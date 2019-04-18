package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.RestException
import com.github.kerubistan.kerub.createClient
import com.github.kerubistan.kerub.login
import com.github.kerubistan.kerub.runRestAction
import com.github.kerubistan.kerub.services.StatisticsService
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class StatisticsServiceIT {
	@Test
	fun testUnauthenticated() {
		val client = createClient()
		client.runRestAction(StatisticsService::class) {
			assertThrows<RestException> {
				it.listCaches()
			}
			assertThrows<RestException> {
				it.getStatisticsInfo("TEST")
			}
		}
	}

	@Test
	fun testNotAdmin() {
		val client = createClient()
		client.login("enduser", "password")
		client.runRestAction(StatisticsService::class) {
			assertThrows<RestException> {
				it.listCaches()
			}
			assertThrows<RestException> {
				it.getStatisticsInfo("TEST")
			}
		}
	}

	@Test
	fun getStatisticsInfo() {
		val client = createClient()
		client.login("admin", "password")
		client.runRestAction(StatisticsService::class) {
			service ->
			service.listCaches().forEach { service.getStatisticsInfo(it) }
		}
	}
}