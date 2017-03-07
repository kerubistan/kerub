package com.github.K0zka.kerub.data.hub

import com.github.K0zka.kerub.data.VirtualMachineDao
import com.github.K0zka.kerub.data.VirtualNetworkDao
import com.github.K0zka.kerub.data.VirtualStorageDeviceDao
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.testVm
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class AnyAssetDaoImplTest {

	private val vmDao: VirtualMachineDao = mock()
	private val vNetDao: VirtualNetworkDao = mock()
	private val vStorageDao: VirtualStorageDeviceDao = mock()

	@Test
	fun get() {
		whenever(vmDao.get(eq(testVm.id))).thenReturn(testVm)

		assertEquals(testVm, AnyAssetDaoImpl(vmDao, vStorageDao, vNetDao).get(VirtualMachine::class, testVm.id))

		verify(vmDao).get(testVm.id)
		verify(vNetDao, never()).get(any<UUID>())
		verify(vStorageDao, never()).get(any<UUID>())
	}

	@Test
	fun getAll() {
		whenever(vmDao.get(eq(listOf(testVm.id)))).thenReturn(listOf(testVm))

		assertEquals(listOf(testVm), AnyAssetDaoImpl(vmDao, vStorageDao, vNetDao).getAll(VirtualMachine::class, listOf(testVm.id)))

		verify(vmDao).get(eq(listOf(testVm.id)))
		verify(vNetDao, never()).get(any<List<UUID>>())
		verify(vStorageDao, never()).get(any<List<UUID>>())
	}

}