package com.github.kerubistan.kerub.utils.junix.mdadm

import com.github.kerubistan.kerub.utils.junix.AbstractJunixCommandVerification
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.junit.Test
import org.mockito.ArgumentMatchers.startsWith

class MdadmTest : AbstractJunixCommandVerification() {
	@Test
	fun stop() {
		whenever(session.createExecChannel(startsWith("mdadm"))).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))
		whenever(execChannel.invertedOut).thenReturn(NullInputStream(0))

		Mdadm.stop(session, "/dev/md0")

		verify(execChannel).close(any())
	}

	@Test
	fun build() {
		whenever(session.createExecChannel(startsWith("mdadm"))).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))
		whenever(execChannel.invertedOut).thenReturn(NullInputStream(0))

		Mdadm.build(session, "/dev/md0", 2, listOf("/dev/sda", "/dev/sdd"))

		verify(execChannel).close(any())
	}

}