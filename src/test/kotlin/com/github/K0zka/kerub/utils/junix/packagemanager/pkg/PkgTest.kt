package com.github.K0zka.kerub.utils.junix.packagemanager.pkg

import com.github.K0zka.kerub.expect
import com.github.K0zka.kerub.never
import com.github.K0zka.kerub.on
import com.github.K0zka.kerub.utils.junix.AbstractJunixCommandVerification
import com.github.K0zka.kerub.verify
import org.apache.commons.io.input.NullInputStream
import org.junit.Before
import org.junit.Test

class PkgTest : AbstractJunixCommandVerification() {

	@Before
	fun setup() {
		on(execChannel!!.invertedErr).thenReturn(NullInputStream(0))
		on(execChannel!!.invertedOut).thenReturn(NullInputStream(0))
	}

	@Test
	fun installPackage() {
		Pkg.installPackage(session!!, "foo", "bar", "baz")
		verify(session)!!.createExecChannel("pkg install -y foo bar baz")
	}

	@Test
	fun uninstallPackage() {
		Pkg.uninstallPackage(session!!, "foo", "bar", "baz")
		verify(session)!!.createExecChannel("pkg remove -y foo bar baz")
	}

	@Test
	fun installNoPackage() {
		expect(IllegalArgumentException::class, { Pkg.installPackage(session!!) })
		verify(session, never)!!.createExecChannel("pkg install -y foo bar baz")
	}

	@Test
	fun uninstallNoPackage() {
		expect(IllegalArgumentException::class, { Pkg.uninstallPackage(session!!) })
		verify(session, never)!!.createExecChannel("pkg remove -y foo bar baz")
	}

}