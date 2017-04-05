package com.github.K0zka.kerub.planner.execution

import com.github.K0zka.kerub.data.ExecutionResultDao
import com.github.K0zka.kerub.data.config.HostConfigurationDao
import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.K0zka.kerub.host.ControllerManager
import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.io.VirtualDiskFormat
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.Plan
import com.github.K0zka.kerub.planner.steps.vstorage.fs.create.CreateImage
import com.github.K0zka.kerub.utils.toSize
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import java.util.UUID

class PlanExecutorImplTest {
	val executionResultDao: ExecutionResultDao = mock()
	val controllerManager: ControllerManager = mock()
	val executor: HostCommandExecutor = mock()
	val hostConfigDao = mock<HostConfigurationDao>()
	val hostManager: HostManager = mock()
	val hostDynamicDao: HostDynamicDao = mock()
	val vmDynamicDao: VirtualMachineDynamicDao = mock()
	val virtualStorageDeviceDynamicDao: VirtualStorageDeviceDynamicDao = mock()

	@Test
	fun execute() {
		val host = Host(
				id = UUID.randomUUID(),
		        address = "127.0.0.1",
		        dedicated = true,
		        publicKey = ""
		               )
		val plan = Plan(
				state = OperationalState.fromLists(
						vms = listOf(),
						hosts = listOf(host),
						hostDyns = listOf(),
						vmDyns = listOf()
				                        ),
				steps = listOf(CreateImage(
						host = host,
						disk = VirtualStorageDevice(
								size = "100 MB".toSize(),
								name = "foo"
						                             ),
						path = "/var/",
						format = VirtualDiskFormat.qcow2
				                            )
				                )
		               )
		PlanExecutorImpl(
				executionResultDao,
				controllerManager,
				executor,
				hostManager,
				hostDynamicDao,
				vmDynamicDao,
				virtualStorageDeviceDynamicDao,
				hostConfigDao
		).execute(plan, {})

		//Mockito.verify(executor)!!.execute(Matchers.eq(host) ?: host, Matchers.any() ?: Mockito.mock() )
	}
}