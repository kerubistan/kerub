package com.github.K0zka.kerub.utils.junix.procfs

import com.github.K0zka.kerub.utils.resourceToString
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import org.mockito.Matchers
import org.mockito.Mockito
import java.io.OutputStream
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class StatTest {

	val session: ClientSession = mock()
	val execChannel: ChannelExec = mock()
	val openFuture: OpenFuture = mock()

	@Test
	fun cpuLoadMonitorWithLinux() {
		whenever(session.createExecChannel(any())).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))

		doAnswer {
			val out = it.arguments[0] as OutputStream
			resourceToString("com/github/K0zka/kerub/utils/junix/procfs/procfs-stat-linux.txt").forEach {
				out.write(it.toInt())
			}
			null
		}.`when`(execChannel)!!.out = Matchers.any(OutputStream::class.java)

		var cntr = 0
		Stat.cpuLoadMonitor(session) {
			cntr = cntr + 1
		}
		assertEquals(20, cntr)
	}

	@Test
	fun cpuLoadMonitorWithCygwin() {
		whenever(session.createExecChannel(any())).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))

		doAnswer {
			val out = it.arguments[0] as OutputStream
			resourceToString("com/github/K0zka/kerub/utils/junix/procfs/procfs-stat-cygwin.txt").forEach {
				out.write(it.toInt())
			}
			null
		}.`when`(execChannel)!!.out = Matchers.any(OutputStream::class.java)

		var cntr = 0
		Stat.cpuLoadMonitor(session) {
			cntr = cntr + 1
		}
		assertEquals(23, cntr)
	}

	@Test
	fun testCpuLoadMonitorIncrementalWithLinux() {
		whenever(session.createExecChannel(any())).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))

		doAnswer {
			val out = it.arguments[0] as OutputStream
			resourceToString("com/github/K0zka/kerub/utils/junix/procfs/procfs-stat-linux.txt").forEach {
				out.write(it.toInt())
			}
			null
		}.`when`(execChannel)!!.out = Matchers.any(OutputStream::class.java)

		var cntr = 0
		Stat.cpuLoadMonitorIncremental(session) {
			cntr = cntr + 1
		}

		assertEquals(19, cntr)
	}

	@Test
	fun testCpuLoadMonitorIncrementalWithCygwin() {
		whenever(session.createExecChannel(any())).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))

		doAnswer {
			val out = it.arguments[0] as OutputStream
			resourceToString("com/github/K0zka/kerub/utils/junix/procfs/procfs-stat-cygwin.txt").forEach {
				out.write(it.toInt())
			}
			null
		}.`when`(execChannel)!!.out = Matchers.any(OutputStream::class.java)

		var cntr = 0
		Stat.cpuLoadMonitorIncremental(session) {
			cntr = cntr + 1
		}

		assertEquals(22, cntr)
	}

}