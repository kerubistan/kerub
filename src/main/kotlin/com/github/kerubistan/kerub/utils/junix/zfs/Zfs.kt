package com.github.kerubistan.kerub.utils.junix.zfs

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.utils.flag
import com.github.kerubistan.kerub.utils.skip
import com.github.kerubistan.kerub.utils.toSize
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger

object Zfs {

	private val spaces = "\\s+".toRegex()

	fun list(session: ClientSession): List<ZfsObject> = session.executeOrDie("zfs list -t volume").lines().skip().map {
		val split = it.split("")
		ZfsObject(
				name = split[0],
				used = split[1].toSize(),
				type = TODO(),
				usedbychildren = TODO(),
				usedByDataset = TODO()
		)
	}

	fun create(session: ClientSession,
			   name: String,
			   size: BigInteger,
			   sparse: Boolean = false) {
		session.executeOrDie("zfs create -V ${size} ${sparse.flag("-s")} $name")
	}

}