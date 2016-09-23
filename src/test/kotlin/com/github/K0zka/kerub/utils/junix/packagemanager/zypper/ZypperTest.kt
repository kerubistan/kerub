package com.github.K0zka.kerub.utils.junix.packagemanager.zypper

import com.github.K0zka.kerub.anyString
import com.github.K0zka.kerub.expect
import com.github.K0zka.kerub.never
import com.github.K0zka.kerub.utils.junix.AbstractJunixCommandVerification
import com.github.K0zka.kerub.verify
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
		expect(IllegalArgumentException::class, { Zypper.installPackage(session) })
		verify(session, never)!!.createExecChannel(anyString())
	}

	@Test
	fun uninstallNoPackage() {
		expect(IllegalArgumentException::class, { Zypper.uninstallPackage(session /* no packages listed */) })
		verify(session, never)!!.createExecChannel(anyString())
	}

	@Test
	fun installPackage() {
		Zypper.installPackage(session, "foo", "bar", "baz")

		verify(session)!!.createExecChannel("zypper -n install foo bar baz")
	}

	@Test
	fun uninstallPackage() {
		Zypper.uninstallPackage(session, "foo", "bar", "baz")

		verify(session)!!.createExecChannel("zypper -n remove foo bar baz")
	}
}