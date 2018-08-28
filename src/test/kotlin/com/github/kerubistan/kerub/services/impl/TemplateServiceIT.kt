package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.createClient
import com.github.kerubistan.kerub.createServiceClient
import com.github.kerubistan.kerub.services.LoginService
import com.github.kerubistan.kerub.services.TemplateService
import org.junit.Test
import kotlin.test.assertNotNull

class TemplateServiceIT {
	@Test
	fun autoName() {
		val client = createClient()
		val loginService = createServiceClient(LoginService::class, client)
		loginService.login(LoginService.UsernamePassword(
				username = "admin",
				password = "password"
		))

		val templateService = createServiceClient(TemplateService::class, client)

		assertNotNull(templateService.autoName())
	}
}