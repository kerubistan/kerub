package com.github.K0zka.kerub.host.lom

import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.host.PowerManager
import com.github.K0zka.kerub.host.execute
import com.github.K0zka.kerub.model.Host
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * Uses wake-on-lan magic cookie to wake up the host
 * and the ssh connection to power it off.
 */
public class WakeOnLan(
		private val host: Host,
		private val executor: HostCommandExecutor,
		private val hostManager: HostManager) : PowerManager {

	companion object {
		val wolUdpPort = 9
		val magicCookieHeader = 0xFF.toByte()
		fun buildMagicPocket(mac: ByteArray): ByteArray {
			require(mac.size == 6, {"Mac address must be 6 bytes"})
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

	override fun on() {
		require(host.capabilities?.macAddresses != null, {"mac address list needed to wake up host"})
		require(host.capabilities?.macAddresses!!.isNotEmpty(), {"non-empty mac address list needed to wake up host"})
		for (mac in host.capabilities?.macAddresses!!) {
			val bytes = buildMagicPocket(mac)

			DatagramSocket(null).use {
				it.send(DatagramPacket(bytes, bytes.size, InetAddress.getByName("255.255.255.255"), wolUdpPort))
			}
		}
	}


	override fun off() {
		require(host.dedicated, {"Can not power off a non-dedicated host"})
		executor.execute(host, {
			it.execute("poweroff")
		})
		hostManager.disconnectHost(host)
	}

}