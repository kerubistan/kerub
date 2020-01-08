package com.github.kerubistan.kerub.utils.junix.packagemanager.pkg

import com.github.kerubistan.kerub.host.execute
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import org.apache.sshd.client.session.ClientSession

object Pkg {

	fun installPackage(session: ClientSession, vararg packs: String) {
		require(packs.isNotEmpty())
		session.execute("""pkg install -y ${packs.joinToString(separator = " ")}""")
	}

	fun uninstallPackage(session: ClientSession, vararg packs: String) {
		require(packs.isNotEmpty())
		session.execute("""pkg remove -y ${packs.joinToString(separator = " ")}""")
	}

	fun listPackages(session: ClientSession): List<SoftwarePackage> =
			session.execute("""pkg query "%n\t%v" """).lines().filter { it.isNotEmpty() }.map {
				val split = it.split('\t')
				SoftwarePackage(split[0], Version.fromVersionString(split[1]))
			}

}