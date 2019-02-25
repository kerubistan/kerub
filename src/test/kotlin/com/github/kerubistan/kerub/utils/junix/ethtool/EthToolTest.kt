package com.github.kerubistan.kerub.utils.junix.ethtool

import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.utils.junix.AbstractJunixCommandVerification
import com.github.kerubistan.kerub.utils.toSize
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream

class EthToolTest : AbstractJunixCommandVerification() {

	private val testOutput =
			""""Settings for enp2s0:
	Supported ports: [ TP MII ]
	Supported link modes:   10baseT/Half 10baseT/Full
	                        100baseT/Half 100baseT/Full
	                        1000baseT/Half 1000baseT/Full
	Supported pause frame use: No
	Supports auto-negotiation: Yes
	Advertised link modes:  10baseT/Half 10baseT/Full
	                        100baseT/Half 100baseT/Full
	                        1000baseT/Full
	Advertised pause frame use: Symmetric Receive-only
	Advertised auto-negotiation: Yes
	Speed: 10Mb/s
	Duplex: Half
	Port: MII
	PHYAD: 0
	Transceiver: internal
	Auto-negotiation: on
	Supports Wake-on: pumbg
	Wake-on: g
	Current message level: 0x00000033 (51)
			       drv probe ifdown ifup
	Link detected: no
""""


	@Test
	fun testGetDeviceInformation() {
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))
		whenever(execChannel.invertedOut).thenReturn(ByteArrayInputStream(testOutput.toByteArray(charset("ASCII"))))
		val devInfo = EthTool.getDeviceInformation(session, "enp2s0")

		assertFalse(devInfo.link)
		assertTrue(devInfo.wakeOnLan)
		assertEquals("10 MB".toSize(), devInfo.transferRate)
	}

	@Test
	fun available() {
		assertFalse(EthTool.available(null))
		assertFalse(
				EthTool.available(
						testHostCapabilities.copy(
								installedSoftware = listOf()
						)))
		assertTrue(
				EthTool.available(
						testHostCapabilities.copy(
								installedSoftware = listOf(
										SoftwarePackage(
												name = "ethtool",
												version = Version.fromVersionString("1.4.15")))
						)))
	}
}