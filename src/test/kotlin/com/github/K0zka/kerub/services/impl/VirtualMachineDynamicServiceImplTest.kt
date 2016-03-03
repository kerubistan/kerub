package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.VirtualMachineDao
import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.model.Range
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.DisplaySettings
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.utils.toSize
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.util.UUID

@RunWith(MockitoJUnitRunner::class)
class VirtualMachineDynamicServiceImplTest {

	@Mock
	var dao: VirtualMachineDynamicDao? = null
	@Mock
	var vmDao : VirtualMachineDao? = null

	val vm = VirtualMachine(
			id = UUID.randomUUID(),
			name = "vm-1",
			nrOfCpus = 1
	)

	val vmDyn = VirtualMachineDynamic(
			id = vm.id,
			status = VirtualMachineStatus.Up,
			displaySetting = DisplaySettings(
					hostAddr = "host1.example.com",
					port = 5900,
					password = "wilmaletmein",
					ca = ""
			),
			lastUpdated = System.currentTimeMillis(),
			hostId = UUID.randomUUID(),
			memoryUsed = "512 MB".toSize()
	)

	@Test
	fun spiceConnection() {
		Mockito.`when`(dao!![vm.id]).thenReturn(vmDyn)
		Mockito.`when`(vmDao!![vm.id]).thenReturn(vm)

		val result = VirtualMachineDynamicServiceImpl(dao!!, vmDao!!).spiceConnection(vm.id)
	}
}