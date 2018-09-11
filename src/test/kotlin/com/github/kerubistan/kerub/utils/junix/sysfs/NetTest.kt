package com.github.kerubistan.kerub.utils.junix.sysfs

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Test
import java.io.ByteArrayInputStream
import kotlin.test.assertEquals

class NetTest {

	private val clientSession: ClientSession = mock()
	private val sftpClient: SftpClient = mock()

	@Test
	fun listDevices() {
		val deviceDir = mock<SftpClient.DirEntry>()
		whenever(deviceDir.filename).thenReturn("eth0")
		whenever(clientSession.createSftpClient()).thenReturn(sftpClient)
		whenever(sftpClient.readDir(any<String>())).thenReturn(listOf(deviceDir))

		val device = Net.listDevices(clientSession)

		assertEquals(device.size, 1)
		assertEquals(device[0], "eth0")
	}

	@Test
	fun getMacAddress() {
		val content = ByteArrayInputStream("52:54:00:b5:75:bb".toByteArray(charset("ASCII")))
		whenever(sftpClient.read(any())).thenReturn(content)
		whenever(clientSession.createSftpClient()).thenReturn(sftpClient)

		val response = Net.getMacAddress(clientSession, "eth0")

		assertEquals(response.size, 6)
		assertEquals(response[2], 0.toByte())
	}

}