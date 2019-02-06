package com.github.kerubistan.kerub.planner.steps.storage.share.nfs.daemon

import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.github.kerubistan.kerub.model.controller.config.StorageTechnologiesConfig
import com.github.kerubistan.kerub.model.services.NfsDaemonService
import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testHost
import org.junit.Test
import kotlin.test.assertTrue

class StopNfsDaemonFactoryTest : AbstractFactoryVerifications(StopNfsDaemonFactory) {

	@Test
	fun produce() {
		assertTrue("stop nfs even if it is disabled") {
			StopNfsDaemonFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							hostCfgs = listOf(
									HostConfiguration(id = testHost.id,
													  services = listOf(NfsDaemonService())
									)
							),
							config = ControllerConfig(
									storageTechnologies = StorageTechnologiesConfig(
											nfsEnabled = false
									)
							)
					)
			) == listOf(StopNfsDaemon(testHost))
		}

		assertTrue("stop nfs when nothing to do and it is enabled") {
			StopNfsDaemonFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							hostCfgs = listOf(
									HostConfiguration(id = testHost.id,
													  services = listOf(NfsDaemonService())
									)
							),
							config = ControllerConfig(
									storageTechnologies = StorageTechnologiesConfig(
											nfsEnabled = true
									)
							)
					)
			) == listOf(StopNfsDaemon(testHost))
		}

		assertTrue("do not stop nfs when shares still exist") {
			StopNfsDaemonFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							hostCfgs = listOf(
									HostConfiguration(id = testHost.id,
													  services = listOf(NfsDaemonService(),
																		NfsService(directory = "/kerub", write = true))
									)
							)
					)
			).isEmpty()
		}

	}
}