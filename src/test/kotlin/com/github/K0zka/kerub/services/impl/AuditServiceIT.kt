package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.RestException
import com.github.K0zka.kerub.createClient
import com.github.K0zka.kerub.expect
import com.github.K0zka.kerub.login
import com.github.K0zka.kerub.runRestAction
import com.github.K0zka.kerub.services.AuditService
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