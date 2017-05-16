package com.github.K0zka.kerub.utils.ipmi

import com.github.K0zka.kerub.host.toBase64
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketTimeoutException

open class IpmiClient {

	fun sendPing(address: String, timeOutMs: Int = 1000, onPong: () -> Unit, onTimeout: () -> Unit = {}) {
		val inetAddress = InetAddress.getByName(address)
		try {
			DatagramSocket().use {
				socket ->
				socket.connect(inetAddress, rmcpPort)
				val nr = 0x42.toByte()
				socket.soTimeout = timeOutMs
				socket.send(DatagramPacket(makeRmcpHeader(rmcpClassAsf.toByte()) + makeAsfPingMessage(nr), 12))
				val response = ByteArray(24)
				socket.receive(DatagramPacket(response, 24))
				println(response.toBase64())
				onPong()
			}
		} catch (st: SocketTimeoutException) {
			onTimeout()
		}

		//TODO()
	}

	private fun makeAsfPingMessage(nr: Byte): ByteArray =
			ByteArray(8) {
				idx ->
				when (idx) {
					0 -> 4
					1 -> 5
					2 -> 4
					3 -> 2 // ASF IANA
					4 -> 0x80.toByte() // ping
					5 -> nr
					else -> 0
				}
			}

	internal fun makeRmcpHeader(type: Byte): ByteArray =
			ByteArray(4) {
				idx ->
				when (idx) {
					0 -> rmpCpVersion1_0.toByte()
					2 -> 0xFF.toByte()
					3 -> type
					else -> 0
				}
			}

	fun openSession(address: String, onSession: (IpmiSession) -> Unit) {
		TODO()
	}
}
