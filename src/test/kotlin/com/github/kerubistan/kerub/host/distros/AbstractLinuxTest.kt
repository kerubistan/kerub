package com.github.kerubistan.kerub.host.distros

import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.github.kerubistan.kerub.model.dynamic.CompositeStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.model.lom.WakeOnLanInfo
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.sshtestutils.mockProcess
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import io.github.kerubistan.kroki.io.resourceToString
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.KB
import io.github.kerubistan.kroki.size.TB
import io.github.kerubistan.kroki.strings.toUUID
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import java.util.UUID
import java.util.UUID.randomUUID
import kotlin.test.assertTrue

class AbstractLinuxTest {

	val session: ClientSession = mock()
	val future: OpenFuture = mock()
	val exec: ChannelExec = mock()
	val sftp: SftpClient = mock()

	@Test
	fun getTotalMemory() {
		val linux = Fedora()  // for example (do this better with mockito)
		whenever(session.createExecChannel(any())).thenReturn(exec)
		whenever(exec.open()).thenReturn(future)
		whenever(exec.invertedErr).thenReturn(NullInputStream(0))
		whenever(exec.invertedOut).thenReturn(
				ByteArrayInputStream("MemTotal:       16345292 kB".toByteArray(Charset.forName("US-ASCII")))
		)

		val total = linux.getTotalMemory(session)
		assertEquals(16345292.KB, total)
	}

	@Test
	fun detectPowerManagement() {
		val linux = Fedora()
		whenever(session.createExecChannel(any())).thenReturn(exec)
		whenever(exec.open()).thenReturn(future)
		whenever(exec.invertedErr).thenReturn(NullInputStream(0))
		whenever(session.createSftpClient()).thenReturn(sftp)
		val eth0: SftpClient.DirEntry = mock()
		whenever(eth0.filename).thenReturn("eth0")
		//the strange device that does not have a inet mac address - but must be tolerated
		val blah0: SftpClient.DirEntry = mock()
		whenever(blah0.filename).thenReturn("blah0")
		whenever(sftp.readDir(eq("/sys/class/net/"))).thenReturn(mutableListOf(eth0, blah0))
		whenever(sftp.read(eq("/sys/class/net/eth0/address")))
				.thenReturn(ByteArrayInputStream("12:34:56:12:34:56".toByteArray(Charsets.US_ASCII)))

		whenever(sftp.read(eq("/sys/class/net/blah0/address")))
				.thenReturn(ByteArrayInputStream("00:00:00:00".toByteArray(Charsets.US_ASCII)))

		val pms = linux.detectPowerManagement(session)

		assertEquals(1, pms.size)
		assertTrue { pms[0] is WakeOnLanInfo }
	}

	@Test
	fun listBlockDevices() {
		val linux = Fedora()
		session.mockCommandExecution("lsblk.*".toRegex(), """NAME ROTA RO   RA RM MIN-IO OPT-IO TYPE  SIZE
vda     1  0 4096  0    512      0 disk    8G
""")
		val blockDevices = linux.listBlockDevices(session)
		assertEquals(8.GB, blockDevices.single().storageCapacity)
		assertEquals("vda", blockDevices.single().deviceName)

	}

