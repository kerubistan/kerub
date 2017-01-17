package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.RestException
import com.github.K0zka.kerub.createClient
import com.github.K0zka.kerub.expect
import com.github.K0zka.kerub.login
import com.github.K0zka.kerub.runRestAction
import com.github.K0zka.kerub.services.UsageStatisticsService
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