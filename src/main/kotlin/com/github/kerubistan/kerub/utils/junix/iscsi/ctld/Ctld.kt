package com.github.kerubistan.kerub.utils.junix.iscsi.ctld

import com.github.kerubistan.kerub.host.appendToFile
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.storage.iscsiStorageId
import org.apache.sshd.client.session.ClientSession
import java.util.UUID
import com.github.kerubistan.kerub.utils.storage.iscsiDefaultUser as iscsiUser

object Ctld : OsCommand {

	private const val configFilePath = "/etc/ctl.conf"

	override fun available(hostCapabilities: HostCapabilities?): Boolean
			= hostCapabilities?.os == OperatingSystem.BSD &&
			hostCapabilities.distribution?.name == "FreeBSD"

	private fun begin(id: UUID) = "#cfg-begin $id"
	private fun end(id: UUID) = "#cfg-end $id"

	fun share(session: ClientSession, id: UUID, path: String, readOnly: Boolean = false) {
		val config = """
	${begin(id)}

     auth-group ag-$id {
     	#TODO
     	auth-type none
     }

	target ${iscsiStorageId(id)} {
	auth-group ag-$id
	portal-group $iscsiUser

		lun 0 {
			path $path
			${readonlyOption(readOnly)}
		}
s	}
	s${end(id)}""".trimIndent()
		session.createSftpClient().use {
			sftp ->
			sftp.appendToFile(configFilePath, config)
		}
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