	@Test
	fun startFsMonitoring() {
		val linux = spy<AbstractLinux>()
		val host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						blockDevices = listOf(
								BlockDevice(deviceName = "/dev/sda",storageCapacity = 1.TB),
								BlockDevice(deviceName = "/dev/sdb",storageCapacity = 4.TB)
						),
						storageCapabilities = listOf(
								LvmStorageCapability(
										volumeGroupName = "vg-1",
										size = 4.TB,
										physicalVolumes = mapOf( "/dev/sdb" to 4.TB )
								),
								FsStorageCapability(
										size = 800.GB,
										mountPoint = "/kerub",
										fsType = "ext4",
										id = randomUUID()
								)
						)
				)
		)
		val hostDynDao = mock<HostDynamicDao>()
		val vStorageDeviceDynamicDao = mock<VirtualStorageDeviceDynamicDao>()
		val vStorageDynamicUpdates = mutableListOf<VirtualStorageDeviceDynamic>()

		doAnswer {
			val id = it.arguments[0] as UUID
			val retrieve = it.arguments[1] as (UUID) -> VirtualStorageDeviceDynamic
			val update = it.arguments[2] as (VirtualStorageDeviceDynamic) -> VirtualStorageDeviceDynamic
			val retrieved = retrieve(id)
			val updated = update(retrieved)
			vStorageDynamicUpdates.add(updated)
		}.whenever(
				vStorageDeviceDynamicDao).update(
				id = eq("2aab6c02-6ee9-448f-8b12-5089f40ddc6b".toUUID()),
				retrieve = any(),
				change = any()
		)

		session.mockProcess(
				".*df.*".toRegex(),
				output = resourceToString("com/github/kerubistan/kerub/host/distros/AbstractLinuxTest.df.txt")
		)
		session.mockProcess(
				".*du.*/kerub.*".toRegex(),
				output = resourceToString("com/github/kerubistan/kerub/host/distros/AbstractLinuxTest.du.txt")
		)

		linux.startFsMonitoring(host = host, session = session,
				hostDynDao = hostDynDao,
				vStorageDeviceDynamicDao = vStorageDeviceDynamicDao,
				controllerConfig = ControllerConfig()
		)

		assertEquals(7, vStorageDynamicUpdates.size) // see 'du' test input file for quantities
	}

	@Test
	fun startLvmLvMonitoring() {
		val linux = spy<AbstractLinux>()
		val lvmCap = LvmStorageCapability(
				id = randomUUID(),
				size = 1.TB,
				volumeGroupName = "vg-1",
				physicalVolumes = mapOf("/dev/sda" to 1.TB)
		)
		val host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(lvmCap),
						blockDevices = listOf(
								BlockDevice("/dev/sda", 1.TB)
						)
				)
		)

		val hostDynDao = mock<HostDynamicDao>()
		var hostDynamic = HostDynamic(
				id = host.id,
				storageStatus = listOf(
						CompositeStorageDeviceDynamic(
								id = lvmCap.id,
								reportedFreeCapacity = 800.GB
						)
				)
		)

		doAnswer {
			val hostId = it.arguments[0] as UUID
			val retrieve = it.arguments[1] as (UUID) -> HostDynamic
			val change = it.arguments[2] as (HostDynamic) -> HostDynamic
			hostDynamic = change(retrieve(hostId))
			hostDynamic
		}.whenever(hostDynDao).update(id = any(), retrieve = any(), change = any())

		doReturn(hostDynamic).whenever(hostDynDao)[eq(host.id)]
		doAnswer {
			hostDynamic = it.arguments[0] as HostDynamic
		}.whenever(hostDynDao).update(any())
		val vStorageDynDao = mock<VirtualStorageDeviceDynamicDao>()

		var testDiskDyn = VirtualStorageDeviceDynamic(
				id = testDisk.id,
				allocations = listOf(
						VirtualStorageLvmAllocation(
								capabilityId = lvmCap.id,
								actualSize = testDisk.size,
								vgName = lvmCap.volumeGroupName,
								hostId = host.id,
								path = "/dev/${lvmCap.volumeGroupName}/${testDisk.id}"
						)
				)
		)

		doAnswer {
			invocation ->
			val change = invocation.arguments[1] as (VirtualStorageDeviceDynamic) -> VirtualStorageDeviceDynamic
			testDiskDyn = change(testDiskDyn)
			testDiskDyn
		}.whenever(vStorageDynDao).updateIfExists(eq(testDisk.id), any())

		session.mockProcess(".*lvm lvs.*".toRegex(),
				output = """  vg-1:BUCw3V-1Bd3-LWzT-H5bA-GW0k-iuma-9Yf0hY:libvirt:/dev/vg-1/libvirt:216895848448B:::linear:
  vg-1:BUCw3V-bLWB-LWzT-H5bA-GW0k-iuma-9Yf0hY:${testDisk.id}:/dev/vg-1/${testDisk.id}:216895848448B:::linear:
  vg-1:Kju0Bg-bLWB-TPBZ-VUfl-aFQq-1G1e-kfMbku:pool-1::21474836480B:::thin,pool:
--end
""")
		linux.startLvmLvMonitoring(
				host = host,
				session = session,
				hostDynDao = hostDynDao,
				vStorageDeviceDynamicDao = vStorageDynDao
		)

		assertTrue("there must be pools synced into the data structure") {
			(hostDynamic.storageStatus.single() as CompositeStorageDeviceDynamic).pools.isNotEmpty()
		}
		assertTrue { testDiskDyn.allocations.single().actualSize == 216895848448.toBigInteger() }
	}

}