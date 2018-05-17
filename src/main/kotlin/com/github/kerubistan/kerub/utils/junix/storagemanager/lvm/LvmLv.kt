package com.github.kerubistan.kerub.utils.junix.storagemanager.lvm

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.utils.emptyString
import com.github.kerubistan.kerub.utils.getLogger
import com.github.kerubistan.kerub.utils.toSize
import org.apache.commons.io.input.NullInputStream
import org.apache.commons.io.output.NullOutputStream
import org.apache.sshd.client.session.ClientSession
import java.io.OutputStream
import java.math.BigInteger

object LvmLv : Lvm() {

	private val minimalSize = "4 MB".toSize()
	private val logger = getLogger(LvmLv::class)

	private fun checkErrorOutput(err: String): Boolean = err.isNotBlank() && !err.trim().startsWith("WARNING")

	fun roundUp(size: BigInteger, minimum: BigInteger = minimalSize): BigInteger =
			if (size.mod(minimum) == BigInteger.ZERO && size != BigInteger.ZERO) {
				size
		} else {
				val newSize = (size.div(minimum) + BigInteger.ONE) * minimum
			logger.info("Rounded up requested size {} to {}", size, newSize)
				newSize
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
		require(fields.size == 8, { "This row does not look any good: \n$row\n" })
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

	private fun optionalInt(input: String?): Int? =
			if (input?.isBlank() != false) {
				null
			} else {
				input.toInt()
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
				.lines().filterNot { it.isEmpty() }.map { row ->
			parseRow(row)
		}
	}

	val cacheMetaRatio = 1000
	val cacheMetaMinSize = "8 MB".toSize()

	private fun String.cache() = this.plus("_cache")
	private fun String.cacheMeta() = this.plus("_cachemeta")

	fun createCache(session: ClientSession, vgName: String, cacheVg: String, name: String, cacheSize: BigInteger,
					originSize: BigInteger) {
		session.executeOrDie("""lvm lvcreate -n ${name.cache()} -L ${roundUp(cacheSize)}B vg $cacheVg
			&& lvm lvcreate -n ${name.cacheMeta()} -L ${roundUp(originSize, minimum = cacheMetaMinSize)}B vg $cacheVg
			&& lvm lvconvert --type cache-pool --poolmetadata $cacheVg/${name.cacheMeta()} $cacheVg/${name.cache()}
			&& lvm lvconvert --type cache --cachepool $cacheVg/${name.cache()} $vgName/$name
		""".trimIndent())
	}

	fun removeCache(session: ClientSession, vgName: String, cacheVg: String, name: String) {
		session.executeOrDie("lvm lvconvert --uncache $cacheVg/${name.cache()}")
	}

	fun createPool(session: ClientSession, vgName: String, name: String, size: BigInteger, metaSize: BigInteger) =
			session.executeOrDie(
					("lvm lvcreate $vgName -n $name -L ${roundUp(size)}B -Wn -Zy " +
							"&& lvm lvcreate $vgName -n ${name}_meta -L ${roundUp(metaSize)}B -Wn -Zy" +
							"&& lvm lvconvert --type thin-pool ${name}_meta $name").trimIndent()
					, ::checkErrorOutput)

	fun extend(session: ClientSession, vgName: String,
			   lvName: String,
			   addSize: BigInteger) {
		assert(addSize > BigInteger.ZERO) {
			"lvextend $vgName/$lvName needs a size bigger than zero. given: $addSize"
		}
		session.executeOrDie("lvm lvextend $vgName/$lvName -L +${roundUp(addSize)}B")
	}

	fun remove(session: ClientSession,
			   vgName: String,
			   name: String) {
		remove(session, "/dev/$vgName/$name")
	}

	fun remove(session: ClientSession,
			   path: String) {
		session.executeOrDie("lvm lvremove -f $path")
	}

	fun create(session: ClientSession,
			   vgName: String,
			   name: String,
			   size: BigInteger,
			   minRecovery: Int? = null,
			   maxRecovery: Int? = null,
			   poolName: String? = null
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

		val sizeParam = if (poolName == null) {
			"-L"
		} else {
			"-V"
		}
		session.executeOrDie(
				"lvm lvcreate $vgName -n $name " +
						"-Wn -Zy " +
						"$sizeParam ${roundUp(size)}B ${minRecovery(minRecovery)} ${maxRecovery(maxRecovery)}" +
						if (poolName != null) {
							"--thinpool $poolName"
						} else {
							""
						},
				::checkErrorOutput)
		return list(session, volGroupName = vgName, volName = name).first { it.name == name }
	}

	fun delete(session: ClientSession, volumeName: String) {
		session.executeOrDie("lvm lvremove -f $volumeName")
	}

}