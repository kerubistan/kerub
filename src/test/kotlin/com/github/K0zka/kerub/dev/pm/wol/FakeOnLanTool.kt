package com.github.K0zka.kerub.dev.pm.wol

import com.github.K0zka.kerub.utils.macToString
import org.libvirt.Connect
import org.libvirt.DomainInfo
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class FakeOnLanTool(val connect: Connect) {

	fun run() {
		val buffer = ByteArray(1024)
		val socket = DatagramSocket(9, InetAddress.getByName("0.0.0.0"))
		while (true) {
			socket.receive(DatagramPacket(buffer, buffer.size))
			val isMagicPacket =
					(0..5).all { buffer[it] == (0xFF.toByte()) }
							&& (1..15).all { buffer[it * 6] == buffer[6 + (it * 6)] }
			if (isMagicPacket) {
				val mac = buffer.copyOfRange(6, 12)
				val formattedMac = macToString(mac).toLowerCase()
				println("magic packet for $formattedMac")
				for (domName in connect.listDefinedDomains()) {
					val domain = connect.domainLookupByName(domName)
					if (domain.getXMLDesc(0).toLowerCase().contains(formattedMac)
							&& domain.info.state == DomainInfo.DomainState.VIR_DOMAIN_SHUTOFF) {
						println(" starting $domName")
						domain.create()
					}
				}
			} else {
				println("no magic: ignored")
			}

		}
	}

	companion object {
		@JvmStatic fun main(args: Array<String>) {
			val hypervisor = args.getOrElse(0) { "qemu:///system" }
			println("connecting $hypervisor")
			FakeOnLanTool(Connect(hypervisor)).run()
			//return 0
		}
	}
}