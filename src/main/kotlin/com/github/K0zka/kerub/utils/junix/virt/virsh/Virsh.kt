package com.github.K0zka.kerub.utils.junix.virt.virsh

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.model.display.RemoteConsoleProtocol
import com.github.K0zka.kerub.utils.base64
import com.github.K0zka.kerub.utils.getLogger
import com.github.K0zka.kerub.utils.junix.common.OsCommand
import com.github.K0zka.kerub.utils.silent
import com.github.K0zka.kerub.utils.substringBetween
import com.github.K0zka.kerub.utils.toBigInteger
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.session.ClientSession
import java.io.OutputStream
import java.io.StringReader
import java.util.Properties
import java.util.UUID
import javax.xml.bind.JAXBContext

object Virsh : OsCommand {

	val logger = getLogger(Virsh::class)
	val utf8 = charset("UTF-8")

	fun setSecret(session: ClientSession, id : UUID, type : SecretType, value : String) {
		val secretDefFile = "/tmp/$id-secret.xml"
		val secretDef = """<?xml version="1.0" encoding="UTF-8"?>
<secret ephemeral='no' private='yes'>
	<uuid>$id</uuid>
	<usage type='$type'>
		<target>$id</target>
	</usage>
</secret>"""
		session.createSftpClient().use {
			sftp ->
			try {
				sftp.write(secretDefFile).use {
					file ->
					file.write(secretDef.toByteArray(utf8))
				}
				session.executeOrDie("virsh secret-define $secretDefFile")
				session.executeOrDie("virsh secret-set-value $id ${value.base64().toString(Charsets.US_ASCII)}")
			} finally {
				sftp.remove(secretDefFile)
			}
		}

	}

	fun clearSecret(session: ClientSession, id : UUID) {
		session.executeOrDie("virsh secret-undefine $id")
	}

	fun create(session: ClientSession, id: UUID, domainDef: String) {
		val domainDefFile = "/tmp/$id.xml"
		logger.info("creating domain: \n {}", domainDef)
		session.createSftpClient().use {
			sftp ->
			try {
				sftp.write(domainDefFile).use {
					file ->
					file.write(domainDef.toByteArray(utf8))
				}
				session.executeOrDie("virsh create $domainDefFile")
			} finally {
				silent { sftp.remove(domainDefFile) }
			}
		}
	}

	fun migrate(
			session: ClientSession,
			id: UUID,
			targetAddress: String,
			live: Boolean = true,
			compressed: Boolean = true) {
		session.executeOrDie(
				"virsh migrate $id qemu+ssh://$targetAddress/system " +
						"${if (live) "--live" else ""} ${if (compressed) " --compressed" else ""}" +
						" --p2p --tunnelled"
		)
	}

	fun destroy(session: ClientSession, id: UUID) {
		session.executeOrDie("virsh destroy $id  --graceful")
	}

	fun list(session: ClientSession): List<UUID>
			= session.executeOrDie("virsh list --uuid").lines().map { UUID.fromString(it) }

	fun suspend(session: ClientSession, id: UUID) {
		session.executeOrDie("virsh suspend $id")
	}

	fun resume(session: ClientSession, id: UUID) {
		session.executeOrDie("virsh resume $id")
	}

	private val domstatsCommand = "virsh domstats --raw"

	fun domStat(session: ClientSession): List<DomainStat> {
		return parseDomStats(session.executeOrDie(domstatsCommand))
	}

	internal fun parseDomStats(output: String) = output.split("\n\n").filter { it.isNotBlank() }.map { toDomStat(it.trim()) }

	internal class DomStatsOutputHandler(private val handler: (List<DomainStat>) -> Unit) : OutputStream() {

		private val buff = StringBuilder()

		override fun write(data: Int) {
			buff.append(data.toChar())
			if (buff.length > 2 && buff.endsWith("\n\n\n")) {
				handler(parseDomStats(buff.toString()))
				buff.setLength(0)
			}
		}
	}

