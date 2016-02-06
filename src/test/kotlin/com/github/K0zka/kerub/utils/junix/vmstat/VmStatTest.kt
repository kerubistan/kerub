package com.github.K0zka.kerub.utils.junix.vmstat

import com.github.K0zka.kerub.anyString
import com.github.K0zka.kerub.on
import org.apache.sshd.ClientSession
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.io.OutputStream
import java.util.ArrayList
import java.util.Collections

@RunWith(MockitoJUnitRunner::class) class VmStatTest {
	@Mock
	var clientSession : ClientSession? = null
	@Mock
	var execChannel : ChannelExec? = null
	@Mock
	var openFuture : OpenFuture? = null

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
		on(clientSession!!.createExecChannel(anyString())).thenReturn( execChannel )
		Mockito.doAnswer {
			val out = it.arguments[0] as OutputStream
			sample.forEach {
				out.write( it.toInt() )
			}
			null
		} .`when`(execChannel)!!.out = Matchers.any(OutputStream::class.java)
		on(execChannel!!.open()).thenReturn(openFuture)

		val results = Collections.synchronizedList(ArrayList<VmStat.VmStatEvent>())
		VmStat.vmstat(clientSession!!, { results.add(it) })

		Assert.assertThat(results.size, CoreMatchers.`is`(6))

	}
}