package com.github.K0zka.kerub.security

import com.github.K0zka.kerub.data.hub.AnyEntityDao
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.VirtualNetwork
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.testHost
import com.github.K0zka.kerub.testVirtualDisk
import com.github.K0zka.kerub.testVirtualNetwork
import com.github.K0zka.kerub.testVm
import com.github.K0zka.kerub.utils.toSize
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertNull

class EntityAccessControllerImplTest {

	val assetAccessController: AssetAccessController = mock()
	val anyEntityDao: AnyEntityDao = mock()

	@Test
	fun statClassOf() {
		assertEquals(VirtualMachine::class, EntityAccessControllerImpl.statClassOf(VirtualMachineDynamic::class))
		assertEquals(
				VirtualStorageDevice::class,
				EntityAccessControllerImpl.statClassOf(VirtualStorageDeviceDynamic::class)
		)
		assertEquals(
				Host::class,
				EntityAccessControllerImpl.statClassOf(HostDynamic::class)
		)

		assertNull(EntityAccessControllerImpl.statClassOf(Host::class))
	}

	@Test
	fun statFromDynamic() {
		val dyn = VirtualMachineDynamic(
				id = testVm.id,
				memoryUsed = "1 GB".toSize(),
				status = VirtualMachineStatus.Up,
				hostId = testHost.id
		)
		whenever(anyEntityDao.get(eq(VirtualMachine::class), eq(testVm.id))).thenReturn(testVm)
		assertEquals(testVm, EntityAccessControllerImpl(assetAccessController, anyEntityDao).statFromDynamic(dyn))
	}

	@Test
	fun checkAndDoWithAsset() {
		val action = mock<() -> VirtualNetwork>()
		EntityAccessControllerImpl(assetAccessController, anyEntityDao).checkAndDo(testVirtualNetwork, action)
		verify(assetAccessController).checkAndDo(eq(testVirtualNetwork), eq(action))
		verify(action, never()).invoke()
	}

	@Test
	fun checkAndDoWithDyn() {
		val action = mock<() -> VirtualStorageDeviceDynamic>()
		EntityAccessControllerImpl(assetAccessController, anyEntityDao).checkAndDo(testVirtualDisk, action)
		verify(assetAccessController).checkAndDo(eq(testVirtualDisk), eq(action))
		verify(action, never()).invoke()
	}

}