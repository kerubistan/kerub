package com.github.kerubistan.kerub.planner.steps.storage.fs.create

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.host.mockHost
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.sshtestutils.verifyCommandExecution
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFsCapability
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testVirtualDisk
import com.nhaarman.mockito_kotlin.mock
import io.github.kerubistan.kroki.size.GB
import org.apache.sshd.client.session.ClientSession
import org.junit.Test

internal class CreateImageBasedOnTemplateExecutorTest {
	@Test
	fun execute() {
		val hostCommandExecutor = mock<HostCommandExecutor>()
		val deviceDynamicDao = mock<VirtualStorageDeviceDynamicDao>()
		val host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(
								testFsCapability
						)
				)
		)
		val session = mock<ClientSession>()
		hostCommandExecutor.mockHost(host, session)
		session.mockCommandExecution("qemu-img create.*".toRegex())
		session.mockCommandExecution("qemu-img info.*".toRegex(), """{
    "virtual-size": 102400,
    "filename": "${testDisk.id}.qcow2",
    "cluster-size": 65536,
    "format": "qcow2",
    "actual-size": 200704,
    "format-specific": {
        "type": "qcow2",
        "data": {
            "compat": "1.1",
            "lazy-refcounts": false,
            "refcount-bits": 16,
            "corrupt": false
        }
    },
	"backing-filename": "${testVirtualDisk.id}.qcow2",
    "dirty-flag": false
}
""")
		CreateImageBasedOnTemplateExecutor(hostCommandExecutor, deviceDynamicDao).execute(
				CreateImageBasedOnTemplate(
						host = host,
						baseAllocation = VirtualStorageFsAllocation(
								hostId = host.id,
								type = VirtualDiskFormat.qcow2,
								fileName = "",
								mountPoint = "/kerub",
								capabilityId = testFsCapability.id,
								actualSize = 100.GB
						),
						format = VirtualDiskFormat.qcow2,
						capability = testFsCapability,
						disk = testDisk,
						baseDisk = testVirtualDisk
				)
		)
		session.verifyCommandExecution("qemu-img create.*".toRegex())
		session.verifyCommandExecution("qemu-img info.*".toRegex())
	}
}