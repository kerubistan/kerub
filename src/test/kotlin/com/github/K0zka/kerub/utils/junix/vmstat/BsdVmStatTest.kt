package com.github.K0zka.kerub.utils.junix.vmstat

import com.github.K0zka.kerub.utils.resource
import com.github.K0zka.kerub.utils.resourceToString
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.hamcrest.CoreMatchers
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.runners.MockitoJUnitRunner
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
		val sample = resourceToString("com/github/K0zka/kerub/utils/junix/bsd-vmstat.txt")
		doAnswer {
			val out = it.arguments[0] as OutputStream
			sample.forEach {
				out.write( it.toInt() )
			}
		}.whenever(channelExec).out = Matchers.any(OutputStream::class.java)

		var nr = 0
		BsdVmStat.vmstat(session, {
			nr = nr + 1
		})
		assertTrue(nr > 0)
	}
}