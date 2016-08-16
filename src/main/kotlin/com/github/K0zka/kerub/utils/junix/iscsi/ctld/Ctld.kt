package com.github.K0zka.kerub.utils.junix.iscsi.ctld

import com.github.K0zka.kerub.utils.junix.common.OsCommand
import org.apache.sshd.client.session.ClientSession
import java.util.UUID

object Ctld : OsCommand {
	fun share(session: ClientSession, id: UUID, path: String, readOnly: Boolean = false) {
		TODO()
	}

	fun unshare(session: ClientSession) {
		TODO()
	}
}