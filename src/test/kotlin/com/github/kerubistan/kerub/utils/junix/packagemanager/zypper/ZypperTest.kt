package com.github.kerubistan.kerub.utils.junix.packagemanager.zypper

import com.github.kerubistan.kerub.expect
import com.github.kerubistan.kerub.utils.junix.AbstractJunixCommandVerification
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.junit.Before
import org.junit.Test

class ZypperTest : AbstractJunixCommandVerification() {

	@Before
	fun setup() {
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))
		whenever(execChannel.invertedOut).thenReturn(NullInputStream(0))
	}

	@Test
	fun installNoPackage() {
		expect(IllegalArgumentException::class) { Zypper.installPackage(session) }
		verify(session, never()).createExecChannel(any())
	}

	@Test
	fun uninstallNoPackage() {
		expect(IllegalArgumentException::class) { Zypper.uninstallPackage(session /* no packages listed */) }
		verify(session, never()).createExecChannel(any())
	}

	@Test
	fun installPackage() {
		Zypper.installPackage(session, "foo", "bar", "baz")

		verify(session).createExecChannel("zypper -n install foo bar baz")
	}

	@Test
	fun uninstallPackage() {
		Zypper.uninstallPackage(session, "foo", "bar", "baz")

		verify(session).createExecChannel("zypper -n remove foo bar baz")
	}
}