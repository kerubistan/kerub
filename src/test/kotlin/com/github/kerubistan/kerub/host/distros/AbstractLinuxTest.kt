package com.github.kerubistan.kerub.host.distros

import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.SoftwarePackage.Companion.pack
import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.github.kerubistan.kerub.model.dynamic.CompositeStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.model.lom.WakeOnLanInfo
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.sshtestutils.mockProcess
import com.github.kerubistan.kerub.sshtestutils.verifyCommandExecution
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.utils.junix.common.Centos
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

	@Test
	fun startLvmVgMonitoring() {
		val linux = spy<AbstractLinux>()
		val host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(
								LvmStorageCapability(
										id = randomUUID(),
										volumeGroupName = "vg-1",
										physicalVolumes = mapOf("/dev/sda" to 1.TB),
										size = 1.TB
								)
						)
				)
		)
		val hostDynDao = mock<HostDynamicDao>()
		session.mockProcess("bash.*lvm vgs.*".toRegex(),
				output =  """  RsDNYC-Un0h-QvhF-hMqe-dEny-Bjlg-qbeeZa:vg-1:433800085504B:4194304B:103426:1
--end
  RsDNYC-Un0h-QvhF-hMqe-dEny-Bjlg-qbeeZa:vg-1:433800085504B:4194304B:103426:1
--end
""")

		var hostDynamic = HostDynamic(
				id = host.id
		)

		doAnswer {
			hostDynamic
		}.whenever(hostDynDao)[eq(host.id)]

		doAnswer {
			val change = it.arguments[2] as (HostDynamic) -> HostDynamic
			hostDynamic = change(hostDynamic)
			hostDynamic
		}.whenever(hostDynDao).update(id = eq(host.id), retrieve = any(), change = any())

		session.mockProcess("bash.*lvm pvs.*".toRegex(), output = """  9pySAz-Uot3-JrcR-IJXJ-moDI-c3GF-02hPgW:/dev/sda:322118352896B:36498833408B:HS7Pxs-uWRe-9fj6-IMQ7-teEP-C5pZ-2M2a43:vg-1
--end
""")


		linux.startLvmVgMonitoring(host, session, hostDynDao)

		assertTrue {
			hostDynamic.storageStatus.let {
						it.single().freeCapacity == 4194304.toBigInteger()
						&& (it.single() as CompositeStorageDeviceDynamic).items.single().freeCapacity == 36498833408.toBigInteger()
			}
		}
	}

	@Test
	fun detectHostCpuType() {
		session.mockCommandExecution("^uname -p$".toRegex(),"x86_64")
		val linux = spy<AbstractLinux>()

		val cpuType = linux.detectHostCpuType(session)

		assertEquals("x86_64", cpuType)
		session.verifyCommandExecution("^uname -p$".toRegex())
	}

	@Test
	fun detectHostCpuTypeFallback() {
		session.mockCommandExecution("^uname -p$".toRegex(),"unknown")
		session.mockCommandExecution("^uname -m$".toRegex(),"aarch64")
		val linux = spy<AbstractLinux>()

		val cpuType = linux.detectHostCpuType(session)

		assertEquals("aarch64", cpuType)
		session.verifyCommandExecution("^uname -p$".toRegex())
	}

	@Test
	fun listLvmVolumes() {
		session.mockCommandExecution("^lvm vgs.*".toRegex(),
				"""  HS7Pxs-uWRe-9fj6-IMQ7-teEP-C5pZ-2M2a43:system:578176417792B:100352917504B:137848:23926
  vLC3jw-pbd1-cTyH-PMS1-OxCO-CvRi-VyXxYQ:testarea:678076350464B:98213822464B:161666:23416
""")
		session.mockCommandExecution("^lvm pvs.*".toRegex(),
				"""  9pySAz-Uot3-JrcR-IJXJ-moDI-c3GF-02hPgW:/dev/sda1:322118352896B:36498833408B:HS7Pxs-uWRe-9fj6-IMQ7-teEP-C5pZ-2M2a43:system
  orJJdF-SU8F-iDHw-5Lu4-aLcn-hSAT-eTrB4J:/dev/sda2:678076350464B:98213822464B:vLC3jw-pbd1-cTyH-PMS1-OxCO-CvRi-VyXxYQ:testarea
  gmPpnr-DoLB-67uq-iEw3-B5lc-FL9D-6qBcl2:/dev/sdb:256058064896B:63854084096B:HS7Pxs-uWRe-9fj6-IMQ7-teEP-C5pZ-2M2a43:system
""")

		val linux = spy<AbstractLinux>()

		val capabilities = linux.listLvmVolumes(session, pack(Centos, "7.5"), listOf(
				pack("lvm2", "1.2.3")
		))

		assertEquals(2, capabilities.size)
		assertEquals(2, capabilities.single { it.volumeGroupName == "system" }.physicalVolumes.size)
	}

	@Test
	fun listFilesystems() {
		session.mockCommandExecution("^df -l -P$".toRegex(),
				"""esystem                   1024-blocks      Used Available Capacity Mounted on
					udev                             8148912         0   8148912       0% /dev
					tmpfs                            1634412      1528   1632884       1% /run
					/dev/mapper/system-root         25671908  16404172   7940632      68% /
					tmpfs                            8172048    154980   8017068       2% /dev/shm
					tmpfs                               5120         4      5116       1% /run/lock
					tmpfs                            8172048         0   8172048       0% /sys/fs/cgroup
					/dev/loop1                        207744    207744         0     100% /snap/vlc/1049
					/dev/loop0                         91264     91264         0     100% /snap/core/7713
					/dev/loop3                         91264     91264         0     100% /snap/core/7917
					/dev/loop2                        207232    207232         0     100% /snap/vlc/770
					/dev/mapper/system-home        177394224 157699380  10896252      94% /home
					/dev/mapper/testarea-libvirt   206293688 184785772  11005772      95% /var/lib/libvirt
					tmpfs                            1634408        20   1634388       1% /run/user/1000
					""".trimIndent())
		session.mockCommandExecution("^mount$".toRegex(), """sysfs on /sys type sysfs (rw,nosuid,nodev,noexec,relatime)
					proc on /proc type proc (rw,nosuid,nodev,noexec,relatime)
					udev on /dev type devtmpfs (rw,nosuid,relatime,size=8148912k,nr_inodes=2037228,mode=755)
					devpts on /dev/pts type devpts (rw,nosuid,noexec,relatime,gid=5,mode=620,ptmxmode=000)
					tmpfs on /run type tmpfs (rw,nosuid,noexec,relatime,size=1634412k,mode=755)
					/dev/mapper/system-root on / type ext4 (rw,relatime,errors=remount-ro,data=ordered)
					securityfs on /sys/kernel/security type securityfs (rw,nosuid,nodev,noexec,relatime)
					tmpfs on /dev/shm type tmpfs (rw,nosuid,nodev)
					tmpfs on /run/lock type tmpfs (rw,nosuid,nodev,noexec,relatime,size=5120k)
					tmpfs on /sys/fs/cgroup type tmpfs (ro,nosuid,nodev,noexec,mode=755)
					cgroup on /sys/fs/cgroup/unified type cgroup2 (rw,nosuid,nodev,noexec,relatime,nsdelegate)
					cgroup on /sys/fs/cgroup/systemd type cgroup (rw,nosuid,nodev,noexec,relatime,xattr,name=systemd)
					pstore on /sys/fs/pstore type pstore (rw,nosuid,nodev,noexec,relatime)
					cgroup on /sys/fs/cgroup/rdma type cgroup (rw,nosuid,nodev,noexec,relatime,rdma)
					cgroup on /sys/fs/cgroup/hugetlb type cgroup (rw,nosuid,nodev,noexec,relatime,hugetlb)
					cgroup on /sys/fs/cgroup/cpu,cpuacct type cgroup (rw,nosuid,nodev,noexec,relatime,cpu,cpuacct)
					cgroup on /sys/fs/cgroup/perf_event type cgroup (rw,nosuid,nodev,noexec,relatime,perf_event)
					cgroup on /sys/fs/cgroup/net_cls,net_prio type cgroup (rw,nosuid,nodev,noexec,relatime,net_cls,net_prio)
					cgroup on /sys/fs/cgroup/cpuset type cgroup (rw,nosuid,nodev,noexec,relatime,cpuset)
					cgroup on /sys/fs/cgroup/freezer type cgroup (rw,nosuid,nodev,noexec,relatime,freezer)
					cgroup on /sys/fs/cgroup/pids type cgroup (rw,nosuid,nodev,noexec,relatime,pids)
					cgroup on /sys/fs/cgroup/devices type cgroup (rw,nosuid,nodev,noexec,relatime,devices)
					cgroup on /sys/fs/cgroup/blkio type cgroup (rw,nosuid,nodev,noexec,relatime,blkio)
					cgroup on /sys/fs/cgroup/memory type cgroup (rw,nosuid,nodev,noexec,relatime,memory)
					systemd-1 on /proc/sys/fs/binfmt_misc type autofs (rw,relatime,fd=27,pgrp=1,timeout=0,minproto=5,maxproto=5,direct,pipe_ino=571)
					hugetlbfs on /dev/hugepages type hugetlbfs (rw,relatime,pagesize=2M)
					mqueue on /dev/mqueue type mqueue (rw,relatime)
					debugfs on /sys/kernel/debug type debugfs (rw,relatime)
					fusectl on /sys/fs/fuse/connections type fusectl (rw,relatime)
					configfs on /sys/kernel/config type configfs (rw,relatime)
					/dev/mapper/system-home on /home type ext4 (rw,relatime,data=ordered)
					/dev/mapper/testarea-libvirt on /var/lib/libvirt type ext4 (rw,relatime,data=ordered)
					binfmt_misc on /proc/sys/fs/binfmt_misc type binfmt_misc (rw,relatime)
					tmpfs on /run/user/1000 type tmpfs (rw,nosuid,nodev,relatime,size=1634408k,mode=700,uid=1000,gid=1000)
					gvfsd-fuse on /run/user/1000/gvfs type fuse.gvfsd-fuse (rw,nosuid,nodev,relatime,user_id=1000,group_id=1000)
					""".trimIndent())
		val linux = spy<AbstractLinux>()

		val fsCapabilities = linux.listFilesystems(session)

		assertTrue(fsCapabilities.isNotEmpty())
	}
}