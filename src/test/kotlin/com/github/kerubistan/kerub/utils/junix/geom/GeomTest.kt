package com.github.kerubistan.kerub.utils.junix.geom

import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.utils.junix.common.FreeBSD
import com.github.kerubistan.kerub.utils.resourceToString
import com.nhaarman.mockito_kotlin.mock
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GeomTest {

	@Test
	fun available() {
		assertFalse("unknown system - no geom") {
			Geom.available(null)
		}
		assertTrue("FreeBSD - yes geom") {
			Geom.available(
					testHostCapabilities.copy(
							os = OperatingSystem.BSD,
							distribution = SoftwarePackage(name = FreeBSD, version = Version.fromVersionString("12.0"))
					))
		}
	}

	@Test
	fun list() {
		val session = mock<ClientSession>()
		session.mockCommandExecution(
				"geom disk list.*".toRegex(),
				resourceToString("com/github/kerubistan/kerub/utils/junix/geom/geomdisks.txt"))
		val storageDevices = Geom.list(session)
		assertEquals(4, storageDevices.size)
		assertEquals("vtbd0", storageDevices.first().name)
		assertEquals(21474836480, storageDevices.first().mediaSize.toLong())
	}
}