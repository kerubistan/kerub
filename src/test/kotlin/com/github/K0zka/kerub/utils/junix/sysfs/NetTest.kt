package com.github.K0zka.kerub.utils.junix.sysfs

import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.io.ByteArrayInputStream

@RunWith(MockitoJUnitRunner::class) class NetTest {

	@Mock
	var clientSession: ClientSession? = null
	@Mock
	var sftpClient: SftpClient? = null

	@Test
	fun listDevices() {
		val deviceDir = Mockito.mock(SftpClient.DirEntry::class.java)
		Mockito.`when`(deviceDir.filename).thenReturn("eth0")
		Mockito.`when`(clientSession!!.createSftpClient()).thenReturn(sftpClient)
		Mockito.`when`(sftpClient!!.readDir(Matchers.anyString())).thenReturn(listOf(deviceDir))

		val device = Net.listDevices(clientSession!!)

		Assert.assertEquals(device.size, 1)
		Assert.assertEquals(device[0], "eth0")
	}

	@Test
	fun getMacAddress() {
		val content = ByteArrayInputStream("52:54:00:b5:75:bb".toByteArray(charset("ASCII")))
		Mockito.`when`(sftpClient!!.read(Matchers.anyString())).thenReturn(content)
		Mockito.`when`(clientSession!!.createSftpClient()).thenReturn(sftpClient)

		val response = Net.getMacAddress(clientSession!!, "eth0")

		Assert.assertEquals(response.size, 6)
		Assert.assertEquals(response[2], 0.toByte())
	}

}