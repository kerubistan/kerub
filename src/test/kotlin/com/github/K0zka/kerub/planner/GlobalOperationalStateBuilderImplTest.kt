package com.github.K0zka.kerub.planner

import com.github.K0zka.kerub.data.ControllerConfigDao
import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.data.VirtualMachineDao
import com.github.K0zka.kerub.data.VirtualStorageDeviceDao
import com.github.K0zka.kerub.data.config.HostConfigurationDao
import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.K0zka.kerub.model.config.HostConfiguration
import com.github.K0zka.kerub.model.controller.config.ControllerConfig
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.testDisk
import com.github.K0zka.kerub.testHost
import com.github.K0zka.kerub.testVm
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import java.math.BigInteger
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GlobalOperationalStateBuilderImplTest {

	private val hostDyn: HostDynamicDao = mock()
	private val hostDao: HostDao = mock()
	private val hostCfg: HostConfigurationDao = mock()
	private val virtualStorageDao: VirtualStorageDeviceDao = mock()
	private val virtualStorageDynDao: VirtualStorageDeviceDynamicDao = mock()
	private val vmDao: VirtualMachineDao = mock()
	private val vmDynDao: VirtualMachineDynamicDao = mock()
	private val configDao: ControllerConfigDao = mock()

	@Test
	fun buildState() {

		whenever(hostDao.list()).thenReturn(listOf(testHost))
		whenever(hostDyn.get(eq(listOf(testHost.id)))).thenReturn(listOf(HostDynamic(id = testHost.id)))
		whenever(hostCfg.get(eq(listOf(testHost.id)))).thenReturn(listOf(HostConfiguration(id = testHost.id)))
		whenever(virtualStorageDao.list()).thenReturn(listOf(testDisk))
		whenever(vmDao.list()).thenReturn(listOf(testVm))
		whenever(vmDynDao.get(eq(listOf(testVm.id)))).thenReturn(listOf(VirtualMachineDynamic(id = testVm.id, hostId = testHost.id, memoryUsed = BigInteger.TEN)))
		whenever(configDao.get()).thenReturn(ControllerConfig())

		val state = GlobalOperationalStateBuilderImpl(
				hostDyn, hostDao, hostCfg, virtualStorageDao, virtualStorageDynDao, vmDao, vmDynDao, configDao
		).buildState()

		assertTrue { state.hosts.containsKey(testHost.id) }
		assertNotNull(state.hosts[testHost.id]?.dynamic)
		assertNotNull(state.hosts[testHost.id]?.config)
		assertTrue { state.vms.containsKey(testVm.id) }
		assertNotNull(state.vms[testVm.id]?.dynamic)
		assertEquals(testVm, state.vms[testVm.id]?.stat)
		assertEquals(testDisk, state.vStorage[testDisk.id]?.stat)

		verify(hostDao).list()
		verify(hostDyn).get(listOf(testHost.id))
		verify(hostCfg).get(listOf(testHost.id))

		verify(vmDao).list()
		verify(vmDynDao).get(listOf(testVm.id))

		verify(virtualStorageDao).list()
		verify(virtualStorageDynDao).get(listOf(testDisk.id))

		verify(configDao).get()
	}
}