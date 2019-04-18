package com.github.kerubistan.kerub.utils.junix.packagemanager.apt

import com.github.kerubistan.kerub.utils.junix.AbstractJunixCommandVerification
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class AptTest : AbstractJunixCommandVerification() {

	@Before
	fun setup() {
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))
		whenever(execChannel.invertedOut).thenReturn(NullInputStream(0))
	}

	@Test
	fun installPackage() {
		Apt.installPackage(session, "vim", "pidgin", "openttd")
		verify(session).createExecChannel(eq("apt-get -y install vim pidgin openttd"))
	}

	@Test
	fun uninstallPackage() {
		Apt.uninstallPackage(session, "vim", "pidgin", "openttd")
		verify(session).createExecChannel(eq("apt-get -y remove vim pidgin openttd"))
	}

	@Test
	fun installNoPackage() {
		assertThrows<IllegalArgumentException> { Apt.installPackage(session /* no packages! */) }
		verify(session, never()).createExecChannel(any())
	}

	@Test
	fun uninstallNoPackage() {
		assertThrows<IllegalArgumentException> { Apt.uninstallPackage(session /* no packages! */) }
		verify(session, never()).createExecChannel(any())
	}

}