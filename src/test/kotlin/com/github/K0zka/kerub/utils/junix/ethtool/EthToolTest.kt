package com.github.K0zka.kerub.utils.junix.ethtool

import com.github.K0zka.kerub.on
import com.github.K0zka.kerub.utils.junix.AbstractJunixCommandVerification
import com.github.K0zka.kerub.utils.toSize
import org.apache.commons.io.input.NullInputStream
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import java.io.ByteArrayInputStream

@RunWith(MockitoJUnitRunner::class)
class EthToolTest : AbstractJunixCommandVerification() {

    val testOutput =
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
        on(execChannel!!.invertedErr).thenReturn(NullInputStream(0))
        on(execChannel!!.invertedOut).thenReturn(ByteArrayInputStream(testOutput.toByteArray("ASCII")))
        val devInfo = EthTool.getDeviceInformation(session!!, "enp2s0")

        assertFalse(devInfo.link)
        assertTrue(devInfo.wakeOnLan)
        assertEquals("10 MB".toSize(), devInfo.transferRate)
    }
}