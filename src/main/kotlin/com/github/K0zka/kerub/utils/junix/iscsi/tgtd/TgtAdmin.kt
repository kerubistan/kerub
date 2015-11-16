package com.github.K0zka.kerub.utils.junix.iscsi.tgtd

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.host.use
import org.apache.sshd.ClientSession
import java.util.UUID

object TgtAdmin {

	fun shareBlockDevice(session: ClientSession, id: UUID, path: String) {
		session.createSftpClient().use {
			ftp ->
			ftp.write(configurationPath(id)).use {
				out ->
				out.write("""
<target ${volumePrefix}.${id}>
    direct-store ${path}
</target>
			""".toByteArray("ASCII"))
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
		session.executeOrDie("tgt-admin --delete ${volumePrefix}.${id}")
	}

	//TODO: this path may change by OS distribution
	private fun configurationPath(id: UUID) =
			"/etc/tgt/conf.d/${id}.conf"

}