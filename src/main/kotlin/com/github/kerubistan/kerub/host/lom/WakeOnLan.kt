package com.github.kerubistan.kerub.host.lom

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.lom.WakeOnLanInfo
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * Uses wake-on-lan magic cookie to wake up the host
 * and the ssh connection to power it off.
 */
class WakeOnLan(private val host: Host) {

	companion object {
		val wolUdpPort = 9
		val magicCookieHeader = 0xFF.toByte()
		fun buildMagicPocket(mac: ByteArray): ByteArray {
			require(mac.size == 6, { "Mac address must be 6 bytes" })
			val bytes = ByteArray(102)
			bytes[0] = magicCookieHeader
			bytes[1] = magicCookieHeader
			bytes[2] = magicCookieHeader
			bytes[3] = magicCookieHeader
			bytes[4] = magicCookieHeader
			bytes[5] = magicCookieHeader

			for (i in 1..16) {
				//TODO: find a nice kotlin fn to do this
				val offset = i * 6
				bytes[offset] = mac[0]
				bytes[offset + 1] = mac[1]
				bytes[offset + 2] = mac[2]
				bytes[offset + 3] = mac[3]
				bytes[offset + 4] = mac[4]
				bytes[offset + 5] = mac[5]
			}
			return bytes
		}
	}

	fun on() {
		val info = host.capabilities?.powerManagment?.first { it is WakeOnLanInfo } as WakeOnLanInfo?
		require(info != null, { "mac address list needed to wake up host" })
		require(info!!.macAddresses.isNotEmpty(), { "non-empty mac address list needed to wake up host" })
		for (mac in info.macAddresses) {
			val bytes = buildMagicPocket(mac)

			DatagramSocket(null).use {
				it.send(DatagramPacket(bytes, bytes.size, InetAddress.getByName("255.255.255.255"), wolUdpPort))
			}
		}
	}

}