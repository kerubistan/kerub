package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.RestException
import com.github.kerubistan.kerub.createClient
import com.github.kerubistan.kerub.expect
import com.github.kerubistan.kerub.login
import com.github.kerubistan.kerub.runRestAction
import com.github.kerubistan.kerub.services.AuditService
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

class AuditServiceIT {
	@Test
	fun security() {
		createClient().runRestAction(AuditService::class) {
			expect(RestException::class,
					action = { it.listById(UUID.randomUUID()) },
					check = { assertEquals("AUTH1", it.code) })
		}

		val endUser = createClient()
		endUser.login("enduser", "password")
		endUser.runRestAction(AuditService::class) {
			expect(RestException::class,
					action = { it.listById(UUID.randomUUID()) },
					check = { assertEquals("SEC1", it.code) })
		}

	}

}