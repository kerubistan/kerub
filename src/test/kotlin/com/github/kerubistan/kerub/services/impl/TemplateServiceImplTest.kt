package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.TemplateDao
import com.github.kerubistan.kerub.data.VirtualMachineDao
import com.github.kerubistan.kerub.data.VirtualStorageDeviceDao
import com.github.kerubistan.kerub.model.Asset
import com.github.kerubistan.kerub.model.VirtualStorageLink
import com.github.kerubistan.kerub.model.expectations.CloneOfStorageExpectation
import com.github.kerubistan.kerub.model.io.BusType
import com.github.kerubistan.kerub.model.io.DeviceType
import com.github.kerubistan.kerub.security.AssetAccessController
import com.github.kerubistan.kerub.testCdrom
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testVm
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import java.util.UUID

import kotlin.test.assertTrue

class TemplateServiceImplTest {

	@Test
	fun buildFromVm() {
		assertTrue("") {

			val vm = testVm.copy(
					virtualStorageLinks = listOf(
							VirtualStorageLink(
									virtualStorageId = testDisk.id,
									expectations = listOf(),
									bus = BusType.sata,
									device = DeviceType.disk
							),
							VirtualStorageLink(
									virtualStorageId = testCdrom.id,
									expectations = listOf(),
									bus = BusType.sata,
									device = DeviceType.disk
							)
					)
			)

			val templateDao = mock<TemplateDao>()
			val vmDao = mock<VirtualMachineDao>()
			val virtualStorageDeviceDao = mock<VirtualStorageDeviceDao>()
			val accessController = mock<AssetAccessController>()

			whenever(accessController.doAndCheck(any<() -> Asset>()) ).then {
				(it.arguments[0] as () -> Asset).invoke()
			}
			whenever(vmDao[vm.id]).thenReturn(vm)
			whenever(virtualStorageDeviceDao[testDisk.id]).thenReturn(testDisk)
			whenever(virtualStorageDeviceDao[testCdrom.id]).thenReturn(testCdrom)
			whenever(virtualStorageDeviceDao.list(argThat<List<UUID>> { containsAll(listOf(testDisk.id, testCdrom.id)) }))
					.thenReturn(listOf(testDisk, testCdrom))

			TemplateServiceImpl(templateDao, vmDao, virtualStorageDeviceDao, accessController).buildFromVm(vm.id)

			verify(templateDao).add(argThat { vm.id == testVm.id && vm.virtualStorageLinks.size == 2 })
			verify(virtualStorageDeviceDao).addAll( argThat {
				single().let { device ->
					!device.readOnly
							&& device.name == testDisk.name
							&& device.id != testDisk.id
							&& device.expectations.any { it is CloneOfStorageExpectation } }
			})
			true
		}
	}
}