package com.github.kerubistan.kerub.utils.junix.du

import com.github.kerubistan.kerub.utils.junix.AbstractJunixCommandVerification
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.commons.io.output.NullOutputStream
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream
import java.math.BigInteger

class DUTest : AbstractJunixCommandVerification() {

	@Test
	fun du() {
		whenever(session.createExecChannel(eq("du -B 1 /kerub/file"))).thenReturn(execChannel)
		whenever(execChannel.invertedOut).thenReturn(ByteArrayInputStream(
				"1234	/kerub/file".toByteArray(Charsets.US_ASCII))
		)
		whenever(execChannel.invertedIn).thenReturn(NullOutputStream())
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))

		assertEquals(BigInteger("1234"), DU.du(session, "/kerub/file"))
	}

}