package com.github.kerubistan.kerub.utils.junix.lsblk

import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.utils.resource
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LsblkTest {

	private val session: ClientSession = mock()
	private val future: OpenFuture = mock()
	private val exec: ChannelExec = mock()

	@Test
	fun available() {
		assertFalse("no capabilities info - no lsblk") {
			Lsblk.available(null)
		}
		assertTrue("lsblk should be part of the default install on any linux") {
			Lsblk.available(testHostCapabilities.copy(os = OperatingSystem.Linux))
		}
		assertFalse("lsblk is not available on any other operating systems") {
			(OperatingSystem.values().toList() - OperatingSystem.Linux).any {
				Lsblk.available(testHostCapabilities.copy(os = it))
			}
		}
	}

	@Test
	fun list() {
		whenever(session.createExecChannel(argThat { startsWith("lsblk") })).thenReturn(exec)
		whenever(exec.open()).thenReturn(future)
		whenever(exec.invertedOut).then { resource("com/github/kerubistan/kerub/utils/junix/lsblk/lsblk-centos7.txt") }
		whenever(exec.invertedErr).then { NullInputStream(0) }

		val list = Lsblk.list(session)

		verify(session).createExecChannel(argThat { startsWith("lsblk") })
		assertEquals(3, list.size)
	}
}