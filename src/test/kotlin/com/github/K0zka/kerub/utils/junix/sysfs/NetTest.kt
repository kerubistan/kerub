package com.github.K0zka.kerub.utils.junix.sysfs

import org.apache.sshd.ClientSession
import org.apache.sshd.client.SftpClient
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.io.ByteArrayInputStream

@RunWith(MockitoJUnitRunner::class)
public class NetTest {

	@Mock
	var clientSession: ClientSession? = null
	@Mock
	var sftpClient: SftpClient? = null

	@Test
	fun listDevices() {
		val deviceDir = SftpClient.DirEntry("eth0", "eth0", null)
		Mockito.`when`(clientSession!!.createSftpClient()).thenReturn(sftpClient)
		Mockito.`when`(sftpClient!!.readDir(Matchers.anyString())).thenReturn(listOf(deviceDir))

		val device = Net.listDevices(clientSession!!)

		Assert.assertEquals(device.size, 1)
		Assert.assertEquals(device[0], "eth0")
	}

	@Test
	fun getMacAddress() {
		val content = ByteArrayInputStream("52:54:00:b5:75:bb".toByteArray("ASCII"))
		Mockito.`when`(sftpClient!!.read(Matchers.anyString())).thenReturn(content)
		Mockito.`when`(clientSession!!.createSftpClient()).thenReturn(sftpClient)

		val response = Net.getMacAddress(clientSession!!, "eth0")

		Assert.assertEquals(response.size, 6)
		Assert.assertEquals(response[2], 0.toByte())
	}

	@Test
	fun stringToMac() {
		val mac = Net.stringToMac("52:54:00:b5:75:bb")
		Assert.assertEquals(mac.size, Net.macAddressSize)
	}

	@Test(expected = IllegalArgumentException::class)
	fun stringToMacWithWrongLength() {
		val mac = Net.stringToMac("52:54:00:b5:75:bb:ff")
	}

	@Test(expected = IllegalArgumentException::class)
	fun stringToMacWithWrongByteLength() {
		val mac = Net.stringToMac("52:54:00:b5:75:bbbb:ff")
	}

	@Test
	fun macToString() {
		Assert.assertEquals(Net.macToString(ByteArray(6)), "00:00:00:00:00:00")
	}

	@Test(expected = IllegalArgumentException::class)
	fun macToStringWithWrongLength() {
		Net.macToString(ByteArray(7))
	}

}