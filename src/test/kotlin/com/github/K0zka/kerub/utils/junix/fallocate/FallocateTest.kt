package com.github.K0zka.kerub.utils.junix.fallocate

import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.testHostCapabilities
import com.github.K0zka.kerub.utils.junix.AbstractJunixCommandVerification
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class FallocateTest : AbstractJunixCommandVerification() {
	@Test
	fun available() {
		assertTrue(Fallocate.available(testHostCapabilities.copy(
				os = OperatingSystem.Linux,
				installedSoftware = listOf(
						SoftwarePackage("util-linux", Version.fromVersionString("2.29"))
				)
		)))
	}

	@Test
	fun dig() {
		val randId = UUID.randomUUID()
		whenever(execChannel.invertedOut).thenReturn(NullInputStream(0))
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))
		Fallocate.dig(session, "/kerub/$randId")

		verify(session).createExecChannel(eq("fallocate -d /kerub/$randId"))
	}
}