	fun domStat(session: ClientSession, callback: (List<DomainStat>) -> Unit) {
		val channel = session.createExecChannel("""bash -c "while true; do $domstatsCommand; sleep 1; done" """)
		channel.out = DomStatsOutputHandler(callback)
		channel.`in` = NullInputStream(0)
		channel.open()
	}


	internal fun toDomStat(virshDomStat: String): DomainStat {
		val header = virshDomStat.substringBefore('\n')
		val propertiesSource = virshDomStat.substringAfter('\n')
		val props = StringReader(propertiesSource).use {
			val tmp = Properties()
			tmp.load(it)
			tmp
		}
		val vcpuMax = requireNotNull(props.getProperty("vcpu.maximum")).toInt()
		val netCount = props.getProperty("net.count")?.toInt() ?: 0
		return DomainStat(
				name = header.substringBetween("Domain: '", "'"),
				balloonMax = props.getProperty("balloon.maximum")?.toBigInteger(),
				balloonSize = props.getProperty("balloon.current")?.toBigInteger(),
				vcpuMax = vcpuMax,
				netStats = (0..netCount - 1).map {
					netId ->
					toNetStat(
							props.filter { it.key.toString().startsWith("net.$netId.") }
									.map { it.key.toString() to it.value.toString() }
									.toMap()
							, netId
					)
				},
				cpuStats = (0..vcpuMax - 1).map {
					vcpuid ->
					toCpuStat(props
							.filter { it.key.toString().startsWith("vcpu.$vcpuid.") }
							.map { it.key.toString() to it.value.toString() }
							.toMap(), vcpuid)
				}
		)
	}

	private fun toNetStat(props: Map<String, String>, netId: Int) = NetStat(
			name = requireNotNull(props["net.$netId.name"]),
			received = toNetTrafficStat(props, netId, "rx"),
			sent = toNetTrafficStat(props, netId, "tx")
	)

	fun netToLong(props: Map<String, String>, netId: Int, type: String, prop: String) = requireNotNull(props["net.$netId.$type.$prop"]).toLong()

	private fun toNetTrafficStat(props: Map<String, String>, netId: Int, type: String): NetTrafficStat = NetTrafficStat(
			bytes = netToLong(props, netId, type, "bytes"),
			drop = netToLong(props, netId, type, "drop"),
			errors = netToLong(props, netId, type, "errs"),
			packets = netToLong(props, netId, type, "pkts")
	)

	fun getDisplay(session: ClientSession, vmId: UUID): Pair<RemoteConsoleProtocol, Int> {
		val display = session.executeOrDie("virsh domdisplay $vmId")
		val protocol = RemoteConsoleProtocol.valueOf(display.substringBefore("://").toLowerCase())
		val port = display.substringAfterLast(":").trim().toInt()
		return protocol to port
	}

	internal fun toCpuStat(props: Map<String, String>, id: Int): VcpuStat =
			VcpuStat(
					state = VcpuState.running,
					time = props["vcpu.$id.time"]?.toLong()
			)

	fun capabilities(session: ClientSession) : LibvirtCapabilities {
		val capabilities = session.executeOrDie("virsh capabilities")
		val jaxbContext = JAXBContext.newInstance(LibvirtXmlArch::class.java, LibvirtXmlCapabilities::class.java, LibvirtXmlGuest::class.java)
		return StringReader(capabilities).use {
			val xmlCapabilities = jaxbContext.createUnmarshaller().unmarshal(it) as LibvirtXmlCapabilities
			LibvirtCapabilities(xmlCapabilities.guests.map {
				xmlGuest ->
				val arch = requireNotNull(xmlGuest.arch)
				LibvirtGuest(
						osType = requireNotNull(xmlGuest.osType),
						arch = LibvirtArch(name = arch.name, emulator = arch.emulator, wordsize = arch.wordsize)
				)
			})
		}
	}

}

