package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.createClient
import com.github.kerubistan.kerub.createServiceClient
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.services.LoginService
import com.github.kerubistan.kerub.services.TemplateService
import com.github.kerubistan.kerub.services.VirtualMachineService
import org.junit.Test
import java.util.UUID.randomUUID
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

	@Test
	fun createTemplateFromVm() {
		val client = createClient()
		val loginService = createServiceClient(LoginService::class, client)
		loginService.login(LoginService.UsernamePassword(
				username = "admin",
				password = "password"
		))

		val templateService = createServiceClient(TemplateService::class, client)
		val vmService = createServiceClient(VirtualMachineService::class, client)

		val virtualMachine = vmService.add(
				VirtualMachine(
						id = randomUUID(),
						name = "vm-for-template",
						nrOfCpus = 1
				)
		)

		val template = templateService.buildFromVm(virtualMachine.id)

		templateService.delete(template.id)
		vmService.delete(virtualMachine.id)
	}

}