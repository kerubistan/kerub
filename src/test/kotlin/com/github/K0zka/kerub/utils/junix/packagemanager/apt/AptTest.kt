package com.github.K0zka.kerub.utils.junix.packagemanager.apt

import com.github.K0zka.kerub.anyString
import com.github.K0zka.kerub.eq
import com.github.K0zka.kerub.expect
import com.github.K0zka.kerub.never
import com.github.K0zka.kerub.on
import com.github.K0zka.kerub.utils.junix.AbstractJunixCommandVerification
import com.github.K0zka.kerub.verify
import org.apache.commons.io.input.NullInputStream
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AptTest : AbstractJunixCommandVerification() {

	@Before
	fun setup() {
		on(execChannel!!.invertedErr).thenReturn(NullInputStream(0))
		on(execChannel!!.invertedOut).thenReturn(NullInputStream(0))
	}

	@Test
	fun installPackage() {
		Apt.installPackage(session!!, "vim", "pidgin", "openttd")
		verify(session)!!.createExecChannel(eq("apt-get -y install vim pidgin openttd"))
	}

	@Test
	fun uninstallPackage() {
		Apt.uninstallPackage(session!!, "vim", "pidgin", "openttd")
		verify(session)!!.createExecChannel(eq("apt-get -y remove vim pidgin openttd"))
	}

	@Test
	fun installNoPackage() {
		expect(IllegalArgumentException::class, { Apt.installPackage(session!! /* no packages! */) })
		verify(session, never)!!.createExecChannel(anyString())
	}

	@Test
	fun uninstallNoPackage() {
		expect(IllegalArgumentException::class, { Apt.uninstallPackage(session!! /* no packages! */) })
		verify(session, never)!!.createExecChannel(anyString())
	}

}