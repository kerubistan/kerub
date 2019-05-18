package com.github.kerubistan.kerub.utils.junix.lshw

import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.utils.browse
import com.github.kerubistan.kerub.utils.junix.AbstractJunixCommandVerification
import com.nhaarman.mockito_kotlin.whenever
import io.github.kerubistan.kroki.io.resource
import org.apache.commons.io.input.NullInputStream
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LshwTest : AbstractJunixCommandVerification() {

	companion object {
		private val filter: (HardwareItem) -> Boolean = {
			it.children?.requireNoNulls()
			true
		}
	}

	@Test
	fun list() {
		whenever(execChannel.invertedErr).then { NullInputStream(0) }
		whenever(execChannel.invertedOut).then { resource("com/github/kerubistan/kerub/utils/junix/lshw/lshw.json") }
		val system = Lshw.list(session)
		system.browse(HardwareItem::children, filter)
	}

	@Test
	fun listWithSsd() {
		whenever(execChannel.invertedErr).then { NullInputStream(0) }
		whenever(execChannel.invertedOut).then { resource("com/github/kerubistan/kerub/utils/junix/lshw/lshw-ssd.json") }
		val system = Lshw.list(session)
		system.browse(HardwareItem::children, filter)
	}

	@Test
	fun listWithVm() {
		whenever(execChannel.invertedErr).then { NullInputStream(0) }
		whenever(execChannel.invertedOut).then { resource("com/github/kerubistan/kerub/utils/junix/lshw/vm-with-bond.json") }
		val system = Lshw.list(session)
		system.browse(HardwareItem::children, filter)
	}

	@Test
	fun listWithEspressoBin() {
		whenever(execChannel.invertedErr).then { NullInputStream(0) }
		whenever(execChannel.invertedOut).then {
			resource("com/github/kerubistan/kerub/utils/junix/lshw/lshw-espressobin.json")
		}
		val system = Lshw.list(session)
		system.browse(HardwareItem::children, filter)
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