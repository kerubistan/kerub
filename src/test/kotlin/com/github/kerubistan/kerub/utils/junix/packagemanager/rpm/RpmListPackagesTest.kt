package com.github.kerubistan.kerub.utils.junix.packagemanager.rpm

import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.utils.junix.AbstractJunixCommandVerification
import com.github.kerubistan.kerub.utils.resource
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.commons.io.output.NullOutputStream
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RpmListPackagesTest : AbstractJunixCommandVerification() {
	@Test
	fun execute() {
		whenever(session.createExecChannel(any())).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedOut).then {
			resource("com/github/kerubistan/kerub/utils/junix/packagemanager/rpm/rpm-qa.txt")
		}
		whenever(execChannel.invertedIn).then { NullOutputStream() }
		whenever(execChannel.invertedErr).then { NullInputStream(0) }

		val pkgs = RpmListPackages.execute(session)

		assertTrue(pkgs.contains(
				SoftwarePackage(name = "zlib", version = Version.fromVersionString("1.2.11"))
		))
		assertEquals(987, pkgs.size)
	}
}