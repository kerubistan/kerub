package com.github.K0zka.kerub.utils.junix.vmstat

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import org.mockito.Matchers
import org.mockito.Mockito
import java.io.OutputStream
import java.util.ArrayList
import java.util.Collections

class VmStatTest {
	val clientSession: ClientSession = mock()
	val execChannel: ChannelExec = mock()
	val openFuture: OpenFuture = mock()

	val sample = """procs -----------memory---------- ---swap-- -----io---- -system-- ----cpu----
 r  b   swpd   free   buff  cache   si   so    bi    bo   in   cs us sy id wa
 1  0 162500 401208 1730444 1251504    0    0    52   118   33   40 39  9 51  1
 0  0 162500 401068 1730444 1251504    0    0     0    24 1398 2397 13  6 79  2
 0  0 162500 401068 1730444 1251504    0    0     0   600 1437 2252 12  6 82  0
 1  0 162500 401100 1730444 1251504    0    0     0     0 1395 2144 13  5 82  0
 0  0 162500 400712 1730456 1251508    0    0     0   308 1547 2593 14  6 75  5
 1  0 162500 400624 1730460 1251508    0    0     0     4 1497 2357 13  5 80  2
"""

	@Test
	fun vmstat() {
		whenever(clientSession.createExecChannel(any())).thenReturn(execChannel)
		Mockito.doAnswer {
			val out = it.arguments[0] as OutputStream
			sample.forEach {
				out.write(it.toInt())
			}
			null
		}.whenever(execChannel)!!.out = Matchers.any(OutputStream::class.java)
		whenever(execChannel.open()).thenReturn(openFuture)

		val results = Collections.synchronizedList(ArrayList<VmStatEvent>())
		VmStat.vmstat(clientSession, { results.add(it) })

		Assert.assertThat(results.size, CoreMatchers.`is`(6))

	}
}