package com.github.K0zka.kerub.utils.junix.storagemanager.lvm

import com.github.K0zka.kerub.utils.toSize
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.io.ByteArrayInputStream
import java.math.BigInteger
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class LvmPvTest {

	@Mock
	var session : ClientSession? = null

	@Mock
	var execChannel: ChannelExec? = null

	@Mock
	var createExecChannel: ChannelExec? = null

	@Mock
	var openFuture: OpenFuture? = null


	companion object {
		val testOutput = """  KNQsfE-ddlI-PEnl-3S7i-qu3U-8w1X-l6Nen1:/dev/sda2:166673252352B:0B:gEUr1s-SwpD-vwZ4-trFZ-ZxJp-7kAr-E0QA5g
  02YYyV-qaA1-hSo7-M5Ua-5zx4-IzQm-f6eLRq:/dev/sda3:832997163008B:299804655616B:gEUr1s-SwpD-vwZ4-trFZ-ZxJp-7kAr-E0QA5g
"""

	}

	@Test
	fun list() {
		Mockito.`when`(session?.createExecChannel(Matchers.startsWith("lvm pvs"))).thenReturn(execChannel)
		Mockito.`when`(execChannel?.open()).thenReturn(openFuture)
		Mockito.`when`(execChannel?.invertedOut).thenReturn(ByteArrayInputStream(testOutput.toByteArray(charset("ASCII"))))
		Mockito.`when`(execChannel?.invertedErr).thenReturn(NullInputStream(0))

		val list = LvmPv.list(session!!)

		Assert.assertEquals(2, list.size)

		Assert.assertEquals("166673252352 B".toSize(), list[0].size)
		Assert.assertEquals(BigInteger.ZERO, list[0].freeSize)
		Assert.assertEquals("/dev/sda2", list[0].device)
		Assert.assertEquals("gEUr1s-SwpD-vwZ4-trFZ-ZxJp-7kAr-E0QA5g", list[0].volumeGroupId)
	}

	@Test
	fun listEmpty() {
		Mockito.`when`(session?.createExecChannel(Matchers.startsWith("lvm pvs"))).thenReturn(execChannel)
		Mockito.`when`(execChannel?.open()).thenReturn(openFuture)
		Mockito.`when`(execChannel?.invertedOut).thenReturn(ByteArrayInputStream("\n".toByteArray(charset("ASCII"))))
		Mockito.`when`(execChannel?.invertedErr).thenReturn(NullInputStream(0))

		val list = LvmPv.list(session!!)

		assertEquals(listOf<PhysicalVolume>(), list)
	}
}