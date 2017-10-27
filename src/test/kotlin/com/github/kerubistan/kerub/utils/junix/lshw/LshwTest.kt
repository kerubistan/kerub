package com.github.kerubistan.kerub.utils.junix.lshw

import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.utils.junix.AbstractJunixCommandVerification
import com.github.kerubistan.kerub.utils.resource
import com.nhaarman.mockito_kotlin.whenever
import junit.framework.Assert.assertFalse
import org.apache.commons.io.input.NullInputStream
import org.junit.Test
import kotlin.test.assertTrue

class LshwTest : AbstractJunixCommandVerification() {
	@Test
	fun list() {
		whenever(execChannel.invertedErr).then { NullInputStream(0) }
		whenever(execChannel.invertedOut).then { resource("com/github/kerubistan/kerub/utils/junix/lshw/lshw.json") }
		val system = Lshw.list(session)

	}

	@Test
	fun available() {
		assertTrue(
				Lshw.available(testHostCapabilities.copy(
						os = OperatingSystem.Linux,
						installedSoftware = listOf(
								SoftwarePackage(name = "lshw", version = Version.fromVersionString("B.02.17"))
						)
				))
		)
		assertFalse(
				Lshw.available(testHostCapabilities.copy(
						os = OperatingSystem.BSD
				))
		)
	}

}