package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.RestException
import com.github.kerubistan.kerub.createClient
import com.github.kerubistan.kerub.createServiceClient
import com.github.kerubistan.kerub.expect
import com.github.kerubistan.kerub.model.Pool
import com.github.kerubistan.kerub.services.LoginService
import com.github.kerubistan.kerub.services.PoolService
import com.github.kerubistan.kerub.utils.getLogger
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

class PoolServiceIT {

	private companion object {
		val logger = getLogger(PoolServiceIT::class)
	}

	@Test
	fun crud() {
		val client = createClient()
		val loginService = createServiceClient(LoginService::class, client)
		loginService.login(LoginService.UsernamePassword(
				username = "admin",
				password = "password"
		))

		val poolService = createServiceClient(PoolService::class, client)

		val pool = poolService.add(Pool(
				id = UUID.randomUUID(),
				name = "test-pool",
				templateId = UUID.randomUUID(),
				expectations = listOf())
		)

		val poolGet = poolService.getById(pool.id)
		assertEquals(pool, poolGet)

		val poolUpdate = poolService.update(pool.id, pool.copy(name = "test-pool-update"))
		assertEquals(pool.id, poolUpdate.id)
		assertEquals(pool.templateId, poolUpdate.templateId)

		poolService.delete(pool.id)
		expect(RestException::class) {
			poolService.getById(pool.id)
		}
	}

	@Test
	fun security() {

		val client = createClient()
		val poolService = createServiceClient(PoolService::class, client)

		expect(RestException::class) {
			poolService.add(Pool(
					id = UUID.randomUUID(),
					name = "test-pool",
					templateId = UUID.randomUUID(),
					expectations = listOf())
			)
		}

		expect(RestException::class) {
			poolService.listAll(start = 0, limit = 1000, sort = "id")
		}


	}

}