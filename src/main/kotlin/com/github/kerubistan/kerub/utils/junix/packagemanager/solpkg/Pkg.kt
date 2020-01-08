package com.github.kerubistan.kerub.utils.junix.packagemanager.solpkg

import com.github.kerubistan.kerub.host.execute
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import org.apache.sshd.client.session.ClientSession

/**
 * Opensolaris / OpenIndiana pkg utility wrapper
 */
object Pkg {

	fun installPackage(session: ClientSession, vararg packs: String) {
		require(packs.isNotEmpty())
		session.execute("""pkg install ${packs.joinToString(separator = " ")}""")
	}

	fun uninstallPackage(session: ClientSession, vararg packs: String) {
		require(packs.isNotEmpty())
		session.execute("""pkg uninstall ${packs.joinToString(separator = " ")}""")
	}

	fun listPackages(session: ClientSession): List<SoftwarePackage> =
			session.execute("""pkg list """).lines().filter { it.isNotEmpty() }.map {
				val split = it.split('\t')
				SoftwarePackage(split[0], Version.fromVersionString(split[1]))
			}

}