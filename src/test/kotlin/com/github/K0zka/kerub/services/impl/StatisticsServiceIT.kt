package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.RestException
import com.github.K0zka.kerub.createClient
import com.github.K0zka.kerub.expect
import com.github.K0zka.kerub.login
import com.github.K0zka.kerub.runRestAction
import com.github.K0zka.kerub.services.StatisticsService
import org.junit.Test

class StatisticsServiceIT {
	@Test
	fun testUnauthenticated() {
		val client = createClient()
		client.runRestAction(StatisticsService::class) {
			expect(RestException::class) {
				it.listCaches()
			}
			expect(RestException::class) {
				it.getStatisticsInfo("TEST")
			}
		}
	}

	@Test
	fun testNotAdmin() {
		val client = createClient()
		client.login("enduser", "password")
		client.runRestAction(StatisticsService::class) {
			expect(RestException::class) {
				it.listCaches()
			}
			expect(RestException::class) {
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