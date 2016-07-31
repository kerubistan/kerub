package com.github.K0zka.kerub.utils.junix.storagemanager.gvinum

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.utils.substringBetween
import com.github.K0zka.kerub.utils.toSize
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger

object GVinum {

	fun listDrives(session: ClientSession) = parseDriveList(gvinum("ld", session))
	fun listSubDisks(session: ClientSession): List<GvinumSubDisk> = parseSubDiskList(gvinum("ls", session))
	fun listVolumes(session: ClientSession): List<GvinumVolume> = parseVolumeList(gvinum("lv", session))
	fun listPlexes(session: ClientSession) = parsePlexesList(gvinum("lp", session))

	private val header = Regex.fromLiteral("\\n+\\s\\S+:$")
	private val driveHeader = Regex.fromLiteral("Drive ")
	private val subdiskHeader = Regex.fromLiteral("Subdisk")
	private val plexHeader = Regex.fromLiteral("Plex ")
	private val volumeHeader = Regex.fromLiteral("Volume ")
	private val up = "up"

	private fun gvinum(command: String, session: ClientSession): String
			= session.executeOrDie("gvinum $command -v")

	internal fun parseDriveList(output: String): List<GvinumDrive> =
			output.substringAfter("drives:\n").split(driveHeader).filter { it.isNotBlank() }.map {
				str ->
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
			output.substringAfter("subdisks:").split(subdiskHeader).filter { it.isNotBlank() }.map {
				str ->
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
			output.substringAfter("plexes:").split(plexHeader).filter { it.isNotBlank() }.map {
				str ->
				GvinumPlex(
						name = str.substringBefore(":").trim(),
						subdisks = str.substringBetween("Subdisks:", "\n").trim().toInt(),
						up = isUp(str),
						volume = str.substringAfter("Part of volume ").trim()
				)
			}

	internal fun parseVolumeList(output: String): List<GvinumVolume>
			= output.substringAfter("volumes:").split(volumeHeader).filter { it.isNotBlank() }.map {
		str ->
		GvinumVolume(
				name = str.substringBefore(":").trim(),
				size = str.substringBetween("Size: ", " (").toSize(),
				up = isUp(str)
		)
	}

	fun createSimpleVolume(session: ClientSession, volName: String, disk : String, size : BigInteger) {
		val config =
				createVolume(config =
				"""
					volume $volName
					  plex org concat
						sd length ${size}b drive $disk
				""", session = session, volName = volName)
	}

	private fun createVolume(config: String, session: ClientSession, volName: String) {
		session.createSftpClient().use {
			sftp ->
			val tempConfigFile = "/tmp/$volName"
			sftp.write(tempConfigFile).writer(charset("ASCII")).use {
				writer ->
				writer.write(config)
			}
			session.executeOrDie("gvinum create $tempConfigFile")
			sftp.remove(tempConfigFile)
		}
	}
}