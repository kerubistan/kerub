package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.RestException
import com.github.kerubistan.kerub.createClient
import com.github.kerubistan.kerub.expect
import com.github.kerubistan.kerub.login
import com.github.kerubistan.kerub.runRestAction
import com.github.kerubistan.kerub.services.UsageStatisticsService
import org.junit.Test
import kotlin.test.assertEquals

class UsageStatisticsServiceIT {
	@Test
	fun security() {
		val anonClient = createClient()
		expect(RestException::class, check = { assertEquals("AUTH1", it.code) }, action = {
			anonClient.runRestAction(UsageStatisticsService::class) {
				it.basicBalanceReport()
			}
		})
		val enduserClient = createClient()
		enduserClient.login("enduser", "password")
		expect(RestException::class, check = { assertEquals("SEC1", it.code) }, action = {
			enduserClient.runRestAction(UsageStatisticsService::class) {
				it.basicBalanceReport()
			}
		})
	}

	@Test
	fun basicBalanceReport() {
		val client = createClient()
		client.login("admin", "password")
		val report = client.runRestAction(UsageStatisticsService::class) {
			it.basicBalanceReport()
		}

	}
}