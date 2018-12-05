package com.github.kerubistan.kerub.planner.execution

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.data.ExecutionResultDao
import com.github.kerubistan.kerub.data.HostDao
import com.github.kerubistan.kerub.data.VirtualStorageDeviceDao
import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.ControllerManager
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.model.FsStorageCapability
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
	private val executionResultDao: ExecutionResultDao = mock()
	private val controllerManager: ControllerManager = mock()
	private val executor: HostCommandExecutor = mock()
	private val hostConfigDao = mock<HostConfigurationDao>()
	private val hostManager: HostManager = mock()
	private val hostDao: HostDao = mock()
	private val hostDynamicDao: HostDynamicDao = mock()
	private val vmDynamicDao: VirtualMachineDynamicDao = mock()
	private val vssDao = mock<VirtualStorageDeviceDao>()
	private val virtualStorageDeviceDynamicDao: VirtualStorageDeviceDynamicDao = mock()

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
						format = VirtualDiskFormat.qcow2,
						capability = FsStorageCapability(id = UUID.randomUUID(), size = 100.GB, fsType = "ext4", mountPoint = "/var/")
				))
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
				vssDao,
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