package com.github.K0zka.kerub.hypervisor.kvm

import com.github.K0zka.kerub.anyString
import com.github.K0zka.kerub.data.VirtualStorageDeviceDao
import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.K0zka.kerub.eq
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.VirtualStorageLink
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.K0zka.kerub.model.io.BusType
import com.github.K0zka.kerub.never
import com.github.K0zka.kerub.utils.toSize
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.io.ByteArrayOutputStream
import java.util.UUID

@RunWith(MockitoJUnitRunner::class)
class KvmHypervisorTest {

	@Mock
	var client: ClientSession? = null
	@Mock
	var execChannel: ChannelExec? = null
	@Mock
	var openFuture : OpenFuture? = null
	@Mock
	var vmDynDao: VirtualMachineDynamicDao? = null
	@Mock
	var virtualStorageDao: VirtualStorageDeviceDao? = null
	@Mock
	var virtualStorageDynDao: VirtualStorageDeviceDynamicDao? = null
	@Mock
	var sftpClient: SftpClient? = null

	val host = Host(
			id = UUID.randomUUID(),
			address = "host-1.example.com",
			dedicated = true,
			publicKey = ""
	)

	@Test
	fun testStartVmWithNoStorage() {
		val vm = VirtualMachine(id = UUID.randomUUID(), name = "vm-1")

		Mockito.`when`(client!!.createSftpClient()).thenReturn(sftpClient!!)
		Mockito.`when`(sftpClient!!.write(anyString())).thenReturn(ByteArrayOutputStream())
		Mockito.`when`(client!!.createExecChannel(anyString())).thenReturn(execChannel!!)
		Mockito.`when`(execChannel!!.open()).thenReturn(openFuture!!)
		Mockito.`when`(execChannel!!.invertedErr).thenReturn(NullInputStream(0))
		Mockito.`when`(execChannel!!.invertedOut).thenReturn(NullInputStream(0))

		KvmHypervisor(client!!, host, vmDynDao!!, virtualStorageDao!!, virtualStorageDynDao!!).startVm(vm)

		Mockito.verify(virtualStorageDao!!, never).get(Mockito.any(UUID::class.java) ?: UUID.randomUUID())
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
				allocation = VirtualStorageLvmAllocation(
						hostId = host.id,
						path = "/dev/blah"
				),
				actualSize = "100 GB".toSize()
		)
		val vm = VirtualMachine(
				id = UUID.randomUUID(),
				name = "vm-1",
				virtualStorageLinks = listOf(VirtualStorageLink(
						bus = BusType.sata,
						virtualStorageId = vDisk.id
				))
		)

		Mockito.`when`(client!!.createSftpClient()).thenReturn(sftpClient!!)
		Mockito.`when`(sftpClient!!.write(anyString())).thenReturn(ByteArrayOutputStream())
		Mockito.`when`(client!!.createExecChannel(anyString())).thenReturn(execChannel!!)
		Mockito.`when`(execChannel!!.open()).thenReturn(openFuture!!)
		Mockito.`when`(execChannel!!.invertedErr).thenReturn(NullInputStream(0))
		Mockito.`when`(execChannel!!.invertedOut).thenReturn(NullInputStream(0))
		Mockito.`when`(virtualStorageDao!![eq(vDisk.id)]).thenReturn(vDisk)
		Mockito.`when`(virtualStorageDynDao!![eq(vDisk.id)]).thenReturn(virtualStorageDeviceDynamic)

		KvmHypervisor(client!!, host, vmDynDao!!, virtualStorageDao!!, virtualStorageDynDao!!).startVm(vm)

		Mockito.verify(virtualStorageDao!!).get(Mockito.any(UUID::class.java) ?: UUID.randomUUID())
	}


	@Test
	fun testStopVm() {
	}

	@Test
	fun testMigrate() {
	}
}