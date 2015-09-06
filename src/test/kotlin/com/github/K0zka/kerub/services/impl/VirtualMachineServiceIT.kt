package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.createClient
import com.github.K0zka.kerub.createServiceClient
import com.github.K0zka.kerub.model.ExpectationLevel
import com.github.K0zka.kerub.model.Range
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.expectations.CacheSizeExpectation
import com.github.K0zka.kerub.model.expectations.ClockFrequencyExpectation
import com.github.K0zka.kerub.model.expectations.MemoryClockFrequencyExpectation
import com.github.K0zka.kerub.services.LoginService
import com.github.K0zka.kerub.services.VirtualMachineService
import org.junit.Assert
import org.junit.Test
import java.util.UUID

public class VirtualMachineServiceIT {
	Test
	fun crud() {
		val client = createClient()
		val loginService = createServiceClient(LoginService::class, client)
		loginService.login(LoginService.UsernamePassword(
				username = "admin",
		        password = "password"
		                                                                                    ))

		val vmService = createServiceClient( VirtualMachineService::class, client )

		val vmToSave = VirtualMachine(
				id = UUID.randomUUID(),
				name = "test",
				memoryMb = Range(
						min = 1024,
						max = 2048
				                     ),
				nrOfCpus = 1,
				expectations = listOf(
						ClockFrequencyExpectation(
								level = ExpectationLevel.DealBreaker,
								minimalClockFrequency = 1700
						                         ),
						MemoryClockFrequencyExpectation(

								level = ExpectationLevel.Want,
								min = 1700
						                               ),
						CacheSizeExpectation(
								level = ExpectationLevel.Wish,
								minKbytes = 1024
						                    )
				                     )
		                                   )
		vmService.add(
				vmToSave
		             )

		val saved = vmService.getById(vmToSave.id)
		Assert.assertEquals(vmToSave, saved)

		val update = vmToSave.copy(
				nrOfCpus = 2
		                           )
		vmService.update(update.id, update)
		val updated = vmService.getById(update.id)
		Assert.assertEquals(update, updated)

		vmService.delete(updated.id)
	}
}