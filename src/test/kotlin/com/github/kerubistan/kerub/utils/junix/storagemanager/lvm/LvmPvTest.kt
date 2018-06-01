package com.github.kerubistan.kerub.utils.junix.storagemanager.lvm

import com.github.kerubistan.kerub.utils.toSize
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import java.io.ByteArrayInputStream
import java.math.BigInteger
import kotlin.test.assertEquals

class LvmPvTest {

	val session : ClientSession = mock()
	val execChannel: ChannelExec = mock()
	val createExecChannel: ChannelExec = mock()
	val openFuture: OpenFuture = mock()


	companion object {
		val testOutput = """  KNQsfE-ddlI-PEnl-3S7i-qu3U-8w1X-l6Nen1:/dev/sda2:166673252352B:0B:gEUr1s-SwpD-vwZ4-trFZ-ZxJp-7kAr-E0QA5g
  02YYyV-qaA1-hSo7-M5Ua-5zx4-IzQm-f6eLRq:/dev/sda3:832997163008B:299804655616B:gEUr1s-SwpD-vwZ4-trFZ-ZxJp-7kAr-E0QA5g
"""

	}

	@Test
	fun list() {
		whenever(session.createExecChannel(argThat { startsWith("lvm pvs") })).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedOut).thenReturn(ByteArrayInputStream(testOutput.toByteArray(charset("ASCII"))))
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))

		val list = LvmPv.list(session)

		assertEquals(2, list.size)

		assertEquals("166673252352 B".toSize(), list[0].size)
		assertEquals(BigInteger.ZERO, list[0].freeSize)
		assertEquals("/dev/sda2", list[0].device)
		assertEquals("gEUr1s-SwpD-vwZ4-trFZ-ZxJp-7kAr-E0QA5g", list[0].volumeGroupId)
	}

	@Test
	fun listEmpty() {
		whenever(session.createExecChannel(argThat { startsWith("lvm pvs") })).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedOut).thenReturn(ByteArrayInputStream("\n".toByteArray(charset("ASCII"))))
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))

		val list = LvmPv.list(session)

		assertEquals(listOf(), list)
	}
}