package com.github.kerubistan.kerub.utils.junix.sysfs

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import java.io.ByteArrayInputStream

class NetTest {

	val clientSession: ClientSession = mock()
	val sftpClient: SftpClient = mock()

	@Test
	fun listDevices() {
		val deviceDir = mock<SftpClient.DirEntry>()
		Mockito.`when`(deviceDir.filename).thenReturn("eth0")
		Mockito.`when`(clientSession.createSftpClient()).thenReturn(sftpClient)
		Mockito.`when`(sftpClient.readDir(any<String>())).thenReturn(listOf(deviceDir))

		val device = Net.listDevices(clientSession)

		Assert.assertEquals(device.size, 1)
		Assert.assertEquals(device[0], "eth0")
	}

	@Test
	fun getMacAddress() {
		val content = ByteArrayInputStream("52:54:00:b5:75:bb".toByteArray(charset("ASCII")))
		Mockito.`when`(sftpClient.read(any<String>())).thenReturn(content)
		Mockito.`when`(clientSession.createSftpClient()).thenReturn(sftpClient)

		val response = Net.getMacAddress(clientSession, "eth0")

		Assert.assertEquals(response.size, 6)
		Assert.assertEquals(response[2], 0.toByte())
	}

}