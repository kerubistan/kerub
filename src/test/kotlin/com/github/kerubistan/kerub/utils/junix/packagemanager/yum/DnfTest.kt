package com.github.kerubistan.kerub.utils.junix.packagemanager.yum

import com.github.kerubistan.kerub.utils.junix.AbstractJunixCommandVerification
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class DnfTest : AbstractJunixCommandVerification() {

	@Before
	fun setup() {
		whenever(execChannel.invertedOut).thenReturn(NullInputStream(0))
	}

	@Test
	fun installNoPackage() {
		assertThrows<IllegalArgumentException> { Dnf.installPackage(session) }
		verify(session, never()).createExecChannel("dnf -y install foo bar baz")
	}

	@Test
	fun uninstallNoPackage() {
		assertThrows<IllegalArgumentException> { Dnf.uninstallPackage(session) }
		verify(session, never()).createExecChannel("dnf -y remove foo bar baz")
	}

	@Test
	fun installPackage() {
		Dnf.installPackage(session, "foo", "bar", "baz")
		verify(session).createExecChannel("dnf -y install foo bar baz")
	}

	@Test
	fun uninstallPackage() {
		Dnf.uninstallPackage(session, "foo", "bar", "baz")
		verify(session).createExecChannel("dnf -y remove foo bar baz")
	}

}