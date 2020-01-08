package com.github.kerubistan.kerub.utils.junix.storagemanager.gvinum

import com.github.kerubistan.kerub.host.bashMonitor
import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.utils.KB
import com.github.kerubistan.kerub.utils.getLogger
import com.github.kerubistan.kerub.utils.toSize
import io.github.kerubistan.kroki.strings.substringBetween
import org.apache.sshd.client.session.ClientSession
import java.io.OutputStream
import java.math.BigInteger

object GVinum {

	private val logger = getLogger(GVinum::class)

	fun listDrives(session: ClientSession) = parseDriveList(gvinum("ld", session))
	fun listSubDisks(session: ClientSession): List<GvinumSubDisk> = parseSubDiskList(gvinum("ls", session))
	fun listVolumes(session: ClientSession): List<GvinumVolume> = parseVolumeList(gvinum("lv", session))
	fun listPlexes(session: ClientSession) = parsePlexesList(gvinum("lp", session))

	private const val separator = "--sep"

	private val header = Regex.fromLiteral("\\n+\\s\\S+:$")
	private val driveHeader = Regex.fromLiteral("Drive ")
	private val subdiskHeader = Regex.fromLiteral("Subdisk")
	private val plexHeader = Regex.fromLiteral("Plex ")
	private val volumeHeader = Regex.fromLiteral("Volume ")
	private const val up = "up"

	private fun gvinum(command: String, session: ClientSession): String = session.executeOrDie("gvinum $command -v")

	internal fun parseDriveList(output: String): List<GvinumDrive> =
			output.substringAfter("drives:\n")
					.substringAfter("1 drive:").split(driveHeader).filter { it.isNotBlank() }.map { str ->
						GvinumDrive(
								name = str.substringBefore(":"),
								device = str.substringBetween("Device ", "\n"),
								available = str.substringBetween("Available:", " (").toSize(),
								size = str.substringBetween("Size:", " (").toSize(),
								up = isUp(str),
								used = str.substringBetween("Used:", " (").toSize()
						)
					}

	private fun isUp(str: String) = str.substringBetween("State: ", "\n") == up

	internal fun parseSubDiskList(output: String): List<GvinumSubDisk> =
			output.substringAfter("subdisks:").split(subdiskHeader).filter { it.isNotBlank() }.map { str ->
				GvinumSubDisk(
						name = str.substringBefore(":").trim(),
						drive = str.substringBetween("Drive ", " ("),
						up = isUp(str),
						size = str.substringBetween("Size:", " (").toSize(),
						offset = BigInteger(str.substringBetween(") at offset ", " (")),
						plex = str.substringBetween("Plex ", " at offset ")
				)
			}

	internal fun parsePlexesList(output: String): List<GvinumPlex> =
			output.substringAfter("plexes:").split(plexHeader).filter { it.isNotBlank() }.map { str ->
				GvinumPlex(
						name = str.substringBefore(":").trim(),
						subdisks = str.substringBetween("Subdisks:", "\n").trim().toInt(),
						up = isUp(str),
						volume = str.substringAfter("Part of volume ").trim()
				)
			}

	internal fun parseVolumeList(output: String): List<GvinumVolume> =
			output.substringAfter("volumes:").split(volumeHeader).filter { it.isNotBlank() }.map { str ->
				GvinumVolume(
						name = str.substringBefore(":").trim(),
						size = str.substringBetween("Size: ", " (").toSize(),
						up = isUp(str)
				)
			}

	private fun roundUpKb(size: BigInteger): BigInteger {
		val kbs = size / KB.toBigInteger()
		return (kbs + BigInteger.ONE)
	}

	fun createSimpleVolume(session: ClientSession, volName: String, disk: String, size: BigInteger) {
		createVolume(config =
		"""
		volume $volName
			  plex org concat
				sd length ${roundUpKb(size)}kb drive $disk
		""", session = session, volName = volName)
	}

	fun createConcatenatedVolume(session: ClientSession, volName: String, disks: Map<String, BigInteger>) {
		createVolume(config =
		buildString(128 + (disks.size * 64)) {
			append(
					"""
			volume $volName
			  plex org concat
					""")
			disks.forEach { disk ->
				append("sd length ").append(roundUpKb(disk.value)).append(" drive ").append(disk.key).append("\n")
			}
		}, session = session, volName = volName)
	}

	private fun createVolume(config: String, session: ClientSession, volName: String) {
		logger.debug("creating gvinum volume $volName with config $config")
		session.createSftpClient().use { sftp ->
			val tempConfigFile = "/tmp/$volName"
			sftp.write(tempConfigFile).writer(charset("ASCII")).use { writer ->
				writer.write(config)
			}
			session.executeOrDie("gvinum create $tempConfigFile")
			sftp.remove(tempConfigFile)
		}
	}

	fun removeVolume(session: ClientSession, volName: String) {
		session.executeOrDie("gvinum rm $volName")
	}

	class GvinumDriveMonitorOutputStream(private val callback: (List<GvinumDrive>) -> Unit) : OutputStream() {
		private val buffer = StringBuilder(128)
		override fun write(input: Int) {
			buffer.append(input.toChar())
			if (buffer.endsWith(separator)) {
				callback(parseDriveList(buffer.substring(0, buffer.length - separator.length)))
				buffer.clear()
			}
		}
	}

	fun monitorDrives(session: ClientSession, callback: (List<GvinumDrive>) -> Unit) {
		session.bashMonitor("gvinum ld -v", 60, separator, GvinumDriveMonitorOutputStream(callback))
	}

}