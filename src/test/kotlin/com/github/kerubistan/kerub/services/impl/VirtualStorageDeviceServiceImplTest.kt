package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.HostDao
import com.github.kerubistan.kerub.data.VirtualMachineDao
import com.github.kerubistan.kerub.data.VirtualStorageDeviceDao
import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.data.mockUpdate
import com.github.kerubistan.kerub.data.mockWaitAndGet
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.host.mockDataConnection
import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.security.AssetAccessController
import com.github.kerubistan.kerub.security.mockAccessGranted
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.toInputStream
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.github.kerubistan.kroki.size.TB
import org.apache.sshd.client.scp.ScpClient
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import java.io.InputStream
import java.util.UUID.randomUUID
import javax.ws.rs.container.AsyncResponse

class VirtualStorageDeviceServiceImplTest {

	private val virtualStorageDeviceDao = mock<VirtualStorageDeviceDao>()
	private val assetAccessController = mock<AssetAccessController>()
	private val virtualStorageDeviceDynamicDao = mock<VirtualStorageDeviceDynamicDao>()
	private val hostDao = mock<HostDao>()
	private val hostDynDao = mock<HostDynamicDao>()
	private val hostCommandExecutor = mock<HostCommandExecutor>()
	private val vmDao = mock<VirtualMachineDao>()

	@Test
	fun add() {
		assetAccessController.mockAccessGranted(testDisk)

		val service =
				spy(
						VirtualStorageDeviceServiceImpl(
								virtualStorageDeviceDao,
								assetAccessController,
								virtualStorageDeviceDynamicDao,
								hostDao,
								hostDynDao,
								hostCommandExecutor,
								vmDao
						)
				)

		service.add(testDisk)

		verify(service).add(eq(testDisk))
		verify(virtualStorageDeviceDao, times(1)).add(eq(testDisk))
		verify(assetAccessController, times(1)).checkAndDo(eq(testDisk), action = any<() -> VirtualStorageDevice>())
	}

	@Test
	fun download() {
		val async = mock<AsyncResponse>()
		val testFsCapability = FsStorageCapability(
				id = randomUUID(),
				size = 10.TB,
				fsType = "ext4",
				mountPoint = "/kerub"
		)
		val host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(
								testFsCapability
						)
				)
		)
		val allocation = VirtualStorageFsAllocation(
				hostId = host.id,
				actualSize = testDisk.size,
				capabilityId = testFsCapability.id,
				mountPoint = testFsCapability.mountPoint,
				type = VirtualDiskFormat.raw,
				fileName = "${testFsCapability.mountPoint}/${testDisk.id}.raw"
		)
		val testDiskDyn = VirtualStorageDeviceDynamic(
				id = testDisk.id,
				allocations = listOf(allocation)
		)
		assetAccessController.mockAccessGranted(testDisk)
		virtualStorageDeviceDao.mockUpdate(testDisk)
		virtualStorageDeviceDynamicDao.mockWaitAndGet(testDiskDyn)
		val hostDyn = hostUp(host)
		whenever(hostDynDao[eq(listOf(host.id))]).thenReturn(listOf(hostDyn))
		whenever(hostDao[host.id]).thenReturn(host)
		doReturn(testDiskDyn).whenever(virtualStorageDeviceDynamicDao)[eq(testDisk.id)]

		val service =
				spy(
						VirtualStorageDeviceServiceImpl(
								virtualStorageDeviceDao,
								assetAccessController,
								virtualStorageDeviceDynamicDao,
								hostDao,
								hostDynDao,
								hostCommandExecutor,
								vmDao
						))
		doReturn(testDisk).whenever(service).getById(testDisk.id)

		service.download(testDisk.id, VirtualDiskFormat.raw, async)

		verify(async)
				.resume(any<Any>())

	}

	@Test
	fun load() {
		val service =
				spy(
						VirtualStorageDeviceServiceImpl(
								virtualStorageDeviceDao,
								assetAccessController,
								virtualStorageDeviceDynamicDao,
								hostDao,
								hostDynDao,
								hostCommandExecutor,
								vmDao
						))
		val testFsCapability = FsStorageCapability(
				id = randomUUID(),
				size = 10.TB,
				fsType = "ext4",
				mountPoint = "/kerub"
		)
		val host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(
								testFsCapability
						)
				)
		)
		val allocation = VirtualStorageFsAllocation(
				hostId = host.id,
				actualSize = testDisk.size,
				capabilityId = testFsCapability.id,
				mountPoint = testFsCapability.mountPoint,
				type = VirtualDiskFormat.raw,
				fileName = "${testFsCapability.mountPoint}/${testDisk.id}.raw"
		)
		val testDiskDyn = VirtualStorageDeviceDynamic(
				id = testDisk.id,
				allocations = listOf(allocation)
		)
		val session = mock<ClientSession>()
		val scpClient = mock<ScpClient>()
		whenever(session.createScpClient()).thenReturn(scpClient)
		whenever(hostDao[host.id]).thenReturn(host)
		assetAccessController.mockAccessGranted(testDisk)
		doReturn(testDisk).whenever(service).getById(testDisk.id)
		virtualStorageDeviceDynamicDao.mockWaitAndGet(testDiskDyn)
		hostCommandExecutor.mockDataConnection<Boolean>(host, session)
		val async = mock<AsyncResponse>()

		service.load(testDisk.id, async, "TEST".toInputStream())

		verify(scpClient).upload(any<InputStream>(), any(), any(), any(), any())
	}
}