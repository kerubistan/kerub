package com.github.kerubistan.kerub.utils.junix.vmstat

import com.github.kerubistan.kerub.utils.resourceToString
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import java.io.OutputStream

class BsdVmStatTest {

	val session = mock<ClientSession>()
	val openFuture = mock<OpenFuture>()
	val channelExec = mock<ChannelExec>()

	@Test
	fun vmstat() {
		whenever(session.createExecChannel(any())).thenReturn(channelExec)
		whenever(channelExec.open()).thenReturn(openFuture)
		whenever(channelExec.invertedErr).thenReturn(NullInputStream(0))
		val sample = resourceToString("com/github/kerubistan/kerub/utils/junix/bsd-vmstat.txt")
		doAnswer {
			val out = it.arguments[0] as OutputStream
			sample.forEach {
				out.write( it.toInt() )
			}
		}.whenever(channelExec).out = any()

		val handler = mock<(BsdVmStatEvent) -> Unit>()
		BsdVmStat.vmstat(session, handler)
		verify(handler, times(114)).invoke(any())
	}
}