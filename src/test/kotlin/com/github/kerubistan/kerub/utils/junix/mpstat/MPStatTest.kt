package com.github.kerubistan.kerub.utils.junix.mpstat

import com.github.kerubistan.kerub.model.dynamic.CpuStat
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert
import org.junit.Test
import java.io.OutputStream

class MPStatTest {
	private val testInput = """Linux 4.1.6-201.fc22.x86_64 (localshot) 	11/02/2015 	_x86_64_	(2 CPU)

07:11:03 AM  CPU    %usr   %nice    %sys %iowait    %irq   %soft  %steal  %guest  %gnice   %idle
07:11:04 AM  all   10.88    0.00    8.81    0.00    0.00    0.52    0.00    0.00    0.00   79.79
07:11:04 AM    0   13.27    0.00   11.22    0.00    0.00    1.02    0.00    0.00    0.00   74.49
07:11:04 AM    1    9.28    0.00    6.19    0.00    0.00    0.00    0.00    0.00    0.00   84.54

07:11:04 AM  CPU    %usr   %nice    %sys %iowait    %irq   %soft  %steal  %guest  %gnice   %idle
07:11:05 AM  all   31.79    0.00    6.67    0.00    0.00    0.00    0.00    0.00    0.00   61.54
07:11:05 AM    0   32.99    0.00    8.25    0.00    0.00    0.00    0.00    0.00    0.00   58.76
07:11:05 AM    1   30.61    0.00    6.12    0.00    0.00    0.00    0.00    0.00    0.00   63.27
"""

	val session : ClientSession = mock()
	val execChannel : ChannelExec = mock()
	val openFuture : OpenFuture = mock()

	@Test
	fun monitor() {
		whenever(session.createExecChannel(any())).thenReturn( execChannel )
		doAnswer {
			val out = it.arguments[0] as OutputStream
			testInput.forEach {
				out.write( it.toInt() )
			}
			null
		} .whenever(execChannel)!!.out = any()
		whenever(execChannel.open()).thenReturn(openFuture)

		var stat = listOf<CpuStat>()

		MPStat.monitor(session, { stat += it } , interval = 1)

		Assert.assertEquals(0, stat[0].cpuNr)
		Assert.assertEquals(13.27, stat[0].user.toDouble(), 0.1)
		Assert.assertEquals(11.22, stat[0].system.toDouble(), 0.1)
		Assert.assertEquals(74.49, stat[0].idle.toDouble(), 0.1)
		Assert.assertEquals(0.toDouble(), stat[0].ioWait.toDouble(), 0.1)
	}

}