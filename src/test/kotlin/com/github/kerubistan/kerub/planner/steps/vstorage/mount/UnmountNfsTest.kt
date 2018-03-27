package com.github.kerubistan.kerub.planner.steps.vstorage.mount

import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.services.NfsMount
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testHost
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class UnmountNfsTest {

	@Test
	fun take() {
		assertTrue("unmount the nfs share") {
			val local = testHost.copy(id = UUID.randomUUID())
			val remote = testHost.copy(id = UUID.randomUUID())
			val state = UnmountNfs(host = local, mountDir = "/mnt").take(
					OperationalState.fromLists(
							hosts = listOf(local, remote),
							hostDyns = listOf(
									HostDynamic(id = local.id, status = HostStatus.Up),
									HostDynamic(id = remote.id, status = HostStatus.Up)
							),
							hostCfgs = listOf(
									HostConfiguration(
											id = local.id,
											services = listOf(
													NfsMount(
															remoteHostId = remote.id,
															localDirectory = "/mnt",
															remoteDirectory = "/kerub"
													)
											)
									)
							)
					)
			)
			state.hosts[local.id]!!.config!!.services.isEmpty()
		}

	}
}