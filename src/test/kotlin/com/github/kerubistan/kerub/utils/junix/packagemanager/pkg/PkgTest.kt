package com.github.kerubistan.kerub.utils.junix.packagemanager.pkg

import com.github.kerubistan.kerub.utils.junix.AbstractJunixCommandVerification
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class PkgTest : AbstractJunixCommandVerification() {

	@Before
	fun setup() {
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))
		whenever(execChannel.invertedOut).thenReturn(NullInputStream(0))
	}

	@Test
	fun installPackage() {
		Pkg.installPackage(session, "foo", "bar", "baz")
		verify(session).createExecChannel("pkg install -y foo bar baz")
	}

	@Test
	fun uninstallPackage() {
		Pkg.uninstallPackage(session, "foo", "bar", "baz")
		verify(session).createExecChannel("pkg remove -y foo bar baz")
	}

	@Test
	fun installNoPackage() {
		assertThrows<IllegalArgumentException>  { Pkg.installPackage(session) }
		verify(session, never()).createExecChannel("pkg install -y foo bar baz")
	}

	@Test
	fun uninstallNoPackage() {
		assertThrows<IllegalArgumentException>  { Pkg.uninstallPackage(session) }
		verify(session, never()).createExecChannel("pkg remove -y foo bar baz")
	}

}