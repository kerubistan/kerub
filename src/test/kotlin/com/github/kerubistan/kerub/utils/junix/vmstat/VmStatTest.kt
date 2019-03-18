package com.github.kerubistan.kerub.utils.junix.vmstat

import com.github.kerubistan.kerub.sshtestutils.mockProcess
import com.nhaarman.mockito_kotlin.mock
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import java.util.ArrayList
import java.util.Collections
import kotlin.test.assertEquals

class VmStatTest {
	private val clientSession: ClientSession = mock()

	private val sample = """procs -----------memory---------- ---swap-- -----io---- -system-- ----cpu----
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
		clientSession.mockProcess("vmstat .*".toRegex(), output = sample)

		val results = Collections.synchronizedList(ArrayList<VmStatEvent>())
		VmStat.vmstat(clientSession, { results.add(it) })

		assertEquals(6, results.size)

	}
}