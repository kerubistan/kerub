package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.data.ControllerConfigDao
import com.github.kerubistan.kerub.data.HostDao
import com.github.kerubistan.kerub.data.VirtualMachineDao
import com.github.kerubistan.kerub.data.VirtualStorageDeviceDao
import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVm
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
		whenever(hostCfg.get(eq(listOf(testHost.id)))).thenReturn(
				listOf(HostConfiguration(id = testHost.id, storageConfiguration = listOf(), services = listOf()))
		)
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