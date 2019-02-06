package com.github.kerubistan.kerub.planner.steps.storage.remove

import com.github.kerubistan.kerub.data.VirtualStorageDeviceDao
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.testDisk
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class RemoveVirtualStorageExecutorTest {
	@Test
	fun execute() {
		val vsdDao: VirtualStorageDeviceDao = mock()
		val vsdDynDao: VirtualStorageDeviceDynamicDao = mock()

		RemoveVirtualStorageExecutor(vsdDao, vsdDynDao).execute(RemoveVirtualStorage(testDisk))

		verify(vsdDao).remove(eq(testDisk.id))
		verify(vsdDynDao).remove(eq(testDisk.id))
	}
}