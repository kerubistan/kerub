package com.github.kerubistan.kerub.utils.junix.packagemanager.dpkg

import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.utils.junix.AbstractJunixCommandVerification
import com.github.kerubistan.kerub.utils.resource
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.commons.io.output.NullOutputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DpkgTest : AbstractJunixCommandVerification() {

	@Test
	fun listPackages() {
		whenever(session.createExecChannel(any())).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedOut).then {
			resource("com/github/kerubistan/kerub/utils/junix/packagemanager/dpkg/dpkg-query-debian8.txt")
		}
		whenever(execChannel.invertedIn).then { NullOutputStream() }
		whenever(execChannel.invertedErr).then { NullInputStream(0) }

		val pkgs = Dpkg.listPackages(session)

		assertEquals(540, pkgs.size)
		assertTrue(pkgs.contains(
				SoftwarePackage(name = "wget", version = Version.fromVersionString("1.16-1+deb8u2"))
		))
	}

}