package com.github.kerubistan.kerub.utils.junix.vmstat

import com.github.kerubistan.kerub.sshtestutils.mockProcess
import com.github.kerubistan.kerub.utils.resourceToString
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.apache.sshd.client.session.ClientSession
import org.junit.Test

class BsdVmStatTest {

	val session = mock<ClientSession>()

	@Test
	fun vmstat() {
		session.mockProcess(
				"vmstat .*".toRegex(),
				resourceToString("com/github/kerubistan/kerub/utils/junix/bsd-vmstat.txt"))

		val handler = mock<(BsdVmStatEvent) -> Unit>()
		BsdVmStat.vmstat(session, handler)
		verify(handler, times(41)).invoke(any())
	}
}