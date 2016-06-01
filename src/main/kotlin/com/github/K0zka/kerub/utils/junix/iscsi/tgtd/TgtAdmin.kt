package com.github.K0zka.kerub.utils.junix.iscsi.tgtd

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.host.use
import com.github.K0zka.kerub.utils.junix.common.OsCommand
import com.github.K0zka.kerub.utils.storage.iscsiStorageId
import org.apache.sshd.client.session.ClientSession
import java.util.UUID

object TgtAdmin : OsCommand {

	fun shareBlockDevice(session: ClientSession, id: UUID, path: String, readOnly: Boolean = false) {
		session.createSftpClient().use {
			ftp ->
			ftp.write(configurationPath(id)).use {
				out ->
				out.write("""
<target ${iscsiStorageId(id)}>
    direct-store ${path}
    readonly ${if(readOnly) "1" else "0"}
</target>
			""".toByteArray(charset("ASCII")))
			}
		}
		session.executeOrDie("tgt-admin -e")
	}

	fun unshareBlockDevice(session: ClientSession, id: UUID) {
		//first remove the configuration, if anyone calls tgt-admin -e, this will be automatically removed
		session.createSftpClient().use {
			ftp ->
			ftp.remove(configurationPath(id))
		}
		//remove the configuration from the tgt admin
		session.executeOrDie("tgt-admin --delete ${iscsiStorageId(id)}")
	}

	//TODO: this path may change by OS distribution
	private fun configurationPath(id: UUID) =
			"/etc/tgt/conf.d/${id}.conf"

}