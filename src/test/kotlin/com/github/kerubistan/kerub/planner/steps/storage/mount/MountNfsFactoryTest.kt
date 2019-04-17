package com.github.kerubistan.kerub.planner.steps.storage.mount

import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.github.kerubistan.kerub.model.controller.config.StorageTechnologiesConfig
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.services.NfsDaemonService
import com.github.kerubistan.kerub.model.services.NfsMount
import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testHost
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class MountNfsFactoryTest : AbstractFactoryVerifications(MountNfsFactory) {

	@Test
	fun produce() {
		assertTrue("do not mount when the nfs feature is disabled") {
			val nfsHost = testHost.copy(
					id = UUID.randomUUID()
			)
			val nfsClient = testHost.copy(
					id = UUID.randomUUID()
			)
			MountNfsFactory.produce(OperationalState.fromLists(
					hosts = listOf(nfsHost, nfsClient),
					hostDyns = listOf(HostDynamic(
							id = nfsHost.id,
							status = HostStatus.Up
					), HostDynamic(
							id = nfsClient.id,
							status = HostStatus.Up
					)),
					hostCfgs = listOf(
							HostConfiguration(
									id = nfsHost.id,
									services = listOf(NfsDaemonService(), NfsService("/kerub", true))
							)
					),
					config = ControllerConfig(
							storageTechnologies = StorageTechnologiesConfig(
									nfsEnabled = false
							)
					)
			)).isEmpty()
		}

		assertTrue("Make NfsMount step when it is not yet mounted") {
			val nfsHost = testHost.copy(
					id = UUID.randomUUID()
			)
			val nfsClient = testHost.copy(
					id = UUID.randomUUID()
			)
			MountNfsFactory.produce(OperationalState.fromLists(
					hosts = listOf(nfsHost, nfsClient),
					hostDyns = listOf(HostDynamic(
							id = nfsHost.id,
							status = HostStatus.Up
					), HostDynamic(
							id = nfsClient.id,
							status = HostStatus.Up
					)),
					hostCfgs = listOf(
							HostConfiguration(
									id = nfsHost.id,
									services = listOf(NfsDaemonService(), NfsService("/kerub", true))
							)
					),
					config = ControllerConfig(
							storageTechnologies = StorageTechnologiesConfig(
									nfsEnabled = true
							)
					)
			)) == listOf(MountNfs(remoteHost = nfsHost, host = nfsClient, directory = "/mnt/${nfsHost.id}/kerub",
								  remoteDirectory = "/kerub"))
		}

		assertTrue("Make no step when it is already mounted") {
			val nfsHost = testHost.copy(
					id = UUID.randomUUID()
			)
			val nfsClient = testHost.copy(
					id = UUID.randomUUID()
			)
			MountNfsFactory.produce(OperationalState.fromLists(
					hosts = listOf(nfsHost, nfsClient),
					hostDyns = listOf(HostDynamic(
							id = nfsHost.id,
							status = HostStatus.Up
					), HostDynamic(
							id = nfsClient.id,
							status = HostStatus.Up
					)),
					hostCfgs = listOf(
							HostConfiguration(
									id = nfsHost.id,
									services = listOf(NfsDaemonService(), NfsService("/kerub", true))
							),
							HostConfiguration(
									id = nfsClient.id,
									services = listOf(NfsDaemonService(),
													  NfsMount(nfsHost.id, "/kerub", "/mnt/${nfsHost.id}"))
							)
					),
					config = ControllerConfig(
							storageTechnologies = StorageTechnologiesConfig(
									nfsEnabled = true
							)
					)
			)).isEmpty()
		}

	}
}