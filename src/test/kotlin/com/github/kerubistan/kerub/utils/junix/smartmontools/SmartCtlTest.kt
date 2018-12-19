package com.github.kerubistan.kerub.utils.junix.smartmontools

import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.utils.resourceToString
import com.nhaarman.mockito_kotlin.mock
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SmartCtlTest {

	val session = mock<ClientSession>()

	@Test
	fun available() {
		assertFalse("smartmontools usually not part of the default isntall, so no info - no smartmontools") {
			SmartCtl.available(null)
		}
		assertFalse("smartmontools usually not part of the default isntall, so no info - no smartmontools") {
			SmartCtl.available(testHostCapabilities.copy(installedSoftware = listOf()))
		}
		assertTrue("smartmontools installed") {
			SmartCtl.available(
					testHostCapabilities.copy(
							installedSoftware = listOf(
									SoftwarePackage("smartmontools", version = Version.fromVersionString("0.6.2"))
							)))
		}
	}

	@Test
	fun infoWithSsd() {
		session.mockCommandExecution(
				"smartctl -i .*".toRegex(),
				resourceToString("com/github/kerubistan/kerub/utils/junix/smartmontools/smartctl-info-ssd.txt"))
		val storageDevice = SmartCtl.info(session, "/dev/sda")
		assertEquals("14320DF8BD3F", storageDevice.serialNumber)
		assertEquals(256060514304, storageDevice.userCapacity.toLong())
		assertNull(storageDevice.rotationRate)
	}

	@Test
	fun infoWithHddSata() {
		session.mockCommandExecution(
				"smartctl -i .*".toRegex(),
				resourceToString("com/github/kerubistan/kerub/utils/junix/smartmontools/smartctl-info-hdd-sata.txt"))
		val storageDevice = SmartCtl.info(session, "/dev/sda")
		assertEquals("WD-WXN1E9485PZA", storageDevice.serialNumber)
		assertEquals(1000204886016, storageDevice.userCapacity.toLong())
		assertEquals(5400, storageDevice.rotationRate)
	}

	@Test
	fun infoWithQemuSata() {
		session.mockCommandExecution(
				"smartctl -i .*".toRegex(),
				resourceToString("com/github/kerubistan/kerub/utils/junix/smartmontools/smartctl-info-qemu-sata.txt"))
		val storageDevice = SmartCtl.info(session, "/dev/sda")
		assertEquals("QM00005", storageDevice.serialNumber)
		assertEquals(8589934592, storageDevice.userCapacity.toLong())
		assertNull(storageDevice.rotationRate)
	}

	@Test
	fun healthCheck() {
		session.mockCommandExecution(
				"smartctl -H .*".toRegex(),
				resourceToString("com/github/kerubistan/kerub/utils/junix/smartmontools/smartctl-healthcheck-ssd.txt"))
		assertTrue(SmartCtl.healthCheck(session, "/dev/sda"))
	}

	@Test
	fun healthCheckHdd() {
		session.mockCommandExecution(
				"smartctl -H .*".toRegex(),
				resourceToString("com/github/kerubistan/kerub/utils/junix/smartmontools/smartctl-healthcheck-hdd.txt"))
		assertTrue(SmartCtl.healthCheck(session, "/dev/sda"))
	}

	@Test
	fun healthCheckQemu() {
		session.mockCommandExecution(
				"smartctl -H .*".toRegex(),
				resourceToString("com/github/kerubistan/kerub/utils/junix/smartmontools/smartctl-healthcheck-qemu.txt"))
		assertTrue(SmartCtl.healthCheck(session, "/dev/sda"))
	}

}