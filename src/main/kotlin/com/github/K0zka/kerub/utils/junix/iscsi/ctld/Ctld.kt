package com.github.K0zka.kerub.utils.junix.iscsi.ctld

import com.github.K0zka.kerub.utils.junix.common.OsCommand
import com.github.K0zka.kerub.utils.storage.iscsiStorageId
import org.apache.sshd.client.session.ClientSession
import java.util.UUID

object Ctld : OsCommand {
	fun share(session: ClientSession, id: UUID, path: String, readOnly: Boolean = false) {
		val config = """
	#begin ${id}

     auth-group ag-${id} {
     	#TODO
     	auth-type none
     }

	target ${iscsiStorageId(id)} {
	auth-group ag-${id}
	portal-group kerub

		lun 0 {
			path $path
			${readonlyOption(readOnly)}
		}
	}
	#end ${id}

		"""
		TODO()
	}

	private fun readonlyOption(readOnly: Boolean) = if (readOnly) {
		"readonly on"
	} else {
		""
	}

	fun unshare(session: ClientSession) {
		TODO()
	}
}