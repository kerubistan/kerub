package com.github.K0zka.kerub.utils.junix.storagemanager.lvm

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.utils.emptyString
import com.github.K0zka.kerub.utils.getLogger
import com.github.K0zka.kerub.utils.toSize
import org.apache.commons.io.input.NullInputStream
import org.apache.commons.io.output.NullOutputStream
import org.apache.sshd.client.session.ClientSession
import java.io.OutputStream
import java.math.BigInteger

object LvmLv : Lvm() {

	val minimalSize = "4 MB".toSize()
	val logger = getLogger(LvmLv::class)

	fun roundUp(size : BigInteger) : BigInteger {
		if(size.mod(minimalSize) == BigInteger.ZERO && size != BigInteger.ZERO) {
			return size
		} else {
			val newSize = (size.div(minimalSize) + BigInteger.ONE) * minimalSize
			logger.info("Rounded up requested size {} to {}", size, newSize)
			return newSize
		}
	}

	class LvmMonitorOutputStream(
			private val callback: (List<LogicalVolume>) -> Unit
	) : OutputStream() {

		private val buff = StringBuilder()
		private var lvStats = listOf<LogicalVolume>()

		override fun write(data: Int) {
			if (data == 10) {
				parseOutput()
				buff.setLength(0)
			} else {
				buff.append(data.toChar())
			}
		}

		private fun parseOutput() {
			val row = buff.toString()
			if (row.trim() == separator) {
				callback(lvStats)
				lvStats = listOf()
			} else {
				lvStats += LvmLv.parseRow(row)
			}
		}
	}

	private fun parseRow(row: String): LogicalVolume {
		val fields = row.trim().split(fieldSeparator)
		require(fields.size == 8, { "This row does not look any good: \n$row\n"})
		return LogicalVolume(
				id = fields[0],
				name = fields[1],
				path = fields[2],
				size = fields[3].toSize(),
				layout = fields[6],
				dataPercent = if (fields[7].isEmpty()) {
					null
				} else {
					fields[7].toDouble()
				},
				minRecovery = optionalInt(fields[4]),
				maxRecovery = optionalInt(fields[5])
		)
	}

	fun checkErrorOutput(err: String): Boolean {
		return err.isNotBlank() && !err.trim().startsWith("WARNING")
	}

	internal fun optionalInt(input: String?): Int? =
			if (input?.isBlank() ?: true) {
				null
			} else {
				input?.toInt()
			}

	fun monitor(session: ClientSession, callback: (List<LogicalVolume>) -> Unit) {
		val channel = session.createExecChannel(
				"""bash -c "while true; do lvm lvs -o $fields $listOptions; echo $separator; sleep 60; done;"  """)

		channel.`in` = NullInputStream(0)
		channel.err = NullOutputStream()
		channel.out = LvmMonitorOutputStream(callback)
		channel.open().verify()
	}

	/**
	 * Check if the LV exists.
	 * Simple but, but with large number of LVs it may proove inefficient since it downloads all the
	 * logical volumes of the volume group.
	 * The problem here is that lvs rather than returning an empty list will raise an error if no LV found.
	 * This of course still expects that the VG does exist.
	 */
	fun exists(
			session: ClientSession,
			volGroupName: String,
			volName: String
	): Boolean = list(session = session, volGroupName = volGroupName).any { it.name == volName }

	fun list(
			session: ClientSession,
			volGroupName: String? = null,
			volName: String? = null
	): List<LogicalVolume> {
		val filter = if (volGroupName == null) {
			""
		} else {
			"$volGroupName${if (volName == null) emptyString else '/' + volName}"
		}
		return session.executeOrDie(
				"lvm lvs -o $fields $listOptions $filter")
				.lines().filterNot { it.isEmpty() }.map {
			row ->
			parseRow(row)
		}
	}

	fun create(session: ClientSession,
			   vgName: String,
			   name: String,
			   size: BigInteger,
			   minRecovery: Int? = null,
			   maxRecovery: Int? = null
	): LogicalVolume {
		fun minRecovery(minRecovery: Int?) = if (minRecovery == null) {
			""
		} else {
			"--minrecoveryrate $minRecovery"
		}

		fun maxRecovery(maxRecovery: Int?) = if (maxRecovery == null) {
			""
		} else {
			"--maxrecoveryrate $maxRecovery"
		}
		session.executeOrDie(
				"lvm lvcreate $vgName -n $name " +
						"-Wn -Zy " +
						"-L ${roundUp(size)}B ${minRecovery(minRecovery)} ${maxRecovery(maxRecovery)}",
				{ checkErrorOutput(it) })
		return list(session, volGroupName = vgName, volName = name).first { it.name == name }
	}

	fun delete(session: ClientSession, volumeName: String) {
		session.executeOrDie("lvm lvremove -f $volumeName")
	}

}