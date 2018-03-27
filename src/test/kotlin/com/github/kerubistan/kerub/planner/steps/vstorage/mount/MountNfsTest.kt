package com.github.kerubistan.kerub.planner.steps.vstorage.mount

import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.services.NfsMount
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testHost
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class MountNfsTest {

	@Test
	fun take() {
		assertTrue("mount the nfs share") {
			val local = testHost.copy(id = UUID.randomUUID())
			val remote = testHost.copy(id = UUID.randomUUID())
			val state = MountNfs(host = local, remoteDirectory = "/kerub", remoteHost = remote,
								 directory = "/mnt").take(
					OperationalState.fromLists(
						hosts = listOf(local, remote),
						hostDyns = listOf(
								HostDynamic(id = local.id, status = HostStatus.Up),
								HostDynamic(id = remote.id, status = HostStatus.Up)
						)
					)
			)
			state.hosts[local.id]!!.config!!.services.single() == NfsMount(remoteDirectory = "/kerub",
																		   localDirectory = "/mnt",
																		   remoteHostId = remote.id)
		}
	}
}