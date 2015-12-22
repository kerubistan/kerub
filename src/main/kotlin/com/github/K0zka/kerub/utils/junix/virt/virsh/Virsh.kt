package com.github.K0zka.kerub.utils.junix.virt.virsh

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.host.use
import com.github.K0zka.kerub.utils.getLogger
import com.github.K0zka.kerub.utils.rows
import com.github.K0zka.kerub.utils.silent
import org.apache.sshd.ClientSession
import java.util.UUID

object Virsh {

	val logger = getLogger(Virsh::class)

	fun create(session: ClientSession, id: UUID, domainDef: String) {
		val domainDefFile = "/tmp/${id}.xml"
		logger.info("creating domain: \n {}", domainDef)
		session.createSftpClient().use {
			sftp ->
			try {
				sftp.write(domainDefFile).use {
					file ->
					file.write(domainDef.toByteArray("UTF-8"))
				}
				session.executeOrDie("virsh create $domainDefFile")
			} finally {
				silent {sftp.remove(domainDefFile)}
			}
		}
	}

	fun destroy(session: ClientSession, id: UUID) {
		session.executeOrDie("virsh destroy $id  --graceful")
	}

	fun list(session: ClientSession): List<UUID>
			= session.executeOrDie("virsh list --uuid").rows().map { UUID.fromString(it) }

	fun suspend(session: ClientSession, id: UUID) {
		session.executeOrDie("virsh suspend $id")
	}

	fun resume(session: ClientSession, id: UUID) {
		session.executeOrDie("virsh resume $id")
	}

	fun domStat(session: ClientSession) {

	}
}