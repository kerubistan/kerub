package com.github.kerubistan.kerub.hypervisor.kvm

import com.github.kerubistan.kerub.data.HostDao
import com.github.kerubistan.kerub.data.VirtualStorageDeviceDao
import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.VirtualStorageLink
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.io.BusType
import com.github.kerubistan.kerub.utils.toSize
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Test
import org.mockito.Mockito
import java.io.ByteArrayOutputStream
import java.util.UUID

class KvmHypervisorTest {

	val client: ClientSession = mock()
	val execChannel: ChannelExec = mock()
	val openFuture: OpenFuture = mock()
	val vmDynDao: VirtualMachineDynamicDao = mock()
	val virtualStorageDao: VirtualStorageDeviceDao = mock()
	val virtualStorageDynDao: VirtualStorageDeviceDynamicDao = mock()
	val hostDao: HostDao = mock()
	val hostDynDao: HostDynamicDao = mock()
	val hostCfgDao: HostConfigurationDao = mock()
	val sftpClient: SftpClient = mock()

	val host = Host(
			id = UUID.randomUUID(),
			address = "host-1.example.com",
			dedicated = true,
			publicKey = ""
	)

	@Test
	fun testStartVmWithNoStorage() {
		val vm = VirtualMachine(id = UUID.randomUUID(), name = "vm-1")

		Mockito.`when`(client.createSftpClient()).thenReturn(sftpClient)
		Mockito.`when`(sftpClient.write(any())).thenReturn(ByteArrayOutputStream())
		Mockito.`when`(client.createExecChannel(any())).thenReturn(execChannel)
		Mockito.`when`(execChannel.open()).thenReturn(openFuture)
		Mockito.`when`(execChannel.invertedErr).thenReturn(NullInputStream(0))
		Mockito.`when`(execChannel.invertedOut).thenReturn(NullInputStream(0))

		KvmHypervisor(client, host, hostDao, hostCfgDao, hostDynDao, vmDynDao, virtualStorageDao, virtualStorageDynDao)
				.startVm(vm, "")

		Mockito.verify(virtualStorageDao, never()).get(Mockito.any(UUID::class.java) ?: UUID.randomUUID())
	}

	@Test
	fun testStartVmWithStorage() {
		val vDisk = VirtualStorageDevice(
				id = UUID.randomUUID(),
				name = "system disk",
				size = "120 GB".toSize()
		)
		val virtualStorageDeviceDynamic = VirtualStorageDeviceDynamic(
				id = vDisk.id,
				allocations = listOf(VirtualStorageLvmAllocation(
						hostId = host.id,
						actualSize = "100 GB".toSize(),
						path = "/dev/blah",
						vgName = "blah"
				))
		)
		val vm = VirtualMachine(
				id = UUID.randomUUID(),
				name = "vm-1",
				virtualStorageLinks = listOf(VirtualStorageLink(
						bus = BusType.sata,
						virtualStorageId = vDisk.id
				))
		)

		val hostDyn = HostDynamic(
				id = host.id,
				status = HostStatus.Up,
				memFree = "2GB".toSize()
		)

		Mockito.`when`(client.createSftpClient()).thenReturn(sftpClient)
		Mockito.`when`(sftpClient.write(any())).thenReturn(ByteArrayOutputStream())
		Mockito.`when`(client.createExecChannel(any())).thenReturn(execChannel)
		Mockito.`when`(execChannel.open()).thenReturn(openFuture)
		Mockito.`when`(execChannel.invertedErr).thenReturn(NullInputStream(0))
		Mockito.`when`(execChannel.invertedOut).thenReturn(NullInputStream(0))
		Mockito.`when`(virtualStorageDao[eq(vDisk.id)]).thenReturn(vDisk)
		Mockito.`when`(virtualStorageDynDao[eq(vDisk.id)]).thenReturn(virtualStorageDeviceDynamic)
		Mockito.`when`(virtualStorageDynDao[eq(listOf(vDisk.id))]).thenReturn(listOf(virtualStorageDeviceDynamic))
		Mockito.`when`(virtualStorageDao[eq(listOf(vDisk.id))]).thenReturn(listOf(vDisk))
		Mockito.`when`(hostDao[eq(listOf(host.id))]).thenReturn(listOf(host))
		Mockito.`when`(hostDynDao[eq(listOf(host.id))]).thenReturn(listOf(hostDyn))

		KvmHypervisor(client, host, hostDao, hostCfgDao, hostDynDao, vmDynDao, virtualStorageDao, virtualStorageDynDao)
				.startVm(vm, "")

		Mockito.verify(client).createExecChannel(any())
	}

}