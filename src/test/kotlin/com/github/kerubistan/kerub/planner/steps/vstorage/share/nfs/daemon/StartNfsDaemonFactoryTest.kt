package com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs.daemon

import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.github.kerubistan.kerub.model.controller.config.StorageTechnologiesConfig
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.services.NfsDaemonService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class StartNfsDaemonFactoryTest {

	@Test
	fun produce() {
		val hostWithNfs = testHost.copy(
				id = UUID.randomUUID(),
				capabilities = testHostCapabilities.copy(
						distribution = SoftwarePackage("Centos Linux", version = Version.fromVersionString("7.0")),
						installedSoftware = listOf(
								//among others
								SoftwarePackage("nfs-utils", version = Version.fromVersionString("1.2.3.4"))
						)
				)
		)
		val hostWithoutNfs = testHost.copy(
				id = UUID.randomUUID()
		)
		assertTrue("even if nfs is enabled, if no nfs installed, no step generated") {
			StartNfsDaemonFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(hostWithoutNfs),
							config = ControllerConfig(
									storageTechnologies = StorageTechnologiesConfig(nfsEnabled = true))
					)
			).isEmpty()
		}
		assertTrue("with disabled feature, the nfs daemon should not be allowed to start") {
			StartNfsDaemonFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(hostWithNfs),
							config = ControllerConfig(
									storageTechnologies = StorageTechnologiesConfig(nfsEnabled = false))
					)
			).isEmpty()
		}
		assertTrue("single host with nfs not started - should generate a step") {
			StartNfsDaemonFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(hostWithNfs),
							hostDyns = listOf(
									HostDynamic(
											id = hostWithNfs.id,
											status = HostStatus.Up
									)
							)
					)
			) == listOf(StartNfsDaemon(hostWithNfs))
		}
		assertTrue("single host with nfs not started - should not generate a step because it is not running") {
			StartNfsDaemonFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(hostWithNfs)
					)
			).isEmpty()
		}
		assertTrue("single host with nfs already started - no step") {
			StartNfsDaemonFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(hostWithNfs),
							hostCfgs = listOf(
									HostConfiguration(id = hostWithNfs.id, services = listOf(NfsDaemonService())))
					)
			).isEmpty()
		}
	}
}