package com.github.K0zka.kerub.utils.junix.packagemanager.solpkg

import com.github.K0zka.kerub.host.execute
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.utils.getLogger
import org.apache.sshd.client.session.ClientSession

/**
 * Opensolaris / OpenIndiana pkg utility wrapper
 */
object Pkg {
	private val logger = getLogger(Pkg::class)

	fun installPackage(session: ClientSession, vararg packs: String) {
		require(packs.isNotEmpty())
		session.execute("""pkg install ${packs.joinToString(separator = " ")}""")
	}

	fun uninstallPackage(session: ClientSession, vararg packs: String) {
		require(packs.isNotEmpty())
		session.execute("""pkg uninstall ${packs.joinToString(separator = " ")}""")
	}

	fun listPackages(session: ClientSession): List<SoftwarePackage>
			= session.execute("""pkg list """).lines().filter { it.isNotEmpty() }.map {
		val split = it.split('\t')
		SoftwarePackage(split[0], Version.fromVersionString(split[1]))
	}

}