package com.github.kerubistan.kerub.planner.execution

import com.github.kerubistan.kerub.data.ExecutionResultDao
import com.github.kerubistan.kerub.data.HostDao
import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.ControllerManager
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.steps.vstorage.fs.create.CreateImage
import com.github.kerubistan.kerub.utils.toSize
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

class PlanExecutorImplTest {
	val executionResultDao: ExecutionResultDao = mock()
	val controllerManager: ControllerManager = mock()
	val executor: HostCommandExecutor = mock()
	val hostConfigDao = mock<HostConfigurationDao>()
	val hostManager: HostManager = mock()
	val hostDao: HostDao = mock()
	val hostDynamicDao: HostDynamicDao = mock()
	val vmDynamicDao: VirtualMachineDynamicDao = mock()
	val virtualStorageDeviceDynamicDao: VirtualStorageDeviceDynamicDao = mock()

	@Test
	fun execute() {
		whenever(controllerManager.getControllerId()).thenReturn("TEST-CONTROLLER")
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
		val callback = mock<(Plan) -> Unit>()
		val called = AtomicBoolean(false)
		whenever(callback.invoke(any())).then {
			called.set(true)
		}
		PlanExecutorImpl(
				executionResultDao,
				controllerManager,
				executor,
				hostManager,
				hostDao,
				hostDynamicDao,
				vmDynamicDao,
				virtualStorageDeviceDynamicDao,
				hostConfigDao
		).execute(plan, callback)

		var cntr = 1
		while(!called.get() && cntr++ < 10) {
			Thread.sleep(100)
		}
		verify(callback).invoke(eq(plan))
		verify(executionResultDao).add(any())
	}

}