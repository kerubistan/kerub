package com.github.K0zka.kerub.utils.junix.iscsi.ctld

import com.github.K0zka.kerub.host.appendToFile
import com.github.K0zka.kerub.model.HostCapabilities
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.utils.junix.common.OsCommand
import com.github.K0zka.kerub.utils.storage.iscsiStorageId
import org.apache.sshd.client.session.ClientSession
import java.util.UUID
import com.github.K0zka.kerub.utils.storage.iscsiDefaultUser as iscsiUser

object Ctld : OsCommand {

	private val configFilePath = "/etc/ctl.conf"

	override fun available(hostCapabilities: HostCapabilities?): Boolean
			= hostCapabilities?.os == OperatingSystem.BSD &&
			hostCapabilities.distribution?.name == "FreeBSD"

	internal fun begin(id: UUID) = "#cfg-begin $id"
	internal fun end(id: UUID) = "#cfg-end $id"

	fun share(session: ClientSession, id: UUID, path: String, readOnly: Boolean = false) {
		val config = """
${begin(id)}

     auth-group ag-${id} {
     	#TODO
     	auth-type none
     }

	target ${iscsiStorageId(id)} {
	auth-group ag-${id}
	portal-group $iscsiUser

		lun 0 {
			path $path
			${readonlyOption(readOnly)}
		}
	}
${end(id)}"""
		session.createSftpClient().use {
			sftp ->
			sftp.appendToFile(configFilePath, config)
		}
		session
	}

	private fun readonlyOption(readOnly: Boolean) = if (readOnly) {
		"readonly on"
	} else {
		""
	}

	fun unshare(session: ClientSession, id: UUID) {
		session.createSftpClient().use {
			sftp ->
			val config = sftp.read(configFilePath).reader(Charsets.US_ASCII).use {
				it.readText().removeSurrounding(begin(id), end(id))
			}
			sftp.write(configFilePath).writer(Charsets.US_ASCII).use {
				it.write(config)
			}
		}
	}
}