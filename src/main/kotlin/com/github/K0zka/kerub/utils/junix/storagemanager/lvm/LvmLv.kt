package com.github.K0zka.kerub.utils.junix.storagemanager.lvm

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.utils.toSize
import org.apache.sshd.ClientSession
import java.math.BigInteger

object LvmLv {

	internal fun optionalInt(input: String?): Int? =
			if (input?.isBlank() ?: true) {
				null
			} else {
				input?.toInt()
			}

	fun list(session: ClientSession): List<LogicalVolume> =
			session.executeOrDie(
					"lvs " +
							"-o lv_uuid,lv_name,lv_path,lv_size,raid_min_recovery_rate,raid_max_recovery_rate ${listOptions}")
					.split("\n").map {
				row ->
				val fields = row.trim().split(",")
				LogicalVolume(
						id = fields[0],
						name = fields[1],
						path = fields[2],
						size = fields[3].toSize(),
						minRecovery = optionalInt(fields[4]),
						maxRecovery = optionalInt(fields[5])
				)
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
				"""lvcreate -n $name -L $size B ${minRecovery(minRecovery)} ${maxRecovery(maxRecovery)}""")
		return list(session).first { it.name == name }
	}

	fun delete(session: ClientSession, volumeName: String) {
		session.executeOrDie("lvremove -f $volumeName")
	}

}