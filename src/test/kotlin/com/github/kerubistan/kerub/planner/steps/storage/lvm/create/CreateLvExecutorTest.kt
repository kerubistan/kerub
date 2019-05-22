package com.github.kerubistan.kerub.planner.steps.storage.lvm.create

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.testLvmCapability
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LogicalVolume
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.MB
import org.junit.Ignore
import org.junit.Test
import java.util.UUID

class CreateLvExecutorTest {

	private val hostCommandExecutor : HostCommandExecutor = mock()
	private val virtualDiskDynDao: VirtualStorageDeviceDynamicDao = mock()

	private val host = Host(
			id = UUID.randomUUID(),
			address = "host-1.example.com",
			dedicated = true,
			publicKey = ""
	)

	private val vDisk = VirtualStorageDevice(
			id = UUID.randomUUID(),
			name = "system disk",
			size = 16.GB
	)

	private val lv = LogicalVolume(
			id = UUID.randomUUID().toString(),
			layout = "",
			name = "test-lv",
			path = "/dev/test/test-lv",
			size = 128.MB,
			dataPercent = null,
			maxRecovery = 0,
			minRecovery = 0
	)

	@Test
	@Ignore("not functional")
	fun execute() {

		whenever(hostCommandExecutor.execute<LogicalVolume>(eq(host), any())).thenReturn(lv)

		CreateLvExecutor(hostCommandExecutor, virtualDiskDynDao).execute(
				CreateLv(
						host = host,
						disk = vDisk,
						capability = testLvmCapability
				)
		)
	}

}