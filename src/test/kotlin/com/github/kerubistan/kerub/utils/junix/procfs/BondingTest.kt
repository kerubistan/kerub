package com.github.kerubistan.kerub.utils.junix.procfs

import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.testHostCapabilities
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.github.kerubistan.kroki.io.resource
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BondingTest {

	@Test
	fun available() {
		assertTrue(
				Bonding.available(testHostCapabilities.copy(os = OperatingSystem.Linux))
		)
	}

	@Test
	fun listBondInterfaces() {
		val session = mock<ClientSession>()
		val sftpClient = mock<SftpClient>()
		whenever(session.createSftpClient()).thenReturn(sftpClient)
		val handle = mock<SftpClient.CloseableHandle>()
		whenever(sftpClient.open(eq("/proc/net/bonding"))).thenReturn(handle)
		val bond0 = mock<SftpClient.DirEntry>()
		whenever(bond0.filename).thenReturn("bond0")
		val bond1 = mock<SftpClient.DirEntry>()
		whenever(bond1.filename).thenReturn("bond1")
		whenever(sftpClient.listDir(eq(handle))).thenReturn(listOf(bond0, bond1))

		val interfaces = Bonding.listBondInterfaces(session)

		assertEquals(listOf("bond0", "bond1"), interfaces)
	}

	@Test
	fun getBondInfo() {
		val session = mock<ClientSession>()
		val sftpClient = mock<SftpClient>()
		whenever(session.createSftpClient()).thenReturn(sftpClient)
		whenever(sftpClient.read(eq("/proc/net/bonding/bond0")))
				.thenReturn(resource("com/github/kerubistan/kerub/utils/junix/procfs/bond0.txt"))

		val bondInfo = Bonding.getBondInfo(session, "bond0")

		assertEquals(BondingMode.LoadBalancing, bondInfo.mode)
		assertEquals(3, bondInfo.slaves.size)
		assertEquals(
				SlaveInterface(name = "ens9", speedMbps = 100, hardwareAddress = "52:54:00:06:31:80", duplex = true),
				bondInfo.slaves[0]
		)
		assertEquals(
				SlaveInterface(name = "ens8", speedMbps = 100, hardwareAddress = "52:54:00:5f:5b:1c", duplex = true),
				bondInfo.slaves[1]
		)
		assertEquals(
				SlaveInterface(name = "ens3", speedMbps = null, hardwareAddress = "52:54:00:50:6f:cc", duplex = false),
				bondInfo.slaves[2]
		)
	}
}